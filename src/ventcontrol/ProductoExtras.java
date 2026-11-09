package ventcontrol;

import claseConectar.conectar;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Complementos de la lista de productos: tarjetas de resumen, filtros rápidos (sin stock, bajo
 * mínimo, por vencer, sin movimiento), columna de ganancia y semáforo de stock.
 *
 * Funciona sobre CUALQUIER modelo que se le ponga a la tabla (la lista se recarga desde varios
 * lugares): envuelve el modelo agregando la columna "Ganancia" y lee, en segundo plano, los
 * datos numéricos de cada producto (costo, venta, stock, mínimo, vencimiento, última venta).
 */
final class ProductoExtras {

    /** Días hacia adelante para considerar un producto "por vencer". */
    static final int DIAS_VENCER = 30;
    /** Días sin ventas para considerar un producto "sin movimiento". */
    static final int DIAS_SIN_MOV = 90;
    /** true: ganancia = (venta - costo) / costo; false: (venta - costo) / venta. */
    static final boolean GANANCIA_SOBRE_COSTO = true;

    static final Color AMBAR_TXT = new Color(176, 98, 0);
    static final Color AMBAR_FONDO = new Color(254, 246, 225);
    static final Color ROJO_FONDO = new Color(253, 236, 234);
    static final Color VERDE_TXT = new Color(24, 130, 74);

    static final int TODOS = 0, SIN_STOCK = 1, BAJO = 2, VENCER = 3, SIN_MOV = 4;

    /** Datos numéricos de un producto. */
    static final class Info {
        double costo, venta, stock, minimo;
        Date vence, ultVenta;
        String nombre = "";
    }

    private final JTable tabla;
    private final Map<String, Info> info = new HashMap<String, Info>();
    private boolean infoLista = false, cargando = false, movOk = true, hayFaltantes = false;
    private int filtro = TODOS;
    private TableRowSorter<TableModel> sorter;

    final Kpi kpiItems = new Kpi("PRODUCTOS", EstiloUI.AZUL_VIVO);
    final Kpi kpiCosto = new Kpi("INVENTARIO A COSTO", EstiloUI.ROJO);
    final Kpi kpiVenta = new Kpi("INVENTARIO A PRECIO DE VENTA", EstiloUI.AZUL);
    final Kpi kpiGanancia = new Kpi("GANANCIA POTENCIAL", VERDE_TXT);
    final Chip[] chips = new Chip[5];
    final javax.swing.JButton verificar = new javax.swing.JButton("Verificar totales");

    // ---- auditoría de totales: cómo se arma el listado frente a la tabla de productos ----
    private Map<String, Integer> enLista = new HashMap<String, Integer>();
    private final java.util.Set<String> sinMarca = new java.util.HashSet<String>();
    private final java.util.Set<String> sinTipo = new java.util.HashSet<String>();
    private final java.util.Set<String> sinDroga = new java.util.HashSet<String>();
    private boolean auditoriaOk = false, abrirAlCargar = false;

    private final DecimalFormat miles = new DecimalFormat("###,###");

    ProductoExtras(JTable tabla) {
        this.tabla = tabla;
        String[] nombres = {"Todos", "Sin stock", "Bajo mínimo", "Por vencer", "Sin movimiento"};
        Color[] puntos = {EstiloUI.NAVY, EstiloUI.ROJO, new Color(230, 140, 20), new Color(196, 80, 160), new Color(110, 120, 135)};
        ButtonGroup grupo = new ButtonGroup();
        for (int i = 0; i < chips.length; i++) {
            final int cual = i;
            chips[i] = new Chip(nombres[i], puntos[i]);
            grupo.add(chips[i]);
            chips[i].addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    filtro = cual;
                    aplicarFiltro();
                }
            });
        }
        chips[SIN_MOV].setToolTipText("Con stock y sin ventas en los últimos " + DIAS_SIN_MOV + " días");
        chips[VENCER].setToolTipText("Con stock y que vencen en los próximos " + DIAS_VENCER + " días (o ya vencidos)");
        chips[BAJO].setToolTipText("Stock menor o igual a la cantidad mínima cargada en el producto");
        chips[TODOS].setSelected(true);
        tabla.setToolTipText("");
        EstiloUI.botonSecundario(verificar);
        verificar.setFont(EstiloUI.fuente(Font.BOLD, 12));
        verificar.setToolTipText("Compara lo que suma el listado con el total real de la base de datos");
        verificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrirAlCargar = true;
                recargar();
            }
        });
    }

    /** Componentes que hay que agregar al panel: tarjetas y chips. */
    JComponent[] componentes() {
        JComponent[] r = new JComponent[5 + chips.length];
        r[0] = kpiItems;
        r[1] = kpiCosto;
        r[2] = kpiVenta;
        r[3] = kpiGanancia;
        for (int i = 0; i < chips.length; i++) {
            r[4 + i] = chips[i];
        }
        r[4 + chips.length] = verificar;
        return r;
    }

    // =====================================================================================
    //  Colocación
    // =====================================================================================

    static final int KPI_H = 74, CHIP_H = 32;

    /** Coloca las tarjetas (fila de alto KPI_H en y) y los chips (en y2). Devuelve el y siguiente. */
    int colocar(int x, int y, int ancho) {
        int g = 12;
        int w = (ancho - 3 * g) / 4;
        Kpi[] ks = {kpiItems, kpiCosto, kpiVenta, kpiGanancia};
        for (int i = 0; i < 4; i++) {
            ks[i].setBounds(x + i * (w + g), y, i == 3 ? ancho - 3 * (w + g) : w, KPI_H);
        }
        chipsX = x;
        chipsY = y + KPI_H + 10;
        verificar.setBounds(x + ancho - 190, chipsY, 190, CHIP_H);
        ubicarChips();
        return chipsY + CHIP_H + 10;
    }

    private int chipsX, chipsY;

    /** Los chips cambian de ancho cuando cambia su contador. */
    private void ubicarChips() {
        int cx = chipsX;
        for (Chip c : chips) {
            Dimension d = c.getPreferredSize();
            c.setBounds(cx, chipsY, d.width, CHIP_H);
            cx += d.width + 8;
        }
    }

    // =====================================================================================
    //  Modelo con columna de ganancia
    // =====================================================================================

    /** Modelo que agrega al final la columna "Ganancia" (se mueve junto al precio de venta en la vista). */
    final class ModeloGanancia extends AbstractTableModel {
        final TableModel base;

        ModeloGanancia(TableModel base) {
            this.base = base;
            base.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    fireTableChanged(new TableModelEvent(ModeloGanancia.this, e.getFirstRow(), e.getLastRow(),
                            e.getColumn(), e.getType()));
                }
            });
        }

        public int getRowCount() {
            return base.getRowCount();
        }

        public int getColumnCount() {
            return base.getColumnCount() + 1;
        }

        @Override
        public String getColumnName(int c) {
            return c < base.getColumnCount() ? base.getColumnName(c) : "Ganancia";
        }

        public Object getValueAt(int r, int c) {
            if (c < base.getColumnCount()) {
                return base.getValueAt(r, c);
            }
            Info i = infoDe(base.getValueAt(r, 0));
            Double g = i == null ? null : ganancia(i);
            return g;
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return c < base.getColumnCount() ? Object.class : Double.class;
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    }

    private Info infoDe(Object cod) {
        return cod == null ? null : info.get(cod.toString());
    }

    static Double ganancia(Info i) {
        double base = GANANCIA_SOBRE_COSTO ? i.costo : i.venta;
        if (base <= 0) {
            return null;
        }
        return Double.valueOf((i.venta - i.costo) / base * 100.0);
    }

    /**
     * Debe llamarse cada vez que cambia el modelo de la tabla. Devuelve true cuando el modelo ya es el
     * envuelto y la tabla quedó lista; false si recién lo envolvió (el cambio de modelo vuelve a avisar).
     */
    boolean alCambiarModelo() {
        TableModel m = tabla.getModel();
        if (!(m instanceof ModeloGanancia)) {
            tabla.setModel(new ModeloGanancia(m));
            return false;
        }
        // orden por columnas (clic en el encabezado) y filtro
        sorter = new TableRowSorter<TableModel>(m);
        Comparator<Object> numero = new Comparator<Object>() {
            public int compare(Object a, Object b) {
                return Double.compare(aNumero(a), aNumero(b));
            }
        };
        sorter.setComparator(0, new Comparator<Object>() {
            public int compare(Object a, Object b) {
                try {
                    return Long.valueOf(a.toString().trim()).compareTo(Long.valueOf(b.toString().trim()));
                } catch (NumberFormatException ex) {
                    return a.toString().compareToIgnoreCase(b.toString());
                }
            }
        });
        sorter.setComparator(2, numero);
        sorter.setComparator(3, numero);
        sorter.setComparator(4, numero);
        sorter.setComparator(m.getColumnCount() - 1, new Comparator<Object>() {
            public int compare(Object a, Object b) {
                double x = a == null ? -1e18 : ((Double) a).doubleValue();
                double y = b == null ? -1e18 : ((Double) b).doubleValue();
                return Double.compare(x, y);
            }
        });
        sorter.setRowFilter(crearFiltro());
        tabla.setRowSorter(sorter);

        // la ganancia va junto al precio de venta
        int n = tabla.getColumnModel().getColumnCount();
        if (n > 4) {
            tabla.moveColumn(n - 1, 4);
        }
        recalcular();
        if (hayFaltantes && !cargando) {
            recargar();
        }
        return true;
    }

    private double aNumero(Object o) {
        if (o == null) {
            return 0;
        }
        try {
            return miles.parse(o.toString().trim()).doubleValue();
        } catch (java.text.ParseException ex) {
            try {
                return Double.parseDouble(o.toString().trim());
            } catch (NumberFormatException ex2) {
                return 0;
            }
        }
    }

    void estilizarColumnas() {
        for (int i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
            TableColumn c = tabla.getColumnModel().getColumn(i);
            String h = String.valueOf(c.getHeaderValue());
            if ("Stock".equals(h)) {
                c.setCellRenderer(new StockRenderer());
            } else if ("Ganancia".equals(h)) {
                c.setCellRenderer(new GananciaRenderer());
            }
        }
    }

    // =====================================================================================
    //  Filtro y resumen
    // =====================================================================================

    /** true si el producto cumple la condición del chip. */
    boolean cumple(Info i, int que) {
        Date hoy = hoy();
        switch (que) {
            case SIN_STOCK:
                return i.stock <= 0;
            case BAJO:
                return i.stock > 0 && i.minimo > 0 && i.stock <= i.minimo;
            case VENCER:
                return i.stock > 0 && i.vence != null && i.vence.getTime() <= sumarDias(hoy, DIAS_VENCER).getTime();
            case SIN_MOV:
                return movOk && i.stock > 0 && (i.ultVenta == null || i.ultVenta.getTime() < sumarDias(hoy, -DIAS_SIN_MOV).getTime());
            default:
                return true;
        }
    }

    private RowFilter<TableModel, Integer> crearFiltro() {
        if (filtro == TODOS) {
            return null;
        }
        return new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> e) {
                Info i = infoDe(e.getValue(0));
                return i != null && cumple(i, filtro);
            }
        };
    }

    private void aplicarFiltro() {
        if (sorter != null) {
            sorter.setRowFilter(crearFiltro());
        }
        recalcular();
    }

    /** Recalcula contadores de los chips y tarjetas a partir de lo que muestra la tabla. */
    void recalcular() {
        TableModel m = tabla.getModel();
        int[] cuenta = new int[5];
        hayFaltantes = false;
        for (int r = 0; r < m.getRowCount(); r++) {
            Info i = infoDe(m.getValueAt(r, 0));
            if (i == null) {
                hayFaltantes = true;
                continue;
            }
            cuenta[TODOS]++;
            for (int k = 1; k < 5; k++) {
                if (cumple(i, k)) {
                    cuenta[k]++;
                }
            }
        }
        for (int k = 0; k < chips.length; k++) {
            if (k == TODOS) {
                chips[k].setCuenta(m.getRowCount());
            } else if (k == SIN_MOV && !movOk) {
                chips[k].setCuenta(-1);
            } else {
                chips[k].setCuenta(infoLista ? cuenta[k] : -1);
            }
        }

        ubicarChips();

        // tarjetas: sobre las filas visibles
        int items = tabla.getRowCount();
        double costo = 0, venta = 0;
        int conStock = 0, sinStock = 0;
        for (int v = 0; v < items; v++) {
            Info i = infoDe(tabla.getValueAt(v, 0));
            if (i == null) {
                continue;
            }
            double s = Math.max(i.stock, 0);
            costo += i.costo * s;
            venta += i.venta * s;
            if (i.stock > 0) {
                conStock++;
            } else {
                sinStock++;
            }
        }
        kpiItems.set(miles.format(items), infoLista ? conStock + " con stock · " + sinStock + " sin stock" : "calculando…");
        kpiCosto.set(infoLista ? miles.format(costo) : "…", "Lo invertido en la mercadería");
        kpiVenta.set(infoLista ? miles.format(venta) : "…", "Si se vendiera todo el stock");
        double gan = venta - costo;
        String pct = "";
        if (infoLista && costo > 0) {
            pct = new DecimalFormat("0.#").format(GANANCIA_SOBRE_COSTO ? gan / costo * 100 : (venta > 0 ? gan / venta * 100 : 0))
                    + (GANANCIA_SOBRE_COSTO ? "% sobre el costo" : "% sobre la venta");
        }
        kpiGanancia.set(infoLista ? miles.format(gan) : "…", pct);
        tabla.repaint();
    }

    // =====================================================================================
    //  Carga de datos
    // =====================================================================================

    /** Vuelve a leer costo, precio, stock, mínimo, vencimiento y última venta de todos los productos. */
    void recargar() {
        if (cargando) {
            return;
        }
        cargando = true;
        new SwingWorker<Map<String, Info>, Void>() {
            boolean movBien = true;

            @Override
            protected Map<String, Info> doInBackground() throws Exception {
                Map<String, Info> r = new HashMap<String, Info>();
                Connection cn = null;
                try {
                    cn = new conectar().conexion();
                    Statement st = cn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT codprodu, costo, venta, stock, historial, vencimiento, nomprodu FROM producto");
                    while (rs.next()) {
                        Info i = new Info();
                        i.costo = num(rs.getString(2));
                        i.venta = num(rs.getString(3));
                        i.stock = num(rs.getString(4));
                        i.minimo = num(rs.getString(5));
                        i.vence = fecha(rs.getString(6));
                        i.nombre = rs.getString(7) == null ? "" : rs.getString(7);
                        r.put(rs.getString(1), i);
                    }
                    rs.close();
                    try {
                        rs = st.executeQuery("SELECT dv.producto_codprodu::text, max(v.fecha::text) FROM detventa dv"
                                + " JOIN venta v ON dv.venta_codventa::text = v.codventa::text GROUP BY dv.producto_codprodu::text");
                        while (rs.next()) {
                            Info i = r.get(rs.getString(1));
                            if (i != null) {
                                i.ultVenta = fecha(rs.getString(2));
                            }
                        }
                        rs.close();
                    } catch (Exception ex) {
                        movBien = false;
                    }
                    leerAuditoria(st);
                } finally {
                    if (cn != null) {
                        try {
                            cn.close();
                        } catch (Exception ignorada) {
                        }
                    }
                }
                return r;
            }

            @Override
            protected void done() {
                cargando = false;
                if (abrirAlCargar) {
                    abrirAlCargar = false;
                    pedirMostrar = true;
                }
                try {
                    Map<String, Info> r = get();
                    info.clear();
                    info.putAll(r);
                    infoLista = true;
                    movOk = movBien;
                } catch (Exception ex) {
                    infoLista = false;
                }
                hayFaltantes = false;
                if (sorter != null) {
                    sorter.setRowFilter(crearFiltro());
                }
                recalcular();
                actualizarBotonVerificar();
                if (pedirMostrar) {
                    pedirMostrar = false;
                    mostrarAuditoria();
                }
            }
        }.execute();
    }

    private boolean pedirMostrar = false;

    // =====================================================================================
    //  Auditoría de totales
    // =====================================================================================

    /** Lee (en el hilo de fondo) con las mismas uniones que usa el listado, y qué productos quedan afuera. */
    private void leerAuditoria(Statement st) {
        Map<String, Integer> lista = new HashMap<String, Integer>();
        java.util.Set<String> sm = new java.util.HashSet<String>();
        java.util.Set<String> stp = new java.util.HashSet<String>();
        java.util.Set<String> sd = new java.util.HashSet<String>();
        boolean ok = false;
        try {
            ResultSet rs = st.executeQuery("SELECT p.codprodu FROM producto p JOIN marca a ON a.id_marca=p.marca"
                    + " INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE p.codprodu!='5'");
            while (rs.next()) {
                String c = rs.getString(1);
                Integer n = lista.get(c);
                lista.put(c, n == null ? 1 : n + 1);
            }
            rs.close();
            ok = true;
        } catch (Exception ex) {
            ok = false;
        }
        String[][] causas = {
            {"marca", "a.id_marca=p.marca"}, {"tipo", "t.id=p.tipo_id"}, {"droga", "d.id_droga=p.id_droga"}};
        java.util.Set<String>[] destino = new java.util.Set[]{sm, stp, sd};
        String[] tablas = {"marca a", "tipo t", "droga d"};
        for (int k = 0; k < 3; k++) {
            try {
                ResultSet rs = st.executeQuery("SELECT p.codprodu FROM producto p WHERE NOT EXISTS (SELECT 1 FROM "
                        + tablas[k] + " WHERE " + causas[k][1] + ")");
                while (rs.next()) {
                    destino[k].add(rs.getString(1));
                }
                rs.close();
            } catch (Exception ex) {
                // la causa queda sin identificar
            }
        }
        enLista = lista;
        sinMarca.clear();
        sinMarca.addAll(sm);
        sinTipo.clear();
        sinTipo.addAll(stp);
        sinDroga.clear();
        sinDroga.addAll(sd);
        auditoriaOk = ok;
    }

    private int fueraDelListado() {
        int n = 0;
        for (String c : info.keySet()) {
            if (!enLista.containsKey(c)) {
                n++;
            }
        }
        return n;
    }

    private int repetidos() {
        int n = 0;
        for (Integer v : enLista.values()) {
            if (v > 1) {
                n++;
            }
        }
        return n;
    }

    private void actualizarBotonVerificar() {
        int problemas = 0;
        if (infoLista && auditoriaOk) {
            // el código 5 se excluye a propósito: no cuenta como problema
            for (String c : info.keySet()) {
                if (!enLista.containsKey(c) && !"5".equals(c)) {
                    problemas++;
                }
            }
            problemas += repetidos();
        }
        verificar.setText(problemas > 0 ? "⚠ Verificar totales (" + problemas + ")" : "Verificar totales");
    }

    private void mostrarAuditoria() {
        if (!infoLista) {
            javax.swing.JOptionPane.showMessageDialog(null, "No se pudieron leer los productos de la base de datos.", "Verificar totales", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        double realC = 0, realV = 0, listC = 0, listV = 0, negC = 0, negV = 0, fueraC = 0, fueraV = 0, cinco = 0;
        int neg = 0;
        for (Map.Entry<String, Info> e : info.entrySet()) {
            Info i = e.getValue();
            double s = Math.max(i.stock, 0);
            realC += i.costo * s;
            realV += i.venta * s;
            if (i.stock < 0) {
                neg++;
                negC += i.costo * i.stock;
                negV += i.venta * i.stock;
            }
            Integer n = enLista.get(e.getKey());
            if (n != null) {
                listC += n * i.costo * s;
                listV += n * i.venta * s;
            } else {
                fueraC += i.costo * s;
                fueraV += i.venta * s;
                if ("5".equals(e.getKey())) {
                    cinco = i.costo * s;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("PRODUCTOS\n");
        sb.append("  En la base de datos: ").append(info.size()).append("\n");
        if (auditoriaOk) {
            int filas = 0;
            for (Integer v : enLista.values()) {
                filas += v;
            }
            sb.append("  En el listado: ").append(filas).append(" filas (").append(enLista.size()).append(" distintos)\n");
        } else {
            sb.append("  No se pudo comprobar qué productos muestra el listado.\n");
        }
        sb.append("\nINVENTARIO A COSTO  (costo x stock, sin contar stock negativo)\n");
        sb.append("  Total real, todos los productos: ").append(miles.format(realC)).append("\n");
        if (auditoriaOk) {
            sb.append("  Lo que suma el listado:          ").append(miles.format(listC)).append("\n");
            double dif = listC - realC;
            sb.append("  Diferencia:                      ").append(dif == 0 ? "ninguna" : miles.format(dif)).append("\n");
        }
        sb.append("\nINVENTARIO A PRECIO DE VENTA\n");
        sb.append("  Total real, todos los productos: ").append(miles.format(realV)).append("\n");
        if (auditoriaOk) {
            sb.append("  Lo que suma el listado:          ").append(miles.format(listV)).append("\n");
            double dif = listV - realV;
            sb.append("  Diferencia:                      ").append(dif == 0 ? "ninguna" : miles.format(dif)).append("\n");
        }
        if (neg > 0) {
            sb.append("\nSTOCK NEGATIVO: ").append(neg).append(neg == 1 ? " producto" : " productos").append("\n");
            sb.append("  Valen ").append(miles.format(negC)).append(" a costo. Las tarjetas no los cuentan;\n");
            sb.append("  la suma anterior del pie de la lista los restaba del total.\n");
        }
        if (auditoriaOk) {
            int fuera = fueraDelListado();
            if (fuera > 0) {
                sb.append("\nNO APARECEN EN EL LISTADO: ").append(fuera).append("\n");
                int mostrados = 0;
                for (Map.Entry<String, Info> e : info.entrySet()) {
                    if (enLista.containsKey(e.getKey())) {
                        continue;
                    }
                    if (mostrados++ >= 15) {
                        sb.append("  ...\n");
                        break;
                    }
                    String c = e.getKey();
                    StringBuilder motivo = new StringBuilder();
                    if ("5".equals(c)) {
                        motivo.append("el código 5 se excluye del listado a propósito");
                    } else {
                        if (sinMarca.contains(c)) {
                            motivo.append("marca inexistente");
                        }
                        if (sinTipo.contains(c)) {
                            motivo.append(motivo.length() > 0 ? ", " : "").append("tipo inexistente");
                        }
                        if (sinDroga.contains(c)) {
                            motivo.append(motivo.length() > 0 ? ", " : "").append("droga inexistente");
                        }
                        if (motivo.length() == 0) {
                            motivo.append("causa no identificada");
                        }
                    }
                    sb.append("  ").append(c).append("  ").append(e.getValue().nombre).append("  [").append(motivo).append("]\n");
                }
                sb.append("  Valor de estos productos:\n    ").append(miles.format(fueraC)).append(" a costo\n    ")
                        .append(miles.format(fueraV)).append(" a precio de venta\n");
                if (cinco > 0) {
                    sb.append("    (incluye el código 5)\n");
                }
            }
            int rep = repetidos();
            if (rep > 0) {
                sb.append("\nREPETIDOS EN EL LISTADO (se suman más de una vez): ").append(rep).append("\n");
                int mostrados = 0;
                for (Map.Entry<String, Integer> e : enLista.entrySet()) {
                    if (e.getValue() > 1) {
                        if (mostrados++ >= 15) {
                            sb.append("  ...\n");
                            break;
                        }
                        Info i = info.get(e.getKey());
                        sb.append("  ").append(e.getKey()).append("  ").append(i == null ? "" : i.nombre)
                                .append("  aparece ").append(e.getValue()).append(" veces (marca, tipo o droga duplicada)\n");
                    }
                }
            }
            if (fuera == 0 && rep == 0) {
                sb.append("\nEl listado incluye cada producto una sola vez.\n");
            }
        }
        javax.swing.JTextArea ta = new javax.swing.JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ta.setCaretPosition(0);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(640, 420));
        javax.swing.JOptionPane.showMessageDialog(tabla.getTopLevelAncestor(), sp, "Verificar totales", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    static double num(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Double.parseDouble(s.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /** Lee "yyyy-MM-dd..." o "dd/MM/yyyy"; null si no se puede interpretar. */
    static Date fecha(String s) {
        if (s == null || s.trim().length() < 8) {
            return null;
        }
        s = s.trim();
        try {
            if (s.length() >= 10 && s.charAt(4) == '-') {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s.substring(0, 10));
            }
            if (s.length() >= 10 && s.charAt(2) == '/') {
                return new SimpleDateFormat("dd/MM/yyyy").parse(s.substring(0, 10));
            }
        } catch (java.text.ParseException ex) {
            return null;
        }
        return null;
    }

    static Date hoy() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    static Date sumarDias(Date d, int dias) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }

    // =====================================================================================
    //  Tooltip de la fila (por qué un producto aparece marcado)
    // =====================================================================================

    String tooltipDe(MouseEvent e) {
        int v = tabla.rowAtPoint(e.getPoint());
        if (v < 0) {
            return null;
        }
        Info i = infoDe(tabla.getValueAt(v, 0));
        if (i == null) {
            return null;
        }
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("Stock: ").append(fmt(i.stock));
        if (i.minimo > 0) {
            sb.append(" (mínimo ").append(fmt(i.minimo)).append(")");
        }
        if (i.vence != null) {
            long dias = (i.vence.getTime() - hoy().getTime()) / 86400000L;
            sb.append("<br>Vence: ").append(f.format(i.vence));
            sb.append(dias < 0 ? " (vencido)" : " (en " + dias + " días)");
        }
        if (movOk) {
            sb.append("<br>Última venta: ").append(i.ultVenta == null ? "sin ventas registradas" : f.format(i.ultVenta));
        }
        return sb.append("</html>").toString();
    }

    static String fmt(double d) {
        if (d == Math.rint(d)) {
            return String.valueOf((long) d);
        }
        return new DecimalFormat("0.##").format(d);
    }

    // =====================================================================================
    //  Renderers
    // =====================================================================================

    private final class StockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foco, int row, int col) {
            super.getTableCellRendererComponent(t, value, sel, false, row, col);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
            Info i = infoDe(t.getValueAt(row, 0));
            Color fondo = row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252);
            Color texto = EstiloUI.TEXTO;
            setFont(EstiloUI.fuente(Font.PLAIN, 13));
            if (i != null) {
                if (i.stock <= 0) {
                    texto = EstiloUI.ROJO;
                    fondo = ROJO_FONDO;
                    setFont(EstiloUI.fuente(Font.BOLD, 13));
                } else if (i.minimo > 0 && i.stock <= i.minimo) {
                    texto = AMBAR_TXT;
                    fondo = AMBAR_FONDO;
                    setFont(EstiloUI.fuente(Font.BOLD, 13));
                }
            }
            setForeground(texto);
            if (!sel) {
                setBackground(fondo);
            }
            return this;
        }
    }

    private final class GananciaRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat f = new DecimalFormat("0.#");

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foco, int row, int col) {
            super.getTableCellRendererComponent(t, null, sel, false, row, col);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setFont(EstiloUI.fuente(Font.BOLD, 13));
            if (value instanceof Double) {
                double g = ((Double) value).doubleValue();
                setText(f.format(g) + " %");
                setForeground(g < 0 ? EstiloUI.ROJO : (g < 10 ? AMBAR_TXT : VERDE_TXT));
            } else {
                setText("—");
                setForeground(EstiloUI.MUTED);
            }
            if (!sel) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
            }
            return this;
        }
    }

    // =====================================================================================
    //  Componentes visuales
    // =====================================================================================

    /** Tarjeta de resumen: título, cifra grande y una línea de apoyo. */
    static final class Kpi extends JPanel {
        private final String titulo;
        private final Color acento;
        private String valor = "—", apoyo = "";

        Kpi(String titulo, Color acento) {
            this.titulo = titulo;
            this.acento = acento;
            setOpaque(false);
        }

        void set(String valor, String apoyo) {
            this.valor = valor;
            this.apoyo = apoyo == null ? "" : apoyo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight() - 3;
            g2.setColor(new Color(16, 44, 72, 22));
            g2.fillRoundRect(1, 3, w - 2, h, 14, 14);
            java.awt.Shape forma = new java.awt.geom.RoundRectangle2D.Float(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(Color.WHITE);
            g2.fill(forma);
            java.awt.Shape clip = g2.getClip();
            g2.clip(forma);
            g2.setColor(acento);
            g2.fillRect(0, 0, 5, h);
            g2.setClip(clip);
            g2.setColor(new Color(214, 224, 236));
            g2.draw(forma);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 10));
            g2.setColor(EstiloUI.MUTED);
            g2.drawString(titulo, 18, 20);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 22));
            g2.setColor(EstiloUI.TEXTO);
            FontMetrics fm = g2.getFontMetrics();
            String v = recortar(valor, fm, w - 30);
            g2.drawString(v, 18, 46);
            g2.setFont(EstiloUI.fuente(Font.PLAIN, 11));
            g2.setColor(EstiloUI.MUTED);
            g2.drawString(recortar(apoyo, g2.getFontMetrics(), w - 30), 18, 63);
            g2.dispose();
        }

        private static String recortar(String s, FontMetrics fm, int max) {
            if (fm.stringWidth(s) <= max) {
                return s;
            }
            while (s.length() > 1 && fm.stringWidth(s + "…") > max) {
                s = s.substring(0, s.length() - 1);
            }
            return s + "…";
        }
    }

    /** Botón tipo "píldora" para filtros rápidos (uno solo activo a la vez). */
    static final class Chip extends JToggleButton {
        private final String nombre;
        private final Color punto;
        private int cuenta = -1;

        Chip(String nombre, Color punto) {
            this.nombre = nombre;
            this.punto = punto;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setText(nombre);
        }

        void setCuenta(int c) {
            cuenta = c;
            setText(texto());
            revalidate();
            repaint();
        }

        private String texto() {
            return cuenta < 0 ? nombre : nombre + "  " + cuenta;
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(EstiloUI.fuente(Font.BOLD, 12));
            return new Dimension(fm.stringWidth(texto()) + 46, ProductoExtras.CHIP_H);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            boolean sel = isSelected();
            boolean hover = getModel().isRollover();
            if (sel) {
                g2.setColor(EstiloUI.NAVY);
                g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);
            } else {
                g2.setColor(hover ? new Color(232, 241, 247) : Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);
                g2.setColor(EstiloUI.BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
            }
            g2.setColor(sel ? Color.WHITE : punto);
            g2.fillOval(14, h / 2 - 4, 8, 8);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 12));
            g2.setColor(sel ? Color.WHITE : EstiloUI.TEXTO);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto(), 29, (h + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}

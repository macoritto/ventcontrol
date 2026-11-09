package ventcontrol;

import claseConectar.conectar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Historial de movimientos de un producto: compras, ventas, ajustes, devoluciones y cambios,
 * del más nuevo al más viejo, con el stock que quedó después de cada movimiento.
 *
 * El stock "después de cada movimiento" se calcula hacia atrás a partir del stock actual: si el
 * historial está completo, el stock inicial implícito (el que se muestra en la tarjeta) coincide con
 * el que se cargó al crear el producto. Si no coincide, hay movimientos que no pasaron por estas
 * tablas, o diferencias de stock.
 */
public class ProductoHistorial extends JDialog {

    /** Un movimiento de stock. */
    private static final class Mov {
        String fecha = "", orden = "", tipo = "", detalle = "", usuario = "";
        double delta, precio = Double.NaN, saldo;
        long id;
    }

    private final String cod;
    private final String nombre;
    private double stockActual;
    private boolean stockLeido = false;
    private final List<Mov> movs = new ArrayList<Mov>();
    private final List<String> avisos = new ArrayList<String>();

    private final ProductoExtras.Kpi kStock = new ProductoExtras.Kpi("STOCK ACTUAL", EstiloUI.AZUL_VIVO);
    private final ProductoExtras.Kpi kEntradas = new ProductoExtras.Kpi("ENTRADAS", ProductoExtras.VERDE_TXT);
    private final ProductoExtras.Kpi kSalidas = new ProductoExtras.Kpi("SALIDAS", EstiloUI.ROJO);
    private final ProductoExtras.Kpi kUltima = new ProductoExtras.Kpi("ÚLTIMA VENTA", FormVentaUI.AMBAR);
    private final JTable tabla = new JTable();
    private final JScrollPane scroll = new JScrollPane(tabla);
    private final JLabel titulo = new JLabel("HISTORIAL DEL PRODUCTO");
    private final JLabel producto = new JLabel();
    private final JLabel estado = new JLabel("Cargando movimientos…");
    private final JComboBox ver = new JComboBox(new String[]{"Todos los movimientos", "Solo entradas", "Solo salidas"});
    private final JButton cerrar = new JButton("Cerrar (Esc)");
    private final DecimalFormat miles = new DecimalFormat("###,###");

    public ProductoHistorial(JFrame dueno, String cod, String nombre) {
        super(dueno, true);
        this.cod = cod;
        this.nombre = nombre == null ? "" : nombre;
        setTitle("Historial del producto");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        armar();
        cargar();
    }

    private void armar() {
        final Container cp = getContentPane();
        cp.setLayout(null);
        cp.setBackground(FormVentaUI.FONDO_VENTA);

        titulo.setFont(EstiloUI.fuente(Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        producto.setFont(EstiloUI.fuente(Font.BOLD, 15));
        producto.setForeground(Color.WHITE);
        producto.setHorizontalAlignment(SwingConstants.RIGHT);
        producto.setText(cod + "  ·  " + nombre);
        estado.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        estado.setForeground(EstiloUI.MUTED);

        EstiloUI.tabla(tabla, scroll);
        tabla.setModel(modeloVacio());
        EstiloUI.combo(ver);
        EstiloUI.botonSecundario(cerrar);
        cerrar.setFont(EstiloUI.fuente(Font.BOLD, 13));
        FormVentaUI.ico(cerrar, EstiloUI.ICONO_X, EstiloUI.ROJO);
        cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        ver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                llenarTabla();
            }
        });

        final FormVentaUI.Tarjeta tMov = new FormVentaUI.Tarjeta("MOVIMIENTOS DEL PRODUCTO", FormVentaUI.VERDE, false);
        final FormVentaUI.Cabecera cabecera = new FormVentaUI.Cabecera();
        cp.add(titulo);
        cp.add(producto);
        cp.add(kStock);
        cp.add(kEntradas);
        cp.add(kSalidas);
        cp.add(kUltima);
        cp.add(ver);
        cp.add(scroll);
        cp.add(estado);
        cp.add(cerrar);
        cp.add(tMov);
        cp.add(cabecera);

        final Runnable colocar = new Runnable() {
            public void run() {
                int w = cp.getWidth(), h = cp.getHeight();
                if (w <= 0 || h <= 0) {
                    w = getWidth() - 16;
                    h = getHeight() - 40;
                }
                int M = 20, HEAD = 60, TH = 28, G = 12;
                cabecera.setBounds(0, 0, w, HEAD);
                titulo.setBounds(M, 0, 340, HEAD - 3);
                producto.setBounds(M + 340, 0, w - 2 * M - 340, HEAD - 3);
                int top = HEAD + 14;
                int kw = (w - 2 * M - 3 * G) / 4;
                ProductoExtras.Kpi[] ks = {kStock, kEntradas, kSalidas, kUltima};
                for (int i = 0; i < 4; i++) {
                    ks[i].setBounds(M + i * (kw + G), top, i == 3 ? w - 2 * M - 3 * (kw + G) : kw, ProductoExtras.KPI_H);
                }
                int yt = top + ProductoExtras.KPI_H + 12;
                int bottomH = 40, bottomY = h - M - bottomH;
                tMov.setBounds(M, yt, w - 2 * M, bottomY - 10 - yt);
                ver.setBounds(w - M - 12 - 220, yt + 2, 220, TH - 4);
                scroll.setBounds(M + 8, yt + TH + 6, w - 2 * M - 16, bottomY - 10 - yt - TH - 6 - 11);
                estado.setBounds(M, bottomY, w - 2 * M - 160, bottomH);
                cerrar.setBounds(w - M - 150, bottomY, 150, bottomH);
            }
        };
        cp.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                colocar.run();
            }
        });
        setResizable(true);
        Rectangle pant = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int ancho = Math.min(1080, pant.width - 20);
        int alto = Math.min(700, pant.height - 10);
        setMinimumSize(new Dimension(Math.min(820, ancho), Math.min(520, alto)));
        setSize(ancho, alto);
        colocar.run();
        setLocationRelativeTo(getOwner());
        getRootPane().registerKeyboardAction(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        }, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
    }

    private DefaultTableModel modeloVacio() {
        return new DefaultTableModel(new Object[][]{},
                new String[]{"Fecha", "Tipo", "Detalle", "Cantidad", "Precio", "Usuario", "Stock resultante"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    // =====================================================================================
    //  Carga
    // =====================================================================================

    private void cargar() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                leer();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    avisos.add("el historial");
                }
                resumen();
                llenarTabla();
            }
        }.execute();
    }

    private static String sub(String alias, String col) {
        return "(SELECT u.usuario FROM usuario u WHERE CAST(u.id AS text)=CAST(" + alias + "." + col + " AS text) LIMIT 1)";
    }

    private void leer() {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            // stock actual
            try {
                PreparedStatement ps = cn.prepareStatement("SELECT CAST(stock AS text) FROM producto WHERE CAST(codprodu AS text)=?");
                ps.setString(1, cod);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    stockActual = ProductoExtras.num(rs.getString(1));
                    stockLeido = true;
                }
                rs.close();
                ps.close();
            } catch (Exception ex) {
                avisos.add("el stock actual");
            }

            // compras (+)
            consulta(cn, "las compras",
                    "SELECT CAST(c.fecha AS text), CAST(c.codcompra AS text), CAST(dc.cantidad AS text), CAST(dc.preunit AS text),"
                    + " CAST(c.nrofactura AS text), " + sub("c", "usuario_id")
                    + " FROM detcompra dc JOIN compra c ON CAST(c.codcompra AS text)=CAST(dc.compra_codcompra AS text)"
                    + " WHERE CAST(dc.producto_codprodu AS text)=?",
                    new Fila() {
                        public Mov arma(ResultSet rs) throws Exception {
                            Mov m = new Mov();
                            m.fecha = rs.getString(1);
                            m.id = entero(rs.getString(2));
                            m.tipo = "Compra";
                            m.detalle = "Compra N° " + rs.getString(2) + vacio(rs.getString(5), "  ·  Factura ");
                            m.delta = ProductoExtras.num(rs.getString(3));
                            m.precio = ProductoExtras.num(rs.getString(4));
                            m.usuario = nz(rs.getString(6));
                            return m;
                        }
                    });

            // ventas (-)
            consulta(cn, "las ventas",
                    "SELECT CAST(v.fecha AS text), CAST(v.codventa AS text), CAST(dv.cantidad AS text), CAST(dv.preunit AS text),"
                    + " (SELECT trim(coalesce(cl.nombre,'')||' '||coalesce(cl.apellido,'')) FROM cliente cl WHERE CAST(cl.id AS text)=CAST(v.cliente_id AS text) LIMIT 1),"
                    + sub("v", "usuario_id") + ", CAST(v.estado AS text)"
                    + " FROM detventa dv JOIN venta v ON CAST(v.codventa AS text)=CAST(dv.venta_codventa AS text)"
                    + " WHERE CAST(dv.producto_codprodu AS text)=?",
                    new Fila() {
                        public Mov arma(ResultSet rs) throws Exception {
                            Mov m = new Mov();
                            m.fecha = rs.getString(1);
                            m.id = entero(rs.getString(2));
                            m.tipo = "Venta";
                            m.detalle = "Venta N° " + rs.getString(2) + vacio(rs.getString(5), "  ·  ")
                                    + ("Credito".equalsIgnoreCase(nz(rs.getString(7))) ? "  (crédito)" : "");
                            m.delta = -ProductoExtras.num(rs.getString(3));
                            m.precio = ProductoExtras.num(rs.getString(4));
                            m.usuario = nz(rs.getString(6));
                            return m;
                        }
                    });

            // ajustes de stock (+/-)
            consulta(cn, "los ajustes",
                    "SELECT CAST(r.fecha AS text), CAST(r.id AS text), CAST(r.estado AS text), CAST(r.cantidad AS text),"
                    + sub("r", "usuario") + ", CAST(r.descripcion AS text)"
                    + " FROM reajuste r WHERE CAST(r.producto_codprodu AS text)=?",
                    new Fila() {
                        public Mov arma(ResultSet rs) throws Exception {
                            Mov m = new Mov();
                            m.fecha = rs.getString(1);
                            m.id = entero(rs.getString(2));
                            boolean suma = "Suma".equalsIgnoreCase(nz(rs.getString(3)).trim());
                            m.tipo = suma ? "Ajuste (+)" : "Ajuste (−)";
                            m.detalle = "Ajuste de stock N° " + rs.getString(2) + vacio(nz(rs.getString(6)).trim(), "  ·  ");
                            double c = ProductoExtras.num(rs.getString(4));
                            m.delta = suma ? c : -c;
                            m.usuario = nz(rs.getString(5));
                            return m;
                        }
                    });

            // devoluciones (+)
            consulta(cn, "las devoluciones",
                    "SELECT CAST(d.fecha AS text), CAST(d.id_devo AS text), CAST(dd.cantidad AS text),"
                    + sub("d", "usuario") + ", CAST(d.descripcion AS text)"
                    + " FROM detdevo dd JOIN devolucion d ON CAST(d.id_devo AS text)=CAST(dd.devolucion AS text)"
                    + " WHERE CAST(dd.producto AS text)=?",
                    new Fila() {
                        public Mov arma(ResultSet rs) throws Exception {
                            Mov m = new Mov();
                            m.fecha = rs.getString(1);
                            m.id = entero(rs.getString(2));
                            m.tipo = "Devolución";
                            m.detalle = "Devolución N° " + rs.getString(2) + vacio(nz(rs.getString(5)).trim(), "  ·  ");
                            m.delta = ProductoExtras.num(rs.getString(3));
                            m.usuario = nz(rs.getString(4));
                            return m;
                        }
                    });

            // cambios a granel (el donante descuenta 1, el receptor suma la cantidad: así lo registra el sistema)
            consulta(cn, "los cambios",
                    "SELECT CAST(c.fecha AS text), CAST(c.id AS text), CAST(dc.cantidad AS text), CAST(dc.estado AS text),"
                    + sub("c", "usuario") + ", CAST(c.descripcion AS text)"
                    + " FROM detcambio dc JOIN cambio c ON CAST(c.id AS text)=CAST(dc.cambio_codcambio AS text)"
                    + " WHERE CAST(dc.producto_codprodu AS text)=?",
                    new Fila() {
                        public Mov arma(ResultSet rs) throws Exception {
                            Mov m = new Mov();
                            m.fecha = rs.getString(1);
                            m.id = entero(rs.getString(2));
                            boolean dona = "Donante".equalsIgnoreCase(nz(rs.getString(4)).trim());
                            m.tipo = dona ? "Cambio (entrega)" : "Cambio (recibe)";
                            m.detalle = "Cambio N° " + rs.getString(2) + vacio(nz(rs.getString(6)).trim(), "  ·  ");
                            m.delta = dona ? -1 : ProductoExtras.num(rs.getString(3));
                            m.usuario = nz(rs.getString(5));
                            return m;
                        }
                    });
        } catch (Exception ex) {
            avisos.add("(sin conexión a la base de datos)");
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (Exception ignorada) {
                }
            }
        }

        // del más nuevo al más viejo
        Collections.sort(movs, new Comparator<Mov>() {
            public int compare(Mov a, Mov b) {
                int c = clave(b).compareTo(clave(a));
                if (c != 0) {
                    return c;
                }
                return b.id < a.id ? -1 : (b.id > a.id ? 1 : 0);
            }
        });
        double saldo = stockActual;
        for (Mov m : movs) {
            m.saldo = saldo;
            saldo -= m.delta;
        }
        stockInicial = saldo;
    }

    private double stockInicial;

    private interface Fila {
        Mov arma(ResultSet rs) throws Exception;
    }

    private void consulta(Connection cn, String que, String sql, Fila f) {
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, cod);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Mov m = f.arma(rs);
                String iso = isoDe(m.fecha);
                m.orden = iso;
                m.fecha = mostrar(m.fecha);
                movs.add(m);
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            avisos.add(que);
        }
    }

    private static String clave(Mov m) {
        return m.orden == null ? "" : m.orden;
    }

    private static String isoDe(String s) {
        if (s != null && s.length() >= 10 && s.charAt(4) == '-') {
            return s.substring(0, 10);
        }
        java.util.Date d = ProductoExtras.fecha(s);
        return d == null ? "" : new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    private static String mostrar(String s) {
        if (s != null && s.length() >= 10 && s.charAt(4) == '-') {
            return s.substring(8, 10) + "/" + s.substring(5, 7) + "/" + s.substring(0, 4);
        }
        return s == null ? "" : s;
    }

    private static long entero(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    /** prefijo + texto, o vacío si el texto está vacío. */
    private static String vacio(String s, String prefijo) {
        return s == null || s.trim().length() == 0 ? "" : prefijo + s.trim();
    }

    // =====================================================================================
    //  Presentación
    // =====================================================================================

    private String cant(double d) {
        return ProductoExtras.fmt(d);
    }

    private void resumen() {
        double ent = 0, sal = 0;
        String ult = null;
        for (Mov m : movs) {
            if (m.delta > 0) {
                ent += m.delta;
            } else {
                sal += -m.delta;
            }
            if (ult == null && "Venta".equals(m.tipo)) {
                ult = m.fecha;
            }
        }
        kStock.set(stockLeido ? cant(stockActual) : "—", stockLeido ? "Inicial implícito: " + cant(stockInicial) : "");
        kEntradas.set(cant(ent), "Compras, devoluciones y ajustes (+)");
        kSalidas.set(cant(sal), "Ventas, cambios y ajustes (−)");
        kUltima.set(ult == null ? "—" : ult, ult == null ? "Sin ventas registradas" : "Última salida por venta");
    }

    private void llenarTabla() {
        int modo = ver.getSelectedIndex();
        DefaultTableModel m = modeloVacio();
        int n = 0;
        for (Mov x : movs) {
            if (modo == 1 && x.delta <= 0) {
                continue;
            }
            if (modo == 2 && x.delta >= 0) {
                continue;
            }
            n++;
            m.addRow(new Object[]{x.fecha, x.tipo, x.detalle,
                (x.delta > 0 ? "+" : "−") + cant(Math.abs(x.delta)),
                Double.isNaN(x.precio) ? "" : miles.format(x.precio),
                x.usuario, cant(x.saldo)});
        }
        tabla.setModel(m);
        int[] anchos = {90, 120, 330, 90, 100, 110, 120};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        EstiloUI.derecha(tabla, 3, 4, 6);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, false, r, c);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(EstiloUI.fuente(Font.BOLD, 13));
                String s = String.valueOf(v);
                setForeground(s.startsWith("+") ? ProductoExtras.VERDE_TXT : EstiloUI.ROJO);
                if (!sel) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                return this;
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append(n).append(n == 1 ? " movimiento" : " movimientos");
        if (!avisos.isEmpty()) {
            sb.append("   ·   ⚠ No se pudieron leer: ");
            for (int i = 0; i < avisos.size(); i++) {
                sb.append(i > 0 ? ", " : "").append(avisos.get(i));
            }
            estado.setForeground(ProductoExtras.AMBAR_TXT);
        } else {
            sb.append("   ·   El stock resultante se calcula hacia atrás desde el stock actual.");
            estado.setForeground(EstiloUI.MUTED);
        }
        estado.setText(sb.toString());
    }
}

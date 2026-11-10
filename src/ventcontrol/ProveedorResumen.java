package ventcontrol;

import claseConectar.conectar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Panel de la ventana "Proveedor Seleccionado": lo que ese proveedor nos vendió en total, los productos que más nos vendió y si
 * se le debe algo (con lo vencido). Los datos salen de las compras (compra / detcompra) y de la cuenta por pagar del proveedor.
 */
final class ProveedorResumen extends JPanel {

    /** Un producto comprado a este proveedor, sumando todas sus compras. */
    static final class Producto {

        String cod = "", nombre = "", ultima = "";
        double cantidad;
        long total, ultimoPrecio;
        int compras;
    }

    /** Todo lo que se muestra del proveedor. */
    static final class Datos {

        long totalComprado, deuda, vencido;
        int compras;
        String primera = "", ultima = "";
        final List<Producto> productos = new ArrayList<Producto>();
        boolean deudaLeida;
        String aviso = "";
    }

    private static final String[] ORDEN = {"Mayor monto", "Mayor cantidad", "Más compras", "Más recientes"};

    private final String proveedorId;
    private final int usuarioId;
    private final JLabel lblTotal = new JLabel("-");
    private final JLabel lblTotalSub = new JLabel(" ");
    private final JLabel lblDeuda = new JLabel("-");
    private final JLabel lblDeudaSub = new JLabel(" ");
    private final JLabel lblCompras = new JLabel("-");
    private final JLabel lblComprasSub = new JLabel(" ");
    private final JLabel lblTitulo = UiCaja.caption("PRODUCTOS QUE MÁS NOS VENDIÓ");
    private final JLabel lblPie = new JLabel(" ");
    private final JTable tabla = new JTable();
    private final JComboBox orden = new JComboBox(ORDEN);
    private Color colorDeuda = EstiloUI.AZUL;
    private Datos datos = new Datos();

    ProveedorResumen(String proveedorId, int usuarioId) {
        super(new BorderLayout(0, 10));
        this.proveedorId = proveedorId == null ? "" : proveedorId.trim();
        this.usuarioId = usuarioId;
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

        JPanel arriba = new JPanel(new java.awt.GridLayout(1, 3, 10, 0));
        arriba.setOpaque(false);
        arriba.add(tarjeta("NOS VENDIÓ EN TOTAL", lblTotal, lblTotalSub, EstiloUI.AZUL, 0));
        arriba.add(tarjeta("COMPRAS", lblCompras, lblComprasSub, new Color(110, 80, 170), 1));
        arriba.add(tarjeta("LE DEBEMOS", lblDeuda, lblDeudaSub, UiCaja.NARANJA, 2));
        arriba.setPreferredSize(new Dimension(0, 82));
        add(arriba, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setOpaque(false);
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setOpaque(false);
        barra.add(lblTitulo, BorderLayout.WEST);
        JPanel ord = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 0));
        ord.setOpaque(false);
        ord.add(UiCaja.caption("ORDENAR POR"));
        EstiloUI.combo(orden);
        orden.setPreferredSize(new Dimension(140, 28));
        ord.add(orden);
        barra.add(ord, BorderLayout.EAST);
        centro.add(barra, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(tabla);
        EstiloUI.tabla(tabla, sp);
        tabla.setRowHeight(26);
        centro.add(sp, BorderLayout.CENTER);
        lblPie.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        lblPie.setForeground(EstiloUI.MUTED);
        centro.add(lblPie, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        JButton cuenta = new JButton("Ver cuenta y extracto");
        EstiloUI.botonAccion(cuenta);
        cuenta.setPreferredSize(new Dimension(190, 36));
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        pie.setOpaque(false);
        pie.add(cuenta);
        cuenta.setVisible(Sesion.puede("cuentas_pagar.ver")); // el extracto y la cuenta se ven con ese permiso
        add(pie, BorderLayout.SOUTH);

        cuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                CuentaProveedor.mostrar(ProveedorResumen.this, ProveedorResumen.this.proveedorId, ProveedorResumen.this.usuarioId);
                cargar(); // por si se pagó o se cerró la cuenta
            }
        });
        orden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                mostrarProductos();
            }
        });
        mostrarProductos();
        cargar();
    }

    // ------------------------------------------------------------------ tarjetas

    private JPanel tarjeta(String titulo, final JLabel valor, final JLabel sub, final Color acento, final int idx) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(EstiloUI.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(idx == 2 ? colorDeuda : acento);
                g2.fillRoundRect(0, 6, 4, getHeight() - 13, 4, 4);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 14, 6, 8));
        JLabel t = UiCaja.caption(titulo);
        t.setAlignmentX(0f);
        valor.setFont(EstiloUI.fuente(Font.BOLD, 17));
        valor.setForeground(EstiloUI.TEXTO);
        valor.setAlignmentX(0f);
        sub.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        sub.setForeground(EstiloUI.MUTED);
        sub.setAlignmentX(0f);
        p.add(t);
        p.add(javax.swing.Box.createVerticalStrut(3));
        p.add(valor);
        p.add(javax.swing.Box.createVerticalStrut(1));
        p.add(sub);
        return p;
    }

    // ------------------------------------------------------------------ carga

    void cargar() {
        lblPie.setText("Cargando...");
        new SwingWorker<Datos, Void>() {
            @Override
            protected Datos doInBackground() {
                return leer(proveedorId);
            }

            @Override
            protected void done() {
                try {
                    datos = get();
                } catch (Exception e) {
                    datos = new Datos();
                    datos.aviso = "No se pudo leer: " + e.getMessage();
                }
                aplicar();
            }
        }.execute();
    }

    private void aplicar() {
        lblTotal.setText("Gs. " + UiCaja.monto(datos.totalComprado));
        lblTotalSub.setText(datos.compras == 0 ? "sin compras registradas"
                : "promedio Gs. " + UiCaja.monto(Math.round(datos.totalComprado / (double) datos.compras)));
        lblCompras.setText(String.valueOf(datos.compras));
        lblComprasSub.setText(datos.compras == 0 ? " " : (datos.primera.equals(datos.ultima) ? "el " + fecha(datos.ultima)
                : fecha(datos.primera) + " al " + fecha(datos.ultima)));
        if (!datos.deudaLeida) {
            lblDeuda.setText("-");
            lblDeudaSub.setText("no se pudo leer la cuenta");
            colorDeuda = EstiloUI.MUTED;
        } else if (datos.deuda > 0) {
            lblDeuda.setText("Gs. " + UiCaja.monto(datos.deuda));
            lblDeuda.setForeground(UiCaja.NARANJA);
            lblDeudaSub.setText(datos.vencido > 0 ? "vencido Gs. " + UiCaja.monto(datos.vencido) : "todo en plazo");
            colorDeuda = datos.vencido > 0 ? UiCaja.ROJO : UiCaja.NARANJA;
        } else {
            lblDeuda.setText("No le debemos");
            lblDeuda.setForeground(UiCaja.VERDE);
            lblDeudaSub.setText("cuenta al día");
            colorDeuda = UiCaja.VERDE;
        }
        if (datos.deudaLeida && datos.deuda > 0) {
            lblDeuda.setForeground(datos.vencido > 0 ? UiCaja.ROJO : UiCaja.NARANJA);
        }
        mostrarProductos();
        lblPie.setText(datos.aviso.length() > 0 ? datos.aviso : (datos.compras == 0 ? "Este proveedor todavía no tiene compras registradas." : " "));
        repaint();
    }

    private static String fecha(String iso) {
        return iso != null && iso.length() >= 10 ? iso.substring(8, 10) + "/" + iso.substring(5, 7) + "/" + iso.substring(0, 4) : "";
    }

    private static String cantidad(double c) {
        if (Math.abs(c - Math.rint(c)) < 0.0005) {
            return UiCaja.monto(Math.round(c));
        }
        String s = String.format(java.util.Locale.US, "%.2f", c);
        return s.replaceAll("0+$", "").replaceAll("\\.$", "").replace('.', ',');
    }

    private void mostrarProductos() {
        List<Producto> ps = new ArrayList<Producto>(datos.productos);
        final int o = orden.getSelectedIndex();
        Collections.sort(ps, new Comparator<Producto>() {
            public int compare(Producto a, Producto b) {
                int c;
                if (o == 1) {
                    c = Double.compare(b.cantidad, a.cantidad);
                } else if (o == 2) {
                    c = b.compras < a.compras ? -1 : (b.compras > a.compras ? 1 : 0);
                } else if (o == 3) {
                    c = b.ultima.compareTo(a.ultima);
                } else {
                    c = b.total < a.total ? -1 : (b.total > a.total ? 1 : 0);
                }
                return c != 0 ? c : a.nombre.compareToIgnoreCase(b.nombre);
            }
        });
        DefaultTableModel m = new DefaultTableModel(new Object[]{"#", "Producto", "Cant.", "Compras", "Total", "Últ. precio", "Última"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        int i = 1;
        for (Producto p : ps) {
            m.addRow(new Object[]{Integer.valueOf(i++), p.nombre, cantidad(p.cantidad), Integer.valueOf(p.compras), UiCaja.monto(p.total),
                UiCaja.monto(p.ultimoPrecio), fecha(p.ultima)});
        }
        tabla.setModel(m);
        EstiloUI.derecha(tabla, 0, 2, 3, 4, 5, 6);
        int[] anchos = {36, 170, 64, 80, 104, 98, 100};
        for (int c = 0; c < anchos.length; c++) {
            tabla.getColumnModel().getColumn(c).setPreferredWidth(anchos[c]);
        }
        tabla.getColumnModel().getColumn(0).setMaxWidth(42);
        lblTitulo.setText("PRODUCTOS QUE MÁS NOS VENDIÓ" + (ps.isEmpty() ? "" : " (" + ps.size() + ")"));
    }

    // ------------------------------------------------------------------ consulta

    private static double decimal(String s) {
        if (s == null) {
            return 0;
        }
        String t = s.trim().replace(',', '.');
        if (t.length() == 0) {
            return 0;
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String iso(String f) {
        return f != null && f.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}.*") ? f.substring(0, 10) : "";
    }

    /** Lee compras, productos y deuda del proveedor. Nunca lanza: lo que falle queda indicado en {@link Datos#aviso}. */
    static Datos leer(String proveedorId) {
        Datos d = new Datos();
        String pid = proveedorId == null ? "" : proveedorId.trim();
        if (pid.length() == 0) {
            d.aviso = "Seleccione un proveedor para ver sus compras.";
            return d;
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            if (cn == null) {
                throw new SQLException("Sin conexión con la base de datos.");
            }
            PreparedStatement ps = cn.prepareStatement("SELECT c.codcompra::text AS cod, c.fecha::text AS fecha, c.total::text AS total FROM compra c "
                    + "WHERE trim(c.proveedor_codprov::text) = ?");
            ps.setString(1, pid);
            ResultSet rs = ps.executeQuery();
            Map<String, String> fechas = new LinkedHashMap<String, String>();
            while (rs.next()) {
                String f = iso(rs.getString("fecha"));
                fechas.put(rs.getString("cod").trim(), f);
                d.compras++;
                d.totalComprado += Math.round(decimal(rs.getString("total")));
                if (f.length() > 0) {
                    if (d.primera.length() == 0 || f.compareTo(d.primera) < 0) {
                        d.primera = f;
                    }
                    if (d.ultima.length() == 0 || f.compareTo(d.ultima) > 0) {
                        d.ultima = f;
                    }
                }
            }
            rs.close();
            ps.close();
            ps = cn.prepareStatement("SELECT c.codcompra::text AS compra, d.producto_codprodu::text AS cod, p.nomprodu AS nombre, d.cantidad::text AS cantidad, "
                    + "d.preunit::text AS preunit, d.total::text AS total FROM compra c JOIN detcompra d ON trim(d.compra_codcompra::text) = trim(c.codcompra::text) "
                    + "LEFT JOIN producto p ON trim(p.codprodu::text) = trim(d.producto_codprodu::text) WHERE trim(c.proveedor_codprov::text) = ?");
            ps.setString(1, pid);
            rs = ps.executeQuery();
            Map<String, Producto> mapa = new LinkedHashMap<String, Producto>();
            Map<String, java.util.Set<String>> comprasDe = new LinkedHashMap<String, java.util.Set<String>>();
            while (rs.next()) {
                String cod = CajaDatos.texto(rs.getString("cod"));
                Producto p = mapa.get(cod);
                if (p == null) {
                    p = new Producto();
                    p.cod = cod;
                    String n = CajaDatos.texto(rs.getString("nombre"));
                    p.nombre = n.length() > 0 ? n : "(producto " + cod + " eliminado)";
                    mapa.put(cod, p);
                    comprasDe.put(cod, new java.util.HashSet<String>());
                }
                double cant = decimal(rs.getString("cantidad"));
                double pu = decimal(rs.getString("preunit"));
                String totalTxt = rs.getString("total");
                long total = totalTxt != null && totalTxt.trim().length() > 0 ? Math.round(decimal(totalTxt)) : Math.round(cant * pu);
                p.cantidad += cant;
                p.total += total;
                String compra = CajaDatos.texto(rs.getString("compra"));
                comprasDe.get(cod).add(compra);
                String f = fechas.containsKey(compra) ? fechas.get(compra) : "";
                if (p.ultima.length() == 0 || f.compareTo(p.ultima) >= 0) {
                    p.ultima = f;
                    p.ultimoPrecio = Math.round(pu);
                }
            }
            rs.close();
            ps.close();
            for (Producto p : mapa.values()) {
                p.compras = comprasDe.get(p.cod).size();
                d.productos.add(p);
            }
        } catch (SQLException e) {
            d.aviso = "No se pudieron leer las compras: " + primeraLinea(e.getMessage());
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
        try {
            CuentaProvDb.asegurarEsquema();
            d.deuda = Math.max(0, CuentaProvDb.deuda(pid));
            d.deudaLeida = true;
            if (d.deuda > 0) {
                for (CuentaProvDb.Resumen r : CuentaProvDb.proveedoresConDeuda()) {
                    if (r.id.trim().equals(pid)) {
                        d.vencido = r.vencido;
                    }
                }
            }
        } catch (Exception e) {
            d.aviso = (d.aviso.length() > 0 ? d.aviso + " " : "") + "No se pudo leer la deuda: " + primeraLinea(e.getMessage());
        }
        return d;
    }

    private static String primeraLinea(String s) {
        if (s == null) {
            return "";
        }
        int i = s.indexOf('\n');
        return i > 0 ? s.substring(0, i) : s;
    }
}

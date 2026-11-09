package ventcontrol;

import claseConectar.conectar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Detalle de una venta en solo lectura (cliente, datos de la venta, productos con cantidades y precios, IVA y total).
 * Se abre desde el arqueo de caja; para modificar una venta se usa la pantalla de Ventas, como siempre.
 */
final class VentaDetalle extends JDialog {

    private static final Color VERDE = new Color(30, 132, 84);
    private static final Color NARANJA = new Color(204, 112, 22);
    private static final DecimalFormat ENTERO = new DecimalFormat("###,###");
    private static final DecimalFormat CANTIDAD = new DecimalFormat("#,##0.###");

    /** Lo leído de la base para mostrar. */
    private static final class Datos {
        String codigo = "", fecha = "", hora = "", estado = "", cliente = "", documento = "", vendedor = "", factura = "", obs = "";
        long total;
        BigDecimal iva10 = BigDecimal.ZERO, iva5 = BigDecimal.ZERO, exentas = BigDecimal.ZERO;
        final List<String[]> items = new ArrayList<String[]>();
    }

    private VentaDetalle(Component padre, Datos d) {
        super(SwingUtilities.getWindowAncestor(padre), "Detalle de la venta", ModalityType.APPLICATION_MODAL);
        construir(d);
    }

    /** Abre el detalle de la venta; si no se puede leer, avisa y no abre nada. */
    static void mostrar(Component padre, String codventa) {
        Datos d;
        try {
            d = leer(codventa);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo leer la venta " + codventa + ":\n" + e.getMessage(),
                    "Detalle de la venta", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (d == null) {
            JOptionPane.showMessageDialog(padre, "No se encontró la venta " + codventa + ".", "Detalle de la venta",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        VentaDetalle v = new VentaDetalle(padre, d);
        v.setLocationRelativeTo(padre);
        v.setVisible(true);
    }

    // ------------------------------------------------------------------ lectura

    private static String texto(String s) {
        return s == null ? "" : s.trim();
    }

    private static long importe(String s) {
        return decimal(s).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    private static BigDecimal decimal(String s) {
        if (s == null || s.trim().length() == 0) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /** Valor de una columna que puede no existir en la tabla ("" si no está). */
    private static String col(ResultSet rs, String nombre) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            if (md.getColumnLabel(i).equalsIgnoreCase(nombre)) {
                return texto(rs.getString(i));
            }
        }
        return "";
    }

    private static String dia(String s) {
        if (s.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            return s.substring(8, 10) + "/" + s.substring(5, 7) + "/" + s.substring(0, 4);
        }
        return s;
    }

    private static Datos leer(String codventa) throws SQLException {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            if (cn == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }
            Datos d = new Datos();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM venta WHERE codventa = ?");
            ps.setObject(1, codventa, java.sql.Types.OTHER);
            ResultSet rs = ps.executeQuery();
            String clienteId = "", vendedorId = "";
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            d.codigo = col(rs, "codventa");
            d.fecha = dia(col(rs, "fecha"));
            String h = col(rs, "hora");
            d.hora = h.length() >= 8 ? h.substring(0, 8) : h;
            d.estado = col(rs, "estado");
            d.total = importe(col(rs, "total"));
            d.factura = col(rs, "nrofactura");
            d.obs = col(rs, "descripcion");
            clienteId = col(rs, "cliente_id");
            vendedorId = col(rs, "vendedor_id");
            rs.close();
            ps.close();

            if (clienteId.length() > 0) {
                ps = cn.prepareStatement("SELECT * FROM cliente WHERE id = ?");
                ps.setObject(1, clienteId, java.sql.Types.OTHER);
                rs = ps.executeQuery();
                if (rs.next()) {
                    d.cliente = (col(rs, "nombre") + " " + col(rs, "apellido")).trim();
                    String ruc = col(rs, "ruc");
                    d.documento = ruc.length() > 0 ? ruc : col(rs, "ci");
                }
                rs.close();
                ps.close();
            }
            if (vendedorId.length() > 0) {
                try {
                    ps = cn.prepareStatement("SELECT * FROM vendedor WHERE id = ?");
                    ps.setObject(1, vendedorId, java.sql.Types.OTHER);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        d.vendedor = (col(rs, "nombre") + " " + col(rs, "apellido")).trim();
                    }
                    rs.close();
                    ps.close();
                } catch (SQLException sinVendedor) {
                    // sin la tabla de vendedores el dato simplemente no se muestra
                }
            }

            ps = cn.prepareStatement("SELECT d.cantidad, d.por_ven, d.producto_codprodu, d.preunit, d.total, d.ivacinco, d.ivadiez, d.ivaex, "
                    + "p.unidad_medida FROM detventa d LEFT JOIN producto p ON p.codprodu::text = d.producto_codprodu::text "
                    + "WHERE d.venta_codventa = ? ORDER BY length(d.id::text), d.id::text");
            ps.setObject(1, codventa, java.sql.Types.OTHER);
            rs = ps.executeQuery();
            while (rs.next()) {
                BigDecimal cant = decimal(col(rs, "cantidad"));
                d.items.add(new String[]{CANTIDAD.format(cant), col(rs, "unidad_medida"), col(rs, "producto_codprodu"),
                    col(rs, "por_ven"), ENTERO.format(importe(col(rs, "preunit"))), ENTERO.format(importe(col(rs, "total")))});
                // mismo cálculo que la pantalla de la venta: lo gravado 10% / 11 y lo gravado 5% / 21
                d.iva10 = d.iva10.add(decimal(col(rs, "ivadiez")).divide(new BigDecimal(11), 4, RoundingMode.HALF_UP));
                d.iva5 = d.iva5.add(decimal(col(rs, "ivacinco")).divide(new BigDecimal(21), 4, RoundingMode.HALF_UP));
                d.exentas = d.exentas.add(decimal(col(rs, "ivaex")));
            }
            rs.close();
            ps.close();
            return d;
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    // ------------------------------------------------------------------ pantalla

    private static JPanel dato(String titulo, String valor) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(titulo);
        t.setFont(EstiloUI.fuente(Font.BOLD, 11));
        t.setForeground(EstiloUI.MUTED);
        JLabel v = new JLabel(valor.length() == 0 ? "-" : valor);
        v.setFont(EstiloUI.fuente(Font.BOLD, 14));
        v.setForeground(EstiloUI.TEXTO);
        v.setToolTipText(valor.length() == 0 ? null : valor);
        p.add(t);
        p.add(Box.createVerticalStrut(2));
        p.add(v);
        return p;
    }

    private void construir(Datos d) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);

        // encabezado
        JPanel head = new JPanel(new BorderLayout(14, 0));
        head.setBackground(EstiloUI.NAVY);
        head.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, EstiloUI.AZUL_VIVO),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));
        JPanel tit = new JPanel();
        tit.setOpaque(false);
        tit.setLayout(new BoxLayout(tit, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("Venta N° " + d.codigo);
        t1.setFont(EstiloUI.fuente(Font.BOLD, 22));
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("Detalle de lo vendido (solo lectura)");
        t2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        t2.setForeground(new Color(160, 184, 208));
        tit.add(t1);
        tit.add(t2);
        head.add(tit, BorderLayout.CENTER);
        boolean contado = "Contado".equals(d.estado);
        final Color cEstado = contado ? VERDE : NARANJA;
        JLabel chip = new JLabel(d.estado.length() == 0 ? "-" : d.estado.toUpperCase(), javax.swing.SwingConstants.CENTER) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cEstado);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setOpaque(false);
        chip.setFont(EstiloUI.fuente(Font.BOLD, 12));
        chip.setForeground(Color.WHITE);
        chip.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        JPanel chipBox = new JPanel(new java.awt.GridBagLayout());
        chipBox.setOpaque(false);
        chipBox.add(chip);
        head.add(chipBox, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        // cuerpo
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 18, 24));
        add(body, BorderLayout.CENTER);

        JPanel info = new JPanel(new java.awt.GridLayout(2, 3, 18, 12));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(EstiloUI.BORDE),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));
        info.add(dato("CLIENTE", d.cliente.length() == 0 ? "(sin cliente)" : d.cliente));
        info.add(dato("RUC / CÉDULA", d.documento));
        info.add(dato("FECHA Y HORA", (d.fecha + " " + d.hora).trim()));
        info.add(dato("VENDEDOR", d.vendedor));
        info.add(dato("FACTURA", d.factura));
        info.add(dato("OBSERVACIÓN", d.obs));
        body.add(info, BorderLayout.NORTH);

        DefaultTableModel m = new DefaultTableModel(null,
                new String[]{"Cant.", "Unidad", "Cód.", "Descripción", "P. Unitario", "Subtotal"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (String[] f : d.items) {
            m.addRow(f);
        }
        JTable tabla = new JTable(m);
        JScrollPane sp = new JScrollPane(tabla);
        EstiloUI.tabla(tabla, sp);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        int[] anchos = {70, 90, 100, 380, 120, 130};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        EstiloUI.derecha(tabla, 0, 4, 5);
        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(Color.WHITE);
        caja.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE));
        JLabel th = new JLabel("Productos");
        th.setFont(EstiloUI.fuente(Font.BOLD, 15));
        th.setForeground(EstiloUI.TEXTO);
        th.setBorder(BorderFactory.createEmptyBorder(12, 18, 10, 18));
        caja.add(th, BorderLayout.NORTH);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.BORDE));
        caja.add(sp, BorderLayout.CENTER);

        // pie con IVA y total
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(new Color(249, 250, 252));
        pie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.BORDE),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        int n = d.items.size();
        StringBuilder iva = new StringBuilder(n == 1 ? "1 producto" : n + " productos");
        if (d.iva10.signum() > 0) {
            iva.append("     ·     IVA 10%: ").append(ENTERO.format(d.iva10.setScale(0, RoundingMode.HALF_UP)));
        }
        if (d.iva5.signum() > 0) {
            iva.append("     ·     IVA 5%: ").append(ENTERO.format(d.iva5.setScale(0, RoundingMode.HALF_UP)));
        }
        if (d.exentas.signum() > 0) {
            iva.append("     ·     Exentas: ").append(ENTERO.format(d.exentas.setScale(0, RoundingMode.HALF_UP)));
        }
        JLabel li = new JLabel(iva.toString());
        li.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        li.setForeground(EstiloUI.MUTED);
        JLabel lt = new JLabel("TOTAL   Gs. " + ENTERO.format(d.total));
        lt.setFont(EstiloUI.fuente(Font.BOLD, 20));
        lt.setForeground(EstiloUI.NAVY);
        pie.add(li, BorderLayout.WEST);
        pie.add(lt, BorderLayout.EAST);
        caja.add(pie, BorderLayout.SOUTH);
        body.add(caja, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar (Esc)");
        EstiloUI.botonAccion(cerrar);
        cerrar.setPreferredSize(new Dimension(140, 40));
        cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        JPanel acciones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(cerrar);
        body.add(acciones, BorderLayout.SOUTH);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        getRootPane().setDefaultButton(cerrar);
        setPreferredSize(new Dimension(980, 640));
        setMinimumSize(new Dimension(760, 520));
        pack();
    }
}

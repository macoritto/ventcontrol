package ventcontrol;

import claseConectar.conectar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Módulo Banco: cuentas bancarias y los ingresos que entran a ellas. Cada venta al contado o cobro hecho con QR,
 * transferencia o tarjeta guarda la cuenta destino (columna cuenta_id de venta y pago) y de ahí salen los movimientos:
 * no hay una segunda tabla de movimientos que pueda quedar desparejada. Saldo = saldo inicial + ingresos.
 * La estructura es la de sql/2026-11-08_banco.sql; se crea sola la primera vez que se usa.
 */
final class BancoDb {

    private static boolean esquemaIntentado = false;
    private static String ultimoError = null;
    /** Última cuenta usada por medio de pago, para ofrecerla primero la próxima vez. */
    private static final java.util.Map<String, Integer> ULTIMA = new java.util.HashMap<String, Integer>();

    private BancoDb() {
    }

    static final class Cuenta {
        int id;
        String nombre = "", banco = "", numero = "";
        long saldoInicial;
        boolean activa = true;

        @Override
        public String toString() {
            String extra = (banco.length() > 0 ? banco : "") + (numero.length() > 0 ? (banco.length() > 0 ? " · " : "") + numero : "");
            return extra.length() > 0 ? nombre + "  (" + extra + ")" : nombre;
        }
    }

    /** Un ingreso a una cuenta, tal como sale en el extracto. */
    static final class Movimiento {
        String fecha = "", hora = "", origen = "", medio = "", detalle = "", cajero = "", cuenta = "", orden = "";
        /** Negativo = egreso (pago a un proveedor). */
        long monto;
    }

    // ------------------------------------------------------------------ esquema

    private static String agregar(String tabla, String columna, String tipo) {
        return "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = '" + tabla + "' AND column_name = '" + columna
                + "') THEN ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + tipo + "; END IF; ";
    }

    static synchronized void asegurarEsquema() {
        if (esquemaIntentado) {
            return;
        }
        esquemaIntentado = true;
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS banco_cuenta (id integer PRIMARY KEY, nombre varchar(80) NOT NULL, "
                    + "banco varchar(80), numero varchar(40), saldo_inicial bigint NOT NULL DEFAULT 0, activa boolean NOT NULL DEFAULT true)");
            st.execute("DO $$ BEGIN " + agregar("venta", "cuenta_id", "integer") + agregar("pago", "cuenta_id", "integer") + "END $$");
            st.execute("CREATE INDEX IF NOT EXISTS idx_venta_cuenta ON venta (cuenta_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_pago_cuenta ON pago (cuenta_id)");
            st.close();
        } catch (Exception ex) {
            ultimoError = ex.getMessage();
            System.err.println("No se pudo preparar la base para el banco: " + ex.getMessage());
        } finally {
            cerrar(cn);
        }
    }

    static String ayuda() {
        return "\nSi el error menciona una tabla o columna del banco, ejecute el script sql/2026-11-08_banco.sql."
                + (ultimoError == null ? "" : "\n(Intento automático: " + ultimoError + ")");
    }

    private static void cerrar(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException ignorada) {
            }
        }
    }

    private static Connection conexion() throws SQLException {
        asegurarEsquema();
        Connection cn = new conectar().conexion();
        if (cn == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }
        return cn;
    }

    // ------------------------------------------------------------------ cuentas

    private static Cuenta cuenta(ResultSet rs) throws SQLException {
        Cuenta c = new Cuenta();
        c.id = rs.getInt("id");
        c.nombre = CajaDatos.texto(rs.getString("nombre"));
        c.banco = CajaDatos.texto(rs.getString("banco"));
        c.numero = CajaDatos.texto(rs.getString("numero"));
        c.saldoInicial = rs.getLong("saldo_inicial");
        c.activa = rs.getBoolean("activa");
        return c;
    }

    static List<Cuenta> cuentas(boolean soloActivas) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM banco_cuenta " + (soloActivas ? "WHERE activa " : "") + "ORDER BY nombre, id");
            ResultSet rs = ps.executeQuery();
            List<Cuenta> l = new ArrayList<Cuenta>();
            while (rs.next()) {
                l.add(cuenta(rs));
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            cerrar(cn);
        }
    }

    static Cuenta crear(String nombre, String banco, String numero, long saldoInicial) throws SQLException {
        if (nombre == null || nombre.trim().length() == 0) {
            throw new SQLException("La cuenta necesita un nombre.");
        }
        Connection cn = conexion();
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM banco_cuenta");
            rs.next();
            int id = rs.getInt(1);
            rs.close();
            st.close();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO banco_cuenta (id, nombre, banco, numero, saldo_inicial) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, nombre.trim());
            ps.setString(3, banco == null ? "" : banco.trim());
            ps.setString(4, numero == null ? "" : numero.trim());
            ps.setLong(5, saldoInicial);
            ps.executeUpdate();
            ps.close();
            Cuenta c = new Cuenta();
            c.id = id;
            c.nombre = nombre.trim();
            c.banco = banco == null ? "" : banco.trim();
            c.numero = numero == null ? "" : numero.trim();
            c.saldoInicial = saldoInicial;
            return c;
        } finally {
            cerrar(cn);
        }
    }

    static void editar(int id, String nombre, String banco, String numero, long saldoInicial) throws SQLException {
        if (nombre == null || nombre.trim().length() == 0) {
            throw new SQLException("La cuenta necesita un nombre.");
        }
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("UPDATE banco_cuenta SET nombre = ?, banco = ?, numero = ?, saldo_inicial = ? WHERE id = ?");
            ps.setString(1, nombre.trim());
            ps.setString(2, banco == null ? "" : banco.trim());
            ps.setString(3, numero == null ? "" : numero.trim());
            ps.setLong(4, saldoInicial);
            ps.setInt(5, id);
            ps.executeUpdate();
            ps.close();
        } finally {
            cerrar(cn);
        }
    }

    static void activar(int id, boolean activa) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("UPDATE banco_cuenta SET activa = ? WHERE id = ?");
            ps.setBoolean(1, activa);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ ingresos

    private static final String NUM = "'^\\s*[0-9]+(\\.[0-9]+)?\\s*$'";

    /** Total ingresado a cada cuenta desde siempre: clave = id de cuenta. */
    static java.util.Map<Integer, Long> ingresosPorCuenta() throws SQLException {
        Connection cn = conexion();
        try {
            java.util.Map<Integer, Long> m = new java.util.HashMap<Integer, Long>();
            String[] fuentes = {
                "SELECT cuenta_id, SUM(CASE WHEN total::text ~ " + NUM + " THEN trim(total::text)::numeric ELSE 0 END) FROM venta "
                + "WHERE estado = 'Contado' AND cuenta_id IS NOT NULL GROUP BY cuenta_id",
                "SELECT cuenta_id, SUM(CASE WHEN monto::text ~ " + NUM + " THEN trim(monto::text)::numeric ELSE 0 END) FROM pago "
                + "WHERE cuenta_id IS NOT NULL GROUP BY cuenta_id"};
            for (String sql : fuentes) {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    Integer id = Integer.valueOf(rs.getInt(1));
                    long v = Math.round(rs.getDouble(2));
                    Long antes = m.get(id);
                    m.put(id, Long.valueOf((antes == null ? 0 : antes.longValue()) + v));
                }
                rs.close();
                st.close();
            }
            for (java.util.Map.Entry<Integer, Long> e : egresosPorCuenta().entrySet()) {
                Long antes = m.get(e.getKey());
                m.put(e.getKey(), Long.valueOf((antes == null ? 0 : antes.longValue()) - e.getValue().longValue()));
            }
            return m;
        } finally {
            cerrar(cn);
        }
    }

    /** Lo pagado a proveedores desde cada cuenta (cuentas por pagar). Sin esa tabla o sin pagos, vacío. */
    static java.util.Map<Integer, Long> egresosPorCuenta() {
        try {
            return CuentaProvDb.egresosBancoPorCuenta();
        } catch (Exception e) {
            return new java.util.HashMap<Integer, Long>();
        }
    }

    /** Egresos (pagos a proveedores) de una cuenta (0 = todas) entre dos fechas, con el importe en negativo. */
    private static List<Movimiento> egresos(Connection cn, int cuentaId, java.util.Date desde, java.util.Date hasta) {
        List<Movimiento> l = new ArrayList<Movimiento>();
        try {
            CuentaProvDb.asegurarEsquema();
            PreparedStatement ps = cn.prepareStatement("SELECT x.id, x.fecha::text AS fecha, x.monto, COALESCE(x.descripcion, '') AS descripcion, COALESCE(pr.nombre, '') AS proveedor, "
                    + "COALESCE(u.usuario, '') AS cajero, COALESCE(bc.nombre, '') AS cuenta FROM pago_proveedor x LEFT JOIN proveedor pr ON pr.codprov::text = x.proveedor_id "
                    + "LEFT JOIN usuario u ON u.id::text = x.usuario_id LEFT JOIN banco_cuenta bc ON bc.id = x.cuenta_id "
                    + "WHERE x.origen = 'Banco' AND x.cuenta_id IS NOT NULL AND x.fecha BETWEEN ? AND ?" + (cuentaId > 0 ? " AND x.cuenta_id = ?" : "") + " ORDER BY x.fecha, x.id");
            ps.setDate(1, new java.sql.Date(desde.getTime()));
            ps.setDate(2, new java.sql.Date(hasta.getTime()));
            if (cuentaId > 0) {
                ps.setInt(3, cuentaId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.orden = CajaDatos.texto(rs.getString("fecha"));
                m.fecha = CajaDatos.dia(m.orden);
                m.origen = "Pago prov. " + rs.getInt("id");
                m.medio = "Banco";
                String d = CajaDatos.texto(rs.getString("proveedor"));
                String ds = CajaDatos.texto(rs.getString("descripcion"));
                m.detalle = d + (ds.length() > 0 ? (d.length() > 0 ? " · " : "") + ds : "");
                m.cajero = CajaDatos.texto(rs.getString("cajero"));
                m.cuenta = CajaDatos.texto(rs.getString("cuenta"));
                m.monto = -rs.getLong("monto");
                l.add(m);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            // sin pagos a proveedores
        }
        return l;
    }

    /** Egresos de una cuenta (0 = todas) anteriores a una fecha. */
    private static long egresosAntesDe(Connection cn, int cuentaId, java.util.Date fecha) {
        try {
            CuentaProvDb.asegurarEsquema();
            PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(SUM(monto), 0) FROM pago_proveedor WHERE origen = 'Banco' AND cuenta_id IS NOT NULL AND fecha < ?"
                    + (cuentaId > 0 ? " AND cuenta_id = ?" : ""));
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            if (cuentaId > 0) {
                ps.setInt(2, cuentaId);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            long v = rs.getLong(1);
            rs.close();
            ps.close();
            return v;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Movimientos de una cuenta (0 = todas) entre dos fechas, del más viejo al más nuevo: ingresos y pagos a proveedores (en negativo). */
    static List<Movimiento> movimientos(int cuentaId, java.util.Date desde, java.util.Date hasta) throws SQLException {
        Connection cn = conexion();
        try {
            String filtro = cuentaId > 0 ? " AND x.cuenta_id = ?" : "";
            String sql = "SELECT 'Venta ' || x.codventa::text AS origen, x.fecha, COALESCE(x.entregado::text, '') AS hora, x.total::text AS monto, COALESCE(x.medio_pago, '') AS medio, "
                    + "TRIM(COALESCE(cl.nombre, '') || ' ' || COALESCE(cl.apellido, '')) AS detalle, COALESCE(u.usuario, '') AS cajero, "
                    + "COALESCE(bc.nombre, '') AS cuenta, x.codventa::text AS orden "
                    + "FROM venta x LEFT JOIN cliente cl ON x.cliente_id = cl.id LEFT JOIN usuario u ON u.id::text = x.usuario_id::text "
                    + "LEFT JOIN banco_cuenta bc ON bc.id = x.cuenta_id "
                    + "WHERE x.estado = 'Contado' AND x.cuenta_id IS NOT NULL AND x.fecha BETWEEN ? AND ?" + filtro
                    + " UNION ALL "
                    + "SELECT 'Cobro ' || x.idpago, x.fecha, ''::text, x.monto::text, COALESCE(x.medio_pago, ''), COALESCE(x.descripcion::text, ''), "
                    + "COALESCE(u.usuario, ''), COALESCE(bc.nombre, ''), x.idpago::text "
                    + "FROM pago x LEFT JOIN usuario u ON u.id::text = x.usuario::text LEFT JOIN banco_cuenta bc ON bc.id = x.cuenta_id "
                    + "WHERE x.cuenta_id IS NOT NULL AND x.fecha BETWEEN ? AND ?" + filtro
                    + " ORDER BY 2, 3, 9";
            PreparedStatement ps = cn.prepareStatement(sql);
            int i = 1;
            for (int k = 0; k < 2; k++) {
                ps.setDate(i++, new java.sql.Date(desde.getTime()));
                ps.setDate(i++, new java.sql.Date(hasta.getTime()));
                if (cuentaId > 0) {
                    ps.setInt(i++, cuentaId);
                }
            }
            ResultSet rs = ps.executeQuery();
            List<Movimiento> l = new ArrayList<Movimiento>();
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.origen = CajaDatos.texto(rs.getString("origen"));
                m.fecha = CajaDatos.dia(rs.getString("fecha"));
                m.hora = CajaDatos.texto(rs.getString("hora"));
                m.monto = CajaDatos.importe(rs.getString("monto"));
                m.medio = CajaDatos.texto(rs.getString("medio"));
                m.detalle = CajaDatos.texto(rs.getString("detalle"));
                m.cajero = CajaDatos.texto(rs.getString("cajero"));
                m.cuenta = CajaDatos.texto(rs.getString("cuenta"));
                m.orden = CajaDatos.texto(rs.getString("fecha"));
                l.add(m);
            }
            rs.close();
            ps.close();
            List<Movimiento> egr = egresos(cn, cuentaId, desde, hasta);
            if (!egr.isEmpty()) {
                l.addAll(egr);
                java.util.Collections.sort(l, new java.util.Comparator<Movimiento>() {
                    public int compare(Movimiento a, Movimiento b) {
                        return a.orden.compareTo(b.orden);
                    }
                });
            }
            return l;
        } finally {
            cerrar(cn);
        }
    }

    /** Ingresos menos egresos de una cuenta (0 = todas) anteriores a una fecha: sirve para el saldo con el que arranca un período. */
    static long ingresosAntesDe(int cuentaId, java.util.Date fecha) throws SQLException {
        Connection cn = conexion();
        try {
            String filtro = cuentaId > 0 ? " AND cuenta_id = ?" : " AND cuenta_id IS NOT NULL";
            long total = 0;
            String[] sqls = {
                "SELECT COALESCE(SUM(CASE WHEN total::text ~ " + NUM + " THEN trim(total::text)::numeric ELSE 0 END), 0) FROM venta WHERE estado = 'Contado' AND fecha < ?" + filtro,
                "SELECT COALESCE(SUM(CASE WHEN monto::text ~ " + NUM + " THEN trim(monto::text)::numeric ELSE 0 END), 0) FROM pago WHERE fecha < ?" + filtro};
            for (String sql : sqls) {
                PreparedStatement ps = cn.prepareStatement(sql);
                ps.setDate(1, new java.sql.Date(fecha.getTime()));
                if (cuentaId > 0) {
                    ps.setInt(2, cuentaId);
                }
                ResultSet rs = ps.executeQuery();
                rs.next();
                total += Math.round(rs.getDouble(1));
                rs.close();
                ps.close();
            }
            return total - egresosAntesDe(cn, cuentaId, fecha);
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ al cobrar

    /** Cuentas activas para elegir al cobrar con ese medio, con la última usada por ese medio primero. */
    static List<Cuenta> cuentasParaCobrar(String medio) {
        try {
            List<Cuenta> l = cuentas(true);
            Integer ultima = ULTIMA.get(medio);
            if (ultima != null) {
                for (int i = 0; i < l.size(); i++) {
                    if (l.get(i).id == ultima.intValue()) {
                        l.add(0, l.remove(i));
                        break;
                    }
                }
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    static void recordar(String medio, int cuentaId) {
        ULTIMA.put(medio, Integer.valueOf(cuentaId));
    }

    /**
     * Pregunta en qué cuenta entró un cobro hecho con QR, transferencia o tarjeta. Devuelve null si se cancela (el
     * que llama no debe guardar nada) o si no hay cuentas cargadas, caso en el que avisa cómo crearlas.
     */
    static Cuenta pedirCuenta(Component padre, String medio, long monto) {
        List<Cuenta> cuentas;
        try {
            cuentas = cuentas(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "No se pudieron leer las cuentas bancarias:\n" + e.getMessage() + ayuda(),
                    "Banco", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (cuentas.isEmpty()) {
            JOptionPane.showMessageDialog(padre, "No hay ninguna cuenta bancaria cargada, y un cobro por " + medio
                    + " necesita saber en qué cuenta entra.\nEl administrador la crea en Arqueo y cierre de caja > Banco.",
                    "Banco", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        final JDialog d = new JDialog(UiCaja.ventana(padre), "Cuenta de destino", JDialog.ModalityType.APPLICATION_MODAL);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(EstiloUI.FONDO);
        d.add(UiCaja.cabecera("Cobro por " + medio, "Gs. " + UiCaja.monto(monto) + " entran a una cuenta bancaria"), BorderLayout.NORTH);
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new javax.swing.BoxLayout(cuerpo, javax.swing.BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));
        JLabel cap = UiCaja.caption("CUENTA EN LA QUE ENTRÓ EL DINERO");
        cap.setAlignmentX(0f);
        final JComboBox combo = new JComboBox(cuentas.toArray());
        EstiloUI.combo(combo);
        combo.setAlignmentX(0f);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        Integer ultima = ULTIMA.get(medio);
        if (ultima != null) {
            for (int i = 0; i < cuentas.size(); i++) {
                if (cuentas.get(i).id == ultima.intValue()) {
                    combo.setSelectedIndex(i);
                }
            }
        }
        cuerpo.add(cap);
        cuerpo.add(javax.swing.Box.createVerticalStrut(4));
        cuerpo.add(combo);
        d.add(cuerpo, BorderLayout.CENTER);
        final Cuenta[] elegida = new Cuenta[1];
        JButton ok = new JButton("Aceptar");
        EstiloUI.botonAccion(ok);
        ok.setPreferredSize(new Dimension(130, 38));
        JButton no = new JButton("Cancelar");
        EstiloUI.botonSecundario(no);
        no.setPreferredSize(new Dimension(120, 38));
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                elegida[0] = (Cuenta) combo.getSelectedItem();
                d.dispose();
            }
        });
        no.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                d.dispose();
            }
        });
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 20, 16, 20));
        pie.add(no);
        pie.add(ok);
        d.add(pie, BorderLayout.SOUTH);
        d.getRootPane().setDefaultButton(ok);
        d.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cancelar");
        d.getRootPane().getActionMap().put("cancelar", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                d.dispose();
            }
        });
        d.setPreferredSize(new Dimension(520, 230));
        d.pack();
        d.setLocationRelativeTo(UiCaja.ventana(padre));
        d.setVisible(true);
        if (elegida[0] != null) {
            ULTIMA.put(medio, Integer.valueOf(elegida[0].id));
        }
        return elegida[0];
    }
}

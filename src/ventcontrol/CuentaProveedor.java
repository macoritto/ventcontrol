package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Cuenta corriente de un proveedor (cuentas por pagar): lo que se le debe, cronograma de cuotas de las compras a crédito
 * (vencidas, por vencer, pagadas), movimientos del extracto, pagos desde la caja o el banco, extracto en PDF y cierre de cuenta
 * (solo administrador).
 */
final class CuentaProveedor extends JDialog {

    private final String proveedorId;
    private final int usuarioId;
    private final boolean admin;
    private final SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
    private final JTable tablaCuotas = new JTable();
    private final JTable tablaMov = new JTable();
    private final JTable tablaPagos = new JTable();
    private final JButton btnModPago = new JButton("Modificar pago");
    private final JButton btnAnuPago = new JButton("Anular pago");
    private final JLabel lblPagos = new JLabel(" ");
    private List<CuentaProvDb.Pago> pagos = new ArrayList<CuentaProvDb.Pago>();
    private final JLabel lblDeuda = new JLabel("0");
    private final JLabel lblVencido = new JLabel("0");
    private final JLabel lblPorVencer = new JLabel("0");
    private final JLabel lblProximo = new JLabel("-");
    private final JLabel lblBanner = new JLabel(" ");
    private final JLabel lblBanner2 = new JLabel(" ");
    private final JLabel lblSinPlan = new JLabel(" ");
    private final JPanel banner = new JPanel(new BorderLayout());
    private final JButton btnCobrar = new JButton("Pagar");
    private final JButton btnExtracto = new JButton("Extracto PDF");
    private final JButton btnCerrarCuenta = new JButton("Cerrar cuenta");
    private final JButton btnReabrir = new JButton("Reabrir cuenta");
    private List<CuotasDb.Cuota> cuotas = new ArrayList<CuotasDb.Cuota>();
    private long deuda;
    private boolean cierreVigente;
    private String nombre = "";

    private CuentaProveedor(java.awt.Window dueno, String proveedorId, int usuarioId) {
        super(dueno, "Cuenta del proveedor", ModalityType.APPLICATION_MODAL);
        this.proveedorId = proveedorId;
        this.usuarioId = usuarioId;
        this.admin = TurnoCaja.esAdmin(usuarioId);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Cuenta del proveedor", "Lo que se le debe, cuotas, pagos y movimientos"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(14, 22, 6, 22));

        JPanel arriba = new JPanel();
        arriba.setOpaque(false);
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        banner.setOpaque(true);
        banner.setBackground(new Color(236, 241, 247));
        banner.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        lblBanner.setFont(EstiloUI.fuente(Font.BOLD, 15));
        lblBanner.setForeground(EstiloUI.TEXTO);
        lblBanner2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblBanner2.setForeground(EstiloUI.MUTED);
        JPanel textos = new JPanel(new java.awt.GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(lblBanner);
        textos.add(lblBanner2);
        banner.add(textos, BorderLayout.CENTER);
        banner.setAlignmentX(0f);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        arriba.add(banner);
        arriba.add(Box.createVerticalStrut(12));

        JPanel kpis = new JPanel(new java.awt.GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.add(tarjeta("LE DEBEMOS", lblDeuda, UiCaja.NARANJA));
        kpis.add(tarjeta("CUOTAS VENCIDAS", lblVencido, UiCaja.ROJO));
        kpis.add(tarjeta("CUOTAS POR VENCER", lblPorVencer, EstiloUI.AZUL));
        kpis.add(tarjeta("PRÓXIMO VENCIMIENTO", lblProximo, EstiloUI.NAVY_BTN));
        kpis.setAlignmentX(0f);
        kpis.setPreferredSize(new Dimension(100, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        arriba.add(kpis);
        cuerpo.add(arriba, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(EstiloUI.fuente(Font.BOLD, 13));
        JScrollPane spC = new JScrollPane(tablaCuotas);
        EstiloUI.tabla(tablaCuotas, spC);
        JScrollPane spM = new JScrollPane(tablaMov);
        EstiloUI.tabla(tablaMov, spM);
        JPanel panelCuotas = new JPanel(new BorderLayout(0, 4));
        panelCuotas.setOpaque(false);
        panelCuotas.add(spC, BorderLayout.CENTER);
        lblSinPlan.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblSinPlan.setForeground(EstiloUI.MUTED);
        panelCuotas.add(lblSinPlan, BorderLayout.SOUTH);
        tabs.addTab("Cuotas", panelCuotas);
        tabs.addTab("Movimientos del extracto", spM);
        JScrollPane spP = new JScrollPane(tablaPagos);
        EstiloUI.tabla(tablaPagos, spP);
        EstiloUI.botonSecundario(btnModPago);
        btnModPago.setPreferredSize(new Dimension(150, 36));
        EstiloUI.botonPeligro(btnAnuPago);
        btnAnuPago.setPreferredSize(new Dimension(140, 36));
        lblPagos.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPagos.setForeground(EstiloUI.MUTED);
        JPanel barraPagos = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 6));
        barraPagos.setOpaque(false);
        barraPagos.add(btnModPago);
        barraPagos.add(btnAnuPago);
        barraPagos.add(lblPagos);
        JPanel panelPagos = new JPanel(new BorderLayout(0, 4));
        panelPagos.setOpaque(false);
        panelPagos.add(spP, BorderLayout.CENTER);
        panelPagos.add(barraPagos, BorderLayout.SOUTH);
        tabs.addTab("Pagos", panelPagos);
        btnModPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                modificarPago();
            }
        });
        btnAnuPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                anularPago();
            }
        });
        cuerpo.add(tabs, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        EstiloUI.botonAccion(btnCobrar);
        btnCobrar.setPreferredSize(new Dimension(130, 40));
        EstiloUI.botonSecundario(btnExtracto);
        btnExtracto.setPreferredSize(new Dimension(140, 40));
        EstiloUI.botonPeligro(btnCerrarCuenta);
        btnCerrarCuenta.setPreferredSize(new Dimension(150, 40));
        EstiloUI.botonSecundario(btnReabrir);
        btnReabrir.setPreferredSize(new Dimension(150, 40));
        JButton salir = new JButton("Salir (Esc)");
        EstiloUI.botonSecundario(salir);
        salir.setPreferredSize(new Dimension(120, 40));
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 22, 16, 22));
        JPanel izq = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);
        izq.add(btnCobrar);
        izq.add(btnExtracto);
        JPanel der = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);
        if (admin) {
            der.add(btnReabrir);
            der.add(btnCerrarCuenta);
        }
        der.add(salir);
        pie.add(izq, BorderLayout.WEST);
        pie.add(der, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);

        btnCobrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cobrar();
            }
        });
        btnReabrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reabrirCuenta();
            }
        });
        btnExtracto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ReportesProveedor.extracto(CuentaProveedor.this, CuentaProveedor.this.proveedorId);
            }
        });
        btnCerrarCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cerrarCuenta();
            }
        });
        salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "salir");
        getRootPane().getActionMap().put("salir", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        setPreferredSize(new Dimension(1020, 720));
        pack();
        setLocationRelativeTo(dueno);
        recargar();
    }

    static void mostrar(java.awt.Component padre, String proveedorId, int usuarioId) {
        try {
            CuentaProvDb.asegurarEsquema();
            new CuentaProveedor(UiCaja.ventana(padre), proveedorId, usuarioId).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo abrir la cuenta del proveedor:\n" + e.getMessage() + CuentaProvDb.ayuda(),
                    "Cuenta del proveedor", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------ datos

    private static final class Carga {
        String[] cliente;
        long deuda;
        List<CuotasDb.Cuota> cuotas;
        List<CuentaProvDb.Movimiento> movs;
        List<CuentaProvDb.Pago> pagos;
        CuentaProvDb.Cierre cierre;
    }

    private void recargar() {
        lblBanner.setText("Cargando...");
        new SwingWorker<Carga, Void>() {
            @Override
            protected Carga doInBackground() throws Exception {
                Carga c = new Carga();
                c.cliente = CuentaProvDb.proveedor(proveedorId);
                c.deuda = CuentaProvDb.deuda(proveedorId);
                c.cuotas = CuentaProvDb.cuotas(proveedorId);
                c.movs = CuentaProvDb.movimientos(proveedorId);
                c.pagos = CuentaProvDb.pagos(proveedorId);
                c.cierre = CuentaProvDb.cierreVigente(proveedorId);
                return c;
            }

            @Override
            protected void done() {
                try {
                    aplicar(get());
                } catch (Exception e) {
                    Throwable t = e.getCause() == null ? e : e.getCause();
                    lblBanner.setText("No se pudo leer la cuenta");
                    lblBanner2.setText(String.valueOf(t.getMessage()));
                    JOptionPane.showMessageDialog(CuentaProveedor.this, "No se pudo leer la cuenta del proveedor:\n" + t.getMessage() + CuentaProvDb.ayuda(),
                            "Cuenta del proveedor", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void aplicar(Carga c) {
        nombre = c.cliente[0];
        deuda = c.deuda;
        cuotas = c.cuotas;
        cierreVigente = c.cierre != null;
        setTitle("Cuenta del proveedor " + nombre);

        long vencido = 0, porVencer = 0, enCuotas = 0;
        Date proximo = null;
        long proximoMonto = 0;
        int nVencidas = 0;
        for (CuotasDb.Cuota q : cuotas) {
            enCuotas += q.saldo();
            String e = q.estado();
            if ("Vencida".equals(e)) {
                vencido += q.saldo();
                nVencidas++;
            } else if ("Pendiente".equals(e)) {
                porVencer += q.saldo();
                if (proximo == null) {
                    proximo = q.vencimiento;
                    proximoMonto = q.saldo();
                }
            }
        }
        lblDeuda.setText("Gs. " + UiCaja.monto(Math.max(0, deuda)));
        lblVencido.setText("Gs. " + UiCaja.monto(vencido));
        lblVencido.setForeground(vencido > 0 ? UiCaja.ROJO : EstiloUI.TEXTO);
        lblPorVencer.setText("Gs. " + UiCaja.monto(porVencer));
        lblProximo.setText(proximo == null ? "-" : fecha.format(proximo));
        lblProximo.setToolTipText(proximo == null ? null : "Cuota de Gs. " + UiCaja.monto(proximoMonto));

        Color fondo;
        if (cierreVigente) {
            fondo = new Color(238, 240, 243);
            lblBanner.setText(nombre + "  ·  CUENTA CERRADA");
            lblBanner2.setText("Cerrada el " + c.cierre.fecha + (c.cierre.usuario.length() > 0 ? " por " + c.cierre.usuario : "")
                    + (c.cierre.condonado > 0 ? "  ·  Saldo condonado Gs. " + UiCaja.monto(c.cierre.condonado) : "  ·  Sin saldo pendiente")
                    + (c.cierre.motivo.length() > 0 ? "  ·  " + c.cierre.motivo : ""));
        } else if (deuda <= 0) {
            fondo = new Color(231, 245, 236);
            lblBanner.setText(nombre + "  ·  AL DÍA");
            lblBanner2.setText("No se le debe nada." + (deuda < 0 ? " Pagado de más: Gs. " + UiCaja.monto(-deuda) : ""));
        } else if (nVencidas > 0) {
            fondo = new Color(251, 232, 229);
            lblBanner.setText(nombre + "  ·  " + nVencidas + (nVencidas == 1 ? " CUOTA VENCIDA" : " CUOTAS VENCIDAS"));
            lblBanner2.setText("Hay Gs. " + UiCaja.monto(vencido) + " vencidos de Gs. " + UiCaja.monto(deuda) + " que se le deben.");
        } else {
            fondo = new Color(253, 243, 224);
            lblBanner.setText(nombre + "  ·  CON DEUDA");
            lblBanner2.setText("Se le debe Gs. " + UiCaja.monto(deuda) + (proximo != null ? ". Próximo vencimiento: " + fecha.format(proximo) : "."));
        }
        banner.setBackground(fondo);

        // cuotas
        String[] titulos = {"Compra", "Cuota", "Vence", "Monto", "Pagado", "Saldo", "Estado"};
        DefaultTableModel mc = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };
        for (CuotasDb.Cuota q : cuotas) {
            String e = q.estado();
            mc.addRow(new Object[]{"Nº " + q.ventaCod, q.nro + " de " + q.nTotal, fecha.format(q.vencimiento), UiCaja.monto(q.monto),
                UiCaja.monto(q.pagado), UiCaja.monto(q.saldo()), e + (q.diasAtraso() > 0 ? " · " + q.diasAtraso() + " días" : "")});
        }
        tablaCuotas.setModel(mc);
        EstiloUI.derecha(tablaCuotas, 3, 4, 5);
        int[] anchos = {110, 90, 120, 130, 130, 130, 170};
        for (int i = 0; i < anchos.length; i++) {
            tablaCuotas.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        tablaCuotas.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(EstiloUI.fuente(Font.BOLD, 13));
                String v = String.valueOf(value);
                setForeground(v.startsWith("Vencida") ? UiCaja.ROJO : (v.startsWith("Pagada") ? UiCaja.VERDE : (v.startsWith("Condonada") ? EstiloUI.MUTED : UiCaja.NARANJA)));
                if (!isSelected) {
                    setBackground(v.startsWith("Vencida") ? new Color(253, 236, 234) : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252)));
                }
                return this;
            }
        });
        long sinPlan = Math.max(0, deuda) - enCuotas;
        lblSinPlan.setText(cuotas.isEmpty() ? "Este proveedor no tiene compras en cuotas." + (deuda > 0 ? " La deuda corresponde a compras a crédito sin plan (ver Movimientos)." : "")
                : (sinPlan > 0 ? "Deuda que no está en cuotas (compras a crédito sin plan): Gs. " + UiCaja.monto(sinPlan) : " "));

        // movimientos
        String[] tm = {"Fecha", "Descripción", "Compra a crédito", "Pago", "Deuda"};
        DefaultTableModel mm = new DefaultTableModel(null, tm) {
            @Override
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };
        for (CuentaProvDb.Movimiento m : c.movs) {
            mm.addRow(new Object[]{m.fecha, m.descripcion, m.deuda == 0 ? "" : UiCaja.monto(m.deuda), m.pago == 0 ? "" : UiCaja.monto(m.pago),
                UiCaja.monto(m.saldoAcumulado)});
        }
        tablaMov.setModel(mm);
        EstiloUI.derecha(tablaMov, 2, 3, 4);
        int[] am = {110, 480, 140, 120, 130};
        for (int i = 0; i < am.length; i++) {
            tablaMov.getColumnModel().getColumn(i).setPreferredWidth(am[i]);
        }

        // pagos
        pagos = c.pagos;
        DefaultTableModel mp = new DefaultTableModel(null, new String[]{"Pago", "Fecha", "Monto", "Sale de", "Cuenta", "Descripción", "Turno"}) {
            @Override
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };
        for (CuentaProvDb.Pago p : pagos) {
            mp.addRow(new Object[]{"Nº " + p.id, p.fecha, UiCaja.monto(p.monto), p.origen, p.cuenta, (p.esEntrega() ? "ENTREGA COMPRA Nº " + p.compraCod + (p.descripcion.length() > 0 ? " - " : "") : "") + p.descripcion,
                p.conTurno ? "T" + p.turnoId + (p.turnoCerrado() ? " (cerrado)" : "") : "-"});
        }
        tablaPagos.setModel(mp);
        EstiloUI.derecha(tablaPagos, 2);
        int[] ap = {80, 110, 120, 120, 150, 330, 110};
        for (int i = 0; i < ap.length; i++) {
            tablaPagos.getColumnModel().getColumn(i).setPreferredWidth(ap[i]);
        }
        lblPagos.setText(pagos.isEmpty() ? "No hay pagos a este proveedor." : "Elija un pago para modificarlo o anularlo.");

        btnCerrarCuenta.setEnabled(!cierreVigente && (deuda != 0 || !cuotas.isEmpty()));
        btnReabrir.setEnabled(cierreVigente);
        banner.repaint();
    }

    // ------------------------------------------------------------------ acciones

    /** Pago al proveedor desde la caja o el banco; se aplica a las cuotas más antiguas. */
    private void cobrar() {
        if (PagoProveedor.mostrar(this, proveedorId, nombre, deuda, cuotas, usuarioId)) {
            recargar();
        }
    }

    /** Pago elegido en la tabla, o null (avisa) si no se puede tocar: entrega de venta, cuenta cerrada o turno cerrado sin ser administrador. */
    private CuentaProvDb.Pago pagoEditable(String accion) {
        int fila = tablaPagos.getSelectedRow();
        if (fila < 0 || fila >= pagos.size()) {
            JOptionPane.showMessageDialog(this, "Elija primero un pago de la lista.", accion, JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        CuentaProvDb.Pago p = pagos.get(fila);
        if (p.esEntrega()) {
            JOptionPane.showMessageDialog(this, "Este pago es la entrega que se hizo al comprar a crédito y forma parte del plan de cuotas.\n"
                    + "Si hay que corregirlo, elimine la compra y cárguela de nuevo.", accion, JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (cierreVigente) {
            JOptionPane.showMessageDialog(this, "La cuenta de este proveedor está cerrada; reábrala para cambiar sus pagos.", accion, JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (p.turnoCerrado()) {
            if (!admin) {
                JOptionPane.showMessageDialog(this, "Este pago es del turno " + p.turnoId + ", que ya está cerrado.\nSolo el administrador puede cambiarlo.", accion,
                        JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            if (JOptionPane.showConfirmDialog(this, "Este pago es del turno " + p.turnoId + ", que ya está cerrado.\nSi lo cambia, el cierre de ese turno ya no va a coincidir con la caja.\n\n¿Continuar?",
                    accion, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                return null;
            }
        }
        return p;
    }

    private void modificarPago() {
        CuentaProvDb.Pago p = pagoEditable("Modificar pago");
        if (p != null && PagoProveedor.modificar(this, p, proveedorId, nombre, deuda, usuarioId)) {
            recargar();
        }
    }

    private void anularPago() {
        CuentaProvDb.Pago p = pagoEditable("Anular pago");
        if (p == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Anular el pago Nº " + p.id + " de Gs. " + UiCaja.monto(p.monto) + " (" + p.medio() + ")?\n\n"
                + "La deuda con el proveedor vuelve a subir y las cuotas que ese pago había cubierto quedan pendientes."
                + (CuentaProvDb.CAJA.equals(p.origen) ? "\nSe borra también el retiro del turno." : "\nSe devuelve el importe a la cuenta del banco."), "Anular pago",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            CuentaProvDb.anularPago(p.id);
            JOptionPane.showMessageDialog(this, "Pago anulado.", "Anular pago", JOptionPane.INFORMATION_MESSAGE);
            recargar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo anular el pago:\n" + e.getMessage() + CuentaProvDb.ayuda(), "Anular pago", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarCuenta() {
        String texto = deuda > 0
                ? "Todavía se le debe al proveedor Gs. " + UiCaja.monto(deuda) + ".\nAl cerrar la cuenta ese saldo se CONDONA: queda registrado en el extracto\ny las cuotas pendientes pasan a \"Condonada\"."
                : "No se le debe nada al proveedor. Se registra el cierre de la cuenta.";
        JTextFieldMotivo motivo = new JTextFieldMotivo();
        Object[] contenido = {texto, Box.createVerticalStrut(8), "Motivo" + (deuda > 0 ? " (obligatorio)" : " (opcional)") + ":", motivo};
        int op = JOptionPane.showConfirmDialog(this, contenido, "Cerrar cuenta de " + nombre, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op != JOptionPane.OK_OPTION) {
            return;
        }
        if (deuda > 0 && motivo.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Escriba el motivo para condonar el saldo.", "Cerrar cuenta", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            long condonado = CuentaProvDb.cerrarCuenta(proveedorId, usuarioId, motivo.getText());
            JOptionPane.showMessageDialog(this, condonado > 0 ? "Cuenta cerrada. Se condonó Gs. " + UiCaja.monto(condonado) + "." : "Cuenta cerrada.",
                    "Cerrar cuenta", JOptionPane.INFORMATION_MESSAGE);
            recargar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cerrar la cuenta:\n" + e.getMessage() + CuentaProvDb.ayuda(), "Cerrar cuenta", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reabrirCuenta() {
        if (JOptionPane.showConfirmDialog(this, "¿Reabrir la cuenta de " + nombre + "?\n\nSe deshace el último cierre: el saldo condonado vuelve a ser deuda y las cuotas vuelven a quedar pendientes.",
                "Reabrir cuenta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            CuentaProvDb.reabrirCuenta(proveedorId);
            recargar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo reabrir la cuenta:\n" + e.getMessage() + CuentaProvDb.ayuda(), "Reabrir cuenta", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class JTextFieldMotivo extends javax.swing.JTextField {

        JTextFieldMotivo() {
            super(30);
            EstiloUI.campo(this);
        }
    }

    /** Tarjeta de resumen con una franja de color a la izquierda. */
    private static JPanel tarjeta(String texto, JLabel valor, final Color acento) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth() - 1, h = getHeight() - 1;
                g2.setColor(acento);
                g2.fillRoundRect(0, 0, w, h, 14, 14);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(5, 0, w - 5, h, 14, 14);
                g2.fillRect(5, 0, 14, h);
                g2.setColor(EstiloUI.BORDE);
                g2.drawRoundRect(0, 0, w, h, 14, 14);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 10));
        JLabel t = new JLabel(texto);
        t.setFont(EstiloUI.fuente(Font.BOLD, 11));
        t.setForeground(EstiloUI.MUTED);
        valor.setFont(EstiloUI.fuente(Font.BOLD, 20));
        valor.setForeground(EstiloUI.TEXTO);
        p.add(t);
        p.add(Box.createVerticalGlue());
        p.add(valor);
        return p;
    }
}

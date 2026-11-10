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
 * Cuenta corriente de un cliente: deuda, cronograma de cuotas (vencidas, por vencer, pagadas), movimientos del
 * extracto, cobro, extracto y pagaré en PDF y cierre de cuenta (solo administrador).
 */
final class CuentaCliente extends JDialog {

    private final String clienteId;
    private final int usuarioId;
    private final boolean admin;
    private final SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
    private final JTable tablaCuotas = new JTable();
    private final JTable tablaMov = new JTable();
    private final JTable tablaPagos = new JTable();
    private final JButton btnModPago = new JButton("Modificar pago");
    private final JButton btnAnuPago = new JButton("Anular pago");
    private final JLabel lblPagos = new JLabel(" ");
    private List<CuotasDb.Pago> pagos = new ArrayList<CuotasDb.Pago>();
    private final JLabel lblDeuda = new JLabel("0");
    private final JLabel lblVencido = new JLabel("0");
    private final JLabel lblPorVencer = new JLabel("0");
    private final JLabel lblProximo = new JLabel("-");
    private final JLabel lblBanner = new JLabel(" ");
    private final JLabel lblBanner2 = new JLabel(" ");
    private final JLabel lblSinPlan = new JLabel(" ");
    private final JPanel banner = new JPanel(new BorderLayout());
    private final JButton btnCobrar = new JButton("Cobrar");
    private final JButton btnPagare = new JButton("Pagaré");
    private final JButton btnExtracto = new JButton("Extracto PDF");
    private final JButton btnCerrarCuenta = new JButton("Cerrar cuenta");
    private List<CuotasDb.Cuota> cuotas = new ArrayList<CuotasDb.Cuota>();
    private long deuda;
    private boolean cierreVigente;
    private String nombre = "";

    private CuentaCliente(java.awt.Window dueno, String clienteId, int usuarioId) {
        super(dueno, "Cuenta del cliente", ModalityType.APPLICATION_MODAL);
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.admin = TurnoCaja.esAdmin(usuarioId);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Cuenta del cliente", "Deuda, cuotas y movimientos"), BorderLayout.NORTH);

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
        kpis.add(tarjeta("DEUDA TOTAL", lblDeuda, UiCaja.NARANJA));
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
        EstiloUI.botonSecundario(btnPagare);
        btnPagare.setPreferredSize(new Dimension(120, 40));
        EstiloUI.botonSecundario(btnExtracto);
        btnExtracto.setPreferredSize(new Dimension(140, 40));
        EstiloUI.botonPeligro(btnCerrarCuenta);
        btnCerrarCuenta.setPreferredSize(new Dimension(150, 40));
        JButton salir = new JButton("Salir (Esc)");
        EstiloUI.botonSecundario(salir);
        salir.setPreferredSize(new Dimension(120, 40));
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 22, 16, 22));
        JPanel izq = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);
        izq.add(btnCobrar);
        izq.add(btnPagare);
        izq.add(btnExtracto);
        JPanel der = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);
        if (admin) {
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
        btnPagare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pagare();
            }
        });
        btnExtracto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ReportesCuenta.extracto(CuentaCliente.this, CuentaCliente.this.clienteId);
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

    static void mostrar(java.awt.Component padre, String clienteId, int usuarioId) {
        try {
            CuotasDb.asegurarEsquema();
            new CuentaCliente(UiCaja.ventana(padre), clienteId, usuarioId).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo abrir la cuenta del cliente:\n" + e.getMessage() + CuotasDb.ayuda(),
                    "Cuenta del cliente", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------ datos

    private static final class Carga {
        String[] cliente;
        long deuda;
        List<CuotasDb.Cuota> cuotas;
        List<CuotasDb.Movimiento> movs;
        List<CuotasDb.Pago> pagos;
        CuotasDb.Cierre cierre;
    }

    private void recargar() {
        lblBanner.setText("Cargando...");
        new SwingWorker<Carga, Void>() {
            @Override
            protected Carga doInBackground() throws Exception {
                Carga c = new Carga();
                c.cliente = CuotasDb.cliente(clienteId);
                c.deuda = CuotasDb.deuda(clienteId);
                c.cuotas = CuotasDb.cuotas(clienteId);
                c.movs = CuotasDb.movimientos(clienteId);
                c.pagos = CuotasDb.pagos(clienteId);
                c.cierre = CuotasDb.cierreVigente(clienteId);
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
                    JOptionPane.showMessageDialog(CuentaCliente.this, "No se pudo leer la cuenta del cliente:\n" + t.getMessage() + CuotasDb.ayuda(),
                            "Cuenta del cliente", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void aplicar(Carga c) {
        nombre = c.cliente[0];
        deuda = c.deuda;
        cuotas = c.cuotas;
        cierreVigente = c.cierre != null;
        setTitle("Cuenta de " + nombre);

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
            lblBanner2.setText("No tiene deuda pendiente." + (deuda < 0 ? " Saldo a favor: Gs. " + UiCaja.monto(-deuda) : ""));
        } else if (nVencidas > 0) {
            fondo = new Color(251, 232, 229);
            lblBanner.setText(nombre + "  ·  " + nVencidas + (nVencidas == 1 ? " CUOTA VENCIDA" : " CUOTAS VENCIDAS"));
            lblBanner2.setText("Tiene Gs. " + UiCaja.monto(vencido) + " vencidos de Gs. " + UiCaja.monto(deuda) + " de deuda.");
        } else {
            fondo = new Color(253, 243, 224);
            lblBanner.setText(nombre + "  ·  CON DEUDA");
            lblBanner2.setText("Debe Gs. " + UiCaja.monto(deuda) + (proximo != null ? ". Próximo vencimiento: " + fecha.format(proximo) : "."));
        }
        banner.setBackground(fondo);

        // cuotas
        String[] titulos = {"Venta", "Cuota", "Vence", "Monto", "Pagado", "Saldo", "Estado"};
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
        lblSinPlan.setText(cuotas.isEmpty() ? "Este cliente no tiene ventas en cuotas." + (deuda > 0 ? " Su deuda corresponde a ventas a crédito comunes (ver Movimientos)." : "")
                : (sinPlan > 0 ? "Deuda que no está en cuotas (ventas a crédito comunes): Gs. " + UiCaja.monto(sinPlan) : " "));

        // movimientos
        String[] tm = {"Cód.", "Fecha", "Descripción", "Compra a crédito", "Pago", "Saldo"};
        DefaultTableModel mm = new DefaultTableModel(null, tm) {
            @Override
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };
        for (CuotasDb.Movimiento m : c.movs) {
            mm.addRow(new Object[]{m.id, m.fecha, m.descripcion, m.deuda == 0 ? "" : UiCaja.monto(m.deuda), m.pago == 0 ? "" : UiCaja.monto(m.pago),
                UiCaja.monto(m.saldoAcumulado)});
        }
        tablaMov.setModel(mm);
        EstiloUI.derecha(tablaMov, 3, 4, 5);
        int[] am = {60, 110, 420, 140, 120, 130};
        for (int i = 0; i < am.length; i++) {
            tablaMov.getColumnModel().getColumn(i).setPreferredWidth(am[i]);
        }

        // pagos
        pagos = c.pagos;
        DefaultTableModel mp = new DefaultTableModel(null, new String[]{"Pago", "Fecha", "Monto", "Medio", "Cuenta", "Descripción", "Turno"}) {
            @Override
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };
        for (CuotasDb.Pago p : pagos) {
            mp.addRow(new Object[]{"Nº " + p.id, p.fecha, UiCaja.monto(p.monto), p.medio, p.cuenta, p.descripcion,
                p.conTurno ? "T" + p.turnoId + (p.turnoCerrado() ? " (cerrado)" : "") : "-"});
        }
        tablaPagos.setModel(mp);
        EstiloUI.derecha(tablaPagos, 2);
        int[] ap = {80, 110, 120, 120, 150, 330, 110};
        for (int i = 0; i < ap.length; i++) {
            tablaPagos.getColumnModel().getColumn(i).setPreferredWidth(ap[i]);
        }
        lblPagos.setText(pagos.isEmpty() ? "Este cliente no tiene pagos registrados." : "Elija un pago para modificarlo o anularlo.");

        btnCerrarCuenta.setEnabled(!cierreVigente && (deuda != 0 || !cuotas.isEmpty()));
        btnPagare.setEnabled(!cuotas.isEmpty());
        banner.repaint();
    }

    // ------------------------------------------------------------------ acciones

    /** Cobro moderno: guarda el pago (medio, caja, banco) y el extracto, y lo aplica a las cuotas más antiguas. */
    private void cobrar() {
        if (CobroCuenta.mostrar(this, clienteId, nombre, deuda, cuotas, usuarioId)) {
            recargar();
        }
    }

    /** Pago elegido en la tabla, o null (avisa) si no se puede tocar: entrega de venta, cuenta cerrada o turno cerrado sin ser administrador. */
    private CuotasDb.Pago pagoEditable(String accion) {
        int fila = tablaPagos.getSelectedRow();
        if (fila < 0 || fila >= pagos.size()) {
            JOptionPane.showMessageDialog(this, "Elija primero un pago de la lista.", accion, JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        CuotasDb.Pago p = pagos.get(fila);
        if (p.esEntrega()) {
            JOptionPane.showMessageDialog(this, "Este pago es la entrega que se hizo al vender a crédito y forma parte del plan de cuotas.\n"
                    + "Si hay que corregirlo, edite o elimine la venta.", accion, JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (cierreVigente) {
            JOptionPane.showMessageDialog(this, "La cuenta de este cliente está cerrada; no se pueden cambiar sus pagos.", accion, JOptionPane.INFORMATION_MESSAGE);
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
        CuotasDb.Pago p = pagoEditable("Modificar pago");
        if (p != null && CobroCuenta.modificar(this, p, clienteId, nombre, deuda, usuarioId)) {
            recargar();
        }
    }

    private void anularPago() {
        CuotasDb.Pago p = pagoEditable("Anular pago");
        if (p == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Anular el pago Nº " + p.id + " de Gs. " + UiCaja.monto(p.monto) + " (" + p.medio + ")?\n\n"
                + "La deuda del cliente vuelve a subir y las cuotas que ese pago había cobrado quedan pendientes.", "Anular pago",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            CuotasDb.anularCobro(p.id, clienteId);
            JOptionPane.showMessageDialog(this, "Pago anulado.", "Anular pago", JOptionPane.INFORMATION_MESSAGE);
            recargar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo anular el pago:\n" + e.getMessage() + CuotasDb.ayuda(), "Anular pago", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pagare() {
        if (cuotas.isEmpty()) {
            return;
        }
        // la cuota elegida en la tabla indica el plan; si hay uno solo no hace falta elegir
        Map<Integer, String> planes = new LinkedHashMap<Integer, String>();
        for (CuotasDb.Cuota q : cuotas) {
            if (!planes.containsKey(Integer.valueOf(q.planId))) {
                planes.put(Integer.valueOf(q.planId), "Venta Nº " + q.ventaCod + " (" + q.nTotal + " cuotas)");
            }
        }
        int plan;
        int fila = tablaCuotas.getSelectedRow();
        if (fila >= 0 && fila < cuotas.size()) {
            plan = cuotas.get(fila).planId;
        } else if (planes.size() == 1) {
            plan = planes.keySet().iterator().next().intValue();
        } else {
            Object[] opciones = planes.values().toArray();
            Object elegido = JOptionPane.showInputDialog(this, "¿De qué venta?", "Pagaré", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (elegido == null) {
                return;
            }
            plan = -1;
            for (Map.Entry<Integer, String> e : planes.entrySet()) {
                if (e.getValue().equals(elegido)) {
                    plan = e.getKey().intValue();
                }
            }
        }
        ReportesCuenta.pagare(this, plan);
    }

    private void cerrarCuenta() {
        String texto = deuda > 0
                ? "El cliente todavía debe Gs. " + UiCaja.monto(deuda) + ".\nAl cerrar la cuenta ese saldo se CONDONA: queda registrado en el extracto\ny las cuotas pendientes pasan a \"Condonada\"."
                : "El cliente no tiene deuda pendiente. Se registra el cierre de la cuenta.";
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
            long condonado = CuotasDb.cerrarCuenta(clienteId, usuarioId, motivo.getText());
            JOptionPane.showMessageDialog(this, condonado > 0 ? "Cuenta cerrada. Se condonó Gs. " + UiCaja.monto(condonado) + "." : "Cuenta cerrada.",
                    "Cerrar cuenta", JOptionPane.INFORMATION_MESSAGE);
            recargar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cerrar la cuenta:\n" + e.getMessage() + CuotasDb.ayuda(), "Cerrar cuenta", JOptionPane.ERROR_MESSAGE);
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

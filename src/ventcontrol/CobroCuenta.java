package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Cobro a un cliente desde su cuenta: monto (con atajos para la próxima cuota, lo vencido o toda la deuda), medio de pago,
 * cuenta bancaria si no es efectivo y descripción. Muestra cómo se aplica a las cuotas antes de guardar.
 */
final class CobroCuenta extends JDialog {

    private final String clienteId, nombre;
    private final int usuarioId;
    private final long deuda;
    private final TurnoCaja.Turno turno;
    private final List<CuotasDb.Cuota> pendientes = new ArrayList<CuotasDb.Cuota>();
    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private final JTextField campoMonto = UiCaja.campoMonto();
    private final JTextField txtDescripcion = new JTextField();
    private final SelectorMedio selector = new SelectorMedio();
    private final JLabel lblMedio = UiCaja.caption("MEDIO DE PAGO  ·  F1 a F4");
    private final JLabel lblCuenta = UiCaja.caption("CUENTA EN LA QUE ENTRA EL COBRO");
    private final JComboBox comboCuenta = new JComboBox();
    private final JLabel lblNota = new JLabel(" ");
    private final JLabel lblResto = new JLabel("Gs. 0");
    private final JLabel lblAplica = new JLabel(" ");
    private final com.toedter.calendar.JDateChooser calendar = new com.toedter.calendar.JDateChooser();
    private List<BancoDb.Cuenta> cuentas;
    private boolean guardado;
    /** Pago que se está modificando (null cuando es un cobro nuevo). */
    private final CuotasDb.Pago editar;

    private CobroCuenta(java.awt.Window dueno, String clienteId, String nombre, long deuda, List<CuotasDb.Cuota> cuotas,
            TurnoCaja.Turno turno, int usuarioId, CuotasDb.Pago editar) {
        super(dueno, editar == null ? "Cobrar" : "Modificar pago", ModalityType.APPLICATION_MODAL);
        this.editar = editar;
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.deuda = deuda;
        this.turno = turno;
        this.usuarioId = usuarioId;
        for (CuotasDb.Cuota c : cuotas) {
            if (c.saldo() > 0) {
                pendientes.add(c);
            }
        }
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera(editar == null ? "Cobrar" : "Modificar pago Nº " + editar.id,
                nombre + (turno == null ? "" : "  ·  Turno " + turno.id + " · " + turno.cajaNombre)), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 6, 24));
        JComponent ancho = (JComponent) Box.createRigidArea(new Dimension(592, 0));
        ancho.setAlignmentX(0f);
        cuerpo.add(ancho);

        // deuda
        JPanel grande = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(0, 0, EstiloUI.NAVY, getWidth(), getHeight(), new Color(0, 88, 134)));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        grande.setOpaque(false);
        grande.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        grande.setAlignmentX(0f);
        grande.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        JLabel t1 = new JLabel(editar == null ? "DEUDA DEL CLIENTE" : "DEUDA DEL CLIENTE SIN ESTE PAGO");
        t1.setFont(EstiloUI.fuente(Font.BOLD, 12));
        t1.setForeground(new Color(150, 200, 230));
        JLabel t2 = new JLabel("Gs. " + fmt.format(deuda));
        t2.setFont(EstiloUI.fuente(Font.BOLD, 36));
        t2.setForeground(Color.WHITE);
        grande.add(t1, BorderLayout.NORTH);
        grande.add(t2, BorderLayout.CENTER);
        cuerpo.add(grande);
        cuerpo.add(Box.createVerticalStrut(14));

        // monto y atajos
        JLabel cm = UiCaja.caption("MONTO A COBRAR (Gs.)");
        cm.setAlignmentX(0f);
        campoMonto.setFont(EstiloUI.fuente(Font.BOLD, 24));
        campoMonto.setAlignmentX(0f);
        campoMonto.setPreferredSize(new Dimension(300, 50));
        campoMonto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cuerpo.add(cm);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(campoMonto);
        cuerpo.add(Box.createVerticalStrut(8));
        JPanel atajos = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        atajos.setOpaque(false);
        atajos.setAlignmentX(0f);
        atajos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        long vencido = 0;
        for (CuotasDb.Cuota c : pendientes) {
            if ("Vencida".equals(c.estado())) {
                vencido += c.saldo();
            }
        }
        if (!pendientes.isEmpty()) {
            CuotasDb.Cuota p = pendientes.get(0);
            atajo(atajos, "Próxima cuota  " + fmt.format(Math.min(p.saldo(), deuda)), Math.min(p.saldo(), deuda));
        }
        if (vencido > 0 && vencido <= deuda) {
            atajo(atajos, "Vencido  " + fmt.format(vencido), vencido);
        }
        atajo(atajos, "Toda la deuda  " + fmt.format(deuda), deuda);
        cuerpo.add(atajos);
        cuerpo.add(Box.createVerticalStrut(12));

        // medio de pago
        lblMedio.setAlignmentX(0f);
        selector.setAlignmentX(0f);
        selector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cuerpo.add(lblMedio);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(selector);
        cuerpo.add(Box.createVerticalStrut(8));
        lblCuenta.setAlignmentX(0f);
        EstiloUI.combo(comboCuenta);
        comboCuenta.setAlignmentX(0f);
        comboCuenta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboCuenta.setPreferredSize(new Dimension(300, 40));
        lblNota.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblNota.setForeground(EstiloUI.MUTED);
        lblNota.setAlignmentX(0f);
        cuerpo.add(lblCuenta);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(comboCuenta);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(lblNota);
        cuerpo.add(Box.createVerticalStrut(10));

        // fecha y descripción
        JPanel fila = new JPanel(new java.awt.GridLayout(1, 2, 18, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(0f);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        JPanel fechaBox = new JPanel();
        fechaBox.setOpaque(false);
        fechaBox.setLayout(new BoxLayout(fechaBox, BoxLayout.Y_AXIS));
        JLabel cf = UiCaja.caption("FECHA");
        cf.setAlignmentX(0f);
        JComponent ed = calendar.getDateEditor().getUiComponent();
        if (ed instanceof JTextField) {
            EstiloUI.campo((JTextField) ed);
        }
        calendar.setBorder(null);
        EstiloUI.botonSecundario(calendar.getCalendarButton());
        calendar.setPreferredSize(new Dimension(200, 40));
        calendar.setMaximumSize(new Dimension(220, 40));
        calendar.setAlignmentX(0f);
        calendar.setDate(new Date());
        fechaBox.add(cf);
        fechaBox.add(Box.createVerticalStrut(4));
        fechaBox.add(calendar);
        JPanel descBox = new JPanel();
        descBox.setOpaque(false);
        descBox.setLayout(new BoxLayout(descBox, BoxLayout.Y_AXIS));
        JLabel cd = UiCaja.caption("DESCRIPCIÓN");
        cd.setAlignmentX(0f);
        EstiloUI.campo(txtDescripcion);
        txtDescripcion.setText(pendientes.isEmpty() ? "PAGO" : "PAGO DE CUOTA");
        txtDescripcion.setAlignmentX(0f);
        txtDescripcion.setPreferredSize(new Dimension(260, 40));
        txtDescripcion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        descBox.add(cd);
        descBox.add(Box.createVerticalStrut(4));
        descBox.add(txtDescripcion);
        fila.add(fechaBox);
        fila.add(descBox);
        cuerpo.add(fila);
        cuerpo.add(Box.createVerticalStrut(12));

        // resultado
        JPanel resumen = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(EstiloUI.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        resumen.setOpaque(false);
        resumen.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        resumen.setAlignmentX(0f);
        resumen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        JLabel cr = UiCaja.caption("DEUDA DESPUÉS DEL COBRO");
        cr.setAlignmentX(0f);
        lblResto.setFont(EstiloUI.fuente(Font.BOLD, 20));
        lblResto.setForeground(UiCaja.NARANJA);
        lblResto.setAlignmentX(0f);
        izq.add(cr);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblResto);
        lblAplica.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblAplica.setForeground(EstiloUI.MUTED);
        resumen.add(izq, BorderLayout.WEST);
        resumen.add(lblAplica, BorderLayout.CENTER);
        cuerpo.add(resumen);
        add(cuerpo, BorderLayout.CENTER);

        JButton cancelar = new JButton("Cancelar (Esc)");
        EstiloUI.botonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(140, 44));
        JButton confirmar = new JButton("Cobrar (Enter)");
        EstiloUI.botonAccion(confirmar);
        confirmar.setPreferredSize(new Dimension(180, 44));
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 20, 18, 24));
        pie.add(cancelar);
        pie.add(confirmar);
        add(pie, BorderLayout.SOUTH);

        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        confirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                guardar();
            }
        });
        getRootPane().setDefaultButton(confirmar);
        campoMonto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }
        });
        selector.alCambiar(new Runnable() {
            public void run() {
                actualizarCuentas();
            }
        });
        javax.swing.InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cancelar");
        getRootPane().getActionMap().put("cancelar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        for (int i = 0; i < TurnoCaja.MEDIOS.length; i++) {
            final int idx = i;
            im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1 + i, 0), "medio" + i);
            getRootPane().getActionMap().put("medio" + i, new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selector.elegir(idx);
                }
            });
        }
        if (editar != null) {
            // los datos actuales del pago
            confirmar.setText("Guardar cambios");
            campoMonto.setText(String.valueOf(editar.monto));
            txtDescripcion.setText(editar.descripcion);
            try {
                calendar.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(editar.fecha));
            } catch (java.text.ParseException ignorada) {
            }
            for (int i = 0; i < TurnoCaja.MEDIOS.length; i++) {
                if (TurnoCaja.MEDIOS[i].equals(editar.medio)) {
                    selector.elegir(i);
                }
            }
        } else {
            // por defecto, la próxima cuota (o toda la deuda si no tiene cuotas)
            campoMonto.setText(String.valueOf(pendientes.isEmpty() ? deuda : Math.min(pendientes.get(0).saldo(), deuda)));
        }
        actualizar();
        if (editar != null && editar.cuentaId > 0 && cuentas != null) {
            for (int i = 0; i < comboCuenta.getItemCount(); i++) {
                if (((BancoDb.Cuenta) comboCuenta.getItemAt(i)).id == editar.cuentaId) {
                    comboCuenta.setSelectedIndex(i);
                }
            }
        }
        pack();
        setResizable(false);
        setLocationRelativeTo(dueno);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        campoMonto.requestFocusInWindow();
                        campoMonto.selectAll();
                    }
                });
            }
        });
    }

    /** Botón chico que pone un monto sugerido. */
    private void atajo(JPanel panel, String texto, final long monto) {
        JButton b = new JButton(texto);
        EstiloUI.botonSecundario(b);
        b.setPreferredSize(new Dimension(texto.length() * 8 + 28, 34));
        b.setFocusable(false);
        b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                campoMonto.setText(String.valueOf(monto));
                campoMonto.requestFocusInWindow();
                campoMonto.selectAll();
            }
        });
        panel.add(b);
    }

    /** Muestra la deuda que queda y a qué cuotas se aplica el monto. */
    private void actualizar() {
        long monto = UiCaja.valor(campoMonto);
        long resto = deuda - monto;
        lblResto.setText("Gs. " + fmt.format(resto < 0 ? 0 : resto));
        lblResto.setForeground(monto > deuda ? UiCaja.ROJO : (resto == 0 ? UiCaja.VERDE : UiCaja.NARANJA));
        StringBuilder sb = new StringBuilder("<html>");
        if (monto <= 0) {
            sb.append("Escriba el monto que entrega el cliente.");
        } else if (monto > deuda) {
            sb.append("<font color='#c83e34'>El monto supera la deuda del cliente.</font>");
        } else if (editar != null) {
            sb.append("Se vuelve a aplicar a las cuotas más antiguas con el nuevo monto.");
        } else if (pendientes.isEmpty()) {
            sb.append("Se descuenta de la deuda del cliente.");
        } else {
            long queda = monto;
            int n = 0;
            sb.append("Se aplica a:<br>");
            for (CuotasDb.Cuota c : pendientes) {
                if (queda <= 0) {
                    break;
                }
                if (n == 3) {
                    sb.append("…");
                    break;
                }
                long s = c.saldo();
                String etq = "cuota " + c.nro + " de " + c.nTotal + " (venta Nº " + c.ventaCod + ")";
                if (queda >= s) {
                    sb.append("• ").append(etq).append(" completa<br>");
                } else {
                    sb.append("• ").append(fmt.format(queda)).append(" de la ").append(etq).append("<br>");
                }
                queda -= Math.min(queda, s);
                n++;
            }
            if (queda > 0 && n < 3) {
                sb.append("• ").append(fmt.format(queda)).append(" de deuda que no está en cuotas<br>");
            }
        }
        sb.append("</html>");
        lblAplica.setText(sb.toString());
        actualizarCuentas();
    }

    private void actualizarCuentas() {
        long monto = UiCaja.valor(campoMonto);
        String m = selector.medio();
        boolean banco = monto > 0 && !TurnoCaja.EFECTIVO.equals(m);
        lblCuenta.setVisible(banco);
        comboCuenta.setVisible(banco);
        lblNota.setVisible(banco);
        if (banco) {
            cuentas = BancoDb.cuentasParaCobrar(m);
            comboCuenta.removeAllItems();
            if (cuentas == null) {
                lblNota.setText("No se pudieron leer las cuentas bancarias.");
                lblNota.setForeground(UiCaja.ROJO);
            } else if (cuentas.isEmpty()) {
                lblNota.setText("No hay cuentas bancarias cargadas. El administrador las crea en Arqueo y cierre de caja > Banco.");
                lblNota.setForeground(UiCaja.ROJO);
            } else {
                for (BancoDb.Cuenta c : cuentas) {
                    comboCuenta.addItem(c);
                }
                lblNota.setText("El cobro de Gs. " + fmt.format(monto) + " se acredita en la cuenta elegida.");
                lblNota.setForeground(EstiloUI.MUTED);
            }
        }
        revalidate();
        if (isShowing()) {
            pack();
        }
        repaint();
    }

    private void guardar() {
        long monto = UiCaja.valor(campoMonto);
        if (monto <= 0) {
            JOptionPane.showMessageDialog(this, "El monto a cobrar debe ser mayor a 0.", "Cobrar", JOptionPane.WARNING_MESSAGE);
            campoMonto.requestFocusInWindow();
            return;
        }
        if (monto > deuda) {
            JOptionPane.showMessageDialog(this, "El monto supera la deuda del cliente (Gs. " + fmt.format(deuda) + ").", "Cobrar", JOptionPane.WARNING_MESSAGE);
            campoMonto.requestFocusInWindow();
            campoMonto.selectAll();
            return;
        }
        if (calendar.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Indique la fecha del cobro.", "Cobrar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String descripcion = txtDescripcion.getText().trim().toUpperCase();
        if (descripcion.length() == 0) {
            JOptionPane.showMessageDialog(this, "Escriba una descripción del cobro.", "Cobrar", JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocusInWindow();
            return;
        }
        String medio = selector.medio();
        Integer cuentaId = null;
        if (!TurnoCaja.EFECTIVO.equals(medio)) {
            BancoDb.Cuenta c = (BancoDb.Cuenta) comboCuenta.getSelectedItem();
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Elija la cuenta bancaria donde entra el cobro.\nSi no hay ninguna, el administrador la crea en Arqueo y cierre de caja > Banco.",
                        "Cobrar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cuentaId = Integer.valueOf(c.id);
            BancoDb.recordar(medio, c.id);
        }
        if (editar != null) {
            try {
                CuotasDb.modificarCobro(editar.id, clienteId, monto, calendar.getDate(), descripcion, medio, cuentaId);
                guardado = true;
                JOptionPane.showMessageDialog(this, "Pago Nº " + editar.id + " modificado.\nDeuda restante: Gs. " + fmt.format(deuda - monto), "Modificar pago",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el pago:\n" + e.getMessage() + CuotasDb.ayuda(), "Modificar pago", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        try {
            int idPago = CuotasDb.registrarCobro(clienteId, usuarioId, turno.id, turno.cajaId, monto, calendar.getDate(), descripcion, medio, cuentaId);
            guardado = true;
            JOptionPane.showMessageDialog(this, "Cobro registrado (pago Nº " + idPago + ").\nDeuda restante: Gs. " + fmt.format(deuda - monto), "Cobrar",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el cobro:\n" + e.getMessage() + CuotasDb.ayuda(), "Cobrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pide el cobro (exige un turno de caja abierto). Devuelve true si se registró. */
    static boolean mostrar(java.awt.Component padre, String clienteId, String nombre, long deuda, List<CuotasDb.Cuota> cuotas, int usuarioId) {
        if (deuda <= 0) {
            JOptionPane.showMessageDialog(padre, "El cliente no tiene deuda pendiente.", "Cobrar", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        TurnoCaja.Turno turno = TurnoCaja.requerir(padre, Integer.valueOf(usuarioId));
        if (turno == null) {
            return false;
        }
        CobroCuenta d = new CobroCuenta(UiCaja.ventana(padre), clienteId, nombre, deuda, cuotas, turno, usuarioId, null);
        d.setVisible(true);
        return d.guardado;
    }

    /** Modifica un pago existente. deudaActual es la deuda con el pago incluido; el límite del nuevo monto es esa deuda más el pago. */
    static boolean modificar(java.awt.Component padre, CuotasDb.Pago pago, String clienteId, String nombre, long deudaActual, int usuarioId) {
        CobroCuenta d = new CobroCuenta(UiCaja.ventana(padre), clienteId, nombre, deudaActual + pago.monto, new ArrayList<CuotasDb.Cuota>(), null, usuarioId, pago);
        d.setVisible(true);
        return d.guardado;
    }
}

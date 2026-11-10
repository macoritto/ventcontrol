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
 * Pago a un proveedor desde su cuenta: monto (con atajos para la próxima cuota, lo vencido o toda la deuda), origen del dinero
 * (caja o cuenta del banco) y descripción. Muestra cómo se aplica a las cuotas antes de guardar.
 */
final class PagoProveedor extends JDialog {

    private final String proveedorId, nombre;
    private final int usuarioId;
    private final long deuda;
    private final List<CuotasDb.Cuota> pendientes = new ArrayList<CuotasDb.Cuota>();
    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private final JTextField campoMonto = UiCaja.campoMonto();
    private final JTextField txtDescripcion = new JTextField();
    private final OrigenPago origen = new OrigenPago("DE DÓNDE SALE EL PAGO");
    private final JLabel lblResto = new JLabel("Gs. 0");
    private final JLabel lblAplica = new JLabel(" ");
    private final com.toedter.calendar.JDateChooser calendar = new com.toedter.calendar.JDateChooser();
    private boolean guardado;
    /** Pago que se está modificando (null cuando es un cobro nuevo). */
    private final CuentaProvDb.Pago editar;

    private PagoProveedor(java.awt.Window dueno, String proveedorId, String nombre, long deuda, List<CuotasDb.Cuota> cuotas,
            int usuarioId, CuentaProvDb.Pago editar) {
        super(dueno, editar == null ? "Pagar al proveedor" : "Modificar pago", ModalityType.APPLICATION_MODAL);
        this.editar = editar;
        this.proveedorId = proveedorId;
        this.nombre = nombre;
        this.deuda = deuda;
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
        add(UiCaja.cabecera(editar == null ? "Pagar al proveedor" : "Modificar pago Nº " + editar.id, nombre), BorderLayout.NORTH);

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
        JLabel t1 = new JLabel(editar == null ? "DEUDA CON EL PROVEEDOR" : "DEUDA CON EL PROVEEDOR SIN ESTE PAGO");
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
        JLabel cm = UiCaja.caption("MONTO A PAGAR (Gs.)");
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

        // origen del pago
        origen.setAlignmentX(0f);
        cuerpo.add(origen);
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
        JLabel cr = UiCaja.caption("DEUDA DESPUÉS DEL PAGO");
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
        JButton confirmar = new JButton("Pagar (Enter)");
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
        origen.alCambiar(new Runnable() {
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
        if (editar != null) {
            // los datos actuales del pago
            confirmar.setText("Guardar cambios");
            campoMonto.setText(String.valueOf(editar.monto));
            txtDescripcion.setText(editar.descripcion);
            try {
                calendar.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(editar.fecha));
            } catch (java.text.ParseException ignorada) {
            }
            origen.elegir(editar.origen);
            origen.fijarOrigen();
        } else {
            // por defecto, la próxima cuota (o toda la deuda si no tiene cuotas)
            campoMonto.setText(String.valueOf(pendientes.isEmpty() ? deuda : Math.min(pendientes.get(0).saldo(), deuda)));
        }
        actualizar();
        if (editar != null && editar.cuentaId > 0) {
            origen.elegirCuenta(editar.cuentaId);
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
            sb.append("Escriba el monto que se le paga al proveedor.");
        } else if (monto > deuda) {
            sb.append("<font color='#c83e34'>El monto supera la deuda con el proveedor.</font>");
        } else if (editar != null) {
            sb.append("Se vuelve a aplicar a las cuotas más antiguas con el nuevo monto.");
        } else if (pendientes.isEmpty()) {
            sb.append("Se descuenta de la deuda con el proveedor.");
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
                String etq = "cuota " + c.nro + " de " + c.nTotal + " (compra Nº " + c.ventaCod + ")";
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
    }

    private void actualizarCuentas() {
        origen.monto(UiCaja.valor(campoMonto));
        revalidate();
        if (isShowing()) {
            pack();
        }
        repaint();
    }

    private void guardar() {
        String titulo = editar == null ? "Pagar al proveedor" : "Modificar pago";
        long monto = UiCaja.valor(campoMonto);
        if (monto <= 0) {
            JOptionPane.showMessageDialog(this, "El monto a pagar debe ser mayor a 0.", titulo, JOptionPane.WARNING_MESSAGE);
            campoMonto.requestFocusInWindow();
            return;
        }
        if (monto > deuda) {
            JOptionPane.showMessageDialog(this, "El monto supera la deuda con el proveedor (Gs. " + fmt.format(deuda) + ").", titulo, JOptionPane.WARNING_MESSAGE);
            campoMonto.requestFocusInWindow();
            campoMonto.selectAll();
            return;
        }
        if (calendar.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Indique la fecha del pago.", titulo, JOptionPane.WARNING_MESSAGE);
            return;
        }
        String descripcion = txtDescripcion.getText().trim().toUpperCase();
        if (descripcion.length() == 0) {
            JOptionPane.showMessageDialog(this, "Escriba una descripción del pago.", titulo, JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocusInWindow();
            return;
        }
        if (!origen.validar(this, titulo)) {
            return;
        }
        if (editar != null) {
            try {
                CuentaProvDb.modificarPago(editar.id, proveedorId, monto, calendar.getDate(), descripcion, origen.banco() ? origen.cuenta() : null);
                origen.recordar();
                guardado = true;
                JOptionPane.showMessageDialog(this, "Pago Nº " + editar.id + " modificado.\nDeuda restante: Gs. " + fmt.format(deuda - monto), titulo, JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el pago:\n" + e.getMessage() + CuentaProvDb.ayuda(), titulo, JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        TurnoCaja.Turno turno = null;
        if (!origen.banco()) {
            turno = TurnoCaja.requerir(this, Integer.valueOf(usuarioId));
            if (turno == null) {
                return;
            }
        }
        try {
            int id = CuentaProvDb.registrarPago(proveedorId, usuarioId, origen.origen(), turno, origen.banco() ? origen.cuenta() : null, monto, calendar.getDate(),
                    descripcion, null, false);
            origen.recordar();
            guardado = true;
            JOptionPane.showMessageDialog(this, "Pago registrado (Nº " + id + ") " + (origen.banco() ? "desde el banco" : "desde la caja") + ".\nDeuda restante: Gs. " + fmt.format(deuda - monto),
                    titulo, JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el pago:\n" + e.getMessage() + CuentaProvDb.ayuda(), titulo, JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pide el pago. Devuelve true si se registró. El turno de caja solo se exige si el pago sale de la caja. */
    static boolean mostrar(java.awt.Component padre, String proveedorId, String nombre, long deuda, List<CuotasDb.Cuota> cuotas, int usuarioId) {
        if (deuda <= 0) {
            JOptionPane.showMessageDialog(padre, "No se le debe nada a este proveedor.", "Pagar al proveedor", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        PagoProveedor d = new PagoProveedor(UiCaja.ventana(padre), proveedorId, nombre, deuda, cuotas, usuarioId, null);
        d.setVisible(true);
        return d.guardado;
    }

    /** Modifica un pago existente. deudaActual es la deuda con el pago incluido; el límite del nuevo monto es esa deuda más el pago. */
    static boolean modificar(java.awt.Component padre, CuentaProvDb.Pago pago, String proveedorId, String nombre, long deudaActual, int usuarioId) {
        PagoProveedor d = new PagoProveedor(UiCaja.ventana(padre), proveedorId, nombre, deudaActual + pago.monto, new ArrayList<CuotasDb.Cuota>(), usuarioId, pago);
        d.setVisible(true);
        return d.guardado;
    }
}

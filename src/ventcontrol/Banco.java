package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * Módulo Banco (solo administrador): las cuentas bancarias con su saldo y los ingresos que entraron a cada una por
 * QR, transferencia o tarjeta. Los ingresos salen de las ventas al contado y de los cobros que guardaron la cuenta.
 */
final class Banco extends JDialog {

    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private final JTable tablaCuentas = new JTable();
    private final JTable tablaMov = new JTable();
    private final JLabel lblTitulo = new JLabel("Todas las cuentas");
    private final JLabel lblInicial = new JLabel("0");
    private final JLabel lblIngresos = new JLabel("0");
    private final JLabel lblEgresos = new JLabel("0");
    private final JLabel lblSaldo = new JLabel("0");
    private final JLabel lblPie = new JLabel(" ");
    private final JLabel lblEstado = new JLabel(" ");
    private final JButton btnNueva = new JButton("Nueva cuenta");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnActivar = new JButton("Desactivar");
    private final com.toedter.calendar.JDateChooser desde = new com.toedter.calendar.JDateChooser();
    private final com.toedter.calendar.JDateChooser hasta = new com.toedter.calendar.JDateChooser();
    private List<BancoDb.Cuenta> cuentas;
    private Map<Integer, Long> ingresos;
    private boolean cargando;

    private Banco(java.awt.Window dueno) {
        super(dueno, "Banco", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Banco", "Cuentas bancarias: lo que entra por QR, transferencia y tarjeta, y lo que sale en pagos a proveedores"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(18, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 22, 8, 22));

        // ---- izquierda: cuentas
        JPanel izq = new JPanel(new BorderLayout(0, 10));
        izq.setOpaque(false);
        izq.setPreferredSize(new Dimension(400, 100));
        izq.add(titulo("Cuentas"), BorderLayout.NORTH);
        JScrollPane spC = new JScrollPane(tablaCuentas);
        EstiloUI.tabla(tablaCuentas, spC);
        izq.add(spC, BorderLayout.CENTER);
        JPanel botones = new JPanel(new java.awt.GridLayout(1, 3, 8, 0));
        botones.setOpaque(false);
        JButton nueva = btnNueva;
        EstiloUI.botonAccion(nueva);
        EstiloUI.botonSecundario(btnEditar);
        EstiloUI.botonSecundario(btnActivar);
        nueva.setPreferredSize(new Dimension(100, 38));
        botones.add(nueva);
        botones.add(btnEditar);
        botones.add(btnActivar);
        izq.add(botones, BorderLayout.SOUTH);
        cuerpo.add(izq, BorderLayout.WEST);

        // ---- derecha: movimientos
        JPanel der = new JPanel(new BorderLayout(0, 10));
        der.setOpaque(false);
        JPanel arriba = new JPanel();
        arriba.setOpaque(false);
        arriba.setLayout(new javax.swing.BoxLayout(arriba, javax.swing.BoxLayout.Y_AXIS));
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(0f);
        lblTitulo.setFont(EstiloUI.fuente(Font.BOLD, 15));
        lblTitulo.setForeground(EstiloUI.TEXTO);
        fila.add(lblTitulo, BorderLayout.WEST);
        JPanel fechas = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        fechas.setOpaque(false);
        for (com.toedter.calendar.JDateChooser c : new com.toedter.calendar.JDateChooser[]{desde, hasta}) {
            JComponent ed = c.getDateEditor().getUiComponent();
            if (ed instanceof JTextField) {
                EstiloUI.campo((JTextField) ed);
            }
            c.setBorder(null);
            EstiloUI.botonSecundario(c.getCalendarButton());
            c.setPreferredSize(new Dimension(160, 36));
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        desde.setDate(cal.getTime());
        hasta.setDate(new Date());
        JLabel d1 = UiCaja.caption("DESDE");
        JLabel d2 = UiCaja.caption("HASTA");
        JButton actualizar = new JButton("Actualizar");
        EstiloUI.botonSecundario(actualizar);
        actualizar.setPreferredSize(new Dimension(110, 36));
        fechas.add(d1);
        fechas.add(desde);
        fechas.add(d2);
        fechas.add(hasta);
        fechas.add(actualizar);
        fila.add(fechas, BorderLayout.EAST);
        arriba.add(fila);
        arriba.add(javax.swing.Box.createVerticalStrut(10));
        JPanel kpis = new JPanel(new java.awt.GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setAlignmentX(0f);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        kpis.add(tarjeta("SALDO AL INICIO DEL PERÍODO", lblInicial, EstiloUI.NAVY_BTN));
        kpis.add(tarjeta("INGRESOS DEL PERÍODO", lblIngresos, UiCaja.VERDE));
        kpis.add(tarjeta("EGRESOS DEL PERÍODO", lblEgresos, UiCaja.ROJO));
        kpis.add(tarjeta("SALDO ACTUAL", lblSaldo, EstiloUI.AZUL));
        arriba.add(kpis);
        der.add(arriba, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);
        centro.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE));
        JScrollPane spM = new JScrollPane(tablaMov);
        EstiloUI.tabla(tablaMov, spM);
        spM.setBorder(null);
        centro.add(spM, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(new Color(249, 250, 252));
        pie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.BORDE),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        lblPie.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPie.setForeground(EstiloUI.MUTED);
        lblEstado.setFont(EstiloUI.fuente(Font.BOLD, 13));
        lblEstado.setForeground(EstiloUI.TEXTO);
        pie.add(lblPie, BorderLayout.WEST);
        pie.add(lblEstado, BorderLayout.EAST);
        centro.add(pie, BorderLayout.SOUTH);
        der.add(centro, BorderLayout.CENTER);
        cuerpo.add(der, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar");
        EstiloUI.botonSecundario(cerrar);
        cerrar.setPreferredSize(new Dimension(120, 40));
        JPanel base = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        base.setOpaque(false);
        base.setBorder(BorderFactory.createEmptyBorder(6, 18, 16, 22));
        base.add(cerrar);
        add(base, BorderLayout.SOUTH);

        cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                recargar(seleccionId());
            }
        });
        nueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                formularioCuenta(null);
            }
        });
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                formularioCuenta(seleccionada());
            }
        });
        btnActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                BancoDb.Cuenta c = seleccionada();
                if (c == null) {
                    return;
                }
                try {
                    BancoDb.activar(c.id, !c.activa);
                    recargar(c.id);
                } catch (SQLException ex) {
                    error(ex);
                }
            }
        });
        tablaCuentas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !cargando) {
                    mostrarMovimientos();
                }
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        setPreferredSize(new Dimension(1320, 640));
        pack();
        setLocationRelativeTo(dueno);
        recargar(0);
    }

    private static JLabel titulo(String t) {
        JLabel l = new JLabel(t);
        l.setFont(EstiloUI.fuente(Font.BOLD, 15));
        l.setForeground(EstiloUI.TEXTO);
        return l;
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
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 10));
        JLabel t = new JLabel(texto);
        t.setFont(EstiloUI.fuente(Font.BOLD, 11));
        t.setForeground(EstiloUI.MUTED);
        valor.setFont(EstiloUI.fuente(Font.BOLD, 20));
        valor.setForeground(EstiloUI.TEXTO);
        p.add(t);
        p.add(javax.swing.Box.createVerticalGlue());
        p.add(valor);
        return p;
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage() + BancoDb.ayuda(), "Banco", JOptionPane.ERROR_MESSAGE);
    }

    /** Cuenta elegida en la lista (null si es "Todas las cuentas"). */
    private BancoDb.Cuenta seleccionada() {
        int f = tablaCuentas.getSelectedRow();
        return f >= 1 && cuentas != null && f - 1 < cuentas.size() ? cuentas.get(f - 1) : null;
    }

    private int seleccionId() {
        BancoDb.Cuenta c = seleccionada();
        return c == null ? 0 : c.id;
    }

    private long ingresoDe(int id) {
        Long v = ingresos.get(Integer.valueOf(id));
        return v == null ? 0 : v.longValue();
    }

    /** Vuelve a leer cuentas y saldos y deja elegida la cuenta con ese id (0 = todas). */
    private void recargar(int idSeleccion) {
        try {
            cuentas = BancoDb.cuentas(false);
            ingresos = BancoDb.ingresosPorCuenta();
        } catch (SQLException e) {
            error(e);
            return;
        }
        cargando = true;
        DefaultTableModel m = new DefaultTableModel(null, new String[]{"Cuenta", "Banco", "Saldo"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        long total = 0;
        int sel = 0;
        for (int i = 0; i < cuentas.size(); i++) {
            BancoDb.Cuenta c = cuentas.get(i);
            long saldo = c.saldoInicial + ingresoDe(c.id);
            if (c.activa) {
                total += saldo;
            }
            m.addRow(new String[]{c.nombre + (c.activa ? "" : "  (inactiva)"), c.banco, fmt.format(saldo)});
            if (c.id == idSeleccion) {
                sel = i + 1;
            }
        }
        m.insertRow(0, new String[]{"Todas las cuentas", "", fmt.format(total)});
        tablaCuentas.setModel(m);
        tablaCuentas.getColumnModel().getColumn(0).setPreferredWidth(170);
        tablaCuentas.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaCuentas.getColumnModel().getColumn(2).setPreferredWidth(110);
        EstiloUI.derecha(tablaCuentas, 2);
        cargando = false;
        tablaCuentas.setRowSelectionInterval(sel, sel);
        mostrarMovimientos();
    }

    /** Muestra los ingresos de la cuenta elegida en el período (se lee en segundo plano). */
    private void mostrarMovimientos() {
        final BancoDb.Cuenta c = seleccionada();
        btnEditar.setEnabled(c != null);
        btnActivar.setEnabled(c != null);
        if (c != null) {
            btnActivar.setText(c.activa ? "Desactivar" : "Activar");
        }
        lblTitulo.setText(c == null ? "Todas las cuentas" : c.nombre);
        final Date d = desde.getDate() == null ? new Date() : desde.getDate();
        final Date h = hasta.getDate() == null ? new Date() : hasta.getDate();
        final int id = c == null ? 0 : c.id;
        // el saldo inicial del período es: saldo inicial de la(s) cuenta(s) + lo ingresado antes de la fecha "desde"
        long base = 0;
        if (c != null) {
            base = c.saldoInicial;
        } else {
            for (BancoDb.Cuenta x : cuentas) {
                if (x.activa) {
                    base += x.saldoInicial;
                }
            }
        }
        final long saldoBase = base;
        final long saldoHoy = c != null ? c.saldoInicial + ingresoDe(c.id) : totalActivas();
        lblEstado.setText("Leyendo...");
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                return new Object[]{BancoDb.movimientos(id, d, h), Long.valueOf(BancoDb.ingresosAntesDe(id, d))};
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] r = get();
                    List<BancoDb.Movimiento> movs = (List<BancoDb.Movimiento>) r[0];
                    long antes = ((Long) r[1]).longValue();
                    DefaultTableModel m = new DefaultTableModel(null, new String[]{"Fecha", "Hora", "Origen", "Medio", "Detalle", "Cajero", "Cuenta", "Importe"}) {
                        @Override
                        public boolean isCellEditable(int row, int col) {
                            return false;
                        }
                    };
                    long suma = 0, egresos = 0;
                    for (BancoDb.Movimiento x : movs) {
                        if (x.monto < 0) {
                            egresos -= x.monto;
                        } else {
                            suma += x.monto;
                        }
                        m.addRow(new String[]{x.fecha, x.hora, x.origen, x.medio, x.detalle, x.cajero, x.cuenta, fmt.format(x.monto)});
                    }
                    tablaMov.setModel(m);
                    int[] anchos = {100, 60, 115, 110, 135, 80, 130, 110};
                    for (int i = 0; i < anchos.length; i++) {
                        tablaMov.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
                    }
                    EstiloUI.derecha(tablaMov, 7);
                    lblInicial.setText(fmt.format(saldoBase + antes));
                    lblIngresos.setText(fmt.format(suma));
                    lblEgresos.setText(fmt.format(egresos));
                    lblSaldo.setText(fmt.format(saldoHoy));
                    lblPie.setText(movs.isEmpty() ? "Sin movimientos en el período" : movs.size() + (movs.size() == 1 ? " movimiento" : " movimientos"));
                    lblEstado.setText(movs.isEmpty() ? " " : "Neto del período " + fmt.format(suma - egresos));
                } catch (Exception ex) {
                    Throwable t = ex.getCause() == null ? ex : ex.getCause();
                    lblEstado.setText("No se pudieron leer los movimientos");
                    lblPie.setText(String.valueOf(t.getMessage()).replace('\n', ' '));
                }
            }
        }.execute();
    }

    private long totalActivas() {
        long t = 0;
        for (BancoDb.Cuenta x : cuentas) {
            if (x.activa) {
                t += x.saldoInicial + ingresoDe(x.id);
            }
        }
        return t;
    }

    /** Alta (c == null) o edición de una cuenta. */
    private void formularioCuenta(BancoDb.Cuenta c) {
        JTextField nombre = new JTextField(c == null ? "" : c.nombre, 22);
        JTextField banco = new JTextField(c == null ? "" : c.banco, 22);
        JTextField numero = new JTextField(c == null ? "" : c.numero, 22);
        JTextField inicial = UiCaja.campoMonto();
        inicial.setFont(EstiloUI.fuente(Font.PLAIN, 14));
        inicial.setText(c == null ? "" : fmt.format(c.saldoInicial));
        JPanel p = new JPanel(new java.awt.GridLayout(0, 1, 0, 4));
        p.add(UiCaja.caption("NOMBRE DE LA CUENTA (ej. Itaú Cuenta Corriente)"));
        p.add(nombre);
        p.add(UiCaja.caption("BANCO"));
        p.add(banco);
        p.add(UiCaja.caption("NÚMERO DE CUENTA (opcional)"));
        p.add(numero);
        p.add(UiCaja.caption("SALDO INICIAL (Gs.) · lo que ya tenía la cuenta antes de usar el sistema"));
        p.add(inicial);
        int r = JOptionPane.showConfirmDialog(this, p, c == null ? "Nueva cuenta" : "Editar cuenta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            if (c == null) {
                BancoDb.Cuenta nueva = BancoDb.crear(nombre.getText(), banco.getText(), numero.getText(), UiCaja.valor(inicial));
                recargar(nueva.id);
            } else {
                BancoDb.editar(c.id, nombre.getText(), banco.getText(), numero.getText(), UiCaja.valor(inicial));
                recargar(c.id);
            }
        } catch (SQLException e) {
            error(e);
        }
    }

    static void mostrar(java.awt.Component padre) {
        try {
            BancoDb.asegurarEsquema();
            new Banco(UiCaja.ventana(padre)).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo abrir el módulo Banco:\n" + e.getMessage() + BancoDb.ayuda(),
                    "Banco", JOptionPane.ERROR_MESSAGE);
        }
    }
}

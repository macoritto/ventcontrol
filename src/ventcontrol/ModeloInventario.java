package ventcontrol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Tabla del inventario con conteo físico. Las primeras columnas son la conciliación de movimientos
 * (stock inicial + compras - ventas + devoluciones +/- reajustes = stock teórico) y las últimas el
 * conteo: se escribe lo que se contó en "Contado" y la tabla calcula la diferencia contra el stock
 * del sistema y su valor a costo.
 */
final class ModeloInventario extends DefaultTableModel {

    static final String[] TITULOS = {"Cod", "Nombre", "Stock Ini", "Comprado", "Vendido", "Devuelto", "Reajuste Mas",
        "Reajuste Menos", "Stock Actual", "Stock Teórico", "Diferencia", "Contado", "Dif. Conteo", "Costo", "Valor Dif."};
    static final int COL_ACTUAL = 8, COL_TEORICO = 9, COL_DIF_CONC = 10, COL_CONTADO = 11, COL_DIF = 12, COL_COSTO = 13,
            COL_VALOR = 14;
    static final int[] ANCHOS = {50, 230, 55, 60, 60, 60, 55, 60, 70, 75, 65, 70, 75, 70, 95};

    ModeloInventario() {
        super(null, TITULOS);
    }

    @Override
    public boolean isCellEditable(int fila, int col) {
        return col == COL_CONTADO;
    }

    /** "2,5" y "2.5" son 2,5; "1.234,5" es 1234,5. Lanza NumberFormatException si no es un número. */
    static BigDecimal leer(String s) {
        s = s.trim().replace(" ", "");
        if (s.indexOf(',') >= 0) {
            s = s.replace(".", "").replace(',', '.');
        }
        return new BigDecimal(s);
    }

    static BigDecimal num(Object o) {
        if (o == null || o.toString().trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(o.toString().trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    static String plano(BigDecimal b) {
        return b.signum() == 0 ? "0" : b.stripTrailingZeros().toPlainString();
    }

    @Override
    public void setValueAt(Object valor, int fila, int col) {
        if (col != COL_CONTADO) {
            super.setValueAt(valor, fila, col);
            return;
        }
        String s = valor == null ? "" : valor.toString().trim();
        if (s.isEmpty()) {
            super.setValueAt("", fila, COL_CONTADO);
            super.setValueAt("", fila, COL_DIF);
            super.setValueAt("", fila, COL_VALOR);
            return;
        }
        BigDecimal contado;
        try {
            contado = leer(s);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "\"" + s + "\" no es una cantidad válida.\nUse números, con coma o punto para los decimales.");
            return;
        }
        if (contado.signum() < 0) {
            JOptionPane.showMessageDialog(null, "La cantidad contada no puede ser negativa.");
            return;
        }
        BigDecimal dif = contado.subtract(num(getValueAt(fila, COL_ACTUAL)));
        BigDecimal importe = dif.multiply(num(getValueAt(fila, COL_COSTO))).setScale(0, RoundingMode.HALF_UP);
        super.setValueAt(plano(contado), fila, COL_CONTADO);
        super.setValueAt(plano(dif), fila, COL_DIF);
        super.setValueAt(plano(importe), fila, COL_VALOR);
    }

    /** Resumen de lo contado: cuántos productos, faltante, sobrante y diferencia neta (a costo). */
    static String resumen(TableModel m) {
        if (m == null || m.getColumnCount() <= COL_VALOR) {
            return "";
        }
        int contados = 0, conDif = 0;
        BigDecimal falta = BigDecimal.ZERO, sobra = BigDecimal.ZERO;
        for (int i = 0; i < m.getRowCount(); i++) {
            Object c = m.getValueAt(i, COL_CONTADO);
            if (c == null || c.toString().trim().isEmpty()) {
                continue;
            }
            contados++;
            BigDecimal v = num(m.getValueAt(i, COL_VALOR));
            if (num(m.getValueAt(i, COL_DIF)).signum() != 0) {
                conDif++;
            }
            if (v.signum() < 0) {
                falta = falta.add(v);
            } else {
                sobra = sobra.add(v);
            }
        }
        DecimalFormat f = new DecimalFormat("#,##0");
        return "Contados: " + contados + " de " + m.getRowCount() + "   ·   Con diferencia: " + conDif
                + "   ·   Faltante: " + f.format(falta.negate()) + " Gs   ·   Sobrante: " + f.format(sobra)
                + " Gs   ·   Neto: " + f.format(falta.add(sobra)) + " Gs";
    }

    /** Pinta las diferencias: rojo suave si falta, azul suave si sobra. Sin color si es cero o está vacío. */
    static TableCellRenderer renderDiferencia() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int fila, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foco, fila, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                BigDecimal n = num(v);
                boolean vacio = v == null || v.toString().trim().isEmpty() || n.signum() == 0;
                if (!sel) {
                    if (vacio) {
                        c.setBackground(t.getBackground());
                        c.setForeground(t.getForeground());
                    } else if (n.signum() < 0) {
                        c.setBackground(new Color(255, 224, 224));
                        c.setForeground(new Color(150, 0, 0));
                    } else {
                        c.setBackground(new Color(222, 240, 255));
                        c.setForeground(new Color(0, 70, 140));
                    }
                }
                c.setFont(c.getFont().deriveFont(vacio ? Font.PLAIN : Font.BOLD));
                return c;
            }
        };
    }

    /** La columna donde se escribe el conteo se ve como casilla para completar. */
    static TableCellRenderer renderContado() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int fila, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foco, fila, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (!sel) {
                    c.setBackground(new Color(255, 250, 205));
                    c.setForeground(Color.BLACK);
                }
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                return c;
            }
        };
    }

    /** Números alineados a la derecha. */
    static TableCellRenderer renderNumero() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.RIGHT);
        return r;
    }

    /** Aplica anchos y renderers a la tabla del inventario. */
    static void estilizar(JTable t) {
        t.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        for (int c = 0; c < ANCHOS.length && c < t.getColumnCount(); c++) {
            t.getColumnModel().getColumn(c).setPreferredWidth(ANCHOS[c]);
            if (c >= 2) {
                t.getColumnModel().getColumn(c).setCellRenderer(renderNumero());
            }
        }
        if (t.getColumnCount() > COL_VALOR) {
            t.getColumnModel().getColumn(COL_DIF_CONC).setCellRenderer(renderDiferencia());
            t.getColumnModel().getColumn(COL_DIF).setCellRenderer(renderDiferencia());
            t.getColumnModel().getColumn(COL_VALOR).setCellRenderer(renderDiferencia());
            t.getColumnModel().getColumn(COL_CONTADO).setCellRenderer(renderContado());
        }
    }

    /** Quita el componente y lo vuelve a poner al frente en otra posición (los formularios usan layout absoluto). */
    static void colocar(Container cp, Component c, int x, int y, int w, int h) {
        cp.remove(c);
        cp.add(c, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h), 0);
    }

    /** Agranda el fondo del formulario (detrás de todo) escalando su imagen. */
    static void ampliarFondo(Container cp, JLabel fondo, int ancho, int alto) {
        cp.remove(fondo);
        if (fondo.getIcon() instanceof javax.swing.ImageIcon) {
            java.awt.Image img = ((javax.swing.ImageIcon) fondo.getIcon()).getImage();
            fondo.setIcon(new javax.swing.ImageIcon(img.getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH)));
        }
        cp.add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, ancho, alto));
    }
}

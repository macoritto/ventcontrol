package ventcontrol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/** Piezas de pantalla compartidas por los diálogos de caja y turnos. */
final class UiCaja {

    static final Color VERDE = new Color(30, 132, 84);
    static final Color NARANJA = new Color(204, 112, 22);
    static final Color ROJO = new Color(200, 62, 52);
    private static final DecimalFormat FMT = new DecimalFormat("###,###");

    private UiCaja() {
    }

    static String monto(long v) {
        return FMT.format(v);
    }

    static String fechaHora(Timestamp t) {
        return t == null ? "-" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(t);
    }

    /** La ventana a la que pertenece el componente (o él mismo si ya es una ventana): dueña de los diálogos nuevos. */
    static Window ventana(Component c) {
        if (c instanceof Window) {
            return (Window) c;
        }
        return c == null ? null : SwingUtilities.getWindowAncestor(c);
    }

    /** Banda azul marino con título y subtítulo, como la del resto del sistema. */
    static JPanel cabecera(String titulo, String subtitulo) {
        JPanel head = new JPanel(new java.awt.BorderLayout());
        head.setBackground(EstiloUI.NAVY);
        head.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, EstiloUI.AZUL_VIVO),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));
        JPanel tit = new JPanel();
        tit.setOpaque(false);
        tit.setLayout(new BoxLayout(tit, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel(titulo);
        t1.setFont(EstiloUI.fuente(Font.BOLD, 20));
        t1.setForeground(Color.WHITE);
        tit.add(t1);
        if (subtitulo != null) {
            JLabel t2 = new JLabel(subtitulo);
            t2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
            t2.setForeground(new Color(160, 184, 208));
            tit.add(t2);
        }
        head.add(tit, java.awt.BorderLayout.CENTER);
        return head;
    }

    static JLabel caption(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(EstiloUI.fuente(Font.BOLD, 11));
        l.setForeground(EstiloUI.MUTED);
        return l;
    }

    /** Campo para importes: solo números, con separador de miles mientras se escribe. */
    static JTextField campoMonto() {
        JTextField f = new JTextField();
        EstiloUI.campo(f);
        f.setFont(EstiloUI.fuente(Font.BOLD, 18));
        f.setHorizontalAlignment(JTextField.RIGHT);
        ((AbstractDocument) f.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
                replace(fb, offset, 0, s, a);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int len, String s, AttributeSet a) throws BadLocationException {
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + (s == null ? "" : s) + actual.substring(offset + len);
                String digitos = nuevo.replaceAll("[^0-9]", "");
                if (digitos.length() > 12) {
                    return;
                }
                fb.replace(0, actual.length(), digitos.length() == 0 ? "" : FMT.format(Long.parseLong(digitos)), a);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int len) throws BadLocationException {
                replace(fb, offset, len, "", null);
            }
        });
        return f;
    }

    static long valor(JTextField f) {
        String d = f.getText().replaceAll("[^0-9]", "");
        return d.length() == 0 ? 0 : Long.parseLong(d);
    }
}

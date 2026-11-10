package ventcontrol;

import java.awt.Image;
import java.awt.Window;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/** Ícono de la aplicación (barra de tareas, título de las ventanas). Se carga en varios tamaños para que Windows elija el mejor. */
final class IconoApp {

    private static final int[] TAMANOS = {16, 32, 48, 64, 256};
    private static List<Image> imagenes;

    private IconoApp() {
    }

    static synchronized List<Image> imagenes() {
        if (imagenes == null) {
            List<Image> l = new ArrayList<Image>();
            for (int i = 0; i < TAMANOS.length; i++) {
                URL u = IconoApp.class.getResource("/images/icono_app_" + TAMANOS[i] + ".png");
                if (u != null) {
                    l.add(new ImageIcon(u).getImage());
                }
            }
            if (l.isEmpty()) {
                URL u = IconoApp.class.getResource("/images/ms.png");
                if (u != null) {
                    l.add(new ImageIcon(u).getImage());
                }
            }
            imagenes = l;
        }
        return imagenes;
    }

    /** Pone el ícono de la aplicación en la ventana. */
    static void aplicar(Window w) {
        List<Image> l = imagenes();
        if (w != null && !l.isEmpty()) {
            w.setIconImages(l);
        }
    }
}

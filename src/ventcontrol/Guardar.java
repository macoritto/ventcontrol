
package ventcontrol;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Rojeru San
 */
public class Guardar {
    public menu menuprincipal;
    Integer idproducto;
    public boolean guardarImagen(String idproducto) {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Guardar Código de Barras");
        FileFilter filter = new FileNameExtensionFilter("Barras Image", "PNG");

        fileChooser.addChoosableFileFilter(filter);
        int userSelection = fileChooser.showSaveDialog(new cargar_codigo(menuprincipal, true,idproducto));
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File guardarBarras = fileChooser.getSelectedFile();
            if (!guardarBarras.toString().endsWith(".png")) {
                guardarBarras = new File(fileChooser.getSelectedFile() + ".png");
            }
            try {
                ImageIO.write(cargar_codigo.imagen, "png", guardarBarras);
                return true;
            } catch (IOException ex) {
                Logger.getLogger(Guardar.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return false;
        }
        return false;
    }
}

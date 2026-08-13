/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Utilidades para seleccionar, copiar y previsualizar la imagen de un
 * producto. Las imagenes elegidas por el usuario se copian a una carpeta
 * local "img_productos" (al lado de donde corre la aplicacion, fuera del
 * jar) y en la base de datos solo se guarda el nombre del archivo copiado
 * (columna "imagen" de la tabla producto).
 *
 * @author Usuario
 */
public class ImagenProductoUtil {

    public static final String CARPETA_IMAGENES = "img_productos";

    /**
     * Abre un selector de archivos filtrado a imagenes.
     * @return el archivo elegido, o null si el usuario cancelo.
     */
    public static File elegirImagen(java.awt.Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar imagen del producto");
        chooser.setFileFilter(new FileNameExtensionFilter("Imagenes (jpg, jpeg, png, gif)", "jpg", "jpeg", "png", "gif"));
        int resultado = chooser.showOpenDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Copia el archivo elegido a la carpeta de imagenes de la aplicacion,
     * con un nombre unico basado en el codigo de producto, y devuelve el
     * nombre de archivo guardado (esto es lo que se persiste en la columna
     * "imagen").
     */
    public static String copiarImagen(File origen, String codprodu) throws IOException {
        Path carpeta = Paths.get(CARPETA_IMAGENES);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
        String nombreOriginal = origen.getName();
        String extension = "";
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto >= 0) {
            extension = nombreOriginal.substring(punto);
        }
        String nombreDestino = "producto_" + codprodu + "_" + System.currentTimeMillis() + extension;
        Path destino = carpeta.resolve(nombreDestino);
        Files.copy(origen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return nombreDestino;
    }

    /**
     * Carga una miniatura ya guardada (por nombre de archivo, dentro de la
     * carpeta de imagenes de la aplicacion) escalada al tamaño dado.
     * @return el icono escalado, o null si no hay imagen o no se encuentra.
     */
    public static ImageIcon cargarMiniatura(String nombreArchivo, int ancho, int alto) {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return null;
        }
        File archivo = Paths.get(CARPETA_IMAGENES, nombreArchivo).toFile();
        return cargarMiniaturaDesdeArchivo(archivo, ancho, alto);
    }

    /**
     * Carga una miniatura a partir de un archivo cualquiera (por ejemplo, el
     * que el usuario acaba de elegir, antes de copiarlo a la carpeta de la
     * aplicacion) escalada al tamaño dado.
     * @return el icono escalado, o null si el archivo no existe.
     */
    public static ImageIcon cargarMiniaturaDesdeArchivo(File archivo, int ancho, int alto) {
        if (archivo == null || !archivo.exists()) {
            return null;
        }
        ImageIcon original = new ImageIcon(archivo.getAbsolutePath());
        Image escalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }
}

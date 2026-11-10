package ventcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 * Configuración de impresión de ESTA computadora. Se guarda en un archivo del usuario de Windows
 * (%APPDATA%\ventcontrol\impresoras.properties), no en la base de datos, porque la impresora es un dato de cada equipo:
 * cada PC con la aplicación puede usar la suya.
 */
final class ConfigImpresion {

    /** Tipos de documento que se imprimen directo. */
    static final String TICKET = "ticket";
    static final String FACTURA = "factura";
    static final String PRESUPUESTO = "presupuesto";

    static final String[] TIPOS = {TICKET, FACTURA, PRESUPUESTO};

    static String nombreDe(String tipo) {
        if (FACTURA.equals(tipo)) {
            return "Factura";
        }
        if (PRESUPUESTO.equals(tipo)) {
            return "Presupuesto";
        }
        return "Ticket de venta";
    }

    /** Dónde y cómo se imprime un tipo de documento en esta PC. */
    static final class Destino {

        /** Nombre exacto de la impresora; vacío = la predeterminada de Windows. */
        String impresora = "";
        int copias = 1;
        /** true = mostrar siempre el diálogo de impresión para elegir impresora y opciones. */
        boolean preguntar = false;
    }

    private static Properties props;

    private ConfigImpresion() {
    }

    static File archivo() {
        String base = System.getenv("APPDATA");
        File dir;
        if (base != null && base.trim().length() > 0) {
            dir = new File(base, "ventcontrol");
        } else {
            dir = new File(System.getProperty("user.home", "."), ".ventcontrol");
        }
        return new File(dir, "impresoras.properties");
    }

    static String equipo() {
        try {
            String n = System.getenv("COMPUTERNAME");
            if (n == null || n.length() == 0) {
                n = java.net.InetAddress.getLocalHost().getHostName();
            }
            return n;
        } catch (Exception e) {
            return "este equipo";
        }
    }

    private static synchronized Properties cargar() {
        if (props == null) {
            Properties p = new Properties();
            File f = archivo();
            if (f.isFile()) {
                Reader r = null;
                try {
                    r = new InputStreamReader(new FileInputStream(f), "UTF-8");
                    p.load(r);
                } catch (IOException e) {
                    // archivo ilegible: se trata como sin configurar
                } finally {
                    if (r != null) {
                        try {
                            r.close();
                        } catch (IOException ignorada) {
                            // nada
                        }
                    }
                }
            }
            props = p;
        }
        return props;
    }

    /** ¿Esta PC ya tiene guardada una configuración para el tipo? */
    static synchronized boolean configurado(String tipo) {
        return cargar().getProperty(tipo + ".configurado") != null;
    }

    static synchronized Destino de(String tipo) {
        Properties p = cargar();
        Destino d = new Destino();
        String imp = p.getProperty(tipo + ".impresora");
        d.impresora = imp == null ? "" : imp.trim();
        try {
            d.copias = Math.max(1, Math.min(20, Integer.parseInt(p.getProperty(tipo + ".copias", "1").trim())));
        } catch (NumberFormatException e) {
            d.copias = 1;
        }
        d.preguntar = "true".equalsIgnoreCase(p.getProperty(tipo + ".preguntar", "false").trim());
        return d;
    }

    /** Guarda la configuración de un tipo y escribe el archivo. */
    static synchronized void guardar(String tipo, Destino d) throws IOException {
        Properties p = cargar();
        p.setProperty(tipo + ".configurado", "true");
        p.setProperty(tipo + ".impresora", d.impresora == null ? "" : d.impresora.trim());
        p.setProperty(tipo + ".copias", String.valueOf(Math.max(1, d.copias)));
        p.setProperty(tipo + ".preguntar", String.valueOf(d.preguntar));
        escribir(p);
    }

    private static void escribir(Properties p) throws IOException {
        File f = archivo();
        File dir = f.getParentFile();
        if (dir != null && !dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + dir.getPath());
        }
        Writer w = null;
        try {
            w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            p.store(w, "Impresoras de esta computadora (ventcontrol)");
        } finally {
            if (w != null) {
                w.close();
            }
        }
    }
}

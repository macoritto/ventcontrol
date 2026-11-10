package ventcontrol;

import claseConectar.conectar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

/**
 * Punto único de impresión del sistema. Decide la impresora según la configuración de ESTA computadora
 * ({@link ConfigImpresion}) y avisa con mensajes claros cuando algo falla, en lugar de imprimir en otra impresora sin decirlo.
 */
final class Impresion {

    private Impresion() {
    }

    // ------------------------------------------------------------------ impresoras instaladas

    static PrintService[] instaladas() {
        try {
            PrintService[] s = PrintServiceLookup.lookupPrintServices(null, null);
            return s == null ? new PrintService[0] : s;
        } catch (RuntimeException e) {
            return new PrintService[0];
        }
    }

    static PrintService predeterminada() {
        try {
            return PrintServiceLookup.lookupDefaultPrintService();
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** Busca una impresora instalada por su nombre exacto (sin distinguir mayúsculas). */
    static PrintService buscar(String nombre) {
        if (nombre == null || nombre.trim().length() == 0) {
            return null;
        }
        PrintService[] todas = instaladas();
        for (int i = 0; i < todas.length; i++) {
            if (todas[i].getName().trim().equalsIgnoreCase(nombre.trim())) {
                return todas[i];
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ reportes

    /**
     * Carga un reporte de /reports: compila el .jrxml incluido en la aplicación y, si no está, usa el .jasper ya compilado.
     * Evita las rutas fijas a carpetas de una computadora.
     */
    static JasperReport reporte(String nombre) throws JRException {
        InputStream xml = Impresion.class.getResourceAsStream("/reports/" + nombre + ".jrxml");
        if (xml != null) {
            try {
                return JasperCompileManager.compileReport(xml);
            } finally {
                try {
                    xml.close();
                } catch (java.io.IOException ignorada) {
                    // nada
                }
            }
        }
        InputStream jasper = Impresion.class.getResourceAsStream("/reports/" + nombre + ".jasper");
        if (jasper == null) {
            throw new JRException("No se encontró el reporte " + nombre + " dentro de la aplicación.");
        }
        try {
            return (JasperReport) JRLoader.loadObject(jasper);
        } finally {
            try {
                jasper.close();
            } catch (java.io.IOException ignorada) {
                // nada
            }
        }
    }

    // ------------------------------------------------------------------ imprimir

    /**
     * Imprime el documento según la configuración de esta PC para el tipo indicado ({@link ConfigImpresion#TICKET},
     * {@link ConfigImpresion#FACTURA} o {@link ConfigImpresion#PRESUPUESTO}).
     *
     * @return true si el trabajo se envió a la impresora
     */
    static boolean imprimir(JasperPrint jp, String tipo) {
        try {
            ConfigImpresion.Destino d = ConfigImpresion.de(tipo);
            if (d.preguntar) {
                return JasperPrintManager.printReport(jp, true);
            }
            PrintService ps = elegir(tipo, d, jp);
            if (ps == null) {
                return false;
            }
            enviar(jp, ps, d.copias);
            return true;
        } catch (JRException ex) {
            error("No se pudo imprimir el documento.", ex);
        } catch (RuntimeException ex) {
            error("No se pudo imprimir el documento.", ex);
        }
        return false;
    }

    /** Resuelve la impresora; null si el usuario canceló o ya se imprimió por otra vía. */
    private static PrintService elegir(String tipo, ConfigImpresion.Destino d, JasperPrint jp) throws JRException {
        if (instaladas().length == 0) {
            JOptionPane.showMessageDialog(ventana(), "Esta computadora no tiene ninguna impresora instalada.\n"
                    + "Instale una impresora en Windows y configúrela en Sistema → Configurar impresoras.",
                    "Imprimir", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (d.impresora.length() > 0) {
            PrintService ps = buscar(d.impresora);
            if (ps != null) {
                return ps;
            }
            return alternativa(tipo, d.impresora, jp);
        }
        // sin impresora elegida en esta PC
        if (!ConfigImpresion.configurado(tipo)) {
            PrintService migrada = migrarDeBaseDeDatos(tipo);
            if (migrada != null) {
                return migrada;
            }
        }
        PrintService pre = predeterminada();
        if (pre != null) {
            return pre;
        }
        return alternativa(tipo, "", jp);
    }

    /** La impresora configurada no está en esta PC: se pregunta qué hacer. */
    private static PrintService alternativa(String tipo, String configurada, JasperPrint jp) throws JRException {
        PrintService pre = predeterminada();
        String quien = ConfigImpresion.nombreDe(tipo);
        String msg = configurada.length() > 0
                ? "La impresora configurada para \"" + quien + "\" (" + configurada + ") no está disponible en esta computadora."
                : "No hay una impresora predeterminada en esta computadora.";
        java.util.List<String> op = new java.util.ArrayList<String>();
        if (pre != null) {
            op.add("Usar " + pre.getName());
        }
        op.add("Elegir impresora…");
        op.add("Cancelar");
        int r = JOptionPane.showOptionDialog(ventana(), msg + "\n¿Qué desea hacer?\n"
                + "(Puede cambiarla en Sistema → Configurar impresoras.)", "Imprimir", JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, op.toArray(), op.get(0));
        if (r < 0 || op.get(r).equals("Cancelar")) {
            return null;
        }
        if (op.get(r).startsWith("Usar ")) {
            return pre;
        }
        JasperPrintManager.printReport(jp, true);
        return null;
    }

    /** Envía el trabajo a la impresora, sin diálogos. */
    private static void enviar(JasperPrint jp, PrintService ps, int copias) throws JRException {
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, ps);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        if (copias > 1) {
            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
            attrs.add(new Copies(copias));
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, attrs);
        }
        exporter.exportReport();
    }

    /**
     * Primer uso tras actualizar: si la base de datos tenía una impresora guardada para este documento (configuración
     * antigua, compartida por todas las PC) y esta PC la tiene instalada, se toma como punto de partida y se guarda aquí.
     */
    private static PrintService migrarDeBaseDeDatos(String tipo) {
        if (ConfigImpresion.PRESUPUESTO.equals(tipo)) {
            return null;
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT impresora FROM reporte WHERE UPPER(TRIM(nombre::text)) = ?");
            ps.setString(1, tipo.toUpperCase());
            ResultSet rs = ps.executeQuery();
            String viejo = "";
            while (rs.next()) {
                String v = rs.getString(1);
                if (v != null && v.trim().length() > 0) {
                    viejo = v.trim();
                }
            }
            rs.close();
            ps.close();
            if (viejo.length() == 0) {
                return null;
            }
            PrintService[] todas = instaladas();
            for (int i = 0; i < todas.length; i++) {
                if (todas[i].getName().toUpperCase().contains(viejo.toUpperCase())) {
                    ConfigImpresion.Destino d = ConfigImpresion.de(tipo);
                    d.impresora = todas[i].getName();
                    try {
                        ConfigImpresion.guardar(tipo, d);
                    } catch (java.io.IOException ignorada) {
                        // se usa igual en esta impresión
                    }
                    return todas[i];
                }
            }
        } catch (Exception e) {
            // la tabla vieja puede no existir: se ignora
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (Exception ignorada) {
                    // nada
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ prueba

    /** Imprime una hoja de prueba. Devuelve null si salió bien o el texto del problema. */
    static String probar(String nombreImpresora, int copias) {
        try {
            final PrintService ps = nombreImpresora == null || nombreImpresora.length() == 0
                    ? predeterminada() : buscar(nombreImpresora);
            if (ps == null) {
                return nombreImpresora == null || nombreImpresora.length() == 0
                        ? "Esta computadora no tiene una impresora predeterminada."
                        : "La impresora \"" + nombreImpresora + "\" no está disponible en esta computadora.";
            }
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(ps);
            final String linea3 = ps.getName();
            job.setPrintable(new Printable() {
                public int print(Graphics g, PageFormat pf, int page) {
                    if (page > 0) {
                        return NO_SUCH_PAGE;
                    }
                    Graphics2D g2 = (Graphics2D) g;
                    g2.translate(pf.getImageableX(), pf.getImageableY());
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.drawString("PRUEBA DE IMPRESION", 4, 16);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g2.drawString("Equipo: " + ConfigImpresion.equipo(), 4, 32);
                    g2.drawString("Impresora: " + linea3, 4, 44);
                    g2.drawString(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 4, 56);
                    g2.drawString("Si lee esto, la impresora funciona.", 4, 72);
                    return PAGE_EXISTS;
                }
            });
            if (copias > 1) {
                job.setCopies(copias);
            }
            job.print();
            return null;
        } catch (Exception e) {
            return "No se pudo imprimir la hoja de prueba: " + (e.getMessage() == null ? e.toString() : e.getMessage());
        }
    }

    // ------------------------------------------------------------------ mensajes

    private static Component ventana() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    }

    private static void error(String titulo, Exception ex) {
        java.util.logging.Logger.getLogger(Impresion.class.getName()).log(java.util.logging.Level.SEVERE, titulo, ex);
        String detalle = ex.getMessage() == null ? ex.toString() : ex.getMessage();
        JOptionPane.showMessageDialog(ventana(), titulo + "\n" + detalle
                + "\nRevise la impresora en Sistema → Configurar impresoras.", "Imprimir", JOptionPane.ERROR_MESSAGE);
    }
}

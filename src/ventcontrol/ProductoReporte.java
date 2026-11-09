package ventcontrol;

import claseConectar.conectar;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Reporte PDF (A4 horizontal) del listado de productos, con los mismos filtros que se ven en pantalla:
 * resumen (cantidad, unidades, valor a costo y a venta, productos sin stock), detalle ordenado por nombre
 * y totales. Hay dos versiones: interna (con costos y valorización) y lista de precios (sin costos).
 */
final class ProductoReporte {

    /** Nombre de la empresa que se imprime arriba a la izquierda (vacío = no se imprime). */
    static final String EMPRESA = InventarioReporte.EMPRESA;

    private static final Color NAVY = new Color(16, 44, 72);
    private static final Color AZUL = new Color(0, 132, 190);
    private static final Color GRIS_BORDE = new Color(214, 219, 226);
    private static final Color GRIS_TEXTO = new Color(96, 106, 118);
    private static final Color ZEBRA = new Color(246, 248, 251);
    private static final Color ROJO = new Color(176, 38, 38);
    private static final Color ROJO_FONDO = new Color(253, 236, 234);
    private static final Color AMBAR = new Color(176, 108, 0);
    private static final Color AMBAR_FONDO = new Color(255, 245, 222);
    private static final Color TINTA = new Color(20, 28, 38);

    private static final DecimalFormatSymbols SIMBOLOS = new DecimalFormatSymbols();

    static {
        SIMBOLOS.setGroupingSeparator('.');
        SIMBOLOS.setDecimalSeparator(',');
    }

    private ProductoReporte() {
    }

    // ---------------------------------------------------------------------------------------
    //  Datos
    // ---------------------------------------------------------------------------------------
    static final class Fila {
        String cod, nombre, marca, tipo, lote;
        BigDecimal costo, venta, stock;
    }

    private static BigDecimal bd(String s) {
        if (s == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(s.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private static String texto(String s) {
        return s == null ? "" : s.trim();
    }

    static List<Fila> leer(String nombre, String marca, String tipo, String lote) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT p.codprodu, p.nomprodu, p.costo, p.venta, p.stock, "
                + "a.nombre AS marca, t.nombre AS tipo, d.nombre AS lote FROM producto p "
                + "LEFT JOIN marca a ON a.id_marca::text = p.marca::text "
                + "LEFT JOIN tipo t ON t.id::text = p.tipo_id::text "
                + "LEFT JOIN droga d ON d.id_droga::text = p.id_droga::text "
                + "WHERE p.codprodu::text NOT IN ('5','10000')");
        List<String> params = new ArrayList<String>();
        String[][] filtros = {{nombre, "p.nomprodu"}, {marca, "a.nombre"}, {tipo, "t.nombre"}, {lote, "d.nombre"}};
        for (String[] f : filtros) {
            if (!texto(f[0]).isEmpty()) {
                sql.append(" AND UPPER(").append(f[1]).append(") LIKE UPPER(?)");
                params.add("%" + texto(f[0]) + "%");
            }
        }
        sql.append(" ORDER BY UPPER(p.nomprodu), p.codprodu");
        List<Fila> filas = new ArrayList<Fila>();
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            PreparedStatement ps = cn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Fila f = new Fila();
                f.cod = texto(rs.getString("codprodu"));
                f.nombre = texto(rs.getString("nomprodu"));
                f.marca = texto(rs.getString("marca"));
                f.tipo = texto(rs.getString("tipo"));
                f.lote = texto(rs.getString("lote"));
                if (f.lote.equalsIgnoreCase("NINGUNA") || f.lote.equalsIgnoreCase("NINGUNO")) {
                    f.lote = "";
                }
                f.costo = bd(rs.getString("costo"));
                f.venta = bd(rs.getString("venta"));
                f.stock = bd(rs.getString("stock"));
                filas.add(f);
            }
            rs.close();
            ps.close();
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
        return filas;
    }

    static String usuario(Integer id) {
        if (id == null) {
            return "";
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT usuario FROM usuario WHERE id::text='" + id + "'");
            String u = rs.next() ? rs.getString(1) : "";
            rs.close();
            st.close();
            return u == null ? "" : u;
        } catch (SQLException e) {
            return "";
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    //  Formato
    // ---------------------------------------------------------------------------------------
    private static String gs(BigDecimal b) {
        return new DecimalFormat("#,##0", SIMBOLOS).format(b);
    }

    private static String cant(BigDecimal b) {
        return new DecimalFormat("#,##0.###", SIMBOLOS).format(b);
    }

    private static Font fuente(BaseFont bf, float tam, Color c) {
        return new Font(bf, tam, Font.NORMAL, c);
    }

    private static PdfPCell celda(String texto, Font f, int alin, Color fondo) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setHorizontalAlignment(alin);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(GRIS_BORDE);
        c.setBorderWidth(0.5f);
        c.setPaddingTop(3f);
        c.setPaddingBottom(4f);
        c.setPaddingLeft(4f);
        c.setPaddingRight(4f);
        if (fondo != null) {
            c.setBackgroundColor(fondo);
        }
        return c;
    }

    /** Pie de página con el título, la fecha de emisión y "Página x de y". */
    private static final class Pie extends PdfPageEventHelper {

        private final BaseFont bf;
        private final String izquierda;
        private PdfTemplate total;

        Pie(BaseFont bf, String izquierda) {
            this.bf = bf;
            this.izquierda = izquierda;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(40, 12);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            float y = document.bottom() - 18;
            cb.setColorStroke(GRIS_BORDE);
            cb.setLineWidth(0.5f);
            cb.moveTo(document.left(), y + 11);
            cb.lineTo(document.right(), y + 11);
            cb.stroke();
            cb.beginText();
            cb.setFontAndSize(bf, 8);
            cb.setColorFill(GRIS_TEXTO);
            cb.showTextAligned(Element.ALIGN_LEFT, izquierda, document.left(), y, 0);
            cb.showTextAligned(Element.ALIGN_RIGHT, "Página " + writer.getPageNumber() + " de ", document.right() - 18, y, 0);
            cb.endText();
            cb.addTemplate(total, document.right() - 18, y);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            total.beginText();
            total.setFontAndSize(bf, 8);
            total.setColorFill(GRIS_TEXTO);
            total.showTextAligned(Element.ALIGN_LEFT, String.valueOf(writer.getPageNumber() - 1), 0, 0, 0);
            total.endText();
        }
    }

    /** Tarjeta de resumen: título pequeño arriba y el valor grande abajo. */
    private static PdfPCell tarjeta(BaseFont bf, BaseFont bfb, String titulo, String valor, Color colorValor) {
        PdfPCell c = new PdfPCell();
        c.setBorderColor(GRIS_BORDE);
        c.setBorderWidth(0.6f);
        c.setBackgroundColor(ZEBRA);
        c.setPadding(7);
        c.setPaddingLeft(9);
        Paragraph t = new Paragraph(titulo.toUpperCase(), new Font(bfb, 7f, Font.NORMAL, GRIS_TEXTO));
        Paragraph v = new Paragraph(valor, new Font(bfb, 14f, Font.NORMAL, colorValor));
        v.setSpacingBefore(2);
        c.addElement(t);
        c.addElement(v);
        return c;
    }

    static String descripcionFiltros(String nombre, String marca, String tipo, String lote) {
        List<String> partes = new ArrayList<String>();
        if (!texto(nombre).isEmpty()) {
            partes.add("Nombre: " + texto(nombre));
        }
        if (!texto(marca).isEmpty()) {
            partes.add("Marca: " + texto(marca));
        }
        if (!texto(tipo).isEmpty()) {
            partes.add("Tipo: " + texto(tipo));
        }
        if (!texto(lote).isEmpty()) {
            partes.add("Lote: " + texto(lote));
        }
        if (partes.isEmpty()) {
            return "Todos los productos";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.size(); i++) {
            sb.append(i > 0 ? "   ·   " : "").append(partes.get(i));
        }
        return sb.toString();
    }

    /** Genera el PDF en el archivo indicado. */
    static void escribir(File destino, String nombre, String marca, String tipo, String lote, Integer usuarioId,
            boolean conCostos) throws SQLException, DocumentException, IOException {
        List<Fila> filas = leer(nombre, marca, tipo, lote);
        String responsable = usuario(usuarioId);

        BigDecimal unidades = BigDecimal.ZERO, valorCosto = BigDecimal.ZERO, valorVenta = BigDecimal.ZERO;
        int sinStock = 0, negativos = 0;
        for (Fila f : filas) {
            unidades = unidades.add(f.stock);
            valorCosto = valorCosto.add(f.costo.multiply(f.stock));
            valorVenta = valorVenta.add(f.venta.multiply(f.stock));
            if (f.stock.signum() < 0) {
                negativos++;
            } else if (f.stock.signum() == 0) {
                sinStock++;
            }
        }

        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font fNormal = fuente(bf, 8.5f, TINTA);
        Font fNeg = new Font(bfb, 8.5f, Font.NORMAL, TINTA);
        Font fMuted = fuente(bf, 8f, GRIS_TEXTO);
        Font fCab = new Font(bfb, 8f, Font.NORMAL, Color.WHITE);
        String emitido = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        String titulo = conCostos ? "REPORTE DE PRODUCTOS" : "LISTA DE PRECIOS";

        Document doc = new Document(PageSize.A4.rotate(), 28, 28, 26, 44);
        PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream(destino));
        w.setPageEvent(new Pie(bf, (conCostos ? "Reporte de productos" : "Lista de precios") + "  ·  Emitido el " + emitido
                + (responsable.isEmpty() ? "" : "  ·  " + responsable)));
        doc.addTitle(conCostos ? "Reporte de productos" : "Lista de precios");
        doc.addCreator("ventcontrol");
        doc.open();

        // ---- banda de título ----
        PdfPTable banda = new PdfPTable(new float[]{4f, 1.3f});
        banda.setWidthPercentage(100);
        PdfPCell izq = new PdfPCell();
        izq.setBackgroundColor(NAVY);
        izq.setBorder(Rectangle.NO_BORDER);
        izq.setPadding(10);
        izq.setPaddingLeft(14);
        if (!EMPRESA.isEmpty()) {
            izq.addElement(new Paragraph(EMPRESA.toUpperCase(), fuente(bf, 7.5f, new Color(150, 172, 194))));
        }
        izq.addElement(new Paragraph(titulo, new Font(bfb, 18f, Font.NORMAL, Color.WHITE)));
        izq.addElement(new Paragraph(descripcionFiltros(nombre, marca, tipo, lote), fuente(bf, 9f, new Color(190, 210, 228))));
        PdfPCell der = new PdfPCell();
        der.setBackgroundColor(AZUL);
        der.setBorder(Rectangle.NO_BORDER);
        der.setPadding(10);
        der.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph a1 = new Paragraph("PRODUCTOS", fuente(bf, 7.5f, new Color(220, 238, 250)));
        a1.setAlignment(Element.ALIGN_CENTER);
        Paragraph a2 = new Paragraph(new DecimalFormat("#,##0", SIMBOLOS).format(filas.size()), new Font(bfb, 22f, Font.NORMAL, Color.WHITE));
        a2.setAlignment(Element.ALIGN_CENTER);
        der.addElement(a1);
        der.addElement(a2);
        banda.addCell(izq);
        banda.addCell(der);
        doc.add(banda);

        // ---- resumen ----
        if (conCostos) {
            PdfPTable res = new PdfPTable(5);
            res.setWidthPercentage(100);
            res.setSpacingBefore(8);
            res.addCell(tarjeta(bf, bfb, "Unidades en stock", cant(unidades), TINTA));
            res.addCell(tarjeta(bf, bfb, "Valor a costo (Gs.)", gs(valorCosto), TINTA));
            res.addCell(tarjeta(bf, bfb, "Valor a venta (Gs.)", gs(valorVenta), TINTA));
            res.addCell(tarjeta(bf, bfb, "Ganancia potencial (Gs.)", gs(valorVenta.subtract(valorCosto)),
                    valorVenta.compareTo(valorCosto) >= 0 ? new Color(22, 120, 70) : ROJO));
            res.addCell(tarjeta(bf, bfb, "Sin stock / negativos", sinStock + " / " + negativos, (sinStock + negativos) > 0 ? AMBAR : TINTA));
            doc.add(res);
        } else {
            PdfPTable nota = new PdfPTable(1);
            nota.setWidthPercentage(100);
            nota.setSpacingBefore(8);
            PdfPCell cn = new PdfPCell(new Phrase("Precios de venta en guaraníes (Gs.) vigentes al " + emitido
                    + ". El stock es el disponible al momento de la emisión.", fMuted));
            cn.setBorder(Rectangle.NO_BORDER);
            cn.setPaddingLeft(2);
            nota.addCell(cn);
            doc.add(nota);
        }

        // ---- detalle ----
        float[] anchos = conCostos
                ? new float[]{4f, 6f, 29f, 10f, 10f, 7f, 7.5f, 8.5f, 8.5f, 9.5f}
                : new float[]{4f, 8f, 37f, 15f, 15f, 8f, 13f};
        String[] cols = conCostos
                ? new String[]{"N°", "Cód.", "Producto", "Marca", "Tipo", "Lote", "Stock", "P. Costo", "P. Venta", "Valor a costo"}
                : new String[]{"N°", "Cód.", "Producto", "Marca", "Tipo", "Stock", "P. Venta"};
        PdfPTable t = new PdfPTable(anchos);
        t.setWidthPercentage(100);
        t.setSpacingBefore(8);
        t.setHeaderRows(1);
        t.setSplitLate(false);
        for (int i = 0; i < cols.length; i++) {
            boolean izqCol = cols[i].equals("Producto") || cols[i].equals("Marca") || cols[i].equals("Tipo") || cols[i].equals("Lote");
            boolean numerica = cols[i].equals("Stock") || cols[i].startsWith("P.") || cols[i].startsWith("Valor");
            t.addCell(celda(cols[i], fCab, numerica ? Element.ALIGN_RIGHT : (izqCol ? Element.ALIGN_LEFT : Element.ALIGN_CENTER), NAVY));
        }
        int n = 0;
        for (Fila f : filas) {
            n++;
            Color fondo = (n % 2 == 0) ? ZEBRA : null;
            Font fStock = fNormal;
            Color fondoStock = fondo;
            if (f.stock.signum() < 0) {
                fStock = new Font(bfb, 8.5f, Font.NORMAL, ROJO);
                fondoStock = ROJO_FONDO;
            } else if (f.stock.signum() == 0) {
                fStock = new Font(bfb, 8.5f, Font.NORMAL, AMBAR);
                fondoStock = AMBAR_FONDO;
            }
            t.addCell(celda(String.valueOf(n), fMuted, Element.ALIGN_CENTER, fondo));
            t.addCell(celda(f.cod, fNormal, Element.ALIGN_CENTER, fondo));
            t.addCell(celda(f.nombre, fNeg, Element.ALIGN_LEFT, fondo));
            t.addCell(celda(f.marca, fNormal, Element.ALIGN_LEFT, fondo));
            t.addCell(celda(f.tipo, fNormal, Element.ALIGN_LEFT, fondo));
            if (conCostos) {
                t.addCell(celda(f.lote, fNormal, Element.ALIGN_LEFT, fondo));
            }
            t.addCell(celda(cant(f.stock), fStock, Element.ALIGN_RIGHT, fondoStock));
            if (conCostos) {
                t.addCell(celda(gs(f.costo), fNormal, Element.ALIGN_RIGHT, fondo));
            }
            t.addCell(celda(gs(f.venta), fNeg, Element.ALIGN_RIGHT, fondo));
            if (conCostos) {
                t.addCell(celda(gs(f.costo.multiply(f.stock)), fNormal, Element.ALIGN_RIGHT, fondo));
            }
        }
        if (filas.isEmpty()) {
            PdfPCell vacio = celda("No hay productos que coincidan con el filtro.", fMuted, Element.ALIGN_CENTER, null);
            vacio.setColspan(cols.length);
            vacio.setPadding(14);
            t.addCell(vacio);
        } else if (conCostos) {
            // fila de totales
            PdfPCell tot = celda("TOTALES", fCab, Element.ALIGN_RIGHT, NAVY);
            tot.setColspan(6);
            t.addCell(tot);
            t.addCell(celda(cant(unidades), fCab, Element.ALIGN_RIGHT, NAVY));
            t.addCell(celda("", fCab, Element.ALIGN_RIGHT, NAVY));
            t.addCell(celda("", fCab, Element.ALIGN_RIGHT, NAVY));
            t.addCell(celda(gs(valorCosto), fCab, Element.ALIGN_RIGHT, NAVY));
        }
        doc.add(t);

        if (filas.size() > 0 && (sinStock + negativos) > 0) {
            Paragraph leyenda = new Paragraph("Stock en ámbar: producto sin existencias.   Stock en rojo: existencia negativa "
                    + "(se vendió más de lo registrado; conviene revisar compras o hacer un inventario).", fMuted);
            leyenda.setSpacingBefore(6);
            doc.add(leyenda);
        }
        doc.close();
    }

    // ---------------------------------------------------------------------------------------
    //  Interfaz
    // ---------------------------------------------------------------------------------------
    /** Pregunta qué versión se quiere, pide dónde guardar, genera el PDF y lo abre (o lo manda a imprimir). */
    static void preguntarYGenerar(Component padre, String nombre, String marca, String tipo, String lote, Integer usuarioId,
            boolean imprimir) {
        Object[] op = {"Reporte interno (con costos)", "Lista de precios (sin costos)", "Cancelar"};
        int r = JOptionPane.showOptionDialog(padre,
                "Se incluirán los productos según los filtros que tiene en pantalla.\n"
                + "El reporte interno muestra costos, valor del stock y ganancia potencial; la lista de precios solo muestra el precio de venta.",
                imprimir ? "Imprimir productos" : "Reporte de productos", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (r != 0 && r != 1) {
            return;
        }
        boolean conCostos = r == 0;
        File f;
        if (imprimir) {
            try {
                f = File.createTempFile(conCostos ? "productos_" : "precios_", ".pdf");
                f.deleteOnExit();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(padre, "No se pudo preparar la impresión:\n" + ex.getMessage(), "Imprimir productos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JFileChooser fc = new JFileChooser();
            File docs = new File(System.getProperty("user.home"), "Documents");
            fc.setCurrentDirectory(docs.isDirectory() ? docs : new File(System.getProperty("user.home")));
            fc.setDialogTitle(conCostos ? "Guardar reporte de productos" : "Guardar lista de precios");
            fc.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
            fc.setSelectedFile(new File(fc.getCurrentDirectory(), (conCostos ? "Productos_" : "Lista_de_precios_")
                    + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf"));
            if (fc.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".pdf")) {
                f = new File(f.getParentFile(), f.getName() + ".pdf");
            }
            if (f.exists() && JOptionPane.showConfirmDialog(padre, "El archivo ya existe. ¿Desea reemplazarlo?", "Guardar reporte",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            escribir(f, nombre, marca, tipo, lote, usuarioId, conCostos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el reporte:\n" + ex.getMessage()
                    + (ex instanceof SQLException ? InventarioDb.ayuda() : ""), "Reporte de productos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (imprimir) {
                try {
                    java.awt.Desktop.getDesktop().print(f);
                } catch (Exception sinImpresion) {
                    // si el sistema no tiene un programa de impresión para PDF, se abre para imprimir desde el visor
                    java.awt.Desktop.getDesktop().open(f);
                }
            } else {
                java.awt.Desktop.getDesktop().open(f);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "El reporte se guardó en:\n" + f.getAbsolutePath(), "Reporte de productos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

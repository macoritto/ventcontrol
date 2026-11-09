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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Reporte PDF (A4 horizontal) de un inventario: datos del inventario, resumen del conteo, detalle por
 * producto (conciliación de movimientos y conteo físico con su diferencia) y espacio para firmas.
 */
final class InventarioReporte {

    /** Nombre de la empresa que se imprime arriba a la izquierda (vacío = no se imprime). */
    static final String EMPRESA = "";

    private static final Color NAVY = new Color(16, 44, 72);
    private static final Color AZUL = new Color(0, 132, 190);
    private static final Color GRIS_BORDE = new Color(214, 219, 226);
    private static final Color GRIS_TEXTO = new Color(96, 106, 118);
    private static final Color ZEBRA = new Color(246, 248, 251);
    private static final Color ROJO = new Color(176, 38, 38);
    private static final Color ROJO_FONDO = new Color(253, 236, 234);
    private static final Color AZUL_TXT = new Color(0, 88, 158);
    private static final Color AZUL_FONDO = new Color(229, 242, 253);

    private static final DecimalFormatSymbols SIMBOLOS = new DecimalFormatSymbols();

    static {
        SIMBOLOS.setGroupingSeparator('.');
        SIMBOLOS.setDecimalSeparator(',');
    }

    private InventarioReporte() {
    }

    // ---------------------------------------------------------------------------------------
    //  Datos
    // ---------------------------------------------------------------------------------------
    private static final class Cabecera {
        String id, descripcion, fecha, fechaini, fechafin, hora, alcance, usuario;
        boolean ajustado;
    }

    private static final class Fila {
        String cod, nombre;
        BigDecimal ini, comp, vend, devo, reaj, teorico, sistema, contado, dif, costo, valor;
        boolean hayTeorico;

        boolean conDiferencia() {
            if (contado != null) {
                return dif != null && dif.signum() != 0;
            }
            return hayTeorico && sistema != null && teorico != null && sistema.subtract(teorico).signum() != 0;
        }
    }

    private static BigDecimal bd(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static BigDecimal nz(BigDecimal b) {
        return b == null ? BigDecimal.ZERO : b;
    }

    private static String fechaCorta(String iso) {
        if (iso == null || iso.length() < 10) {
            return iso == null ? "" : iso;
        }
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(iso.substring(0, 10));
            return new SimpleDateFormat("dd/MM/yyyy").format(d);
        } catch (ParseException ex) {
            return iso;
        }
    }

    private static Cabecera leerCabecera(Statement st, int id) throws SQLException {
        ResultSet rs = st.executeQuery("SELECT i.id_inv, i.descripcion, i.fecha, i.fechaini, i.fechafin, i.hora, i.alcance, i.ajustado, u.usuario AS nomusuario "
                + "FROM inventario i LEFT JOIN usuario u ON u.id::text=i.usuario::text WHERE i.id_inv='" + id + "'");
        Cabecera c = null;
        if (rs.next()) {
            c = new Cabecera();
            c.id = rs.getString("id_inv");
            c.descripcion = rs.getString("descripcion");
            c.fecha = fechaCorta(rs.getString("fecha"));
            c.fechaini = fechaCorta(rs.getString("fechaini"));
            c.fechafin = fechaCorta(rs.getString("fechafin"));
            String h = rs.getString("hora");
            c.hora = h == null ? "" : (h.length() >= 5 ? h.substring(0, 5) : h);
            c.alcance = rs.getString("alcance");
            c.usuario = rs.getString("nomusuario");
            c.ajustado = "1".equals(rs.getString("ajustado"));
        }
        rs.close();
        return c;
    }

    private static List<Fila> leerFilas(Statement st, int id) throws SQLException {
        List<Fila> filas = new ArrayList<Fila>();
        ResultSet rs = st.executeQuery("SELECT d.producto AS cod, p.nomprodu AS nom, d.stockini, d.comprado, d.vendido, d.entregado, d.repo, d.rene, d.stock, "
                + "d.contado, d.stock_sistema, d.costo FROM detinv d LEFT JOIN producto p ON p.codprodu::text=d.producto::text "
                + "WHERE d.inventario='" + id + "' ORDER BY p.nomprodu, d.id_inv");
        while (rs.next()) {
            Fila f = new Fila();
            f.cod = rs.getString("cod");
            f.nombre = rs.getString("nom") == null ? "(producto no encontrado)" : rs.getString("nom");
            f.ini = bd(rs.getString("stockini"));
            f.comp = bd(rs.getString("comprado"));
            f.vend = bd(rs.getString("vendido"));       // se guarda con signo negativo
            f.devo = bd(rs.getString("entregado"));
            f.reaj = nz(bd(rs.getString("repo"))).add(nz(bd(rs.getString("rene"))));  // rene se guarda con signo negativo
            BigDecimal sist = bd(rs.getString("stock_sistema"));
            f.sistema = sist != null ? sist : bd(rs.getString("stock"));
            f.contado = bd(rs.getString("contado"));
            f.costo = bd(rs.getString("costo"));
            if (f.ini != null && f.sistema != null) {
                f.hayTeorico = true;
                f.teorico = nz(f.ini).add(nz(f.comp)).add(nz(f.vend)).add(nz(f.devo)).add(f.reaj);
            }
            if (f.contado != null && f.sistema != null) {
                f.dif = f.contado.subtract(f.sistema);
                f.valor = f.dif.multiply(nz(f.costo)).setScale(0, java.math.RoundingMode.HALF_UP);
            }
            filas.add(f);
        }
        rs.close();
        return filas;
    }

    // ---------------------------------------------------------------------------------------
    //  Formato
    // ---------------------------------------------------------------------------------------
    private static String cant(BigDecimal b, boolean signo) {
        if (b == null) {
            return "—";
        }
        if (b.signum() == 0) {
            return "–";
        }
        String s = new DecimalFormat("#,##0.###", SIMBOLOS).format(b);
        return signo && b.signum() > 0 ? "+" + s : s;
    }

    private static String cantSiempre(BigDecimal b, boolean signo) {
        if (b == null) {
            return "—";
        }
        String s = new DecimalFormat("#,##0.###", SIMBOLOS).format(b);
        return signo && b.signum() > 0 ? "+" + s : s;
    }

    private static String gs(BigDecimal b) {
        return b == null ? "—" : new DecimalFormat("#,##0", SIMBOLOS).format(b);
    }

    // ---------------------------------------------------------------------------------------
    //  PDF
    // ---------------------------------------------------------------------------------------
    private static Font fuente(BaseFont bf, float tam, int estilo, Color c) {
        return new Font(bf, tam, estilo, c);
    }

    private static PdfPCell celda(String texto, Font f, int alin, Color fondo) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setHorizontalAlignment(alin);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(GRIS_BORDE);
        c.setBorderWidth(0.5f);
        c.setPaddingTop(3.5f);
        c.setPaddingBottom(4f);
        c.setPaddingLeft(4f);
        c.setPaddingRight(4f);
        if (fondo != null) {
            c.setBackgroundColor(fondo);
        }
        return c;
    }

    private static PdfPCell sinBorde(Phrase p, int alin) {
        PdfPCell c = new PdfPCell(p);
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(alin);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    /** Pie de página con fecha de emisión y "Página x de y". */
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
            String pagina = "Página " + writer.getPageNumber() + " de ";
            cb.showTextAligned(Element.ALIGN_RIGHT, pagina, document.right() - 18, y, 0);
            cb.endText();
            cb.addTemplate(total, document.right() - 18, y);
            // (el total se completa al cerrar el documento)
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

    /** Genera el PDF en el archivo indicado. */
    static void escribir(File destino, int idInv, boolean soloDiferencias) throws SQLException, DocumentException, IOException {
        InventarioDb.asegurarEsquema();
        Cabecera cab;
        List<Fila> todas;
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            cab = leerCabecera(st, idInv);
            if (cab == null) {
                throw new SQLException("No existe el inventario " + idInv + ".");
            }
            todas = leerFilas(st, idInv);
            st.close();
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }

        // ---- resumen ----
        int contados = 0, conDif = 0;
        BigDecimal falta = BigDecimal.ZERO, sobra = BigDecimal.ZERO;
        List<Fila> filas = new ArrayList<Fila>();
        for (Fila f : todas) {
            if (f.contado != null) {
                contados++;
                if (f.valor != null) {
                    if (f.valor.signum() < 0) {
                        falta = falta.add(f.valor);
                    } else {
                        sobra = sobra.add(f.valor);
                    }
                }
            }
            if (f.conDiferencia()) {
                conDif++;
            }
            if (!soloDiferencias || f.conDiferencia()) {
                filas.add(f);
            }
        }

        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font fNormal = fuente(bf, 8.5f, Font.NORMAL, new Color(33, 43, 56));
        Font fNeg = fuente(bfb, 8.5f, Font.NORMAL, new Color(33, 43, 56));
        Font fMuted = fuente(bf, 8f, Font.NORMAL, GRIS_TEXTO);
        Font fCab = fuente(bfb, 8f, Font.NORMAL, Color.WHITE);

        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 28, 44);
        PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream(destino));
        w.setPageEvent(new Pie(bf, "Reporte de inventario N° " + cab.id + "  ·  Emitido el "
                + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
        doc.addTitle("Reporte de inventario N° " + cab.id);
        doc.addCreator("ventcontrol");
        doc.open();

        // ---- banda de título ----
        PdfPTable banda = new PdfPTable(new float[]{3f, 1.2f});
        banda.setWidthPercentage(100);
        PdfPCell izq = new PdfPCell();
        izq.setBackgroundColor(NAVY);
        izq.setBorder(Rectangle.NO_BORDER);
        izq.setPadding(10);
        izq.setPaddingLeft(14);
        if (!EMPRESA.isEmpty()) {
            izq.addElement(new Paragraph(EMPRESA.toUpperCase(), fuente(bf, 8f, Font.NORMAL, new Color(150, 172, 194))));
        }
        izq.addElement(new Paragraph("REPORTE DE INVENTARIO", fuente(bfb, 18f, Font.NORMAL, Color.WHITE)));
        String sub = (cab.alcance == null || cab.alcance.isEmpty()) ? "Inventario de productos" : cab.alcance;
        if (soloDiferencias) {
            sub += "  ·  solo productos con diferencia";
        }
        izq.addElement(new Paragraph(sub, fuente(bf, 9f, Font.NORMAL, new Color(190, 210, 228))));
        PdfPCell der = new PdfPCell();
        der.setBackgroundColor(AZUL);
        der.setBorder(Rectangle.NO_BORDER);
        der.setPadding(10);
        der.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph pn = new Paragraph("INVENTARIO N°", fuente(bf, 8f, Font.NORMAL, new Color(220, 238, 250)));
        pn.setAlignment(Element.ALIGN_CENTER);
        Paragraph pnum = new Paragraph(String.format("%06d", idInv), fuente(bfb, 22f, Font.NORMAL, Color.WHITE));
        pnum.setAlignment(Element.ALIGN_CENTER);
        der.addElement(pn);
        der.addElement(pnum);
        banda.addCell(izq);
        banda.addCell(der);
        doc.add(banda);

        // ---- datos del inventario ----
        PdfPTable datos = new PdfPTable(new float[]{1.1f, 2f, 1.1f, 2f, 1.1f, 2f});
        datos.setWidthPercentage(100);
        datos.setSpacingBefore(10);
        String estado = cab.ajustado ? "Stock ajustado a lo contado"
                : (contados > 0 ? "Solo conteo (stock sin modificar)" : "Sin conteo físico (solo conciliación)");
        String[][] campos = {
            {"Fecha de emisión", cab.fecha + (cab.hora.isEmpty() ? "" : "  " + cab.hora + " hs"), "Período conciliado", cab.fechaini + "  al  " + cab.fechafin,
                "Responsable", cab.usuario == null ? "—" : cab.usuario},
            {"Descripción", cab.descripcion == null || cab.descripcion.trim().isEmpty() ? "—" : cab.descripcion.trim(), "Alcance",
                cab.alcance == null || cab.alcance.isEmpty() ? "Todos los productos" : cab.alcance, "Estado", estado}
        };
        for (String[] fila : campos) {
            for (int i = 0; i < 6; i += 2) {
                PdfPCell l = sinBorde(new Phrase(fila[i].toUpperCase(), fuente(bfb, 7f, Font.NORMAL, GRIS_TEXTO)), Element.ALIGN_LEFT);
                l.setPaddingTop(3);
                l.setPaddingBottom(3);
                PdfPCell v = sinBorde(new Phrase(fila[i + 1], fNeg), Element.ALIGN_LEFT);
                v.setPaddingTop(3);
                v.setPaddingBottom(3);
                datos.addCell(l);
                datos.addCell(v);
            }
        }
        doc.add(datos);

        // ---- tarjetas de resumen ----
        PdfPTable kpi = new PdfPTable(6);
        kpi.setWidthPercentage(100);
        kpi.setSpacingBefore(8);
        BigDecimal neto = falta.add(sobra);
        Object[][] tarjetas = {
            {"PRODUCTOS", String.valueOf(todas.size()), NAVY},
            {"CONTADOS", String.valueOf(contados), NAVY},
            {"CON DIFERENCIA", String.valueOf(conDif), conDif > 0 ? ROJO : NAVY},
            {"FALTANTE (Gs)", contados == 0 ? "—" : gs(falta.negate()), ROJO},
            {"SOBRANTE (Gs)", contados == 0 ? "—" : gs(sobra), AZUL_TXT},
            {"RESULTADO NETO (Gs)", contados == 0 ? "—" : gs(neto), neto.signum() < 0 ? ROJO : (neto.signum() > 0 ? AZUL_TXT : NAVY)}
        };
        for (Object[] t : tarjetas) {
            PdfPCell c = new PdfPCell();
            c.setBorderColor(GRIS_BORDE);
            c.setBorderWidth(0.5f);
            c.setBackgroundColor(ZEBRA);
            c.setPadding(7);
            Paragraph a = new Paragraph((String) t[0], fuente(bfb, 7f, Font.NORMAL, GRIS_TEXTO));
            a.setAlignment(Element.ALIGN_CENTER);
            Paragraph b = new Paragraph((String) t[1], fuente(bfb, 15f, Font.NORMAL, (Color) t[2]));
            b.setAlignment(Element.ALIGN_CENTER);
            c.addElement(a);
            c.addElement(b);
            kpi.addCell(c);
        }
        doc.add(kpi);

        // ---- detalle ----
        PdfPTable t = new PdfPTable(new float[]{6.2f, 24f, 6f, 6f, 6f, 6f, 6.2f, 7f, 7f, 7f, 6.5f, 7f, 9f});
        t.setWidthPercentage(100);
        t.setSpacingBefore(12);
        t.setHeaderRows(2);
        t.setSplitLate(false);
        // fila 1: grupos
        PdfPCell g1 = celda("PRODUCTO", fCab, Element.ALIGN_CENTER, NAVY);
        g1.setColspan(2);
        PdfPCell g2 = celda("CONCILIACIÓN DE MOVIMIENTOS DEL PERÍODO", fCab, Element.ALIGN_CENTER, new Color(31, 68, 102));
        g2.setColspan(6);
        PdfPCell g3 = celda("CONTEO FÍSICO", fCab, Element.ALIGN_CENTER, AZUL);
        g3.setColspan(5);
        t.addCell(g1);
        t.addCell(g2);
        t.addCell(g3);
        String[] cols = {"Cód.", "Producto", "Stock inicial", "Compras", "Ventas", "Devol.", "Reajustes", "Stock teórico",
            "Stock sistema", "Contado", "Diferencia", "Costo (Gs)", "Valor dif. (Gs)"};
        for (int i = 0; i < cols.length; i++) {
            Color fondo = i < 2 ? NAVY : (i < 8 ? new Color(31, 68, 102) : AZUL);
            t.addCell(celda(cols[i], fCab, i == 1 ? Element.ALIGN_LEFT : Element.ALIGN_CENTER, fondo));
        }
        int n = 0;
        for (Fila f : filas) {
            Color zebra = (n++ % 2 == 1) ? ZEBRA : null;
            t.addCell(celda(f.cod == null ? "" : f.cod, fMuted, Element.ALIGN_LEFT, zebra));
            t.addCell(celda(f.nombre, fNormal, Element.ALIGN_LEFT, zebra));
            t.addCell(celda(cantSiempre(f.ini, false), fNormal, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(cant(f.comp, true), fNormal, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(cant(f.vend, true), fNormal, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(cant(f.devo, true), fNormal, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(cant(f.reaj, true), fNormal, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(f.hayTeorico ? cantSiempre(f.teorico, false) : "—", fNeg, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(cantSiempre(f.sistema, false), fNormal, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(f.contado == null ? "—" : cantSiempre(f.contado, false), fNeg, Element.ALIGN_RIGHT, zebra));
            Font fd = fNormal;
            Color fondoDif = zebra;
            String txtDif = "—";
            String txtVal = "—";
            if (f.dif != null) {
                txtDif = f.dif.signum() == 0 ? "0" : cantSiempre(f.dif, true);
                txtVal = f.valor.signum() == 0 ? "0" : gs(f.valor);
                if (f.dif.signum() < 0) {
                    fd = fuente(bfb, 8.5f, Font.NORMAL, ROJO);
                    fondoDif = ROJO_FONDO;
                } else if (f.dif.signum() > 0) {
                    fd = fuente(bfb, 8.5f, Font.NORMAL, AZUL_TXT);
                    fondoDif = AZUL_FONDO;
                }
            }
            t.addCell(celda(txtDif, fd, Element.ALIGN_RIGHT, fondoDif));
            t.addCell(celda(f.costo == null ? "—" : gs(f.costo), fNormal, Element.ALIGN_RIGHT, zebra));
            t.addCell(celda(txtVal, fd, Element.ALIGN_RIGHT, fondoDif));
        }
        if (filas.isEmpty()) {
            PdfPCell vacio = celda(soloDiferencias ? "No hay productos con diferencia en este inventario." : "El inventario no tiene productos.",
                    fMuted, Element.ALIGN_CENTER, null);
            vacio.setColspan(13);
            vacio.setPadding(14);
            t.addCell(vacio);
        } else if (contados > 0) {
            PdfPCell tot = celda("TOTAL DE DIFERENCIAS VALORIZADAS A COSTO", fuente(bfb, 8f, Font.NORMAL, NAVY), Element.ALIGN_RIGHT, new Color(226, 233, 242));
            tot.setColspan(12);
            t.addCell(tot);
            t.addCell(celda(gs(neto), fuente(bfb, 9f, Font.NORMAL, neto.signum() < 0 ? ROJO : (neto.signum() > 0 ? AZUL_TXT : NAVY)),
                    Element.ALIGN_RIGHT, new Color(226, 233, 242)));
        }
        doc.add(t);

        // ---- notas ----
        Paragraph nota = new Paragraph("Stock teórico = stock inicial + compras + ventas (negativas) + devoluciones + reajustes. "
                + "Diferencia = contado - stock del sistema al guardar el inventario (negativa = faltante, positiva = sobrante). "
                + "El valor se calcula con el costo del producto al momento de guardar.", fuente(bf, 7.5f, Font.ITALIC, GRIS_TEXTO));
        nota.setSpacingBefore(8);
        doc.add(nota);

        // ---- firmas ----
        PdfPTable firmas = new PdfPTable(new float[]{1f, 0.18f, 1f, 0.18f, 1f});
        firmas.setWidthPercentage(100);
        firmas.setSpacingBefore(46);
        firmas.setKeepTogether(true);
        String[] roles = {"Contado por", "Verificado por", "Aprobado por"};
        for (int i = 0; i < roles.length; i++) {
            PdfPCell c = new PdfPCell(new Phrase(roles[i] + "\nFirma y aclaración", fuente(bf, 8f, Font.NORMAL, GRIS_TEXTO)));
            c.setBorder(Rectangle.TOP);
            c.setBorderColor(GRIS_TEXTO);
            c.setBorderWidth(0.6f);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setPaddingTop(4);
            firmas.addCell(c);
            if (i < roles.length - 1) {
                PdfPCell sep = new PdfPCell(new Phrase(" "));
                sep.setBorder(Rectangle.NO_BORDER);
                firmas.addCell(sep);
            }
        }
        doc.add(firmas);
        doc.close();
    }

    // ---------------------------------------------------------------------------------------
    //  Planilla de conteo (para contar a mano antes de cargar el inventario)
    // ---------------------------------------------------------------------------------------

    /**
     * Planilla en blanco para contar con birome: una fila por producto con una casilla grande para
     * anotar lo contado. Cada fila de {@code filas} es {código, nombre, stock del sistema}. Por defecto es
     * "ciega" (sin el stock del sistema) para que el conteo no se contamine con lo que dice el sistema.
     */
    static void escribirPlanilla(File destino, List<String[]> filas, String alcance, Integer usuarioId, boolean conStock)
            throws SQLException, DocumentException, IOException {
        java.util.Map<String, String[]> extra = new java.util.HashMap<String, String[]>();
        String responsable = "";
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT p.codprodu, p.estante, m.nombre AS marca FROM producto p "
                    + "LEFT JOIN marca m ON m.id_marca::text=p.marca::text");
            while (rs.next()) {
                extra.put(rs.getString("codprodu").trim(), new String[]{rs.getString("estante"), rs.getString("marca")});
            }
            rs.close();
            if (usuarioId != null) {
                rs = st.executeQuery("SELECT usuario FROM usuario WHERE id::text='" + usuarioId + "'");
                if (rs.next()) {
                    responsable = rs.getString(1);
                }
                rs.close();
            }
            st.close();
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }

        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font fNormal = fuente(bf, 9f, Font.NORMAL, new Color(20, 28, 38));
        Font fNeg = fuente(bfb, 9f, Font.NORMAL, new Color(20, 28, 38));
        Font fMuted = fuente(bf, 8f, Font.NORMAL, GRIS_TEXTO);
        Font fCab = fuente(bfb, 8.5f, Font.NORMAL, Color.WHITE);
        Color borde = new Color(120, 128, 138);
        String emitido = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        Document doc = new Document(PageSize.A4, 28, 28, 26, 44);
        PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream(destino));
        w.setPageEvent(new Pie(bf, "Planilla de conteo  ·  Emitida el " + emitido));
        doc.addTitle("Planilla de conteo de inventario");
        doc.addCreator("ventcontrol");
        doc.open();

        // ---- título ----
        PdfPTable banda = new PdfPTable(new float[]{3f, 1.4f});
        banda.setWidthPercentage(100);
        PdfPCell izq = new PdfPCell();
        izq.setBackgroundColor(NAVY);
        izq.setBorder(Rectangle.NO_BORDER);
        izq.setPadding(9);
        izq.setPaddingLeft(12);
        if (!EMPRESA.isEmpty()) {
            izq.addElement(new Paragraph(EMPRESA.toUpperCase(), fuente(bf, 7.5f, Font.NORMAL, new Color(150, 172, 194))));
        }
        izq.addElement(new Paragraph("PLANILLA DE CONTEO", fuente(bfb, 17f, Font.NORMAL, Color.WHITE)));
        izq.addElement(new Paragraph(alcance == null || alcance.isEmpty() ? "Inventario de productos" : alcance,
                fuente(bf, 8.5f, Font.NORMAL, new Color(190, 210, 228))));
        PdfPCell der = new PdfPCell();
        der.setBackgroundColor(AZUL);
        der.setBorder(Rectangle.NO_BORDER);
        der.setPadding(9);
        der.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph a1 = new Paragraph("PRODUCTOS A CONTAR", fuente(bf, 7.5f, Font.NORMAL, new Color(220, 238, 250)));
        a1.setAlignment(Element.ALIGN_CENTER);
        Paragraph a2 = new Paragraph(String.valueOf(filas.size()), fuente(bfb, 20f, Font.NORMAL, Color.WHITE));
        a2.setAlignment(Element.ALIGN_CENTER);
        der.addElement(a1);
        der.addElement(a2);
        banda.addCell(izq);
        banda.addCell(der);
        doc.add(banda);

        // ---- datos y espacios para completar a mano ----
        PdfPTable datos = new PdfPTable(new float[]{1.2f, 2.3f, 1.2f, 2.3f});
        datos.setWidthPercentage(100);
        datos.setSpacingBefore(8);
        String[][] campos = {
            {"Emitida el", emitido, "Responsable del sistema", responsable.isEmpty() ? "—" : responsable},
            {"Contó", "", "Fecha del conteo", "____ / ____ / ________"},
            {"Hora de inicio", "____ : ____", "Hora de fin", "____ : ____"}
        };
        for (String[] fila : campos) {
            for (int i = 0; i < 4; i += 2) {
                PdfPCell l = sinBorde(new Phrase(fila[i].toUpperCase(), fuente(bfb, 7f, Font.NORMAL, GRIS_TEXTO)), Element.ALIGN_LEFT);
                l.setPaddingTop(4);
                l.setPaddingBottom(4);
                PdfPCell v = sinBorde(new Phrase(fila[i + 1], fNeg), Element.ALIGN_LEFT);
                v.setPaddingTop(4);
                v.setPaddingBottom(4);
                if (fila[i + 1].isEmpty()) {
                    v.setBorder(Rectangle.BOTTOM);
                    v.setBorderColor(borde);
                    v.setBorderWidth(0.6f);
                }
                datos.addCell(l);
                datos.addCell(v);
            }
        }
        doc.add(datos);

        PdfPTable instr = new PdfPTable(1);
        instr.setWidthPercentage(100);
        instr.setSpacingBefore(6);
        PdfPCell ci = new PdfPCell(new Phrase("Cuente cada producto y anote la cantidad en la columna CONTADO. Si no hay unidades escriba 0; "
                + "no deje casillas en blanco. En Observación anote roturas, vencidos o productos fuera de su lugar. "
                + "Anote al pie las ventas o compras que ocurran durante el conteo.", fuente(bf, 8f, Font.NORMAL, new Color(60, 70, 82))));
        ci.setBackgroundColor(new Color(255, 250, 225));
        ci.setBorderColor(new Color(230, 214, 150));
        ci.setBorderWidth(0.6f);
        ci.setPadding(6);
        instr.addCell(ci);
        doc.add(instr);

        // ---- planilla ----
        float[] anchos = conStock ? new float[]{4.5f, 8f, 37f, 8f, 8f, 15f, 19.5f} : new float[]{4.5f, 8.5f, 41f, 8.5f, 15f, 22.5f};
        PdfPTable t = new PdfPTable(anchos);
        t.setWidthPercentage(100);
        t.setSpacingBefore(8);
        t.setHeaderRows(1);
        t.setSplitLate(false);
        String[] cols = conStock ? new String[]{"N°", "Cód.", "Producto", "Estante", "Sistema", "CONTADO", "Observación"}
                : new String[]{"N°", "Cód.", "Producto", "Estante", "CONTADO", "Observación"};
        for (int i = 0; i < cols.length; i++) {
            boolean contado = cols[i].equals("CONTADO");
            t.addCell(celda(cols[i], fCab, (i == 2 || i == cols.length - 1) ? Element.ALIGN_LEFT : Element.ALIGN_CENTER, contado ? AZUL : NAVY));
        }
        int n = 0;
        for (String[] f : filas) {
            n++;
            String[] ex = extra.get(f[0] == null ? "" : f[0].trim());
            String estante = ex == null || ex[0] == null ? "" : ex[0];
            String nombre = f[1] == null ? "" : f[1];
            if (ex != null && ex[1] != null && !ex[1].isEmpty()) {
                nombre += "  (" + ex[1] + ")";
            }
            PdfPCell c1 = celda(String.valueOf(n), fMuted, Element.ALIGN_CENTER, null);
            PdfPCell c2 = celda(f[0] == null ? "" : f[0], fNormal, Element.ALIGN_CENTER, null);
            PdfPCell c3 = celda(nombre, fNormal, Element.ALIGN_LEFT, null);
            PdfPCell c4 = celda(estante, fNormal, Element.ALIGN_CENTER, null);
            PdfPCell cc = celda("", fNeg, Element.ALIGN_CENTER, null);
            cc.setBorderColor(borde);
            cc.setBorderWidth(1.1f);
            PdfPCell co = celda("", fNormal, Element.ALIGN_LEFT, null);
            for (PdfPCell c : new PdfPCell[]{c1, c2, c3, c4, cc, co}) {
                c.setMinimumHeight(25f);
                c.setBorderColor(c == cc ? borde : GRIS_BORDE);
            }
            t.addCell(c1);
            t.addCell(c2);
            t.addCell(c3);
            t.addCell(c4);
            if (conStock) {
                t.addCell(celda(f.length > 2 && f[2] != null ? f[2] : "", fNormal, Element.ALIGN_CENTER, null));
            }
            t.addCell(cc);
            t.addCell(co);
        }
        doc.add(t);

        // ---- movimientos durante el conteo ----
        Paragraph mov = new Paragraph("Ventas o compras durante el conteo (código, cantidad y si fue venta o compra)",
                fuente(bfb, 8f, Font.NORMAL, new Color(60, 70, 82)));
        mov.setSpacingBefore(14);
        doc.add(mov);
        PdfPTable m = new PdfPTable(new float[]{12f, 50f, 14f, 24f});
        m.setWidthPercentage(100);
        m.setSpacingBefore(4);
        m.setKeepTogether(true);
        for (String h : new String[]{"Cód.", "Producto", "Cantidad", "Venta / Compra"}) {
            m.addCell(celda(h, fuente(bfb, 7.5f, Font.NORMAL, GRIS_TEXTO), Element.ALIGN_CENTER, ZEBRA));
        }
        for (int i = 0; i < 32; i++) {
            PdfPCell e = celda(" ", fNormal, Element.ALIGN_LEFT, null);
            e.setMinimumHeight(20f);
            m.addCell(e);
        }
        doc.add(m);

        // ---- firmas ----
        PdfPTable firmas = new PdfPTable(new float[]{1f, 0.18f, 1f});
        firmas.setWidthPercentage(100);
        firmas.setSpacingBefore(34);
        firmas.setKeepTogether(true);
        String[] roles = {"Contó", "Verificó"};
        for (int i = 0; i < roles.length; i++) {
            PdfPCell c = new PdfPCell(new Phrase(roles[i] + "\nFirma y aclaración", fMuted));
            c.setBorder(Rectangle.TOP);
            c.setBorderColor(GRIS_TEXTO);
            c.setBorderWidth(0.6f);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setPaddingTop(4);
            firmas.addCell(c);
            if (i < roles.length - 1) {
                PdfPCell sep = new PdfPCell(new Phrase(" "));
                sep.setBorder(Rectangle.NO_BORDER);
                firmas.addCell(sep);
            }
        }
        doc.add(firmas);
        doc.close();
    }

    /** Pregunta si la planilla lleva o no el stock del sistema, pide dónde guardar, genera el PDF y lo abre. */
    static void preguntarYGenerarPlanilla(Component padre, List<String[]> filas, String alcance, Integer usuarioId) {
        if (filas == null || filas.isEmpty()) {
            JOptionPane.showMessageDialog(padre, "Primero cargue los productos del inventario.");
            return;
        }
        Object[] op = {"Planilla ciega (recomendada)", "Con stock del sistema", "Cancelar"};
        int r = JOptionPane.showOptionDialog(padre,
                "La planilla ciega no muestra el stock del sistema: así el conteo es independiente y detecta mejor las diferencias.\n"
                + "Se imprimen los " + filas.size() + " productos que ve en la tabla, en el mismo orden.",
                "Planilla de conteo", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (r != 0 && r != 1) {
            return;
        }
        JFileChooser fc = new JFileChooser();
        File docs = new File(System.getProperty("user.home"), "Documents");
        fc.setCurrentDirectory(docs.isDirectory() ? docs : new File(System.getProperty("user.home")));
        fc.setDialogTitle("Guardar planilla de conteo");
        fc.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        fc.setSelectedFile(new File(fc.getCurrentDirectory(), "Planilla_conteo_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf"));
        if (fc.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }
        if (f.exists() && JOptionPane.showConfirmDialog(padre, "El archivo ya existe. ¿Desea reemplazarlo?", "Guardar planilla",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            escribirPlanilla(f, filas, alcance, usuarioId, r == 1);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar la planilla:\n" + ex.getMessage(), "Planilla de conteo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            java.awt.Desktop.getDesktop().open(f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "La planilla se guardó en:\n" + f.getAbsolutePath(), "Planilla de conteo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Pide dónde guardar, genera el PDF y lo abre. */
    static void generarYAbrir(Component padre, int idInv, boolean soloDiferencias) {
        JFileChooser fc = new JFileChooser();
        File docs = new File(System.getProperty("user.home"), "Documents");
        fc.setCurrentDirectory(docs.isDirectory() ? docs : new File(System.getProperty("user.home")));
        fc.setDialogTitle("Guardar reporte de inventario");
        fc.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        fc.setSelectedFile(new File(fc.getCurrentDirectory(), "Inventario_" + idInv + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date())
                + (soloDiferencias ? "_diferencias" : "") + ".pdf"));
        if (fc.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }
        if (f.exists() && JOptionPane.showConfirmDialog(padre, "El archivo ya existe. ¿Desea reemplazarlo?", "Guardar reporte",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            escribir(f, idInv, soloDiferencias);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el reporte:\n" + ex.getMessage()
                    + (ex instanceof SQLException ? InventarioDb.ayuda() : ""), "Reporte de inventario", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            java.awt.Desktop.getDesktop().open(f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "El reporte se guardó en:\n" + f.getAbsolutePath(), "Reporte de inventario",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Pregunta si el reporte es completo o solo con diferencias y lo genera. */
    static void preguntarYGenerar(Component padre, int idInv) {
        Object[] op = {"Reporte completo", "Solo con diferencias", "Cancelar"};
        int r = JOptionPane.showOptionDialog(padre, "¿Qué desea incluir en el reporte del inventario N° " + idInv + "?", "Reporte de inventario",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (r == 0 || r == 1) {
            generarYAbrir(padre, idInv, r == 1);
        }
    }
}

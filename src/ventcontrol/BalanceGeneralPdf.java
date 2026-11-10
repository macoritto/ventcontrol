package ventcontrol;

import com.lowagie.text.Chunk;
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
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * PDF A4 del estado general de la empresa: patrimonio, activo y pasivo, indicadores, mercadería, lo que nos deben y lo que
 * debemos. Se escribe a un archivo temporal y se abre con el visor de PDF del equipo.
 */
final class BalanceGeneralPdf {

    private static final Color NAVY = new Color(16, 44, 72);
    private static final Color AZUL = new Color(0, 132, 190);
    private static final Color GRIS_BORDE = new Color(214, 219, 226);
    private static final Color GRIS_TEXTO = new Color(96, 106, 118);
    private static final Color ZEBRA = new Color(246, 248, 251);
    private static final Color VERDE = new Color(22, 120, 70);
    private static final Color ROJO = new Color(176, 38, 38);
    private static final Color ROJO_FONDO = new Color(253, 236, 234);
    private static final Color AMBAR = new Color(138, 94, 0);
    private static final Color AMBAR_FONDO = new Color(255, 247, 226);

    private static final DecimalFormatSymbols SIMBOLOS = new DecimalFormatSymbols();

    static {
        SIMBOLOS.setGroupingSeparator('.');
        SIMBOLOS.setDecimalSeparator(',');
    }

    private static final DecimalFormat FMT = new DecimalFormat("#,##0", SIMBOLOS);
    private static final SimpleDateFormat DIA = new SimpleDateFormat("dd/MM/yyyy");

    private BalanceGeneralPdf() {
    }

    private static String gs(long v) {
        return (v < 0 ? "- " : "") + "Gs. " + FMT.format(Math.abs(v));
    }

    private static String dia(Date d) {
        return d == null ? "" : DIA.format(d);
    }

    // ------------------------------------------------------------------ API

    static void mostrar(Component padre, BalanceGeneralDatos d) {
        try {
            abrir(padre, archivo(d));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el PDF:\n" + e.getMessage(), "Estado general", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void abrir(Component padre, File f) {
        try {
            java.awt.Desktop.getDesktop().open(f);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "El documento se guardó en:\n" + f.getAbsolutePath(), "Documento", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ------------------------------------------------------------------ piezas comunes

    private static final class Fuentes {
        final BaseFont bf, bfB;
        final Font txt, txtB, chico, chicoB, cab;

        Fuentes() throws Exception {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            bfB = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            txt = new Font(bf, 9.5f, Font.NORMAL, Color.BLACK);
            txtB = new Font(bfB, 9.5f, Font.NORMAL, Color.BLACK);
            chico = new Font(bf, 8.5f, Font.NORMAL, GRIS_TEXTO);
            chicoB = new Font(bfB, 8.5f, Font.NORMAL, GRIS_TEXTO);
            cab = new Font(bfB, 8.5f, Font.NORMAL, Color.WHITE);
        }
    }

    private static PdfPTable banda(Fuentes f, String titulo, String sub, String etiqueta, Color colorEtiqueta) {
        PdfPTable banda = new PdfPTable(new float[]{3f, 1.3f});
        banda.setWidthPercentage(100);
        Phrase p = new Phrase();
        p.add(new Chunk(titulo + "\n", new Font(f.bfB, 17, Font.NORMAL, Color.WHITE)));
        p.add(new Chunk(sub, new Font(f.bf, 10.5f, Font.NORMAL, new Color(205, 224, 242))));
        PdfPCell c = new PdfPCell(p);
        c.setBackgroundColor(NAVY);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(12);
        c.setPaddingLeft(16);
        c.setLeading(0, 1.25f);
        banda.addCell(c);
        PdfPCell e = new PdfPCell(new Phrase(etiqueta, new Font(f.bfB, 10, Font.NORMAL, Color.WHITE)));
        e.setBackgroundColor(colorEtiqueta);
        e.setBorder(Rectangle.NO_BORDER);
        e.setHorizontalAlignment(Element.ALIGN_CENTER);
        e.setVerticalAlignment(Element.ALIGN_MIDDLE);
        banda.addCell(e);
        return banda;
    }

    private static void espacio(Document doc, float pt) throws DocumentException {
        Paragraph p = new Paragraph(" ");
        p.setLeading(pt);
        doc.add(p);
    }

    private static void seccion(Document doc, Fuentes f, String titulo, String detalle) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        Phrase ph = new Phrase();
        ph.add(new Chunk(titulo, new Font(f.bfB, 11, Font.NORMAL, NAVY)));
        if (detalle != null && detalle.length() > 0) {
            ph.add(new Chunk("   " + detalle, new Font(f.bf, 8.5f, Font.NORMAL, GRIS_TEXTO)));
        }
        PdfPCell c = new PdfPCell(ph);
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColor(AZUL);
        c.setBorderWidthBottom(1.4f);
        c.setPaddingLeft(0);
        c.setPaddingBottom(4);
        t.addCell(c);
        t.setSpacingAfter(5);
        doc.add(t);
    }

    private static PdfPCell celda(String texto, Font f, int alin, Color fondo) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setHorizontalAlignment(alin);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(GRIS_BORDE);
        c.setBorderWidth(0.5f);
        c.setPaddingTop(3.5f);
        c.setPaddingBottom(4f);
        c.setPaddingLeft(5f);
        c.setPaddingRight(5f);
        if (fondo != null) {
            c.setBackgroundColor(fondo);
        }
        return c;
    }

    private static PdfPCell sinBorde(Phrase p) {
        PdfPCell c = new PdfPCell(p);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPaddingTop(2.5f);
        c.setPaddingBottom(2.5f);
        c.setPaddingLeft(0);
        return c;
    }

    private static final class Pie extends PdfPageEventHelper {

        private final BaseFont bf;
        private final String izquierda;
        private final String derecha;

        Pie(BaseFont bf, String izquierda, String derecha) {
            this.bf = bf;
            this.izquierda = izquierda;
            this.derecha = derecha;
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
            cb.showTextAligned(Element.ALIGN_RIGHT, derecha + "  ·  Página " + writer.getPageNumber(), document.right(), y, 0);
            cb.endText();
        }
    }

    private static Document abrirDoc(File f, Fuentes fu, String derecha) throws Exception {
        Document doc = new Document(PageSize.A4, 40, 40, 36, 52);
        PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream(f));
        w.setPageEvent(new Pie(fu.bf, "Emitido el " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), derecha));
        doc.open();
        return doc;
    }

    // ------------------------------------------------------------------ documento

    private static String cant(double c) {
        return c == Math.rint(c) ? FMT.format((long) c) : new DecimalFormat("#,##0.##", SIMBOLOS).format(c);
    }

    private static String pct(double v) {
        return new DecimalFormat("0.#", SIMBOLOS).format(v) + " %";
    }

    private static PdfPCell fila2(String texto, Font f, int alin, Color fondo, int span) {
        PdfPCell c = celda(texto, f, alin, fondo);
        c.setColspan(span);
        return c;
    }

    static File archivo(BalanceGeneralDatos d) throws Exception {
        File f = File.createTempFile("Estado_general_", ".pdf");
        f.deleteOnExit();
        Fuentes fu = new Fuentes();
        Document doc = abrirDoc(f, fu, "Estado general de la empresa");
        long pat = d.patrimonio();
        doc.add(banda(fu, "ESTADO GENERAL DE LA EMPRESA", "Situación al " + dia(d.fecha),
                d.pasivo() == 0 ? (d.activo() > 0 ? "SIN DEUDAS" : "SIN DATOS") : (d.activo() >= d.pasivo() * 2 ? "SITUACIÓN SANA" : (d.activo() >= d.pasivo() ? "AJUSTADA" : "DEUDAS MAYORES AL ACTIVO")),
                d.pasivo() == 0 || d.activo() >= d.pasivo() * 2 ? new Color(22, 140, 84) : (d.activo() >= d.pasivo() ? new Color(196, 98, 16) : new Color(190, 50, 44))));
        espacio(doc, 8);

        PdfPTable res = new PdfPTable(4);
        res.setWidthPercentage(100);
        resumen(res, fu, "PATRIMONIO NETO", gs(pat), pat >= 0 ? VERDE : ROJO);
        resumen(res, fu, "TOTAL ACTIVO", gs(d.activo()), NAVY);
        resumen(res, fu, "TOTAL PASIVO", gs(d.pasivo()), new Color(196, 98, 16));
        resumen(res, fu, "GANANCIA POTENCIAL", gs(d.gananciaPotencial()), d.gananciaPotencial() >= 0 ? VERDE : ROJO);
        doc.add(res);
        espacio(doc, 10);

        // activo y pasivo lado a lado
        seccion(doc, fu, "SITUACIÓN PATRIMONIAL", "Activo = lo que tenemos · Pasivo = lo que debemos · Patrimonio = activo menos pasivo");
        PdfPTable t = new PdfPTable(new float[]{3f, 1.6f, 3f, 1.6f});
        t.setWidthPercentage(100);
        t.addCell(celda("ACTIVO", fu.cab, Element.ALIGN_LEFT, NAVY));
        t.addCell(celda("", fu.cab, Element.ALIGN_RIGHT, NAVY));
        t.addCell(celda("PASIVO", fu.cab, Element.ALIGN_LEFT, NAVY));
        t.addCell(celda("", fu.cab, Element.ALIGN_RIGHT, NAVY));
        t.addCell(celda("Mercadería (a costo)", fu.txt, Element.ALIGN_LEFT, Color.WHITE));
        t.addCell(celda(gs(d.mercCosto), fu.txt, Element.ALIGN_RIGHT, Color.WHITE));
        t.addCell(celda("Cuentas por pagar (proveedores)", fu.txt, Element.ALIGN_LEFT, Color.WHITE));
        t.addCell(celda(gs(d.porPagar), fu.txt, Element.ALIGN_RIGHT, Color.WHITE));
        t.addCell(celda("Cuentas por cobrar (clientes)", fu.txt, Element.ALIGN_LEFT, ZEBRA));
        t.addCell(celda(gs(d.porCobrar), fu.txt, Element.ALIGN_RIGHT, ZEBRA));
        t.addCell(celda("  de los cuales vencido", fu.chico, Element.ALIGN_LEFT, ZEBRA));
        t.addCell(celda(gs(d.porPagarVencido), new Font(fu.bf, 9.5f, Font.NORMAL, d.porPagarVencido > 0 ? ROJO : Color.BLACK), Element.ALIGN_RIGHT, ZEBRA));
        t.addCell(celda("TOTAL ACTIVO", fu.txtB, Element.ALIGN_LEFT, new Color(231, 238, 246)));
        t.addCell(celda(gs(d.activo()), fu.txtB, Element.ALIGN_RIGHT, new Color(231, 238, 246)));
        t.addCell(celda("TOTAL PASIVO", fu.txtB, Element.ALIGN_LEFT, new Color(231, 238, 246)));
        t.addCell(celda(gs(d.pasivo()), fu.txtB, Element.ALIGN_RIGHT, new Color(231, 238, 246)));
        PdfPCell p1 = celda("PATRIMONIO NETO (activo - pasivo)", new Font(fu.bfB, 10, Font.NORMAL, Color.WHITE), Element.ALIGN_LEFT, NAVY);
        p1.setColspan(3);
        t.addCell(p1);
        t.addCell(celda(gs(pat), new Font(fu.bfB, 10, Font.NORMAL, Color.WHITE), Element.ALIGN_RIGHT, NAVY));
        doc.add(t);
        espacio(doc, 10);

        // indicadores
        seccion(doc, fu, "INDICADORES", null);
        PdfPTable ind = new PdfPTable(new float[]{2.6f, 1.6f, 4f});
        ind.setWidthPercentage(100);
        String[][] fi = {
            {"Capital de trabajo", gs(pat), "Lo que queda del activo después de pagar todas las deudas"},
            {"Activo / pasivo", d.pasivo() > 0 ? new DecimalFormat("0.0#", SIMBOLOS).format(d.razon()) + " veces" : "Sin deudas", d.pasivo() > 0 ? (d.razon() >= 1 ? "El activo cubre las deudas" : "Las deudas superan al activo") : "No se debe a proveedores"},
            {"Endeudamiento", pct(d.endeudamiento()), "Parte del activo que está comprometida con deudas"},
            {"Cobrar menos pagar", gs(d.cobrarMenosPagar()), d.porPagar > 0 ? "Lo que nos deben cubre el " + pct(d.cobertura()) + " de lo que debemos" : "No hay deuda con proveedores"},
            {"Ganancia potencial del stock", gs(d.gananciaPotencial()), pct(d.margenPotencial()) + " sobre el costo (venta " + gs(d.mercVenta) + " menos costo " + gs(d.mercCosto) + ")"}};
        int fz = 0;
        for (String[] r : fi) {
            Color fondo = fz++ % 2 == 0 ? Color.WHITE : ZEBRA;
            ind.addCell(celda(r[0], fu.txtB, Element.ALIGN_LEFT, fondo));
            ind.addCell(celda(r[1], fu.txt, Element.ALIGN_RIGHT, fondo));
            ind.addCell(celda(r[2], fu.chico, Element.ALIGN_LEFT, fondo));
        }
        doc.add(ind);
        espacio(doc, 10);

        // mercadería
        seccion(doc, fu, "MERCADERÍA", d.conStock + " productos con stock de " + d.productos + " · " + cant(d.unidades) + " unidades · " + d.sinStock + " sin stock · " + d.stockBajo + " con stock bajo (hasta " + (int) BalanceGeneralDatos.STOCK_BAJO + ")");
        grupos(doc, fu, "TIPO", d.porTipo, d.mercCosto, 15);
        espacio(doc, 6);
        grupos(doc, fu, "MARCA", d.porMarca, d.mercCosto, 15);
        espacio(doc, 10);

        // clientes
        seccion(doc, fu, "LO QUE NOS DEBEN (CLIENTES)", d.clientes.size() + (d.clientes.size() == 1 ? " cliente" : " clientes") + " · " + gs(d.porCobrar) + " · vencido " + gs(d.porCobrarVencido));
        deudores(doc, fu, d.clientes, null);
        espacio(doc, 10);
        seccion(doc, fu, "LO QUE DEBEMOS (PROVEEDORES)", d.proveedores.size() + (d.proveedores.size() == 1 ? " proveedor" : " proveedores") + " · " + gs(d.porPagar) + " · vencido " + gs(d.porPagarVencido));
        deudores(doc, fu, null, d.proveedores);
        if (!d.reponer.isEmpty()) {
            espacio(doc, 10);
            seccion(doc, fu, "A REPONER", "Productos sin stock o con stock bajo (los primeros 40)");
            PdfPTable r = new PdfPTable(new float[]{0.8f, 3.4f, 1.6f, 1.2f, 1.4f, 1.4f});
            r.setWidthPercentage(100);
            r.setHeaderRows(1);
            String[] cab = {"CÓD.", "PRODUCTO", "TIPO", "STOCK", "COSTO", "VENTA"};
            for (int i = 0; i < cab.length; i++) {
                r.addCell(celda(cab[i], fu.cab, i >= 3 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT, NAVY));
            }
            int n = 0;
            for (BalanceGeneralDatos.Reponer p : d.reponer) {
                if (n >= 40) {
                    break;
                }
                Color fondo = n++ % 2 == 0 ? Color.WHITE : ZEBRA;
                r.addCell(celda(p.cod, fu.txt, Element.ALIGN_LEFT, fondo));
                r.addCell(celda(p.nombre, fu.txt, Element.ALIGN_LEFT, fondo));
                r.addCell(celda(p.tipo, fu.txt, Element.ALIGN_LEFT, fondo));
                r.addCell(celda(cant(p.stock), new Font(fu.bfB, 9.5f, Font.NORMAL, p.stock <= 0 ? ROJO : AMBAR), Element.ALIGN_RIGHT, fondo));
                r.addCell(celda(gs(p.costo), fu.txt, Element.ALIGN_RIGHT, fondo));
                r.addCell(celda(gs(p.venta), fu.txt, Element.ALIGN_RIGHT, fondo));
            }
            doc.add(r);
        }
        if (!d.avisos.isEmpty()) {
            espacio(doc, 8);
            for (String a : d.avisos) {
                doc.add(new Paragraph("Aviso: " + a, fu.chico));
            }
        }
        doc.close();
        return f;
    }

    private static void grupos(Document doc, Fuentes fu, String titulo, List<BalanceGeneralDatos.Grupo> l, long total, int max) throws DocumentException {
        PdfPTable g = new PdfPTable(new float[]{3.2f, 1f, 1.3f, 1.7f, 1.7f, 0.9f});
        g.setWidthPercentage(100);
        g.setHeaderRows(1);
        String[] cab = {titulo, "PROD.", "UNIDADES", "A COSTO", "PRECIO DE VENTA", "% COSTO"};
        for (int i = 0; i < cab.length; i++) {
            g.addCell(celda(cab[i], fu.cab, i >= 1 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT, NAVY));
        }
        int n = 0;
        long resto = 0, restoV = 0;
        for (BalanceGeneralDatos.Grupo x : l) {
            if (n >= max) {
                resto += x.costo;
                restoV += x.venta;
                continue;
            }
            Color fondo = n++ % 2 == 0 ? Color.WHITE : ZEBRA;
            g.addCell(celda(x.nombre, fu.txt, Element.ALIGN_LEFT, fondo));
            g.addCell(celda(String.valueOf(x.productos), fu.txt, Element.ALIGN_RIGHT, fondo));
            g.addCell(celda(cant(x.unidades), fu.txt, Element.ALIGN_RIGHT, fondo));
            g.addCell(celda(gs(x.costo), fu.txtB, Element.ALIGN_RIGHT, fondo));
            g.addCell(celda(gs(x.venta), fu.txt, Element.ALIGN_RIGHT, fondo));
            g.addCell(celda(total > 0 ? pct(x.costo * 100.0 / total) : "-", fu.txt, Element.ALIGN_RIGHT, fondo));
        }
        if (resto > 0 || restoV > 0) {
            g.addCell(fila2("Otros (" + (l.size() - max) + ")", fu.chico, Element.ALIGN_LEFT, null, 3));
            g.addCell(celda(gs(resto), fu.txt, Element.ALIGN_RIGHT, null));
            g.addCell(celda(gs(restoV), fu.txt, Element.ALIGN_RIGHT, null));
            g.addCell(celda(total > 0 ? pct(resto * 100.0 / total) : "-", fu.txt, Element.ALIGN_RIGHT, null));
        }
        if (l.isEmpty()) {
            g.addCell(fila2("Sin mercadería con stock.", fu.chico, Element.ALIGN_LEFT, null, 6));
        }
        doc.add(g);
    }

    private static void deudores(Document doc, Fuentes fu, List<CuotasDb.Resumen> clientes, List<CuentaProvDb.Resumen> proveedores) throws DocumentException {
        PdfPTable t = new PdfPTable(new float[]{0.7f, 3.4f, 1.6f, 1.6f, 1.4f, 1.4f});
        t.setWidthPercentage(100);
        t.setHeaderRows(1);
        String[] cab = {"CÓD.", clientes != null ? "CLIENTE" : "PROVEEDOR", clientes != null ? "NOS DEBE" : "LE DEBEMOS", "VENCIDO", "PRÓX. VENC.", "ESTADO"};
        for (int i = 0; i < cab.length; i++) {
            t.addCell(celda(cab[i], fu.cab, i == 2 || i == 3 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT, NAVY));
        }
        int n = 0;
        int total = clientes != null ? clientes.size() : proveedores.size();
        for (int i = 0; i < total && i < 30; i++) {
            String id, nombre;
            long deuda, vencido;
            Date prox;
            int atraso;
            if (clientes != null) {
                CuotasDb.Resumen r = clientes.get(i);
                id = r.id;
                nombre = r.nombre;
                deuda = r.deuda;
                vencido = r.vencido;
                prox = r.proximo;
                atraso = r.diasAtraso;
            } else {
                CuentaProvDb.Resumen r = proveedores.get(i);
                id = r.id;
                nombre = r.nombre;
                deuda = r.deuda;
                vencido = r.vencido;
                prox = r.proximo;
                atraso = r.diasAtraso;
            }
            Color fondo = n++ % 2 == 0 ? Color.WHITE : ZEBRA;
            t.addCell(celda(id, fu.txt, Element.ALIGN_LEFT, fondo));
            t.addCell(celda(nombre, fu.txt, Element.ALIGN_LEFT, fondo));
            t.addCell(celda(gs(deuda), fu.txtB, Element.ALIGN_RIGHT, fondo));
            t.addCell(celda(gs(vencido), new Font(fu.bf, 9.5f, Font.NORMAL, vencido > 0 ? ROJO : Color.BLACK), Element.ALIGN_RIGHT, fondo));
            t.addCell(celda(prox == null ? "-" : dia(prox), fu.txt, Element.ALIGN_LEFT, fondo));
            t.addCell(celda(vencido > 0 ? "Vencida (" + atraso + " d)" : (prox != null ? "En plazo" : "Sin cuotas"), new Font(fu.bfB, 8.5f, Font.NORMAL, vencido > 0 ? ROJO : VERDE),
                    Element.ALIGN_LEFT, vencido > 0 ? ROJO_FONDO : fondo));
        }
        if (total == 0) {
            PdfPCell v = celda("Sin deudas pendientes.", fu.chico, Element.ALIGN_LEFT, null);
            v.setColspan(6);
            t.addCell(v);
        } else if (total > 30) {
            PdfPCell v = celda("... y " + (total - 30) + " más (ver en pantalla).", fu.chico, Element.ALIGN_LEFT, null);
            v.setColspan(6);
            t.addCell(v);
        }
        doc.add(t);
    }

    private static void resumen(PdfPTable t, Fuentes fu, String titulo, String valor, Color color) {
        Phrase p = new Phrase();
        p.add(new Chunk(titulo + "\n", fu.chicoB));
        p.add(new Chunk(valor, new Font(fu.bfB, 11, Font.NORMAL, color)));
        PdfPCell c = new PdfPCell(p);
        c.setBorderColor(GRIS_BORDE);
        c.setBorderWidth(0.5f);
        c.setPadding(7);
        c.setLeading(0, 1.3f);
        t.addCell(c);
    }
}

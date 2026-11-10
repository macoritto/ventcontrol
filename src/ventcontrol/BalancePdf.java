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
import java.util.Map;
import javax.swing.JOptionPane;

/** PDF A4 del balance de un período: resumen, comparación con el período anterior, medios de pago y el detalle. */
final class BalancePdf {

    private static final Color NAVY = new Color(16, 44, 72);
    private static final Color AZUL = new Color(0, 132, 190);
    private static final Color GRIS_BORDE = new Color(214, 219, 226);
    private static final Color GRIS_TEXTO = new Color(96, 106, 118);
    private static final Color ZEBRA = new Color(246, 248, 251);
    private static final Color TOTAL = new Color(226, 232, 240);
    private static final Color VERDE = new Color(22, 120, 70);
    private static final Color ROJO = new Color(176, 38, 38);
    private static final Color NARANJA = new Color(196, 98, 16);

    private static final DecimalFormat FMT;

    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setGroupingSeparator('.');
        s.setDecimalSeparator(',');
        FMT = new DecimalFormat("#,##0", s);
    }

    private BalancePdf() {
    }

    private static String gs(long v) {
        return (v < 0 ? "- " : "") + "Gs. " + FMT.format(Math.abs(v));
    }

    private static String num(long v) {
        return FMT.format(v);
    }

    private static String periodo(Date d, Date h) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        return BalanceDatos.iso(d).equals(BalanceDatos.iso(h)) ? f.format(d) : f.format(d) + " al " + f.format(h);
    }

    static void mostrar(Component padre, BalanceDatos d) {
        try {
            File f = generar(d);
            try {
                java.awt.Desktop.getDesktop().open(f);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(padre, "El balance se guardó en:\n" + f.getAbsolutePath(), "Balance", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el PDF:\n" + e.getMessage(), "Balance", JOptionPane.ERROR_MESSAGE);
        }
    }

    static File generar(BalanceDatos d) throws Exception {
        File f = File.createTempFile("Balance_" + BalanceDatos.iso(d.desde) + "_" + BalanceDatos.iso(d.hasta) + "_", ".pdf");
        f.deleteOnExit();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        BaseFont bfB = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font txt = new Font(bf, 9.5f, Font.NORMAL, Color.BLACK);
        Font txtB = new Font(bfB, 9.5f, Font.NORMAL, Color.BLACK);
        Font chico = new Font(bf, 8.5f, Font.NORMAL, GRIS_TEXTO);
        Font chicoB = new Font(bfB, 8.5f, Font.NORMAL, GRIS_TEXTO);
        Font cab = new Font(bfB, 8.5f, Font.NORMAL, Color.WHITE);

        Document doc = new Document(PageSize.A4, 40, 40, 36, 52);
        PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream(f));
        w.setPageEvent(new Pie(bf, "Emitido el " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), "Balance"));
        doc.open();
        BalanceDatos.Tot a = d.actual, p = d.previo;

        // banda
        PdfPTable banda = new PdfPTable(new float[]{3f, 1.5f});
        banda.setWidthPercentage(100);
        Phrase ph = new Phrase();
        ph.add(new Chunk("BALANCE\n", new Font(bfB, 17, Font.NORMAL, Color.WHITE)));
        ph.add(new Chunk("Del " + periodo(d.desde, d.hasta), new Font(bf, 10.5f, Font.NORMAL, new Color(205, 224, 242))));
        PdfPCell c = new PdfPCell(ph);
        c.setBackgroundColor(NAVY);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(12);
        c.setPaddingLeft(16);
        c.setLeading(0, 1.25f);
        banda.addCell(c);
        PdfPCell e = new PdfPCell(new Phrase(a.resultado() >= 0 ? "RESULTADO POSITIVO" : "RESULTADO NEGATIVO", new Font(bfB, 10, Font.NORMAL, Color.WHITE)));
        e.setBackgroundColor(a.resultado() >= 0 ? VERDE : ROJO);
        e.setBorder(Rectangle.NO_BORDER);
        e.setHorizontalAlignment(Element.ALIGN_CENTER);
        e.setVerticalAlignment(Element.ALIGN_MIDDLE);
        banda.addCell(e);
        doc.add(banda);
        espacio(doc, 8);

        // tarjetas
        PdfPTable k = new PdfPTable(4);
        k.setWidthPercentage(100);
        tarjeta(k, bfB, chicoB, "VENTAS", gs(a.ventas), NAVY, variacion(a.ventas, p.ventas, false), chico);
        tarjeta(k, bfB, chicoB, "GANANCIA BRUTA", gs(a.ganancia()), VERDE, "Margen " + BalanceDatos.pct(a.margen10()), chico);
        tarjeta(k, bfB, chicoB, "GASTOS", gs(a.gastos), NARANJA, variacion(a.gastos, p.gastos, true), chico);
        tarjeta(k, bfB, chicoB, "RESULTADO", gs(a.resultado()), a.resultado() < 0 ? ROJO : VERDE, variacion(a.resultado(), p.resultado(), false), chico);
        doc.add(k);
        espacio(doc, 4);
        PdfPTable k2 = new PdfPTable(4);
        k2.setWidthPercentage(100);
        tarjeta(k2, bfB, chicoB, "NÚMERO DE VENTAS", String.valueOf(a.nVentas), NAVY, "Ticket promedio " + gs(a.ticket()), chico);
        tarjeta(k2, bfB, chicoB, "AL CONTADO", gs(a.contado), NAVY, "A crédito " + gs(a.credito), chico);
        tarjeta(k2, bfB, chicoB, "PAGOS DE CLIENTES", gs(a.cobros), NAVY, a.nCobros + (a.nCobros == 1 ? " pago" : " pagos"), chico);
        tarjeta(k2, bfB, chicoB, "TOTAL COBRADO", gs(a.cobrado()), NAVY, "Contado + pagos de clientes", chico);
        doc.add(k2);
        espacio(doc, 4);
        PdfPTable k3 = new PdfPTable(4);
        k3.setWidthPercentage(100);
        tarjeta(k3, bfB, chicoB, "DEVOLUCIONES", gs(a.devoluciones), NAVY, "", chico);
        tarjeta(k3, bfB, chicoB, "COMPRAS DEL PERÍODO", gs(a.compras), NAVY, a.nCompras + (a.nCompras == 1 ? " compra" : " compras") + " (informativo)", chico);
        tarjeta(k3, bfB, chicoB, "COSTO DE LO VENDIDO", gs(a.costo), NAVY, "", chico);
        tarjeta(k3, bfB, chicoB, "POR COBRAR (HOY)", d.hayDeuda ? gs(d.deudaClientes) : "-", NAVY, "", chico);
        doc.add(k3);
        Paragraph nota = new Paragraph("Comparado con el período anterior (" + periodo(d.previoDesde, d.previoHasta) + "). Resultado = ganancia bruta - gastos.", chico);
        nota.setSpacingBefore(5);
        doc.add(nota);
        espacio(doc, 8);

        // medios de pago
        if (!d.porMedio.isEmpty()) {
            seccion(doc, bfB, bf, "VENTAS POR MEDIO DE PAGO", "");
            PdfPTable t = new PdfPTable(new float[]{2.2f, 0.9f, 1.6f, 1f});
            t.setWidthPercentage(100);
            String[] ch = {"MEDIO", "VENTAS", "TOTAL", "%"};
            for (int i = 0; i < ch.length; i++) {
                t.addCell(celda(ch[i], cab, i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT, NAVY));
            }
            long suma = 0, cant = 0;
            for (long[] v : d.porMedio.values()) {
                suma += v[0];
                cant += v[1];
            }
            int fila = 0;
            for (Map.Entry<String, long[]> en : d.porMedio.entrySet()) {
                Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
                t.addCell(celda(en.getKey(), txt, Element.ALIGN_LEFT, fondo));
                t.addCell(celda(String.valueOf(en.getValue()[1]), txt, Element.ALIGN_RIGHT, fondo));
                t.addCell(celda(gs(en.getValue()[0]), txt, Element.ALIGN_RIGHT, fondo));
                t.addCell(celda(suma <= 0 ? "-" : BalanceDatos.pct(en.getValue()[0] * 1000.0 / suma), txt, Element.ALIGN_RIGHT, fondo));
            }
            t.addCell(celda("TOTAL", txtB, Element.ALIGN_LEFT, TOTAL));
            t.addCell(celda(String.valueOf(cant), txtB, Element.ALIGN_RIGHT, TOTAL));
            t.addCell(celda(gs(suma), txtB, Element.ALIGN_RIGHT, TOTAL));
            t.addCell(celda("", txtB, Element.ALIGN_RIGHT, TOTAL));
            doc.add(t);
            espacio(doc, 10);
        }

        // ventas por día
        seccion(doc, bfB, bf, "VENTAS Y RESULTADO POR DÍA", "");
        PdfPTable tv = new PdfPTable(new float[]{1.2f, 0.95f, 1.5f, 1.3f, 1.4f, 1.05f, 1.25f, 1.4f});
        tv.setWidthPercentage(100);
        tv.setHeaderRows(1);
        String[] cv = {"FECHA", "VENTAS", "TOTAL VENDIDO", "COSTO", "GANANCIA", "MARGEN", "GASTOS", "RESULTADO"};
        for (int i = 0; i < cv.length; i++) {
            tv.addCell(celda(cv[i], cab, i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT, NAVY));
        }
        int fila = 0;
        for (String key : d.dias.keySet()) {
            long[] v = d.dias.get(key);
            if (v[BalanceDatos.D_N] == 0 && v[BalanceDatos.D_GASTOS] == 0) {
                continue;
            }
            Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
            long res = v[BalanceDatos.D_GANANCIA] - v[BalanceDatos.D_GASTOS];
            tv.addCell(celda(BalanceDatos.dmy(key), txt, Element.ALIGN_LEFT, fondo));
            tv.addCell(celda(String.valueOf(v[BalanceDatos.D_N]), txt, Element.ALIGN_RIGHT, fondo));
            tv.addCell(celda(num(v[BalanceDatos.D_VENTAS]), txt, Element.ALIGN_RIGHT, fondo));
            tv.addCell(celda(num(v[BalanceDatos.D_COSTO]), txt, Element.ALIGN_RIGHT, fondo));
            tv.addCell(celda(num(v[BalanceDatos.D_GANANCIA]), txt, Element.ALIGN_RIGHT, fondo));
            tv.addCell(celda(v[BalanceDatos.D_VENTAS] <= 0 ? "-" : BalanceDatos.pct(v[BalanceDatos.D_GANANCIA] * 1000.0 / v[BalanceDatos.D_VENTAS]), txt, Element.ALIGN_RIGHT, fondo));
            tv.addCell(celda(num(v[BalanceDatos.D_GASTOS]), txt, Element.ALIGN_RIGHT, fondo));
            tv.addCell(celda(num(res), new Font(bfB, 9.5f, Font.NORMAL, res < 0 ? ROJO : Color.BLACK), Element.ALIGN_RIGHT, fondo));
        }
        tv.addCell(celda("TOTAL", txtB, Element.ALIGN_LEFT, TOTAL));
        tv.addCell(celda(String.valueOf(a.nVentas), txtB, Element.ALIGN_RIGHT, TOTAL));
        tv.addCell(celda(num(a.ventas), txtB, Element.ALIGN_RIGHT, TOTAL));
        tv.addCell(celda(num(a.costo), txtB, Element.ALIGN_RIGHT, TOTAL));
        tv.addCell(celda(num(a.ganancia()), txtB, Element.ALIGN_RIGHT, TOTAL));
        tv.addCell(celda(a.ventas <= 0 ? "-" : BalanceDatos.pct(a.margen10()), txtB, Element.ALIGN_RIGHT, TOTAL));
        tv.addCell(celda(num(a.gastos), txtB, Element.ALIGN_RIGHT, TOTAL));
        tv.addCell(celda(num(a.resultado()), new Font(bfB, 9.5f, Font.NORMAL, a.resultado() < 0 ? ROJO : Color.BLACK), Element.ALIGN_RIGHT, TOTAL));
        doc.add(tv);
        espacio(doc, 10);

        // gastos por concepto
        if (!d.gastosPorConcepto.isEmpty()) {
            seccion(doc, bfB, bf, "GASTOS POR CONCEPTO", d.gastos.size() + " gastos");
            PdfPTable tg = new PdfPTable(new float[]{3.5f, 0.7f, 1.6f, 0.9f});
            tg.setWidthPercentage(100);
            tg.setHeaderRows(1);
            String[] cg = {"CONCEPTO", "Nº", "TOTAL", "%"};
            for (int i = 0; i < cg.length; i++) {
                tg.addCell(celda(cg[i], cab, i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT, NAVY));
            }
            fila = 0;
            for (String[] g : d.gastosPorConcepto) {
                Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
                long tot = Long.parseLong(g[2]);
                tg.addCell(celda(g[0], txt, Element.ALIGN_LEFT, fondo));
                tg.addCell(celda(g[1], txt, Element.ALIGN_RIGHT, fondo));
                tg.addCell(celda(gs(tot), txt, Element.ALIGN_RIGHT, fondo));
                tg.addCell(celda(a.gastos <= 0 ? "-" : BalanceDatos.pct(tot * 1000.0 / a.gastos), txt, Element.ALIGN_RIGHT, fondo));
            }
            doc.add(tg);
            espacio(doc, 10);
        }

        // productos
        if (!d.productos.isEmpty()) {
            seccion(doc, bfB, bf, "PRODUCTOS MÁS VENDIDOS", "los " + d.productos.size() + " de mayor venta");
            PdfPTable tp = new PdfPTable(new float[]{3.6f, 0.9f, 1.5f, 1.5f, 0.9f});
            tp.setWidthPercentage(100);
            tp.setHeaderRows(1);
            String[] cp = {"PRODUCTO", "CANT.", "VENDIDO", "GANANCIA", "MARGEN"};
            for (int i = 0; i < cp.length; i++) {
                tp.addCell(celda(cp[i], cab, i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT, NAVY));
            }
            fila = 0;
            for (String[] r : d.productos) {
                Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
                tp.addCell(celda(r[0], txt, Element.ALIGN_LEFT, fondo));
                tp.addCell(celda(r[1], txt, Element.ALIGN_RIGHT, fondo));
                tp.addCell(celda(num(Long.parseLong(r[2])), txt, Element.ALIGN_RIGHT, fondo));
                tp.addCell(celda(num(Long.parseLong(r[3])), txt, Element.ALIGN_RIGHT, fondo));
                tp.addCell(celda(r[4], txt, Element.ALIGN_RIGHT, fondo));
            }
            doc.add(tp);
            espacio(doc, 10);
        }

        // clientes
        if (!d.clientes.isEmpty()) {
            seccion(doc, bfB, bf, "MEJORES CLIENTES", "");
            PdfPTable tc = new PdfPTable(new float[]{3.6f, 0.8f, 1.7f, 1f});
            tc.setWidthPercentage(100);
            tc.setHeaderRows(1);
            String[] cc = {"CLIENTE", "VENTAS", "TOTAL COMPRADO", "% VENTAS"};
            for (int i = 0; i < cc.length; i++) {
                tc.addCell(celda(cc[i], cab, i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT, NAVY));
            }
            fila = 0;
            for (String[] r : d.clientes) {
                Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
                long tot = Long.parseLong(r[2]);
                tc.addCell(celda(r[0], txt, Element.ALIGN_LEFT, fondo));
                tc.addCell(celda(r[1], txt, Element.ALIGN_RIGHT, fondo));
                tc.addCell(celda(num(tot), txt, Element.ALIGN_RIGHT, fondo));
                tc.addCell(celda(a.ventas <= 0 ? "-" : BalanceDatos.pct(tot * 1000.0 / a.ventas), txt, Element.ALIGN_RIGHT, fondo));
            }
            doc.add(tc);
        }
        doc.close();
        return f;
    }

    private static String variacion(long ahora, long antes, boolean subirEsMalo) {
        Double v = BalanceDatos.variacion(ahora, antes);
        if (v == null) {
            return ahora == 0 ? "sin movimientos" : "sin datos del período anterior";
        }
        String signo = v > 0.05 ? "+" : (v < -0.05 ? "-" : "");
        return signo + String.format(java.util.Locale.US, "%.1f", Math.abs(v)).replace('.', ',') + " % vs. anterior";
    }

    private static void tarjeta(PdfPTable t, BaseFont bfB, Font chicoB, String titulo, String valor, Color color, String pie, Font chico) {
        Phrase p = new Phrase();
        p.add(new Chunk(titulo + "\n", chicoB));
        p.add(new Chunk(valor + "\n", new Font(bfB, 11, Font.NORMAL, color)));
        p.add(new Chunk(pie, chico));
        PdfPCell c = new PdfPCell(p);
        c.setBorderColor(GRIS_BORDE);
        c.setBorderWidth(0.5f);
        c.setPadding(7);
        c.setLeading(0, 1.3f);
        t.addCell(c);
    }

    private static void espacio(Document doc, float pt) throws DocumentException {
        Paragraph p = new Paragraph(" ");
        p.setLeading(pt);
        doc.add(p);
    }

    private static void seccion(Document doc, BaseFont bfB, BaseFont bf, String titulo, String detalle) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        Phrase ph = new Phrase();
        ph.add(new Chunk(titulo, new Font(bfB, 11, Font.NORMAL, NAVY)));
        if (detalle != null && detalle.length() > 0) {
            ph.add(new Chunk("   " + detalle, new Font(bf, 8.5f, Font.NORMAL, GRIS_TEXTO)));
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
}

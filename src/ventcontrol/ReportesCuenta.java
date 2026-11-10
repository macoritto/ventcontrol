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
 * PDF A4 de la cuenta de un cliente: el extracto (resumen, cronograma de cuotas y movimientos con saldo) y el pagaré
 * de un plan de cuotas. Se escribe a un archivo temporal y se abre con el visor de PDF del equipo.
 */
final class ReportesCuenta {

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

    private ReportesCuenta() {
    }

    private static String gs(long v) {
        return (v < 0 ? "- " : "") + "Gs. " + FMT.format(Math.abs(v));
    }

    private static String dia(Date d) {
        return d == null ? "" : DIA.format(d);
    }

    // ------------------------------------------------------------------ API

    static void extracto(Component padre, String clienteId) {
        try {
            abrir(padre, extractoArchivo(clienteId));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el extracto:\n" + e.getMessage(), "Extracto", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void pagare(Component padre, int planId) {
        try {
            abrir(padre, pagareArchivo(planId));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el pagaré:\n" + e.getMessage(), "Pagaré", JOptionPane.ERROR_MESSAGE);
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

    // ------------------------------------------------------------------ extracto

    static File extractoArchivo(String clienteId) throws Exception {
        String[] cli = CuotasDb.cliente(clienteId);
        long deuda = CuotasDb.deuda(clienteId);
        List<CuotasDb.Cuota> cuotas = CuotasDb.cuotas(clienteId);
        List<CuotasDb.Movimiento> movs = CuotasDb.movimientos(clienteId);
        CuotasDb.Cierre cierre = CuotasDb.cierreVigente(clienteId);

        File f = File.createTempFile("Extracto_cliente_" + clienteId + "_", ".pdf");
        f.deleteOnExit();
        Fuentes fu = new Fuentes();
        Document doc = abrirDoc(f, fu, "Extracto de cuenta");

        doc.add(banda(fu, "EXTRACTO DE CUENTA", cli[0].length() == 0 ? "Cliente " + clienteId : cli[0],
                cierre != null ? "CUENTA CERRADA" : (deuda > 0 ? "CON DEUDA" : "AL DÍA"),
                cierre != null ? new Color(84, 98, 114) : (deuda > 0 ? new Color(196, 98, 16) : new Color(22, 140, 84))));
        espacio(doc, 8);

        PdfPTable info = new PdfPTable(new float[]{1f, 2f, 1f, 2f});
        info.setWidthPercentage(100);
        info.addCell(sinBorde(new Phrase("CLIENTE", fu.chicoB)));
        info.addCell(sinBorde(new Phrase(cli[0] + "  (Cód. " + clienteId + ")", fu.txt)));
        info.addCell(sinBorde(new Phrase("TELÉFONO", fu.chicoB)));
        info.addCell(sinBorde(new Phrase(cli[1], fu.txt)));
        info.addCell(sinBorde(new Phrase("CI / RUC", fu.chicoB)));
        info.addCell(sinBorde(new Phrase(cli[2], fu.txt)));
        info.addCell(sinBorde(new Phrase("EMITIDO", fu.chicoB)));
        info.addCell(sinBorde(new Phrase(dia(new Date()), fu.txt)));
        doc.add(info);
        espacio(doc, 10);

        // resumen
        long vencido = 0, porVencer = 0;
        Date proximo = null;
        long proximoMonto = 0;
        for (CuotasDb.Cuota c : cuotas) {
            String e = c.estado();
            if ("Vencida".equals(e)) {
                vencido += c.saldo();
            } else if ("Pendiente".equals(e)) {
                porVencer += c.saldo();
                if (proximo == null) {
                    proximo = c.vencimiento;
                    proximoMonto = c.saldo();
                }
            }
        }
        PdfPTable res = new PdfPTable(4);
        res.setWidthPercentage(100);
        resumen(res, fu, "DEUDA TOTAL", gs(Math.max(0, deuda)), deuda > 0 ? new Color(196, 98, 16) : VERDE);
        resumen(res, fu, "CUOTAS VENCIDAS", gs(vencido), vencido > 0 ? ROJO : VERDE);
        resumen(res, fu, "CUOTAS POR VENCER", gs(porVencer), NAVY);
        resumen(res, fu, "PRÓXIMO VENCIMIENTO", proximo == null ? "-" : dia(proximo), NAVY);
        doc.add(res);
        if (cierre != null) {
            Paragraph pc = new Paragraph("Cuenta cerrada el " + cierre.fecha + (cierre.usuario.length() > 0 ? " por " + cierre.usuario : "")
                    + (cierre.condonado > 0 ? " · saldo condonado " + gs(cierre.condonado) : " · sin saldo pendiente")
                    + (cierre.motivo.length() > 0 ? " · " + cierre.motivo : ""), fu.chico);
            pc.setSpacingBefore(4);
            doc.add(pc);
        }
        espacio(doc, 10);

        // cronograma
        if (!cuotas.isEmpty()) {
            seccion(doc, fu, "CRONOGRAMA DE CUOTAS", "Los cobros se aplican primero a la cuota más antigua");
            PdfPTable t = new PdfPTable(new float[]{1f, 0.9f, 1.3f, 1.4f, 1.4f, 1.4f, 1.2f});
            t.setWidthPercentage(100);
            t.setHeaderRows(1);
            String[] cab = {"VENTA", "CUOTA", "VENCE", "MONTO", "PAGADO", "SALDO", "ESTADO"};
            for (int i = 0; i < cab.length; i++) {
                t.addCell(celda(cab[i], fu.cab, i >= 3 && i <= 5 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT, NAVY));
            }
            int fila = 0;
            for (CuotasDb.Cuota c : cuotas) {
                Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
                String e = c.estado();
                Color ce = "Vencida".equals(e) ? ROJO : ("Pagada".equals(e) ? VERDE : ("Condonada".equals(e) ? GRIS_TEXTO : AMBAR));
                t.addCell(celda("Nº " + c.ventaCod, fu.txt, Element.ALIGN_LEFT, fondo));
                t.addCell(celda(c.nro + " de " + c.nTotal, fu.txt, Element.ALIGN_LEFT, fondo));
                t.addCell(celda(dia(c.vencimiento), fu.txt, Element.ALIGN_LEFT, fondo));
                t.addCell(celda(gs(c.monto), fu.txt, Element.ALIGN_RIGHT, fondo));
                t.addCell(celda(gs(c.pagado), fu.txt, Element.ALIGN_RIGHT, fondo));
                t.addCell(celda(gs(c.saldo()), fu.txtB, Element.ALIGN_RIGHT, fondo));
                t.addCell(celda(e + (c.diasAtraso() > 0 ? " (" + c.diasAtraso() + " d)" : ""), new Font(fu.bfB, 8.5f, Font.NORMAL, ce),
                        Element.ALIGN_LEFT, "Vencida".equals(e) ? ROJO_FONDO : fondo));
            }
            doc.add(t);
            espacio(doc, 10);
        }

        // movimientos
        seccion(doc, fu, "MOVIMIENTOS", "Compras a crédito suman a la deuda; los pagos la restan");
        PdfPTable m = new PdfPTable(new float[]{1.25f, 3.6f, 1.45f, 1.45f, 1.5f});
        m.setWidthPercentage(100);
        m.setHeaderRows(1);
        String[] cabm = {"FECHA", "DESCRIPCIÓN", "COMPRA A CRÉDITO", "PAGO", "SALDO"};
        for (int i = 0; i < cabm.length; i++) {
            m.addCell(celda(cabm[i], fu.cab, i >= 2 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT, NAVY));
        }
        if (movs.isEmpty()) {
            PdfPCell vacio = celda("Sin movimientos.", fu.chico, Element.ALIGN_LEFT, null);
            vacio.setColspan(5);
            m.addCell(vacio);
        }
        int fila = 0;
        for (CuotasDb.Movimiento mv : movs) {
            Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
            m.addCell(celda(mv.fecha, fu.txt, Element.ALIGN_LEFT, fondo));
            m.addCell(celda(mv.descripcion, fu.txt, Element.ALIGN_LEFT, fondo));
            m.addCell(celda(mv.deuda == 0 ? "" : gs(mv.deuda), fu.txt, Element.ALIGN_RIGHT, fondo));
            m.addCell(celda(mv.pago == 0 ? "" : gs(mv.pago), new Font(fu.bf, 9.5f, Font.NORMAL, VERDE), Element.ALIGN_RIGHT, fondo));
            m.addCell(celda(gs(mv.saldoAcumulado), fu.txtB, Element.ALIGN_RIGHT, fondo));
        }
        PdfPCell tot = celda("DEUDA ACTUAL", new Font(fu.bfB, 10, Font.NORMAL, Color.WHITE), Element.ALIGN_LEFT, NAVY);
        tot.setColspan(4);
        m.addCell(tot);
        m.addCell(celda(gs(deuda), new Font(fu.bfB, 10, Font.NORMAL, Color.WHITE), Element.ALIGN_RIGHT, NAVY));
        doc.add(m);
        doc.close();
        return f;
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

    // ------------------------------------------------------------------ pagaré

    static File pagareArchivo(int planId) throws Exception {
        CuotasDb.Plan plan = CuotasDb.plan(planId);
        if (plan == null) {
            throw new Exception("No se encontró el plan de cuotas " + planId + ".");
        }
        List<CuotasDb.Cuota> cuotas = CuotasDb.cuotasDePlan(planId);
        String[] cli = CuotasDb.cliente(plan.clienteId);
        long saldo = plan.total - plan.entrega;

        File f = File.createTempFile("Pagare_venta_" + plan.ventaCod + "_", ".pdf");
        f.deleteOnExit();
        Fuentes fu = new Fuentes();
        Document doc = abrirDoc(f, fu, "Pagaré · Venta Nº " + plan.ventaCod);

        doc.add(banda(fu, "PAGARÉ", "Venta a crédito Nº " + plan.ventaCod + "  ·  " + plan.cuotas + " cuotas", "Gs. " + FMT.format(saldo), AZUL));
        espacio(doc, 12);

        String empresa = InventarioReporte.EMPRESA == null ? "" : InventarioReporte.EMPRESA.trim();
        Font fl = new Font(fu.bf, 10.5f, Font.NORMAL, Color.BLACK);
        Font flB = new Font(fu.bfB, 10.5f, Font.NORMAL, Color.BLACK);
        Paragraph texto = new Paragraph();
        texto.setLeading(17f);
        texto.setAlignment(Element.ALIGN_LEFT);
        texto.add(new Chunk("Yo, ", fl));
        texto.add(new Chunk(cli[0].length() == 0 ? "____________________" : cli[0], flB));
        texto.add(new Chunk(cli[2].length() == 0 ? "" : ", con documento Nº " + cli[2], fl));
        texto.add(new Chunk(", me obligo a pagar a la orden de ", fl));
        texto.add(new Chunk(empresa.length() == 0 ? "______________________________" : empresa, flB));
        texto.add(new Chunk(" la suma de GUARANÍES ", fl));
        texto.add(new Chunk(letras(saldo) + " (" + gs(saldo) + ")", flB));
        texto.add(new Chunk(", en " + plan.cuotas + " cuotas, correspondiente al saldo de la venta Nº " + plan.ventaCod
                + " de Gs. " + FMT.format(plan.total) + (plan.entrega > 0 ? ", habiendo entregado Gs. " + FMT.format(plan.entrega) + " al momento de la compra" : "")
                + ", según el siguiente cronograma:", fl));
        doc.add(texto);
        espacio(doc, 10);

        PdfPTable t = new PdfPTable(new float[]{1f, 2f, 2f});
        t.setWidthPercentage(70);
        t.setHeaderRows(1);
        String[] cab = {"CUOTA", "VENCIMIENTO", "MONTO"};
        for (int i = 0; i < cab.length; i++) {
            t.addCell(celda(cab[i], fu.cab, i == 2 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT, NAVY));
        }
        int fila = 0;
        long suma = 0;
        for (CuotasDb.Cuota c : cuotas) {
            Color fondo = fila++ % 2 == 0 ? Color.WHITE : ZEBRA;
            suma += c.monto;
            t.addCell(celda(c.nro + " de " + c.nTotal, fu.txt, Element.ALIGN_LEFT, fondo));
            t.addCell(celda(dia(c.vencimiento), fu.txt, Element.ALIGN_LEFT, fondo));
            t.addCell(celda(gs(c.monto), fu.txt, Element.ALIGN_RIGHT, fondo));
        }
        PdfPCell tt = celda("TOTAL", fu.txtB, Element.ALIGN_LEFT, new Color(226, 232, 240));
        tt.setColspan(2);
        t.addCell(tt);
        t.addCell(celda(gs(suma), fu.txtB, Element.ALIGN_RIGHT, new Color(226, 232, 240)));
        t.setHorizontalAlignment(Element.ALIGN_LEFT);
        doc.add(t);
        espacio(doc, 14);

        Paragraph lugar = new Paragraph("Fecha: " + dia(plan.creado == null ? new Date() : plan.creado) + "          Lugar: ______________________", fl);
        doc.add(lugar);
        espacio(doc, 56);

        PdfPTable firmas = new PdfPTable(new float[]{1f, 0.2f, 1f});
        firmas.setWidthPercentage(100);
        firma(firmas, fu, "Firma del deudor", cli[0].length() == 0 ? "Aclaración" : cli[0], cli[2].length() == 0 ? "CI / RUC:" : "CI / RUC: " + cli[2]);
        PdfPCell sep = new PdfPCell(new Phrase(" "));
        sep.setBorder(Rectangle.NO_BORDER);
        firmas.addCell(sep);
        firma(firmas, fu, "Recibí conforme", empresa.length() == 0 ? "Aclaración" : empresa, "");
        doc.add(firmas);
        espacio(doc, 16);

        PdfPTable nota = new PdfPTable(1);
        nota.setWidthPercentage(100);
        PdfPCell cn = new PdfPCell(new Phrase("Comprobante del plan de cuotas. Los pagos que haga el cliente se aplican a la cuota más antigua pendiente.",
                new Font(fu.bf, 8.5f, Font.NORMAL, AMBAR)));
        cn.setBackgroundColor(AMBAR_FONDO);
        cn.setBorderColor(new Color(240, 214, 140));
        cn.setBorderWidth(0.6f);
        cn.setPadding(8);
        nota.addCell(cn);
        doc.add(nota);
        doc.close();
        return f;
    }

    private static void firma(PdfPTable t, Fuentes fu, String titulo, String linea1, String linea2) {
        Phrase p = new Phrase();
        p.add(new Chunk(titulo + "\n", fu.chicoB));
        p.add(new Chunk(linea1 + "\n", fu.txt));
        p.add(new Chunk(linea2, fu.chico));
        PdfPCell c = new PdfPCell(p);
        c.setBorder(Rectangle.TOP);
        c.setBorderColor(Color.BLACK);
        c.setBorderWidthTop(0.8f);
        c.setPaddingTop(4);
        c.setLeading(0, 1.3f);
        t.addCell(c);
    }

    // ------------------------------------------------------------------ números en letras

    private static final String[] UNIDADES = {"", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE", "DIEZ", "ONCE", "DOCE",
        "TRECE", "CATORCE", "QUINCE", "DIECISÉIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE", "VEINTIÚN", "VEINTIDÓS", "VEINTITRÉS",
        "VEINTICUATRO", "VEINTICINCO", "VEINTISÉIS", "VEINTISIETE", "VEINTIOCHO", "VEINTINUEVE"};
    private static final String[] DECENAS = {"", "", "", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"};
    private static final String[] CENTENAS = {"", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS",
        "OCHOCIENTOS", "NOVECIENTOS"};

    private static String menorMil(int n) {
        if (n == 100) {
            return "CIEN";
        }
        StringBuilder sb = new StringBuilder();
        if (n >= 100) {
            sb.append(CENTENAS[n / 100]).append(' ');
            n %= 100;
        }
        if (n < 30) {
            sb.append(UNIDADES[n]);
        } else {
            sb.append(DECENAS[n / 10]);
            if (n % 10 > 0) {
                sb.append(" Y ").append(UNIDADES[n % 10]);
            }
        }
        return sb.toString().trim();
    }

    /** Importe en letras ("UN MILLÓN QUINIENTOS MIL"), para guaraníes enteros. */
    static String letras(long n) {
        if (n == 0) {
            return "CERO";
        }
        long millones = n / 1000000L;
        long miles = (n % 1000000L) / 1000L;
        int resto = (int) (n % 1000L);
        StringBuilder sb = new StringBuilder();
        if (millones > 0) {
            if (millones == 1) {
                sb.append("UN MILLÓN");
            } else {
                sb.append(letras(millones)).append(" MILLONES");
            }
        }
        if (miles > 0) {
            sb.append(' ').append(miles == 1 ? "MIL" : menorMil((int) miles) + " MIL");
        }
        if (resto > 0) {
            sb.append(' ').append(menorMil(resto));
        }
        return sb.toString().trim().replace("UN MIL", "MIL").replace("  ", " ");
    }
}

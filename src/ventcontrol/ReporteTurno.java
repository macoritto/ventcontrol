package ventcontrol;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Reporte PDF (A4) del cierre de caja: resumen del efectivo que tiene que haber en la caja y lo cobrado por cada
 * medio de pago (efectivo, tarjeta, transferencia, QR). Sirve para que el cajero cierre la caja y haga el arreglo con
 * el jefe: solo el efectivo se entrega; lo demás ya está en el banco o en la tarjeta.
 */
final class ReporteTurno {

    private static final Color NAVY = new Color(16, 44, 72);
    private static final Color AZUL = new Color(0, 132, 190);
    private static final Color GRIS_BORDE = new Color(214, 219, 226);
    private static final Color GRIS_TEXTO = new Color(96, 106, 118);
    private static final Color ZEBRA = new Color(246, 248, 251);
    private static final Color VERDE = new Color(22, 120, 70);
    private static final Color VERDE_FONDO = new Color(231, 245, 236);
    private static final Color ROJO = new Color(176, 38, 38);
    private static final Color ROJO_FONDO = new Color(253, 236, 234);
    private static final Color AMBAR_FONDO = new Color(255, 247, 226);
    private static final Color AMBAR = new Color(138, 94, 0);

    private static final DecimalFormatSymbols SIMBOLOS = new DecimalFormatSymbols();

    static {
        SIMBOLOS.setGroupingSeparator('.');
        SIMBOLOS.setDecimalSeparator(',');
    }

    private static final DecimalFormat FMT = new DecimalFormat("#,##0", SIMBOLOS);

    private ReporteTurno() {
    }

    private static String gs(long v) {
        return "Gs. " + FMT.format(v);
    }

    private static String gsConSigno(long v) {
        return (v < 0 ? "- " : "") + "Gs. " + FMT.format(Math.abs(v));
    }

    // ------------------------------------------------------------------ API

    /**
     * Genera el reporte y lo abre con el visor de PDF del equipo.
     *
     * @param turno  el turno (null para el reporte de un día completo o de lo "sin turno")
     * @param titulo texto del encabezado cuando no hay turno (ej. "Todas las cajas del 08/11/2026")
     * @param dia    día del reporte (solo se usa si no hay turno)
     */
    static void mostrar(Component padre, TurnoCaja.Turno turno, String titulo, Date dia, CajaDatos.Datos d) {
        try {
            File f = generar(turno, titulo, dia, d);
            try {
                java.awt.Desktop.getDesktop().open(f);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(padre, "El reporte se guardó en:\n" + f.getAbsolutePath(), "Reporte de turno",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el reporte:\n" + e.getMessage(), "Reporte de turno",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Lee los datos del turno y muestra su reporte (se usa al cerrar el turno). */
    static void mostrarDeTurno(Component padre, int turnoId) {
        try {
            TurnoCaja.Turno t = TurnoCaja.turno(turnoId);
            if (t == null) {
                return;
            }
            mostrar(padre, t, null, null, CajaDatos.leer(CajaDatos.Alcance.turno(turnoId)));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el reporte:\n" + e.getMessage(), "Reporte de turno",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    static File generar(TurnoCaja.Turno t, String titulo, Date dia, CajaDatos.Datos d) throws Exception {
        String nombre = t != null ? "Reporte_turno_" + t.id
                : "Reporte_caja_" + new SimpleDateFormat("yyyyMMdd").format(dia == null ? new Date() : dia);
        File f = File.createTempFile(nombre + "_", ".pdf");
        f.deleteOnExit();
        escribir(f, t, titulo, dia, d);
        return f;
    }

    // ------------------------------------------------------------------ PDF

    private static void escribir(File f, TurnoCaja.Turno t, String titulo, Date dia, CajaDatos.Datos d) throws Exception {
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        BaseFont bfB = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font fTxt = new Font(bf, 10, Font.NORMAL, Color.BLACK);
        Font fTxtB = new Font(bfB, 10, Font.NORMAL, Color.BLACK);
        Font fChico = new Font(bf, 8.5f, Font.NORMAL, GRIS_TEXTO);
        Font fChicoB = new Font(bfB, 8.5f, Font.NORMAL, GRIS_TEXTO);
        Font fCab = new Font(bfB, 9, Font.NORMAL, Color.WHITE);

        String emitido = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        Document doc = new Document(PageSize.A4, 40, 40, 36, 52);
        PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream(f));
        w.setPageEvent(new Pie(bf, "Emitido el " + emitido));
        doc.open();

        boolean cerrado = t != null && !t.abierto();
        boolean esTurno = t != null;

        // ---------- banda de título
        PdfPTable banda = new PdfPTable(new float[]{3f, 1.4f});
        banda.setWidthPercentage(100);
        String tituloGrande = esTurno ? "REPORTE DE TURNO" : "REPORTE DE CAJA";
        String sub = esTurno ? "Turno " + t.id + "  ·  " + t.cajaNombre + "  ·  " + t.usuarioNombre
                : (titulo == null ? "" : titulo);
        Phrase p1 = new Phrase();
        p1.add(new com.lowagie.text.Chunk(tituloGrande + "\n", new Font(bfB, 17, Font.NORMAL, Color.WHITE)));
        p1.add(new com.lowagie.text.Chunk(sub, new Font(bf, 10.5f, Font.NORMAL, new Color(205, 224, 242))));
        PdfPCell cTit = new PdfPCell(p1);
        cTit.setBackgroundColor(NAVY);
        cTit.setBorder(Rectangle.NO_BORDER);
        cTit.setPadding(12);
        cTit.setPaddingLeft(16);
        cTit.setLeading(0, 1.25f);
        banda.addCell(cTit);
        String estado = !esTurno ? "RESUMEN" : (cerrado ? "TURNO CERRADO" : "TURNO ABIERTO");
        PdfPCell cEst = new PdfPCell(new Phrase(estado, new Font(bfB, 10, Font.NORMAL, Color.WHITE)));
        cEst.setBackgroundColor(!esTurno ? AZUL : (cerrado ? new Color(84, 98, 114) : new Color(22, 140, 84)));
        cEst.setBorder(Rectangle.NO_BORDER);
        cEst.setHorizontalAlignment(Element.ALIGN_CENTER);
        cEst.setVerticalAlignment(Element.ALIGN_MIDDLE);
        banda.addCell(cEst);
        doc.add(banda);
        espacio(doc, 8);

        // ---------- datos del turno
        PdfPTable info = new PdfPTable(new float[]{1f, 2f, 1f, 2f});
        info.setWidthPercentage(100);
        if (esTurno) {
            infoFila(info, "Cajero", t.usuarioNombre, "Caja", t.cajaNombre, fChicoB, fTxt);
            infoFila(info, "Apertura", UiCaja.fechaHora(t.apertura), "Cierre",
                    cerrado ? UiCaja.fechaHora(t.cierre) : "Aún abierto (informe parcial)", fChicoB, fTxt);
        } else {
            String dd = dia == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(dia);
            infoFila(info, "Fecha", dd, "Alcance", titulo == null ? "" : titulo, fChicoB, fTxt);
        }
        doc.add(info);
        espacio(doc, 12);

        // ---------- efectivo en caja
        seccion(doc, "EFECTIVO EN CAJA", "Es lo único que tiene que estar físicamente en la caja", bfB, bf);
        PdfPTable ef = new PdfPTable(new float[]{3.2f, 1.3f});
        ef.setWidthPercentage(100);
        long esperado = d.esperado();
        filaEf(ef, "Fondo inicial (con lo que se abrió)", gs(d.montoInicial), false, fTxt, fTxtB, 0);
        filaEf(ef, "+ Ingresos a caja", gs(d.ingresosTotal), false, fTxt, fTxtB, 1);
        filaEf(ef, "+ Ventas al contado cobradas en efectivo (" + d.nVentasEfectivo + ")", gs(d.ventasEfectivo), false, fTxt, fTxtB, 0);
        filaEf(ef, "+ Cobros de créditos recibidos en efectivo (" + d.nPagosEfectivo + ")", gs(d.pagosEfectivo), false, fTxt, fTxtB, 1);
        filaEf(ef, "- Retiros de caja", gs(d.retirosTotal), false, fTxt, fTxtB, 0);
        filaEf(ef, "- Devoluciones pagadas en efectivo", gs(d.devolucionesTotal), false, fTxt, fTxtB, 1);
        PdfPCell l = celda("EFECTIVO QUE DEBE HABER EN CAJA", new Font(bfB, 11, Font.NORMAL, Color.WHITE), Element.ALIGN_LEFT, NAVY);
        l.setPaddingTop(6);
        l.setPaddingBottom(7);
        ef.addCell(l);
        PdfPCell r = celda(gs(esperado), new Font(bfB, 12, Font.NORMAL, Color.WHITE), Element.ALIGN_RIGHT, NAVY);
        r.setPaddingTop(6);
        r.setPaddingBottom(7);
        ef.addCell(r);
        if (cerrado && t.contado != null) {
            long contado = t.contado.longValue();
            long dif = contado - esperado;
            filaEf(ef, "Efectivo contado al cerrar", gs(contado), false, fTxt, fTxtB, 0);
            String txt = dif == 0 ? "La caja cuadró" : (dif > 0 ? "Sobrante" : "Faltante");
            Color fc = dif == 0 ? VERDE : ROJO;
            Color bg = dif == 0 ? VERDE_FONDO : ROJO_FONDO;
            PdfPCell a = celda(txt, new Font(bfB, 10.5f, Font.NORMAL, fc), Element.ALIGN_LEFT, bg);
            ef.addCell(a);
            PdfPCell b = celda(dif == 0 ? gs(0) : gs(Math.abs(dif)), new Font(bfB, 10.5f, Font.NORMAL, fc), Element.ALIGN_RIGHT, bg);
            ef.addCell(b);
        }
        doc.add(ef);

        long generado = esperado - d.montoInicial;
        Paragraph pe = new Paragraph("Efectivo generado en el turno (sin contar el fondo inicial): " + gsConSigno(generado), fChico);
        pe.setSpacingBefore(4);
        doc.add(pe);
        if (cerrado && t.observacion.length() > 0) {
            Paragraph po = new Paragraph("Observación del cierre: " + t.observacion, fChico);
            po.setSpacingBefore(2);
            doc.add(po);
        }
        espacio(doc, 12);

        // ---------- cobros por medio
        seccion(doc, "COBROS POR MEDIO DE PAGO", "Ventas al contado y cobros de créditos recibidos en el turno", bfB, bf);
        PdfPTable cm = new PdfPTable(new float[]{1.6f, 1.7f, 1.7f, 1.45f, 1.75f});
        cm.setWidthPercentage(100);
        cm.setHeaderRows(1);
        String[] cab = {"MEDIO", "VENTAS CONTADO", "COBROS DE CRÉDITOS", "TOTAL", "DESTINO"};
        for (int i = 0; i < cab.length; i++) {
            cm.addCell(celda(cab[i], fCab, i == 0 || i == 4 ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT, NAVY));
        }
        List<String> medios = new ArrayList<String>();
        for (String m : TurnoCaja.MEDIOS) {
            medios.add(m);
        }
        for (String m : d.ventasPorMedio.keySet()) {
            if (!medios.contains(m)) {
                medios.add(m);
            }
        }
        for (String m : d.pagosPorMedio.keySet()) {
            if (!medios.contains(m)) {
                medios.add(m);
            }
        }
        long totVentas = 0, totPagos = 0;
        int nVentas = 0, nPagos = 0;
        int fila = 0;
        for (String m : medios) {
            long[] v = d.ventasPorMedio.get(m);
            long[] p = d.pagosPorMedio.get(m);
            long mv = v == null ? 0 : v[0], mp = p == null ? 0 : p[0];
            int cv = v == null ? 0 : (int) v[1], cp = p == null ? 0 : (int) p[1];
            totVentas += mv;
            totPagos += mp;
            nVentas += cv;
            nPagos += cp;
            boolean efectivo = TurnoCaja.EFECTIVO.equals(m);
            Color fondo = fila % 2 == 0 ? Color.WHITE : ZEBRA;
            cm.addCell(celda(m, fTxtB, Element.ALIGN_LEFT, fondo));
            cm.addCell(celda(cantidad(mv, cv), fTxt, Element.ALIGN_RIGHT, fondo));
            cm.addCell(celda(cantidad(mp, cp), fTxt, Element.ALIGN_RIGHT, fondo));
            cm.addCell(celda(gs(mv + mp), fTxtB, Element.ALIGN_RIGHT, fondo));
            cm.addCell(celda(efectivo ? "Entra a la caja" : "No entra a la caja",
                    new Font(bfB, 9, Font.NORMAL, efectivo ? VERDE : AMBAR), Element.ALIGN_LEFT, fondo));
            fila++;
        }
        Font fTot = new Font(bfB, 10, Font.NORMAL, Color.BLACK);
        Color gris = new Color(226, 232, 240);
        cm.addCell(celda("TOTAL COBRADO", fTot, Element.ALIGN_LEFT, gris));
        cm.addCell(celda(cantidad(totVentas, nVentas), fTot, Element.ALIGN_RIGHT, gris));
        cm.addCell(celda(cantidad(totPagos, nPagos), fTot, Element.ALIGN_RIGHT, gris));
        cm.addCell(celda(gs(totVentas + totPagos), fTot, Element.ALIGN_RIGHT, gris));
        cm.addCell(celda("", fTot, Element.ALIGN_LEFT, gris));
        doc.add(cm);

        Paragraph pc = new Paragraph("Ventas a crédito del turno (todavía sin cobrar): " + d.nCredito + " · " + gs(d.credito), fChico);
        pc.setSpacingBefore(5);
        doc.add(pc);
        espacio(doc, 12);

        // ---------- nota
        long otros = d.otrosMedios();
        PdfPTable nota = new PdfPTable(1);
        nota.setWidthPercentage(100);
        Phrase pn = new Phrase();
        pn.add(new com.lowagie.text.Chunk("Para cerrar la caja\n", new Font(bfB, 10, Font.NORMAL, AMBAR)));
        pn.add(new com.lowagie.text.Chunk("En la caja tiene que haber " + gs(esperado) + " en efectivo (incluye el fondo inicial de "
                + gs(d.montoInicial) + "). Los " + gs(otros) + " cobrados con tarjeta, transferencia o QR no se cuentan en la caja: "
                + "no hay que entregarlos, ya están en el banco o en la cuenta del comercio.",
                new Font(bf, 9.5f, Font.NORMAL, Color.BLACK)));
        PdfPCell cn = new PdfPCell(pn);
        cn.setBackgroundColor(AMBAR_FONDO);
        cn.setBorderColor(new Color(240, 214, 140));
        cn.setBorderWidth(0.6f);
        cn.setPadding(9);
        cn.setLeading(0, 1.25f);
        nota.addCell(cn);
        doc.add(nota);

        doc.close();
    }

    // ------------------------------------------------------------------ piezas

    private static String cantidad(long monto, int n) {
        return gs(monto) + "  (" + n + ")";
    }

    private static void espacio(Document doc, float pt) throws DocumentException {
        Paragraph p = new Paragraph(" ");
        p.setLeading(pt);
        doc.add(p);
    }

    private static void seccion(Document doc, String titulo, String detalle, BaseFont bfB, BaseFont bf) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        Phrase ph = new Phrase();
        ph.add(new com.lowagie.text.Chunk(titulo, new Font(bfB, 11, Font.NORMAL, NAVY)));
        ph.add(new com.lowagie.text.Chunk("   " + detalle, new Font(bf, 8.5f, Font.NORMAL, GRIS_TEXTO)));
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

    private static void infoFila(PdfPTable t, String a, String va, String b, String vb, Font fEtq, Font fVal) {
        t.addCell(sinBorde(new Phrase(a.toUpperCase(), fEtq)));
        t.addCell(sinBorde(new Phrase(va, fVal)));
        t.addCell(sinBorde(new Phrase(b.toUpperCase(), fEtq)));
        t.addCell(sinBorde(new Phrase(vb, fVal)));
    }

    private static PdfPCell sinBorde(Phrase p) {
        PdfPCell c = new PdfPCell(p);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPaddingTop(2.5f);
        c.setPaddingBottom(2.5f);
        c.setPaddingLeft(0);
        return c;
    }

    private static void filaEf(PdfPTable t, String etq, String valor, boolean fuerte, Font f, Font fB, int zebra) {
        Color fondo = zebra == 1 ? ZEBRA : Color.WHITE;
        t.addCell(celda(etq, f, Element.ALIGN_LEFT, fondo));
        t.addCell(celda(valor, fuerte ? fB : f, Element.ALIGN_RIGHT, fondo));
    }

    private static PdfPCell celda(String texto, Font f, int alin, Color fondo) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setHorizontalAlignment(alin);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(GRIS_BORDE);
        c.setBorderWidth(0.5f);
        c.setPaddingTop(4.5f);
        c.setPaddingBottom(5f);
        c.setPaddingLeft(6f);
        c.setPaddingRight(6f);
        if (fondo != null) {
            c.setBackgroundColor(fondo);
        }
        return c;
    }

    private static final class Pie extends PdfPageEventHelper {

        private final BaseFont bf;
        private final String izquierda;

        Pie(BaseFont bf, String izquierda) {
            this.bf = bf;
            this.izquierda = izquierda;
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
            cb.showTextAligned(Element.ALIGN_RIGHT, "Reporte de caja", document.right(), y, 0);
            cb.endText();
        }
    }
}

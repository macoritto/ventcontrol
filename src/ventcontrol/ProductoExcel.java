package ventcontrol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Exporta el listado de productos a un libro de Excel (.xlsx) con los mismos filtros que se ven en pantalla.
 * El archivo se arma directamente (un .xlsx es un zip de XML), sin necesitar ninguna librería externa:
 * encabezado con estilo, columnas ajustadas, filtros y fila fija, cifras como números reales y totales
 * con fórmulas que respetan el filtro de Excel.
 */
final class ProductoExcel {

    private ProductoExcel() {
    }

    // índices de estilo (ver estilos())
    private static final int S_TITULO = 1, S_SUBTITULO = 2, S_CABECERA = 3, S_TEXTO = 4, S_ENTERO = 5, S_DECIMAL = 6,
            S_CENTRO = 7, S_STOCK_NEG = 8, S_STOCK_CERO = 9, S_TOT_ROT = 10, S_TOT_ENTERO = 11, S_TOT_DECIMAL = 12,
            S_CABECERA_DER = 13;

    /** Ícono verde de planilla para el botón. */
    static Icon icono(final int size) {
        return new Icon() {
            public int getIconWidth() {
                return size;
            }

            public int getIconHeight() {
                return size;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                float u = size / 22f;
                g2.setColor(new Color(33, 130, 78));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(1 * u, 1 * u, 20 * u, 20 * u, 4 * u, 4 * u));
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(1.4f * u));
                g2.draw(new java.awt.geom.Rectangle2D.Float(5 * u, 5.5f * u, 12 * u, 11 * u));
                g2.drawLine(Math.round(5 * u), Math.round(9.2f * u), Math.round(17 * u), Math.round(9.2f * u));
                g2.drawLine(Math.round(5 * u), Math.round(12.8f * u), Math.round(17 * u), Math.round(12.8f * u));
                g2.drawLine(Math.round(10 * u), Math.round(5.5f * u), Math.round(10 * u), Math.round(16.5f * u));
                g2.dispose();
            }
        };
    }

    // ---------------------------------------------------------------------------------------
    //  Armado del XML
    // ---------------------------------------------------------------------------------------
    private static String esc(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '&') {
                sb.append("&amp;");
            } else if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == '"') {
                sb.append("&quot;");
            } else if (ch == '\t' || ch == '\n' || ch == '\r' || ch >= ' ') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static String col(int i) {
        return String.valueOf((char) ('A' + i));
    }

    private static void txt(StringBuilder sb, int c, int r, String v, int estilo) {
        sb.append("<c r=\"").append(col(c)).append(r).append("\" s=\"").append(estilo).append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                .append(esc(v == null ? "" : v)).append("</t></is></c>");
    }

    private static void num(StringBuilder sb, int c, int r, BigDecimal v, int estilo) {
        sb.append("<c r=\"").append(col(c)).append(r).append("\" s=\"").append(estilo).append("\"><v>")
                .append(v.stripTrailingZeros().toPlainString().replace("E", "e")).append("</v></c>");
    }

    private static void formula(StringBuilder sb, int c, int r, String f, BigDecimal valor, int estilo) {
        sb.append("<c r=\"").append(col(c)).append(r).append("\" s=\"").append(estilo).append("\"><f>").append(esc(f)).append("</f><v>")
                .append(valor.stripTrailingZeros().toPlainString()).append("</v></c>");
    }

    private static boolean esNumero(String s) {
        return s != null && s.matches("[0-9]{1,9}");
    }

    private static String estilos() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<numFmts count=\"1\"><numFmt numFmtId=\"164\" formatCode=\"#,##0.###\"/></numFmts>"
                + "<fonts count=\"7\">"
                + "<font><sz val=\"10\"/><name val=\"Calibri\"/></font>"                                              // 0 normal
                + "<font><b/><sz val=\"16\"/><color rgb=\"FF102C48\"/><name val=\"Calibri\"/></font>"                 // 1 título
                + "<font><sz val=\"10\"/><color rgb=\"FF606A76\"/><name val=\"Calibri\"/></font>"                     // 2 subtítulo
                + "<font><b/><sz val=\"10\"/><color rgb=\"FFFFFFFF\"/><name val=\"Calibri\"/></font>"                 // 3 cabecera
                + "<font><b/><sz val=\"10\"/><color rgb=\"FFB02626\"/><name val=\"Calibri\"/></font>"                 // 4 rojo
                + "<font><b/><sz val=\"10\"/><color rgb=\"FFB06C00\"/><name val=\"Calibri\"/></font>"                 // 5 ámbar
                + "<font><b/><sz val=\"10\"/><color rgb=\"FF102C48\"/><name val=\"Calibri\"/></font>"                 // 6 total
                + "</fonts>"
                + "<fills count=\"6\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill>"
                + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF102C48\"/></patternFill></fill>"        // 2 navy
                + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFFDECEA\"/></patternFill></fill>"        // 3 rojo claro
                + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFFFF5DE\"/></patternFill></fill>"        // 4 ámbar claro
                + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFE5F2FD\"/></patternFill></fill>"        // 5 total
                + "</fills>"
                + "<borders count=\"2\"><border><left/><right/><top/><bottom/><diagonal/></border>"
                + "<border><left style=\"thin\"><color rgb=\"FFD6DBE2\"/></left><right style=\"thin\"><color rgb=\"FFD6DBE2\"/></right>"
                + "<top style=\"thin\"><color rgb=\"FFD6DBE2\"/></top><bottom style=\"thin\"><color rgb=\"FFD6DBE2\"/></bottom><diagonal/></border></borders>"
                + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                + "<cellXfs count=\"14\">"
                + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"                                                       // 0
                + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>"                                       // 1 título
                + "<xf numFmtId=\"0\" fontId=\"2\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>"                                       // 2 subtítulo
                + "<xf numFmtId=\"0\" fontId=\"3\" fillId=\"2\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\" wrapText=\"1\"/></xf>" // 3 cabecera
                + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyBorder=\"1\"/>"                                     // 4 texto
                + "<xf numFmtId=\"3\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyBorder=\"1\"/>"              // 5 entero
                + "<xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyBorder=\"1\"/>"            // 6 decimal
                + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\"/></xf>" // 7 centro
                + "<xf numFmtId=\"164\" fontId=\"4\" fillId=\"3\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\"/>" // 8 stock negativo
                + "<xf numFmtId=\"164\" fontId=\"5\" fillId=\"4\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\"/>" // 9 stock cero
                + "<xf numFmtId=\"0\" fontId=\"6\" fillId=\"5\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\"/></xf>" // 10 total rótulo
                + "<xf numFmtId=\"3\" fontId=\"6\" fillId=\"5\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\"/>" // 11 total entero
                + "<xf numFmtId=\"164\" fontId=\"6\" fillId=\"5\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\"/>" // 12 total decimal
                + "<xf numFmtId=\"0\" fontId=\"3\" fillId=\"2\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\" wrapText=\"1\"/></xf>" // 13 cabecera derecha
                + "</cellXfs>"
                + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                + "</styleSheet>";
    }

    /** Escribe el libro en el archivo indicado. */
    static void escribir(File destino, String nombre, String marca, String tipo, String lote, Integer usuarioId, boolean conCostos)
            throws SQLException, IOException {
        List<ProductoReporte.Fila> filas = ProductoReporte.leer(nombre, marca, tipo, lote);
        String responsable = ProductoReporte.usuario(usuarioId);
        String emitido = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        String[] cols = conCostos
                ? new String[]{"Cód.", "Producto", "Marca", "Tipo", "Lote", "Stock", "P. Costo", "P. Venta", "Valor a costo", "Valor a venta"}
                : new String[]{"Cód.", "Producto", "Marca", "Tipo", "Stock", "P. Venta"};
        double[] anchos = conCostos
                ? new double[]{9, 46, 16, 16, 14, 11, 13, 13, 16, 16}
                : new double[]{9, 50, 18, 18, 11, 14};
        final int cabFila = 5;
        int primera = cabFila + 1;
        int ultima = primera + Math.max(filas.size(), 1) - 1;
        int cStock = conCostos ? 5 : 4;

        StringBuilder sh = new StringBuilder();
        sh.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
                .append("<sheetPr><pageSetUpPr fitToPage=\"1\"/></sheetPr>")
                .append("<sheetViews><sheetView workbookViewId=\"0\" showGridLines=\"0\"><pane ySplit=\"").append(cabFila)
                .append("\" topLeftCell=\"A").append(primera).append("\" activePane=\"bottomLeft\" state=\"frozen\"/></sheetView></sheetViews>")
                .append("<sheetFormatPr defaultRowHeight=\"15\"/><cols>");
        for (int i = 0; i < anchos.length; i++) {
            sh.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1).append("\" width=\"").append(anchos[i]).append("\" customWidth=\"1\"/>");
        }
        sh.append("</cols><sheetData>");

        sh.append("<row r=\"1\" ht=\"24\" customHeight=\"1\">");
        txt(sh, 0, 1, conCostos ? "Reporte de productos" : "Lista de precios", S_TITULO);
        sh.append("</row><row r=\"2\">");
        txt(sh, 0, 2, descripcionFiltros(nombre, marca, tipo, lote), S_SUBTITULO);
        sh.append("</row><row r=\"3\">");
        txt(sh, 0, 3, "Emitido el " + emitido + (responsable.isEmpty() ? "" : " por " + responsable)
                + (conCostos ? "" : "  ·  Precios de venta en guaraníes (Gs.)"), S_SUBTITULO);
        sh.append("</row><row r=\"").append(cabFila).append("\" ht=\"22\" customHeight=\"1\">");
        for (int i = 0; i < cols.length; i++) {
            boolean der = cols[i].equals("Stock") || cols[i].startsWith("P.") || cols[i].startsWith("Valor");
            txt(sh, i, cabFila, cols[i], der ? S_CABECERA_DER : S_CABECERA);
        }
        sh.append("</row>");

        BigDecimal totStock = BigDecimal.ZERO, totCosto = BigDecimal.ZERO, totVenta = BigDecimal.ZERO;
        int r = primera;
        for (ProductoReporte.Fila f : filas) {
            sh.append("<row r=\"").append(r).append("\">");
            int c = 0;
            if (esNumero(f.cod)) {
                num(sh, c++, r, new BigDecimal(f.cod), S_CENTRO);
            } else {
                txt(sh, c++, r, f.cod, S_CENTRO);
            }
            txt(sh, c++, r, f.nombre, S_TEXTO);
            txt(sh, c++, r, f.marca, S_TEXTO);
            txt(sh, c++, r, f.tipo, S_TEXTO);
            if (conCostos) {
                txt(sh, c++, r, f.lote, S_TEXTO);
            }
            int sStock = f.stock.signum() < 0 ? S_STOCK_NEG : f.stock.signum() == 0 ? S_STOCK_CERO : S_DECIMAL;
            num(sh, c++, r, f.stock, sStock);
            if (conCostos) {
                num(sh, c++, r, f.costo, S_ENTERO);
            }
            num(sh, c++, r, f.venta, S_ENTERO);
            if (conCostos) {
                formula(sh, c++, r, "F" + r + "*G" + r, f.stock.multiply(f.costo), S_ENTERO);
                formula(sh, c++, r, "F" + r + "*H" + r, f.stock.multiply(f.venta), S_ENTERO);
            }
            sh.append("</row>");
            totStock = totStock.add(f.stock);
            totCosto = totCosto.add(f.stock.multiply(f.costo));
            totVenta = totVenta.add(f.stock.multiply(f.venta));
            r++;
        }
        if (filas.isEmpty()) {
            sh.append("<row r=\"").append(r).append("\">");
            txt(sh, 0, r, "No hay productos que coincidan con el filtro.", S_SUBTITULO);
            sh.append("</row>");
            r++;
        }
        // totales con SUBTOTAL: si se filtra en Excel, el total acompaña lo que queda visible
        sh.append("<row r=\"").append(r).append("\" ht=\"20\" customHeight=\"1\">");
        for (int i = 0; i < cols.length; i++) {
            if (i < cStock) {
                txt(sh, i, r, i == cStock - 1 ? "TOTALES" : "", S_TOT_ROT);
            } else if (i == cStock) {
                formula(sh, i, r, "SUBTOTAL(109," + col(i) + primera + ":" + col(i) + ultima + ")", totStock, S_TOT_DECIMAL);
            } else if (conCostos && i >= 8) {
                formula(sh, i, r, "SUBTOTAL(109," + col(i) + primera + ":" + col(i) + ultima + ")", i == 8 ? totCosto : totVenta, S_TOT_ENTERO);
            } else {
                txt(sh, i, r, "", S_TOT_ROT);
            }
        }
        sh.append("</row></sheetData>");
        sh.append("<autoFilter ref=\"A").append(cabFila).append(":").append(col(cols.length - 1)).append(ultima).append("\"/>");
        sh.append("<pageMargins left=\"0.4\" right=\"0.4\" top=\"0.5\" bottom=\"0.5\" header=\"0.3\" footer=\"0.3\"/>");
        sh.append("<pageSetup paperSize=\"9\" orientation=\"landscape\" fitToWidth=\"1\" fitToHeight=\"0\"/>");
        sh.append("<headerFooter><oddFooter>&amp;L&amp;8Emitido el ").append(esc(emitido)).append("&amp;R&amp;8Página &amp;P de &amp;N</oddFooter></headerFooter>");
        sh.append("</worksheet>");

        String libro = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<sheets><sheet name=\"Productos\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
                + "<definedNames><definedName name=\"_xlnm._FilterDatabase\" localSheetId=\"0\" hidden=\"1\">Productos!$A$" + cabFila
                + ":$" + col(cols.length - 1) + "$" + ultima + "</definedName>"
                + "<definedName name=\"_xlnm.Print_Titles\" localSheetId=\"0\">Productos!$" + cabFila + ":$" + cabFila + "</definedName></definedNames>"
                + "</workbook>";
        String relsLibro = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
                + "</Relationships>";
        String rels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
        String tipos = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
                + "</Types>";

        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(destino));
        try {
            agregar(zip, "[Content_Types].xml", tipos);
            agregar(zip, "_rels/.rels", rels);
            agregar(zip, "xl/workbook.xml", libro);
            agregar(zip, "xl/_rels/workbook.xml.rels", relsLibro);
            agregar(zip, "xl/styles.xml", estilos());
            agregar(zip, "xl/worksheets/sheet1.xml", sh.toString());
        } finally {
            zip.close();
        }
    }

    private static void agregar(ZipOutputStream zip, String nombre, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes("UTF-8"));
        zip.closeEntry();
    }

    private static String descripcionFiltros(String nombre, String marca, String tipo, String lote) {
        return ProductoReporte.descripcionFiltros(nombre, marca, tipo, lote);
    }

    // ---------------------------------------------------------------------------------------
    //  Interfaz
    // ---------------------------------------------------------------------------------------
    /** Pregunta la versión, pide dónde guardar, genera el libro y lo abre. */
    static void preguntarYExportar(Component padre, String nombre, String marca, String tipo, String lote, Integer usuarioId) {
        Object[] op = {"Con costos y valorización", "Solo precios de venta", "Cancelar"};
        int r = JOptionPane.showOptionDialog(padre,
                "Se exportarán los productos según los filtros que tiene en pantalla.\n"
                + "La versión con costos incluye costo, valor del stock a costo y a venta; la otra solo el precio de venta.",
                "Exportar a Excel", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (r != 0 && r != 1) {
            return;
        }
        boolean conCostos = r == 0;
        JFileChooser fc = new JFileChooser();
        File docs = new File(System.getProperty("user.home"), "Documents");
        fc.setCurrentDirectory(docs.isDirectory() ? docs : new File(System.getProperty("user.home")));
        fc.setDialogTitle("Guardar productos en Excel");
        fc.setFileFilter(new FileNameExtensionFilter("Libro de Excel (*.xlsx)", "xlsx"));
        fc.setSelectedFile(new File(fc.getCurrentDirectory(), (conCostos ? "Productos_" : "Lista_de_precios_")
                + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".xlsx"));
        if (fc.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".xlsx")) {
            f = new File(f.getParentFile(), f.getName() + ".xlsx");
        }
        if (f.exists() && JOptionPane.showConfirmDialog(padre, "El archivo ya existe. ¿Desea reemplazarlo?", "Exportar a Excel",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            escribir(f, nombre, marca, tipo, lote, usuarioId, conCostos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo exportar a Excel:\n" + ex.getMessage()
                    + (ex instanceof SQLException ? InventarioDb.ayuda() : ""), "Exportar a Excel", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            java.awt.Desktop.getDesktop().open(f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "El archivo se guardó en:\n" + f.getAbsolutePath(), "Exportar a Excel",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

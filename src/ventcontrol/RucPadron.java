package ventcontrol;

import claseConectar.conectar;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Consulta de RUC contra el padrón público de contribuyentes de la DNIT ("Listado de RUC con sus equivalencias").
 *
 * La consulta oficial en línea de SIFEN exige un certificado digital de firma electrónica; este padrón es el
 * listado oficial y público que la DNIT publica (10 archivos ruc0.zip ... ruc9.zip, actualizados cada mes) con
 * RUC, razón social, dígito verificador y estado. Se descarga una vez a una tabla local (ruc_padron) y desde ahí
 * cada consulta es instantánea y funciona aunque no haya internet.
 */
final class RucPadron {

    static final String PAGINA_DNIT = "https://www.dnit.gov.py/web/portal-institucional/listado-de-ruc-con-sus-equivalencias";
    private static final String BASE_DNIT = "https://www.dnit.gov.py";

    private RucPadron() {
    }

    /** Un contribuyente del padrón. */
    static final class Resultado {
        String ruc = "", dv = "", razon = "", estado = "", anterior = "";

        String rucConDv() {
            return dv.isEmpty() ? ruc : ruc + "-" + dv;
        }

        /** Las personas físicas usan su número de cédula como RUC (los de empresas empiezan en 80.000.000). */
        boolean esPersonaFisica() {
            try {
                return Long.parseLong(ruc) < 80000000L;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    interface Progreso {
        void mensaje(String texto);
    }

    // ---------------------------------------------------------------------------------------
    //  Base de datos
    // ---------------------------------------------------------------------------------------
    private static void asegurarTabla(Connection cn) throws SQLException {
        Statement st = cn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS ruc_padron (ruc VARCHAR(20) PRIMARY KEY, dv VARCHAR(2), razon TEXT, "
                + "ruc_anterior VARCHAR(20), estado VARCHAR(60))");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS ruc_padron_info (id INTEGER PRIMARY KEY, actualizado TIMESTAMP, filas INTEGER, fuente TEXT)");
        st.close();
    }

    /** Cantidad de contribuyentes cargados (0 si todavía no se importó el padrón). */
    static int cantidad() throws SQLException {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            asegurarTabla(cn);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM ruc_padron");
            rs.next();
            int n = rs.getInt(1);
            rs.close();
            st.close();
            return n;
        } finally {
            cerrar(cn);
        }
    }

    /** Fecha de la última actualización del padrón local (null si nunca se cargó). */
    static Date actualizadoEl() {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            asegurarTabla(cn);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT actualizado FROM ruc_padron_info WHERE id=1");
            Date d = rs.next() ? rs.getTimestamp(1) : null;
            rs.close();
            st.close();
            return d;
        } catch (SQLException e) {
            return null;
        } finally {
            cerrar(cn);
        }
    }

    private static void cerrar(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException ignorada) {
            }
        }
    }

    /** Solo los dígitos de lo que escribió el usuario ("1234567-8" -> "12345678"). */
    static String soloDigitos(String t) {
        return t == null ? "" : t.replaceAll("[^0-9]", "");
    }

    /**
     * Busca un RUC. Acepta "1234567-8" (con dígito verificador), "1234567" (sin él) y, si no aparece,
     * prueba también si el último dígito era el verificador y si es un RUC anterior. Devuelve null si no existe.
     */
    static Resultado buscar(String texto) throws SQLException {
        String t = texto == null ? "" : texto.trim();
        String digitos = soloDigitos(t);
        if (digitos.isEmpty()) {
            return null;
        }
        List<String> candidatos = new ArrayList<String>();
        int guion = t.lastIndexOf('-');
        if (guion > 0 && soloDigitos(t.substring(0, guion)).length() > 0) {
            candidatos.add(soloDigitos(t.substring(0, guion)));
        }
        candidatos.add(digitos);
        if (digitos.length() > 3) {
            candidatos.add(digitos.substring(0, digitos.length() - 1));
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            asegurarTabla(cn);
            PreparedStatement ps = cn.prepareStatement("SELECT ruc, dv, razon, estado, ruc_anterior FROM ruc_padron WHERE ruc=?");
            for (String c : candidatos) {
                ps.setString(1, c);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Resultado r = leerFila(rs);
                    rs.close();
                    ps.close();
                    return r;
                }
                rs.close();
            }
            ps.close();
            PreparedStatement pa = cn.prepareStatement("SELECT ruc, dv, razon, estado, ruc_anterior FROM ruc_padron WHERE ruc_anterior=? LIMIT 1");
            pa.setString(1, digitos);
            ResultSet ra = pa.executeQuery();
            Resultado r = ra.next() ? leerFila(ra) : null;
            ra.close();
            pa.close();
            return r;
        } finally {
            cerrar(cn);
        }
    }

    private static Resultado leerFila(ResultSet rs) throws SQLException {
        Resultado r = new Resultado();
        r.ruc = nz(rs.getString(1));
        r.dv = nz(rs.getString(2));
        r.razon = nz(rs.getString(3));
        r.estado = nz(rs.getString(4));
        r.anterior = nz(rs.getString(5));
        return r;
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }

    // ---------------------------------------------------------------------------------------
    //  Lectura de los archivos de la DNIT
    // ---------------------------------------------------------------------------------------
    private static final class Contador {
        int lineas, validas;
    }

    private static Charset detectar(byte[] datos) {
        int n = Math.min(datos.length, 200000);
        int fin = n;
        // no cortar un carácter UTF-8 a la mitad
        while (fin > 0 && fin < datos.length && (datos[fin] & 0xC0) == 0x80) {
            fin--;
        }
        try {
            Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(datos, 0, fin));
            return Charset.forName("UTF-8");
        } catch (CharacterCodingException e) {
            return Charset.forName("windows-1252");
        }
    }

    private static void csv(StringBuilder sb, String v) {
        sb.append('"');
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c == '"') {
                sb.append("\"\"");
            } else if (c != 0 && c != '\r' && c != '\n') {
                sb.append(c);
            }
        }
        sb.append('"');
    }

    /** Convierte el texto de un archivo del padrón (RUC|RAZÓN SOCIAL|DV|RUC ANTERIOR|ESTADO) al CSV de carga. */
    private static String aCsv(byte[] datos, Contador cont) {
        String texto = new String(datos, detectar(datos));
        StringBuilder sb = new StringBuilder(Math.max(1024, texto.length() / 2));
        int ini = 0, len = texto.length();
        while (ini < len) {
            int fin = texto.indexOf('\n', ini);
            if (fin < 0) {
                fin = len;
            }
            String linea = texto.substring(ini, fin).trim();
            ini = fin + 1;
            if (linea.length() == 0) {
                continue;
            }
            if (linea.charAt(0) == '\uFEFF') {
                linea = linea.substring(1);
            }
            cont.lineas++;
            String[] p = linea.split("\\|", -1);
            if (p.length < 2) {
                continue;
            }
            String ruc = p[0].trim();
            if (!ruc.matches("[0-9]{3,12}")) {
                continue;
            }
            String razon = p[1].trim();
            String dv = p.length > 2 ? p[2].trim() : "";
            String anterior = p.length > 3 ? p[3].trim() : "";
            String estado = p.length > 4 ? p[4].trim() : "";
            if (dv.length() > 2) {
                dv = "";
            }
            cont.validas++;
            csv(sb, ruc);
            sb.append(',');
            csv(sb, dv);
            sb.append(',');
            csv(sb, razon);
            sb.append(',');
            csv(sb, anterior);
            sb.append(',');
            csv(sb, estado);
            sb.append('\n');
        }
        return sb.toString();
    }

    private static byte[] leerTodo(InputStream in) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream(1 << 20);
        byte[] buf = new byte[65536];
        int n;
        while ((n = in.read(buf)) > 0) {
            bo.write(buf, 0, n);
        }
        return bo.toByteArray();
    }

    /**
     * Reemplaza el padrón local por el contenido de los archivos (zip con .txt, o .txt/.csv sueltos).
     * Todo ocurre en una transacción: si algo falla, el padrón anterior queda intacto. Devuelve cuántos RUC quedaron cargados.
     */
    static int importar(List<File> archivos, Progreso prog) throws SQLException, IOException {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            cn.setAutoCommit(false);
            asegurarTabla(cn);
            Statement st = cn.createStatement();
            st.executeUpdate("CREATE TEMP TABLE ruc_tmp (ruc VARCHAR(20), dv VARCHAR(2), razon TEXT, ruc_anterior VARCHAR(20), estado VARCHAR(60)) ON COMMIT DROP");
            Contador cont = new Contador();
            String ejemplo = "";
            int k = 0;
            for (File f : archivos) {
                k++;
                if (prog != null) {
                    prog.mensaje("Cargando " + f.getName() + " (" + k + " de " + archivos.size() + ")...");
                }
                List<byte[]> contenidos = new ArrayList<byte[]>();
                if (f.getName().toLowerCase().endsWith(".zip")) {
                    ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
                    try {
                        ZipEntry e;
                        while ((e = zin.getNextEntry()) != null) {
                            if (!e.isDirectory()) {
                                contenidos.add(leerTodo(zin));
                            }
                        }
                    } finally {
                        zin.close();
                    }
                } else {
                    InputStream in = new BufferedInputStream(new FileInputStream(f));
                    try {
                        contenidos.add(leerTodo(in));
                    } finally {
                        in.close();
                    }
                }
                for (byte[] datos : contenidos) {
                    if (ejemplo.isEmpty() && datos.length > 0) {
                        ejemplo = new String(datos, 0, Math.min(datos.length, 160), detectar(datos)).split("\n")[0];
                    }
                    String csv = aCsv(datos, cont);
                    if (csv.length() > 0) {
                        ((org.postgresql.PGConnection) cn).getCopyAPI().copyIn(
                                "COPY ruc_tmp (ruc, dv, razon, ruc_anterior, estado) FROM STDIN WITH CSV", new StringReader(csv));
                    }
                }
            }
            if (cont.validas == 0 || cont.validas < cont.lineas / 2) {
                throw new IOException("Los archivos no tienen el formato esperado (RUC|RAZÓN SOCIAL|DV|RUC ANTERIOR|ESTADO). "
                        + "Se leyeron " + cont.validas + " de " + cont.lineas + " líneas. Primera línea: " + ejemplo);
            }
            if (prog != null) {
                prog.mensaje("Guardando el padrón...");
            }
            st.executeUpdate("DELETE FROM ruc_padron");
            int n = st.executeUpdate("INSERT INTO ruc_padron (ruc, dv, razon, ruc_anterior, estado) "
                    + "SELECT DISTINCT ON (ruc) ruc, dv, razon, ruc_anterior, estado FROM ruc_tmp ORDER BY ruc");
            st.executeUpdate("DELETE FROM ruc_padron_info WHERE id=1");
            PreparedStatement pi = cn.prepareStatement("INSERT INTO ruc_padron_info (id, actualizado, filas, fuente) VALUES (1, now(), ?, ?)");
            pi.setInt(1, n);
            pi.setString(2, "DNIT - Listado de RUC con sus equivalencias");
            pi.executeUpdate();
            pi.close();
            st.close();
            cn.commit();
            return n;
        } catch (SQLException e) {
            rollback(cn);
            throw e;
        } catch (IOException e) {
            rollback(cn);
            throw e;
        } catch (RuntimeException e) {
            rollback(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    private static void rollback(Connection cn) {
        if (cn != null) {
            try {
                cn.rollback();
            } catch (SQLException ignorada) {
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    //  Descarga desde la DNIT
    // ---------------------------------------------------------------------------------------
    private static byte[] descargarBytes(String url, Progreso prog, String etiqueta) throws IOException {
        String actual = url;
        for (int saltos = 0; saltos < 6; saltos++) {
            HttpURLConnection c = (HttpURLConnection) new URL(actual).openConnection();
            c.setInstanceFollowRedirects(false);
            c.setConnectTimeout(20000);
            c.setReadTimeout(120000);
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) ventcontrol");
            int code = c.getResponseCode();
            if (code >= 300 && code < 400 && c.getHeaderField("Location") != null) {
                actual = new URL(new URL(actual), c.getHeaderField("Location")).toString();
                c.disconnect();
                continue;
            }
            if (code != 200) {
                c.disconnect();
                throw new IOException("La DNIT respondió " + code + " al pedir " + etiqueta);
            }
            InputStream in = c.getInputStream();
            try {
                return leerTodo(in);
            } finally {
                in.close();
                c.disconnect();
            }
        }
        throw new IOException("Demasiadas redirecciones al pedir " + etiqueta);
    }

    /** Busca en la página de la DNIT los enlaces de ruc0.zip ... ruc9.zip (el orden de salida es por dígito). */
    static List<String> enlaces(String html) {
        TreeMap<String, String> porDigito = new TreeMap<String, String>();
        Matcher m = Pattern.compile("[\"']([^\"'\\s<>]*?ruc([0-9])\\.zip[^\"'\\s<>]*)[\"']", Pattern.CASE_INSENSITIVE).matcher(html);
        while (m.find()) {
            String url = m.group(1).replace("&amp;", "&");
            if (url.startsWith("//")) {
                url = "https:" + url;
            } else if (url.startsWith("/")) {
                url = BASE_DNIT + url;
            } else if (!url.toLowerCase().startsWith("http")) {
                url = BASE_DNIT + "/" + url;
            }
            if (!porDigito.containsKey(m.group(2))) {
                porDigito.put(m.group(2), url);
            }
        }
        return new ArrayList<String>(porDigito.values());
    }

    /** Descarga los 10 archivos del padrón a la carpeta indicada. */
    static List<File> descargar(File carpeta, Progreso prog) throws IOException {
        if (prog != null) {
            prog.mensaje("Consultando la página de la DNIT...");
        }
        String html = new String(descargarBytes(PAGINA_DNIT, prog, "la página del listado"), "UTF-8");
        List<String> urls = enlaces(html);
        if (urls.isEmpty()) {
            throw new IOException("No se encontraron los archivos del padrón en la página de la DNIT. Pudo haber cambiado la página.");
        }
        List<File> archivos = new ArrayList<File>();
        int k = 0;
        for (String u : urls) {
            k++;
            if (prog != null) {
                prog.mensaje("Descargando archivo " + k + " de " + urls.size() + "...");
            }
            byte[] datos = descargarBytes(u, prog, "el archivo " + k);
            File f = new File(carpeta, "ruc_" + k + ".zip");
            OutputStream o = new FileOutputStream(f);
            try {
                o.write(datos);
            } finally {
                o.close();
            }
            archivos.add(f);
        }
        return archivos;
    }

    // ---------------------------------------------------------------------------------------
    //  Interfaz
    // ---------------------------------------------------------------------------------------
    private static final class Tarea extends SwingWorker<Integer, String> {

        private final JLabel etiqueta;
        private final JDialog dialogo;
        private final List<File> elegidos;
        Throwable error;

        Tarea(JDialog dialogo, JLabel etiqueta, List<File> elegidos) {
            this.dialogo = dialogo;
            this.etiqueta = etiqueta;
            this.elegidos = elegidos;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            Progreso p = new Progreso() {
                public void mensaje(String texto) {
                    publish(texto);
                }
            };
            File tmp = null;
            List<File> usar = elegidos;
            try {
                if (usar == null) {
                    tmp = File.createTempFile("ruc", "");
                    tmp.delete();
                    tmp.mkdirs();
                    usar = descargar(tmp, p);
                }
                return importar(usar, p);
            } catch (Throwable t) {
                error = t;
                return -1;
            } finally {
                if (tmp != null) {
                    File[] fs = tmp.listFiles();
                    if (fs != null) {
                        for (File f : fs) {
                            f.delete();
                        }
                    }
                    tmp.delete();
                }
            }
        }

        @Override
        protected void process(List<String> trozos) {
            etiqueta.setText(trozos.get(trozos.size() - 1));
        }

        @Override
        protected void done() {
            dialogo.dispose();
        }
    }

    /** Pregunta de dónde cargar el padrón y lo carga con una ventana de progreso. Devuelve true si quedó cargado. */
    static boolean cargarInteractivo(Component padre, String motivo) {
        Object[] op = {"Descargar de la DNIT", "Elegir archivos ya descargados", "Cancelar"};
        int r = JOptionPane.showOptionDialog(padre,
                motivo + "\nEl padrón es el listado oficial y público de la DNIT; se guarda en su base de datos y después las consultas son instantáneas.\n"
                + "La descarga son 10 archivos y puede tardar unos minutos.",
                "Padrón de RUC", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (r != 0 && r != 1) {
            return false;
        }
        List<File> elegidos = null;
        if (r == 1) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Elegir los archivos ruc0.zip ... ruc9.zip");
            fc.setMultiSelectionEnabled(true);
            fc.setFileFilter(new FileNameExtensionFilter("Archivos del padrón (*.zip, *.txt)", "zip", "txt", "csv"));
            File descargas = new File(System.getProperty("user.home"), "Downloads");
            if (descargas.isDirectory()) {
                fc.setCurrentDirectory(descargas);
            }
            if (fc.showOpenDialog(padre) != JFileChooser.APPROVE_OPTION || fc.getSelectedFiles().length == 0) {
                return false;
            }
            elegidos = new ArrayList<File>();
            Collections.addAll(elegidos, fc.getSelectedFiles());
        }
        java.awt.Window w = padre == null ? null : javax.swing.SwingUtilities.getWindowAncestor(padre);
        final JDialog d = new JDialog(w, "Padrón de RUC", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JLabel et = new JLabel("Preparando...");
        et.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 22, 8, 22));
        JProgressBar barra = new JProgressBar();
        barra.setIndeterminate(true);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panel.add(et, java.awt.BorderLayout.NORTH);
        javax.swing.JPanel pb = new javax.swing.JPanel(new java.awt.BorderLayout());
        pb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 22, 0, 22));
        pb.add(barra, java.awt.BorderLayout.CENTER);
        panel.add(pb, java.awt.BorderLayout.CENTER);
        d.setContentPane(panel);
        d.setSize(460, 130);
        d.setLocationRelativeTo(padre);
        Tarea t = new Tarea(d, et, elegidos);
        t.execute();
        d.setVisible(true); // modal: vuelve cuando la tarea termina
        int n;
        try {
            n = t.get();
        } catch (Exception e) {
            n = -1;
            t.error = e;
        }
        if (n < 0) {
            Throwable e = t.error;
            JOptionPane.showMessageDialog(padre, "No se pudo cargar el padrón de RUC:\n" + (e == null ? "error desconocido" : e.getMessage())
                    + (elegidos == null ? "\n\nPuede descargar los archivos desde la página de la DNIT y elegirlos con la opción \"Elegir archivos ya descargados\"." : ""),
                    "Padrón de RUC", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        JOptionPane.showMessageDialog(padre, "Padrón actualizado: " + new java.text.DecimalFormat("#,##0").format(n) + " contribuyentes.",
                "Padrón de RUC", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    /** Consulta con avisos al usuario: pide cargar el padrón si falta y ofrece actualizarlo si el RUC no aparece. */
    static Resultado consultar(Component padre, String texto) {
        if (soloDigitos(texto).length() < 3) {
            JOptionPane.showMessageDialog(padre, "Escriba el RUC (con o sin dígito verificador) y vuelva a buscar.", "Buscar RUC",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        try {
            if (cantidad() == 0 && !cargarInteractivo(padre, "Todavía no se cargó el padrón de RUC en este sistema.")) {
                return null;
            }
            Resultado r = buscar(texto);
            if (r != null) {
                return r;
            }
            Date act = actualizadoEl();
            String cuando = act == null ? "" : " (actualizado el " + new SimpleDateFormat("dd/MM/yyyy").format(act) + ")";
            int op = JOptionPane.showConfirmDialog(padre, "No se encontró el RUC " + texto.trim() + " en el padrón" + cuando + ".\n"
                    + "Si es un contribuyente nuevo puede que aparezca al actualizar el padrón. ¿Desea actualizarlo ahora?",
                    "Buscar RUC", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (op == JOptionPane.YES_OPTION && cargarInteractivo(padre, "Se actualizará el padrón de RUC.")) {
                r = buscar(texto);
                if (r == null) {
                    JOptionPane.showMessageDialog(padre, "El RUC " + texto.trim() + " no figura en el padrón de la DNIT.", "Buscar RUC",
                            JOptionPane.WARNING_MESSAGE);
                }
                return r;
            }
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo consultar el padrón de RUC:\n" + e.getMessage(), "Buscar RUC", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static void poner(JTextField campo, String valor, boolean soloSiVacio) {
        if (campo == null) {
            return;
        }
        if (soloSiVacio && campo.getText() != null && campo.getText().trim().length() > 0) {
            return;
        }
        campo.setText(valor);
    }

    /**
     * Consulta el RUC escrito y completa el formulario del cliente: RUC con su dígito verificador y razón social;
     * en personas físicas también la cédula, el apellido y el nombre (si estaban vacíos). Avisa si el contribuyente
     * no está activo.
     */
    static void completar(Component padre, JTextField ruc, JTextField razon, JTextField nombre, JTextField apellido, JTextField ci) {
        Resultado r = consultar(padre, ruc.getText());
        if (r == null) {
            return;
        }
        ruc.setText(r.rucConDv());
        razon.setText(r.razon.toUpperCase());
        if (r.esPersonaFisica()) {
            poner(ci, r.ruc, true);
            int coma = r.razon.indexOf(',');
            if (coma > 0) {
                poner(apellido, r.razon.substring(0, coma).trim().toUpperCase(), true);
                poner(nombre, r.razon.substring(coma + 1).trim().toUpperCase(), true);
            }
        }
        if (r.estado.length() > 0 && !r.estado.toUpperCase().startsWith("ACTIVO")) {
            JOptionPane.showMessageDialog(padre, "Atención: según el padrón de la DNIT este contribuyente figura como \"" + r.estado + "\".",
                    "Buscar RUC", JOptionPane.WARNING_MESSAGE);
        }
    }
}

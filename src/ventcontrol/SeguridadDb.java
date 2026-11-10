package ventcontrol;

import claseConectar.conectar;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Usuarios, roles, permisos e inicio de sesión. Todo con consultas parametrizadas. Las claves se guardan con hash (PBKDF2 con sal);
 * las claves viejas, en texto plano, se aceptan una vez y se convierten solas al primer ingreso correcto. Los ids (usuario.id,
 * usuario.rol_id, rol.id) se tratan como texto para no depender de si la columna es numérica o varchar.
 */
final class SeguridadDb {

    /** Id del rol Administrador: siempre tiene todos los permisos y no se puede borrar ni recortar. */
    static final String ROL_ADMIN = "0";

    /** Un rol con la cantidad de usuarios que lo tienen. */
    static final class Rol {

        String id = "", nombre = "";
        int usuarios;

        boolean admin() {
            return ROL_ADMIN.equals(id);
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    /** Un usuario del sistema (sin la clave). */
    static final class Usuario {

        String id = "", nombre = "", email = "", estado = "", rolId = "", rolNombre = "";

        boolean activo() {
            return !inactivo(estado);
        }
    }

    /** Resultado de intentar ingresar. */
    static final class Ingreso {

        boolean ok;
        int id = -1;
        String mensaje = "";
    }

    private static boolean esquemaListo;

    private SeguridadDb() {
    }

    static boolean inactivo(String estado) {
        String e = estado == null ? "" : estado.trim().toLowerCase();
        return e.equals("inactivo") || e.equals("baja") || e.equals("bloqueado");
    }

    // ------------------------------------------------------------------ conexión y esquema

    private static Connection conexion() throws SQLException {
        Connection cn = new conectar().conexion();
        if (cn == null) {
            throw new SQLException("Sin conexión con la base de datos.");
        }
        return cn;
    }

    private static void cerrar(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException ignorada) {
            }
        }
    }

    private static void deshacer(Connection cn) {
        try {
            cn.rollback();
        } catch (SQLException ignorada) {
        }
    }

    private static String nuevoId(Connection cn, String tabla) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(MAX(CASE WHEN id::text ~ '^[0-9]+$' THEN id::text::bigint END), 0) + 1 FROM " + tabla);
        ResultSet rs = ps.executeQuery();
        rs.next();
        String v = String.valueOf(rs.getLong(1));
        rs.close();
        ps.close();
        return v;
    }

    private static void texto(PreparedStatement ps, int i, String v) throws SQLException {
        ps.setObject(i, v, Types.OTHER);
    }

    /** Crea lo que falta (tablas de permisos, columna de clave más ancha) y carga los roles iniciales la primera vez. */
    static synchronized void asegurarEsquema() throws SQLException {
        if (esquemaListo) {
            return;
        }
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            Statement st = cn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS rol (id integer PRIMARY KEY, nombre varchar(60))");
            st.execute("CREATE TABLE IF NOT EXISTS rol_permiso (rol_id varchar(20) NOT NULL, permiso varchar(60) NOT NULL, PRIMARY KEY (rol_id, permiso))");
            st.execute("CREATE TABLE IF NOT EXISTS seguridad_config (clave varchar(60) PRIMARY KEY, valor varchar(200))");
            st.close();
            // la clave con hash es más larga que una clave común
            PreparedStatement ps = cn.prepareStatement("SELECT character_maximum_length FROM information_schema.columns WHERE table_name = 'usuario' AND column_name = 'clave' "
                    + "AND table_schema = current_schema()");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int largo = rs.getInt(1);
                if (!rs.wasNull() && largo < 200) {
                    Statement a = cn.createStatement();
                    a.execute("ALTER TABLE usuario ALTER COLUMN clave TYPE varchar(255)");
                    a.close();
                }
            }
            rs.close();
            ps.close();
            sembrar(cn);
            cn.commit();
            esquemaListo = true;
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    private static boolean haySemilla(Connection cn) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT 1 FROM seguridad_config WHERE clave = 'semilla_roles'");
        ResultSet rs = ps.executeQuery();
        boolean hay = rs.next();
        rs.close();
        ps.close();
        return hay;
    }

    private static boolean existeRolNombre(Connection cn, String nombre) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT 1 FROM rol WHERE UPPER(TRIM(nombre)) = UPPER(?)");
        ps.setString(1, nombre.trim());
        ResultSet rs = ps.executeQuery();
        boolean hay = rs.next();
        rs.close();
        ps.close();
        return hay;
    }

    private static String insertarRol(Connection cn, String nombre) throws SQLException {
        String id = nuevoId(cn, "rol");
        PreparedStatement ps = cn.prepareStatement("INSERT INTO rol (id, nombre) VALUES (?, ?)");
        texto(ps, 1, id);
        ps.setString(2, nombre.trim());
        ps.executeUpdate();
        ps.close();
        return id;
    }

    private static void poner(Connection cn, String rolId, Set<String> permisos) throws SQLException {
        PreparedStatement del = cn.prepareStatement("DELETE FROM rol_permiso WHERE rol_id = ?");
        del.setString(1, rolId);
        del.executeUpdate();
        del.close();
        PreparedStatement ins = cn.prepareStatement("INSERT INTO rol_permiso (rol_id, permiso) VALUES (?, ?)");
        for (String p : permisos) {
            if (Permisos.existe(p)) {
                ins.setString(1, rolId);
                ins.setString(2, p);
                ins.addBatch();
            }
        }
        ins.executeBatch();
        ins.close();
    }

    /**
     * Primera vez: asegura los roles 0 (Administrador) y 1 (Operador); todo rol que no sea administrador conserva lo que ya veía
     * (el menú recortado) y se agregan "Cajero / Vendedor" y "Encargado de compras" como puntos de partida editables.
     */
    private static void sembrar(Connection cn) throws SQLException {
        if (haySemilla(cn)) {
            return;
        }
        PreparedStatement ps = cn.prepareStatement("SELECT id::text FROM rol");
        ResultSet rs = ps.executeQuery();
        List<String> ids = new ArrayList<String>();
        while (rs.next()) {
            ids.add(rs.getString(1).trim());
        }
        rs.close();
        ps.close();
        if (!ids.contains("0")) {
            PreparedStatement i = cn.prepareStatement("INSERT INTO rol (id, nombre) VALUES (?, 'Administrador')");
            texto(i, 1, "0");
            i.executeUpdate();
            i.close();
        }
        if (!ids.contains("1")) {
            PreparedStatement i = cn.prepareStatement("INSERT INTO rol (id, nombre) VALUES (?, 'Operador')");
            texto(i, 1, "1");
            i.executeUpdate();
            i.close();
            ids.add("1");
        }
        for (String id : ids) {
            if (!ROL_ADMIN.equals(id)) {
                poner(cn, id, Permisos.operador());
            }
        }
        if (!existeRolNombre(cn, "Cajero / Vendedor")) {
            poner(cn, insertarRol(cn, "Cajero / Vendedor"), Permisos.cajero());
        }
        if (!existeRolNombre(cn, "Encargado de compras")) {
            poner(cn, insertarRol(cn, "Encargado de compras"), Permisos.encargadoCompras());
        }
        PreparedStatement m = cn.prepareStatement("INSERT INTO seguridad_config (clave, valor) VALUES ('semilla_roles', ?)");
        m.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        m.executeUpdate();
        m.close();
    }

    // ------------------------------------------------------------------ claves

    private static final String PREFIJO = "pbkdf2$";
    private static final int ITERACIONES = 20000;

    private static String hex(byte[] b) {
        StringBuilder s = new StringBuilder();
        for (byte x : b) {
            s.append(String.format("%02x", x & 0xff));
        }
        return s.toString();
    }

    private static byte[] deHex(String h) {
        byte[] b = new byte[h.length() / 2];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) Integer.parseInt(h.substring(2 * i, 2 * i + 2), 16);
        }
        return b;
    }

    private static byte[] derivar(String clave, byte[] sal, int iter) {
        try {
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, iter, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cifrar la clave: " + e.getMessage(), e);
        }
    }

    /** Guarda la clave con sal e iteraciones: "pbkdf2$iteraciones$sal$hash". */
    static String hash(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return PREFIJO + ITERACIONES + "$" + hex(sal) + "$" + hex(derivar(clave == null ? "" : clave, sal, ITERACIONES));
    }

    static boolean cifrada(String guardada) {
        return guardada != null && guardada.startsWith(PREFIJO);
    }

    /** true si la clave escrita corresponde a la guardada (con hash o, en usuarios viejos, en texto plano). */
    static boolean verificar(String clave, String guardada) {
        String c = clave == null ? "" : clave;
        if (guardada == null) {
            return c.length() == 0;
        }
        if (!cifrada(guardada)) {
            // clave antigua sin cifrar: el formulario viejo forzaba mayúsculas, así que se compara sin distinguirlas
            return c.equalsIgnoreCase(guardada);
        }
        try {
            String[] p = guardada.split("\\$");
            if (p.length != 4) {
                return false;
            }
            byte[] esperado = deHex(p[3]);
            byte[] calculado = derivar(c, deHex(p[2]), Integer.parseInt(p[1]));
            return MessageDigest.isEqual(esperado, calculado);
        } catch (RuntimeException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ ingreso

    /** Valida usuario y clave. Si la clave estaba en texto plano la deja cifrada. */
    static Ingreso ingresar(String usuario, String clave) {
        Ingreso r = new Ingreso();
        String u = usuario == null ? "" : usuario.trim();
        Connection cn = null;
        try {
            asegurarEsquema();
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT id::text, clave, COALESCE(estado::text, '') FROM usuario WHERE UPPER(TRIM(usuario)) = UPPER(?) ORDER BY (TRIM(usuario) = ?) DESC");
            ps.setString(1, u);
            ps.setString(2, u);
            ResultSet rs = ps.executeQuery();
            boolean existe = false;
            while (rs.next()) {
                existe = true;
                String guardada = rs.getString(2);
                if (!verificar(clave, guardada)) {
                    continue;
                }
                if (inactivo(rs.getString(3))) {
                    r.mensaje = "El usuario está inactivo. Consulte con el administrador.";
                    break;
                }
                String id = rs.getString(1).trim();
                try {
                    r.id = Integer.parseInt(id);
                } catch (NumberFormatException e) {
                    r.mensaje = "El usuario tiene un código inválido.";
                    break;
                }
                r.ok = true;
                if (!cifrada(guardada)) {
                    try {
                        PreparedStatement up = cn.prepareStatement("UPDATE usuario SET clave = ? WHERE id::text = ?");
                        up.setString(1, hash(clave));
                        up.setString(2, id);
                        up.executeUpdate();
                        up.close();
                    } catch (SQLException ignorada) {
                        // sigue funcionando con la clave vieja
                    }
                }
                break;
            }
            rs.close();
            ps.close();
            if (!r.ok && r.mensaje.length() == 0) {
                r.mensaje = existe ? "Clave incorrecta." : "Usuario incorrecto.";
            }
        } catch (SQLException e) {
            r.ok = false;
            r.mensaje = "No se pudo comprobar el usuario: " + primeraLinea(e.getMessage());
        } finally {
            cerrar(cn);
        }
        return r;
    }

    // ------------------------------------------------------------------ roles y permisos

    static List<Rol> roles() throws SQLException {
        asegurarEsquema();
        Connection cn = conexion();
        try {
            Map<String, Integer> cuenta = new LinkedHashMap<String, Integer>();
            PreparedStatement ps = cn.prepareStatement("SELECT rol_id::text, COUNT(*) FROM usuario GROUP BY 1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cuenta.put(String.valueOf(rs.getString(1)).trim(), Integer.valueOf(rs.getInt(2)));
            }
            rs.close();
            ps.close();
            List<Rol> l = new ArrayList<Rol>();
            ps = cn.prepareStatement("SELECT id::text, nombre FROM rol ORDER BY CASE WHEN id::text ~ '^[0-9]+$' THEN id::text::bigint ELSE 999999 END, nombre");
            rs = ps.executeQuery();
            while (rs.next()) {
                Rol r = new Rol();
                r.id = rs.getString(1).trim();
                r.nombre = CajaDatos.texto(rs.getString(2));
                r.usuarios = cuenta.containsKey(r.id) ? cuenta.get(r.id).intValue() : 0;
                l.add(r);
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            cerrar(cn);
        }
    }

    /** Permisos de un rol (el administrador tiene todos). */
    static Set<String> permisosDeRol(String rolId) throws SQLException {
        if (ROL_ADMIN.equals(rolId == null ? "" : rolId.trim())) {
            return new LinkedHashSet<String>(Permisos.codigos());
        }
        asegurarEsquema();
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT permiso FROM rol_permiso WHERE rol_id = ?");
            ps.setString(1, rolId == null ? "" : rolId.trim());
            ResultSet rs = ps.executeQuery();
            Set<String> s = new LinkedHashSet<String>();
            while (rs.next()) {
                if (Permisos.existe(rs.getString(1))) {
                    s.add(rs.getString(1));
                }
            }
            rs.close();
            ps.close();
            return s;
        } finally {
            cerrar(cn);
        }
    }

    static void guardarPermisos(String rolId, Set<String> permisos) throws SQLException {
        if (ROL_ADMIN.equals(rolId)) {
            throw new SQLException("El rol Administrador siempre tiene todos los permisos.");
        }
        asegurarEsquema();
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            poner(cn, rolId, permisos);
            cn.commit();
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    /** Crea un rol; si se indica {@code copiarDe} arranca con los mismos permisos. */
    static String crearRol(String nombre, String copiarDe) throws SQLException {
        String n = nombre == null ? "" : nombre.trim();
        if (n.length() == 0) {
            throw new SQLException("Escriba el nombre del rol.");
        }
        asegurarEsquema();
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            if (existeRolNombre(cn, n)) {
                throw new SQLException("Ya existe un rol llamado \"" + n + "\".");
            }
            String id = insertarRol(cn, n);
            if (copiarDe != null && copiarDe.length() > 0 && !ROL_ADMIN.equals(copiarDe)) {
                poner(cn, id, permisosDeRol(copiarDe));
            }
            cn.commit();
            return id;
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    static void renombrarRol(String id, String nombre) throws SQLException {
        String n = nombre == null ? "" : nombre.trim();
        if (n.length() == 0) {
            throw new SQLException("Escriba el nombre del rol.");
        }
        if (ROL_ADMIN.equals(id)) {
            throw new SQLException("El rol Administrador no se puede renombrar.");
        }
        asegurarEsquema();
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT 1 FROM rol WHERE UPPER(TRIM(nombre)) = UPPER(?) AND id::text <> ?");
            ps.setString(1, n);
            ps.setString(2, id);
            ResultSet rs = ps.executeQuery();
            boolean repetido = rs.next();
            rs.close();
            ps.close();
            if (repetido) {
                throw new SQLException("Ya existe un rol llamado \"" + n + "\".");
            }
            ps = cn.prepareStatement("UPDATE rol SET nombre = ? WHERE id::text = ?");
            ps.setString(1, n);
            ps.setString(2, id);
            ps.executeUpdate();
            ps.close();
        } finally {
            cerrar(cn);
        }
    }

    static void eliminarRol(String id) throws SQLException {
        if (ROL_ADMIN.equals(id)) {
            throw new SQLException("El rol Administrador no se puede eliminar.");
        }
        asegurarEsquema();
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM usuario WHERE rol_id::text = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int n = rs.getInt(1);
            rs.close();
            ps.close();
            if (n > 0) {
                throw new SQLException("No se puede eliminar: " + n + (n == 1 ? " usuario tiene" : " usuarios tienen") + " este rol. Cámbieles el rol primero.");
            }
            ps = cn.prepareStatement("DELETE FROM rol_permiso WHERE rol_id = ?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("DELETE FROM rol WHERE id::text = ?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            cn.commit();
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ usuarios

    private static final String SELECT_USUARIOS = "SELECT u.id::text AS id, u.usuario, COALESCE(u.email::text, '') AS email, COALESCE(u.estado::text, '') AS estado, "
            + "COALESCE(u.rol_id::text, '') AS rol_id, COALESCE(r.nombre, '(sin rol)') AS rol FROM usuario u LEFT JOIN rol r ON r.id::text = u.rol_id::text ";

    private static Usuario leerUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.id = rs.getString("id").trim();
        u.nombre = CajaDatos.texto(rs.getString("usuario"));
        u.email = CajaDatos.texto(rs.getString("email"));
        u.estado = CajaDatos.texto(rs.getString("estado"));
        u.rolId = CajaDatos.texto(rs.getString("rol_id"));
        u.rolNombre = CajaDatos.texto(rs.getString("rol"));
        return u;
    }

    static List<Usuario> usuarios() throws SQLException {
        asegurarEsquema();
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement(SELECT_USUARIOS + "ORDER BY CASE WHEN u.id::text ~ '^[0-9]+$' THEN u.id::text::bigint ELSE 999999 END");
            ResultSet rs = ps.executeQuery();
            List<Usuario> l = new ArrayList<Usuario>();
            while (rs.next()) {
                l.add(leerUsuario(rs));
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            cerrar(cn);
        }
    }

    static Usuario usuario(int id) throws SQLException {
        asegurarEsquema();
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement(SELECT_USUARIOS + "WHERE u.id::text = ?");
            ps.setString(1, String.valueOf(id));
            ResultSet rs = ps.executeQuery();
            Usuario u = rs.next() ? leerUsuario(rs) : null;
            rs.close();
            ps.close();
            return u;
        } finally {
            cerrar(cn);
        }
    }

    private static boolean nombreEnUso(Connection cn, String nombre, String exceptoId) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT 1 FROM usuario WHERE UPPER(TRIM(usuario)) = UPPER(?) AND id::text <> ?");
        ps.setString(1, nombre.trim());
        ps.setString(2, exceptoId == null ? "" : exceptoId);
        ResultSet rs = ps.executeQuery();
        boolean hay = rs.next();
        rs.close();
        ps.close();
        return hay;
    }

    /** Administradores activos distintos de {@code excepto} (para no quedarse sin ninguno). */
    private static int otrosAdministradores(Connection cn, String excepto) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(estado::text, '') FROM usuario WHERE rol_id::text = '0' AND id::text <> ?");
        ps.setString(1, excepto == null ? "" : excepto);
        ResultSet rs = ps.executeQuery();
        int n = 0;
        while (rs.next()) {
            if (!inactivo(rs.getString(1))) {
                n++;
            }
        }
        rs.close();
        ps.close();
        return n;
    }

    private static String ahora() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static void validarUsuario(String nombre, String clave, boolean claveObligatoria) throws SQLException {
        if (nombre == null || nombre.trim().length() == 0) {
            throw new SQLException("Escriba el nombre de usuario.");
        }
        if (claveObligatoria && (clave == null || clave.length() == 0)) {
            throw new SQLException("Escriba una clave.");
        }
        if (clave != null && clave.length() > 0 && clave.length() < 4) {
            throw new SQLException("La clave debe tener al menos 4 caracteres.");
        }
    }

    static void crearUsuario(String nombre, String clave, String email, String rolId, String estado, String quien) throws SQLException {
        validarUsuario(nombre, clave, true);
        asegurarEsquema();
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            if (nombreEnUso(cn, nombre, null)) {
                throw new SQLException("Ya existe un usuario llamado \"" + nombre.trim() + "\".");
            }
            String id = nuevoId(cn, "usuario");
            PreparedStatement ps = cn.prepareStatement("INSERT INTO usuario (id, usuario, clave, email, estado, usuariocreacion, fechacreacion, usuariomodificacion, fechamodificacion, rol_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            texto(ps, 1, id);
            ps.setString(2, nombre.trim());
            ps.setString(3, hash(clave));
            ps.setString(4, email == null ? "" : email.trim());
            ps.setString(5, estado == null || estado.length() == 0 ? "Activo" : estado);
            texto(ps, 6, quien == null ? "" : quien);
            texto(ps, 7, ahora());
            texto(ps, 8, "");
            texto(ps, 9, ahora());
            texto(ps, 10, rolId);
            ps.executeUpdate();
            ps.close();
            cn.commit();
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    /** Modifica un usuario; si {@code clave} viene vacía se conserva la actual. */
    static void modificarUsuario(String id, String nombre, String clave, String email, String rolId, String estado, String quien) throws SQLException {
        validarUsuario(nombre, clave, false);
        asegurarEsquema();
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            if (nombreEnUso(cn, nombre, id)) {
                throw new SQLException("Ya existe un usuario llamado \"" + nombre.trim() + "\".");
            }
            PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(rol_id::text, ''), COALESCE(estado::text, '') FROM usuario WHERE id::text = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("El usuario ya no existe.");
            }
            boolean eraAdmin = ROL_ADMIN.equals(rs.getString(1).trim()) && !inactivo(rs.getString(2));
            rs.close();
            ps.close();
            boolean seguiraAdmin = ROL_ADMIN.equals(rolId) && !inactivo(estado);
            if (eraAdmin && !seguiraAdmin && otrosAdministradores(cn, id) == 0) {
                throw new SQLException("Debe quedar al menos un administrador activo.");
            }
            boolean cambiaClave = clave != null && clave.length() > 0;
            ps = cn.prepareStatement("UPDATE usuario SET usuario = ?, " + (cambiaClave ? "clave = ?, " : "")
                    + "email = ?, estado = ?, usuariomodificacion = ?, fechamodificacion = ?, rol_id = ? WHERE id::text = ?");
            int i = 1;
            ps.setString(i++, nombre.trim());
            if (cambiaClave) {
                ps.setString(i++, hash(clave));
            }
            ps.setString(i++, email == null ? "" : email.trim());
            ps.setString(i++, estado == null || estado.length() == 0 ? "Activo" : estado);
            texto(ps, i++, quien == null ? "" : quien);
            texto(ps, i++, ahora());
            texto(ps, i++, rolId);
            ps.setString(i, id);
            ps.executeUpdate();
            ps.close();
            cn.commit();
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    static void eliminarUsuario(String id, int usuarioActual) throws SQLException {
        if (String.valueOf(usuarioActual).equals(id)) {
            throw new SQLException("No puede eliminar el usuario con el que inició sesión.");
        }
        asegurarEsquema();
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(rol_id::text, ''), COALESCE(estado::text, '') FROM usuario WHERE id::text = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            boolean admin = rs.next() && ROL_ADMIN.equals(rs.getString(1).trim()) && !inactivo(rs.getString(2));
            rs.close();
            ps.close();
            if (admin && otrosAdministradores(cn, id) == 0) {
                throw new SQLException("Debe quedar al menos un administrador activo.");
            }
            ps = cn.prepareStatement("DELETE FROM usuario WHERE id::text = ?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            cn.commit();
        } catch (SQLException e) {
            deshacer(cn);
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                throw new SQLException("El usuario tiene ventas, compras o movimientos registrados y no se puede eliminar. Márquelo como Inactivo.");
            }
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    private static String primeraLinea(String s) {
        if (s == null) {
            return "";
        }
        int i = s.indexOf('\n');
        return i > 0 ? s.substring(0, i) : s;
    }

    /** Solo para pruebas: permite volver a revisar el esquema. */
    static synchronized void reiniciar() {
        esquemaListo = false;
    }
}

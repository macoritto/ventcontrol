package ventcontrol;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Usuario que inició sesión y sus permisos. Se carga al ingresar ({@link #iniciar(int)}) y se consulta con {@link #puede(String)}.
 * El administrador (rol 0) puede todo; si los permisos no se pueden leer, un usuario común no puede nada.
 */
final class Sesion {

    private static int usuarioId = -1;
    private static String usuarioNombre = "", rolId = "", rolNombre = "";
    private static Set<String> permisos = Collections.emptySet();

    private Sesion() {
    }

    /** Carga el usuario, su rol y los permisos del rol desde la base. */
    static synchronized void iniciar(int id) {
        usuarioId = id;
        usuarioNombre = "";
        rolId = "";
        rolNombre = "";
        permisos = Collections.emptySet();
        try {
            SeguridadDb.Usuario u = SeguridadDb.usuario(id);
            if (u != null) {
                usuarioNombre = u.nombre;
                rolId = u.rolId.trim();
                rolNombre = u.rolNombre;
                permisos = SeguridadDb.permisosDeRol(rolId);
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron leer los permisos: " + e.getMessage());
            permisos = Collections.emptySet();
        }
    }

    /** Vuelve a leer los permisos del usuario actual (por ejemplo, tras editar su rol). */
    static void recargar() {
        if (usuarioId >= 0) {
            iniciar(usuarioId);
        }
    }

    static synchronized void cerrar() {
        usuarioId = -1;
        usuarioNombre = "";
        rolId = "";
        rolNombre = "";
        permisos = Collections.emptySet();
    }

    /** true si ya hay sesión iniciada para ese usuario. */
    static synchronized boolean activa(int id) {
        return usuarioId == id && id >= 0;
    }

    static synchronized boolean esAdmin() {
        return usuarioId >= 0 && SeguridadDb.ROL_ADMIN.equals(rolId);
    }

    /** true si el usuario actual tiene ese permiso (el administrador los tiene todos). */
    static synchronized boolean puede(String codigo) {
        if (usuarioId < 0) {
            return false;
        }
        return esAdmin() || permisos.contains(codigo);
    }

    /** true si tiene al menos uno de los permisos. */
    static boolean puedeAlguno(String... codigos) {
        for (String c : codigos) {
            if (puede(c)) {
                return true;
            }
        }
        return false;
    }

    static synchronized int usuarioId() {
        return usuarioId;
    }

    static synchronized String usuarioNombre() {
        return usuarioNombre;
    }

    static synchronized String rolNombre() {
        return rolNombre;
    }

    static synchronized Set<String> permisos() {
        return new LinkedHashSet<String>(permisos);
    }
}

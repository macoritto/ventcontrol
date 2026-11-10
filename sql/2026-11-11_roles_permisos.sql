-- Roles y permisos (la aplicación crea todo esto sola al iniciar; este script es solo para referencia o para correrlo a mano).
-- Los permisos de cada rol se guardan como filas (rol_id, permiso); el rol 0 (Administrador) tiene siempre todos y no se guarda nada.

CREATE TABLE IF NOT EXISTS rol (id integer PRIMARY KEY, nombre varchar(60));
CREATE TABLE IF NOT EXISTS rol_permiso (
    rol_id  varchar(20) NOT NULL,
    permiso varchar(60) NOT NULL,
    PRIMARY KEY (rol_id, permiso)
);
CREATE TABLE IF NOT EXISTS seguridad_config (clave varchar(60) PRIMARY KEY, valor varchar(200));

-- La clave con hash (PBKDF2) es más larga que una clave común.
ALTER TABLE usuario ALTER COLUMN clave TYPE varchar(255);

-- Las claves existentes siguen funcionando: se convierten a hash solas la primera vez que cada usuario ingresa.
-- En el primer arranque la aplicación carga los roles iniciales: Operador (lo que hasta ahora veía cualquier usuario que no era
-- administrador, sin eliminar ni anular), Cajero / Vendedor y Encargado de compras. Se pueden editar desde
-- Sistema > Administrar Usuarios > Roles y permisos.

-- Cajas, turnos de caja y usuarios por caja.
-- El sistema ejecuta lo mismo automáticamente la primera vez que se usa (clase TurnoCaja); este script es
-- para quien prefiera aplicarlo a mano. Se puede ejecutar más de una vez sin problema.

-- Cajas físicas (puestos de cobro). Se crea "Caja 1": hasta ahora todo se guardaba con caja = '1'.
CREATE TABLE IF NOT EXISTS cajas (
    id      integer PRIMARY KEY,
    nombre  varchar(60) NOT NULL,
    activa  boolean NOT NULL DEFAULT true
);
INSERT INTO cajas (id, nombre) SELECT 1, 'Caja 1' WHERE NOT EXISTS (SELECT 1 FROM cajas);

-- Usuarios autorizados a abrir cada caja. Una caja sin filas aquí la puede abrir cualquier usuario.
CREATE TABLE IF NOT EXISTS caja_usuario (
    caja_id     integer NOT NULL,
    usuario_id  integer NOT NULL,
    PRIMARY KEY (caja_id, usuario_id)
);

-- Turno: un usuario abre una caja con un monto inicial y la cierra contando el efectivo.
CREATE TABLE IF NOT EXISTS turno (
    id             integer PRIMARY KEY,
    caja_id        integer NOT NULL,
    usuario_id     integer NOT NULL,
    apertura       timestamp NOT NULL DEFAULT now(),
    monto_inicial  bigint NOT NULL DEFAULT 0,
    estado         varchar(10) NOT NULL DEFAULT 'Abierto',   -- 'Abierto' | 'Cerrado'
    cierre         timestamp,
    esperado       bigint,        -- efectivo que el sistema calculó al cerrar
    contado        bigint,        -- efectivo que el cajero contó
    diferencia     bigint,        -- contado - esperado (positivo: sobrante, negativo: faltante)
    observacion    varchar(300)
);
-- A lo sumo un turno abierto por caja y uno por usuario.
CREATE UNIQUE INDEX IF NOT EXISTS ux_turno_caja_abierto    ON turno (caja_id)    WHERE estado = 'Abierto';
CREATE UNIQUE INDEX IF NOT EXISTS ux_turno_usuario_abierto ON turno (usuario_id) WHERE estado = 'Abierto';

-- Cada operación de caja queda ligada al turno en que se hizo (lo anterior queda en NULL = "sin turno").
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'venta' AND column_name = 'turno_id') THEN
        ALTER TABLE venta ADD COLUMN turno_id integer; END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'venta' AND column_name = 'medio_pago') THEN
        ALTER TABLE venta ADD COLUMN medio_pago varchar(20) DEFAULT 'Efectivo'; END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'pago' AND column_name = 'turno_id') THEN
        ALTER TABLE pago ADD COLUMN turno_id integer; END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'pago' AND column_name = 'medio_pago') THEN
        ALTER TABLE pago ADD COLUMN medio_pago varchar(20) DEFAULT 'Efectivo'; END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'retiro' AND column_name = 'turno_id') THEN
        ALTER TABLE retiro ADD COLUMN turno_id integer; END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'devolucion' AND column_name = 'turno_id') THEN
        ALTER TABLE devolucion ADD COLUMN turno_id integer; END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'caja' AND column_name = 'turno_id') THEN
        ALTER TABLE caja ADD COLUMN turno_id integer; END IF;
END $$;
CREATE INDEX IF NOT EXISTS idx_venta_turno      ON venta (turno_id);
CREATE INDEX IF NOT EXISTS idx_pago_turno       ON pago (turno_id);
CREATE INDEX IF NOT EXISTS idx_retiro_turno     ON retiro (turno_id);
CREATE INDEX IF NOT EXISTS idx_devolucion_turno ON devolucion (turno_id);
CREATE INDEX IF NOT EXISTS idx_caja_turno       ON caja (turno_id);

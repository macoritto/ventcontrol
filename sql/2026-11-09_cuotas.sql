-- Ventas a cuotas y cierre de cuenta del cliente.
-- (La tabla "cuota" que ya existe en la base es otra y no se toca: estas usan plan_cuotas y cuota_detalle.)
-- El sistema crea estas tablas solo la primera vez que se usan; este script es por si el usuario
-- de la base no tiene permiso para crearlas. Se puede ejecutar más de una vez sin problema.

-- Si un intento anterior de este script dejó una tabla cuota_plan vacía, se descarta (ya no se usa).
DROP TABLE IF EXISTS cuota_plan;

CREATE TABLE IF NOT EXISTS plan_cuotas (
    id             serial PRIMARY KEY,
    venta_cod      varchar(30) NOT NULL,          -- venta.codventa
    cliente_id     varchar(30) NOT NULL,          -- cliente.id
    total          bigint      NOT NULL,          -- total de la venta
    entrega        bigint      NOT NULL DEFAULT 0,-- lo pagado al vender (el resto se divide en cuotas)
    cuotas         integer     NOT NULL,
    intervalo_dias integer     NOT NULL DEFAULT 30, -- 7 semanal, 15 quincenal, 30 mensual (mes calendario)
    primer_venc    date        NOT NULL,
    creado         timestamp   NOT NULL DEFAULT now(),
    usuario_id     varchar(30),
    estado         varchar(12) NOT NULL DEFAULT 'Activo'  -- Activo / Cerrado
);

CREATE TABLE IF NOT EXISTS cuota_detalle (
    id          serial PRIMARY KEY,
    plan_id     integer     NOT NULL REFERENCES plan_cuotas (id) ON DELETE CASCADE,
    nro         integer     NOT NULL,
    vencimiento date        NOT NULL,
    monto       bigint      NOT NULL,
    pagado      bigint      NOT NULL DEFAULT 0,
    fecha_pago  date,
    estado      varchar(12) NOT NULL DEFAULT 'Pendiente'  -- Pendiente / Pagada / Condonada
);

-- Cada cierre de cuenta de un cliente (saldo_condonado > 0 si todavía debía algo).
-- A qué cuotas se aplicó cada pago (para poder deshacerlo si el pago se anula o se modifica).
CREATE TABLE IF NOT EXISTS cuota_pago (
    id       serial PRIMARY KEY,
    cuota_id integer     NOT NULL REFERENCES cuota_detalle (id) ON DELETE CASCADE,
    pago_id  varchar(30) NOT NULL,
    monto    bigint      NOT NULL
);

CREATE TABLE IF NOT EXISTS cuenta_cierre (
    id              serial PRIMARY KEY,
    cliente_id      varchar(30) NOT NULL,
    fecha           timestamp   NOT NULL DEFAULT now(),
    usuario_id      varchar(30),
    saldo_condonado bigint      NOT NULL DEFAULT 0,
    motivo          text,
    ref_extracto    bigint      NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_cuota_pago ON cuota_pago (pago_id);
CREATE INDEX IF NOT EXISTS idx_plan_cuotas ON cuota_detalle (plan_id);
CREATE INDEX IF NOT EXISTS idx_plan_cuotas_cliente ON plan_cuotas (cliente_id);

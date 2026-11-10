-- Cuentas por pagar: compras a crédito en cuotas a proveedores, pagos desde la caja o el banco y cierre de cuenta.
-- El sistema crea estas tablas solo la primera vez que se usan; este script es por si el usuario de la base
-- no tiene permiso para crearlas. Se puede ejecutar más de una vez sin problema.
-- Una compra a crédito queda en compra.estado = 'Credito' (las de contado siguen en 'Pagado').
-- Un pago desde la caja se anota además en "retiro" (del turno abierto), así baja el efectivo esperado del arqueo.
-- Un pago desde el banco descuenta el saldo de la cuenta (banco_cuenta) y sale como egreso en el módulo Banco.

CREATE TABLE IF NOT EXISTS plan_cuotas_prov (
    id             serial PRIMARY KEY,
    compra_cod     varchar(30) NOT NULL,           -- compra.codcompra
    proveedor_id   varchar(30) NOT NULL,           -- proveedor.codprov
    total          bigint      NOT NULL,           -- total de la compra
    entrega        bigint      NOT NULL DEFAULT 0, -- lo pagado al comprar (el resto se divide en cuotas)
    cuotas         integer     NOT NULL,
    intervalo_dias integer     NOT NULL DEFAULT 30,
    primer_venc    date        NOT NULL,
    creado         timestamp   NOT NULL DEFAULT now(),
    usuario_id     varchar(30),
    estado         varchar(12) NOT NULL DEFAULT 'Activo'  -- Activo / Cerrado
);

CREATE TABLE IF NOT EXISTS cuota_prov (
    id          serial PRIMARY KEY,
    plan_id     integer     NOT NULL REFERENCES plan_cuotas_prov (id) ON DELETE CASCADE,
    nro         integer     NOT NULL,
    vencimiento date        NOT NULL,
    monto       bigint      NOT NULL,
    pagado      bigint      NOT NULL DEFAULT 0,
    fecha_pago  date,
    estado      varchar(12) NOT NULL DEFAULT 'Pendiente'  -- Pendiente / Pagada / Condonada
);

CREATE TABLE IF NOT EXISTS pago_proveedor (
    id           serial PRIMARY KEY,
    proveedor_id varchar(30) NOT NULL,
    fecha        date        NOT NULL,
    monto        bigint      NOT NULL,
    origen       varchar(10) NOT NULL,   -- Caja / Banco / Cierre (condonación al cerrar la cuenta)
    cuenta_id    integer,                -- banco_cuenta.id cuando sale del banco
    turno_id     integer,                -- turno de caja cuando sale de la caja
    caja_id      integer,
    retiro_cod   varchar(30),            -- retiro.cod que se anotó en la caja
    descripcion  varchar(250),
    usuario_id   varchar(30),
    compra_cod   varchar(30),            -- compra a la que pertenece una entrega inicial
    entrega      boolean     NOT NULL DEFAULT false,
    creado       timestamp   NOT NULL DEFAULT now()
);

-- Cómo se aplicó cada pago a las cuotas (para poder anularlo o corregirlo)
CREATE TABLE IF NOT EXISTS cuota_prov_pago (
    id       serial PRIMARY KEY,
    cuota_id integer NOT NULL REFERENCES cuota_prov (id) ON DELETE CASCADE,
    pago_id  integer NOT NULL,
    monto    bigint  NOT NULL
);

CREATE TABLE IF NOT EXISTS cuenta_prov_cierre (
    id              serial PRIMARY KEY,
    proveedor_id    varchar(30) NOT NULL,
    fecha           timestamp   NOT NULL DEFAULT now(),
    usuario_id      varchar(30),
    saldo_condonado bigint      NOT NULL DEFAULT 0,
    motivo          text,
    ref_compra      bigint      NOT NULL DEFAULT 0,
    ref_pago        bigint      NOT NULL DEFAULT 0,
    pago_id         integer
);

CREATE INDEX IF NOT EXISTS idx_plan_prov        ON plan_cuotas_prov (proveedor_id);
CREATE INDEX IF NOT EXISTS idx_cuota_prov_plan  ON cuota_prov (plan_id);
CREATE INDEX IF NOT EXISTS idx_pago_prov        ON pago_proveedor (proveedor_id);
CREATE INDEX IF NOT EXISTS idx_pago_prov_cuenta ON pago_proveedor (cuenta_id);
CREATE INDEX IF NOT EXISTS idx_cuota_prov_pago  ON cuota_prov_pago (pago_id);

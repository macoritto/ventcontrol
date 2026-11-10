-- Modulo Banco: cuentas bancarias y la cuenta destino de cada cobro por QR, transferencia o tarjeta.
-- El sistema crea esto solo la primera vez que se usa; este script sirve para correrlo a mano si hace falta.
-- Requiere antes sql/2026-11-08_cajas_turnos.sql (columna medio_pago de venta y pago).

CREATE TABLE IF NOT EXISTS banco_cuenta (
    id            integer PRIMARY KEY,
    nombre        varchar(80) NOT NULL,
    banco         varchar(80),
    numero        varchar(40),
    saldo_inicial bigint NOT NULL DEFAULT 0,
    activa        boolean NOT NULL DEFAULT true
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'venta' AND column_name = 'cuenta_id') THEN
        ALTER TABLE venta ADD COLUMN cuenta_id integer;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'pago' AND column_name = 'cuenta_id') THEN
        ALTER TABLE pago ADD COLUMN cuenta_id integer;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_venta_cuenta ON venta (cuenta_id);
CREATE INDEX IF NOT EXISTS idx_pago_cuenta ON pago (cuenta_id);

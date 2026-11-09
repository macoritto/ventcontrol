-- =============================================================================
-- ventcontrol - Inventario con CONTEO FISICO
-- Fecha   : 2026-11-07
-- Base    : PostgreSQL 9.5 o superior (base "macocar")
-- Uso     : ejecutar UNA vez en pgAdmin / psql sobre la base macocar.
--           Es seguro ejecutarlo varias veces (no repite columnas ni indices).
--           El programa tambien lo aplica solo al abrir el modulo de Inventario
--           (si el usuario de la conexion tiene permiso para modificar tablas).
-- =============================================================================
--
-- Que agrega
--   detinv.contado         cantidad contada fisicamente (NULL = no se conto ese producto)
--   detinv.stock_sistema   stock que tenia el sistema al guardar, ANTES de ajustar
--   detinv.costo           costo unitario del producto al guardar (para valorizar la diferencia)
--   inventario.alcance     que se conto (ej. "Marca: BOSCH · Tipo: FILTROS"); permite inventarios parciales
--   inventario.ajustado    1 si al guardar se ajusto el stock a lo contado, 0 si solo se guardo el conteo
--
-- Notas
--   * detinv.stock sigue siendo el stock con el que queda el producto DESPUES del inventario
--     (si se ajusto, es lo contado). Es la base del siguiente periodo, igual que antes.
--   * No se modifica ni se borra ningun dato existente. Los inventarios viejos quedan con
--     contado / stock_sistema / costo en NULL y ajustado = 0.
--   * Los ajustes de stock se registran en la tabla "reajuste" (estado Suma / Resta), con la
--     descripcion "INVENTARIO N° <id>", igual que los reajustes manuales.
-- =============================================================================

BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                    WHERE table_schema = current_schema() AND table_name = 'detinv' AND column_name = 'contado') THEN
        ALTER TABLE detinv ADD COLUMN contado numeric(14,3);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                    WHERE table_schema = current_schema() AND table_name = 'detinv' AND column_name = 'stock_sistema') THEN
        ALTER TABLE detinv ADD COLUMN stock_sistema numeric(14,3);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                    WHERE table_schema = current_schema() AND table_name = 'detinv' AND column_name = 'costo') THEN
        ALTER TABLE detinv ADD COLUMN costo numeric(14,2);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                    WHERE table_schema = current_schema() AND table_name = 'inventario' AND column_name = 'alcance') THEN
        ALTER TABLE inventario ADD COLUMN alcance varchar(150);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                    WHERE table_schema = current_schema() AND table_name = 'inventario' AND column_name = 'ajustado') THEN
        ALTER TABLE inventario ADD COLUMN ajustado smallint NOT NULL DEFAULT 0;
    END IF;
END
$$;

-- Acelera la consulta de "ultima foto de cada producto" y la carga de un inventario.
CREATE INDEX IF NOT EXISTS idx_detinv_inventario ON detinv (inventario);

COMMIT;

-- -----------------------------------------------------------------------------
-- Para deshacer (solo si hiciera falta; borra los conteos guardados con el nuevo modulo):
--   ALTER TABLE detinv      DROP COLUMN contado, DROP COLUMN stock_sistema, DROP COLUMN costo;
--   ALTER TABLE inventario  DROP COLUMN alcance, DROP COLUMN ajustado;
--   DROP INDEX IF EXISTS idx_detinv_inventario;
-- -----------------------------------------------------------------------------

-- ============================================================
-- V2 - Initial Reference Data
-- SRM Credit Engine
-- ============================================================

-- ============================================================
-- Currencies
-- ============================================================

INSERT INTO currency (
    code,
    name
)
VALUES
    ('BRL', 'Brazilian Real'),
    ('USD', 'United States Dollar')
    ON CONFLICT (code) DO NOTHING;


-- ============================================================
-- Receivable Types
-- ============================================================

INSERT INTO receivable_type (
    name,
    spread_rate
)
VALUES
    ('Duplicata Mercantil', 0.015000),
    ('Cheque Pré-datado', 0.025000)
    ON CONFLICT (name) DO NOTHING;
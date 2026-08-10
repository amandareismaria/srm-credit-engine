ALTER TABLE settlement
    ADD CONSTRAINT uk_settlement_receivable
        UNIQUE (receivable_id);
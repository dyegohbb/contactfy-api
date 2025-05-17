CREATE SCHEMA IF NOT EXISTS desafio;

CREATE TABLE IF NOT EXISTS desafio.contato (
    contato_id SERIAL PRIMARY KEY,
    contato_nome VARCHAR(100),
    contato_email VARCHAR(255),
    contato_celular VARCHAR(11),
    contato_telefone VARCHAR(10),
    contato_sn_favorito CHARACTER(1),
    contato_sn_ativo CHARACTER(1),
    contato_dh_cad TIMESTAMP WITHOUT TIME ZONE
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_favorito_sn'
          AND conrelid = 'desafio.contato'::regclass
    ) THEN
        ALTER TABLE desafio.contato
        ADD CONSTRAINT chk_favorito_sn 
        CHECK (contato_sn_favorito IN ('S', 'N'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_ativo_sn'
          AND conrelid = 'desafio.contato'::regclass
    ) THEN
        ALTER TABLE desafio.contato
        ADD CONSTRAINT chk_ativo_sn 
        CHECK (contato_sn_ativo IN ('S', 'N'));
    END IF;
END
$$;

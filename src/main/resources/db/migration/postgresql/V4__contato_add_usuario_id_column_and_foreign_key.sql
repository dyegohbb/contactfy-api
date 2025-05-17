DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'contato'
          AND column_name = 'usuario_id'
          AND table_schema = 'desafio'
    ) THEN
        ALTER TABLE desafio.contato
        ADD COLUMN usuario_id BIGINT;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_type = 'FOREIGN KEY'
          AND constraint_name = 'fk_contato_usuario'
          AND table_schema = 'desafio'
    ) THEN
        ALTER TABLE desafio.contato
        ADD CONSTRAINT fk_contato_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id);
    END IF;
END
$$;
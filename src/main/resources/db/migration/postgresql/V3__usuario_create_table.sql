CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    identificador VARCHAR(255) NOT NULL UNIQUE,
    nome_usuario VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    excluido BOOLEAN NOT NULL,
    excluido_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'idx_usuario_nome_usuario'
    ) THEN
        CREATE INDEX idx_usuario_nome_usuario ON usuario(nome_usuario);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'idx_usuario_email'
    ) THEN
        CREATE INDEX idx_usuario_email ON usuario(email);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'idx_usuario_identificador'
    ) THEN
        CREATE INDEX idx_usuario_identificador ON usuario(identificador);
    END IF;
END
$$;
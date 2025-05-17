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
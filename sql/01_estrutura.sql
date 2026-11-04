CREATE DATABASE IF NOT EXISTS assistencia_tecnica
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE assistencia_tecnica;

CREATE TABLE IF NOT EXISTS cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    telefone VARCHAR(25) NOT NULL,
    email VARCHAR(254)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tecnico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    telefone VARCHAR(25) NOT NULL,
    email VARCHAR(254)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS categoria_equipamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS equipamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id INT NOT NULL,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(120) NOT NULL,
    numero_serie VARCHAR(100),
    CONSTRAINT fk_equipamento_categoria FOREIGN KEY (categoria_id)
        REFERENCES categoria_equipamento (id) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ordem_servico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    tecnico_id INT NOT NULL,
    equipamento_id INT NOT NULL,
    data_abertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_encerramento DATETIME,
    status ENUM('ABERTA', 'EM_ANALISE', 'EM_REPARO', 'CONCLUIDA', 'ENTREGUE', 'CANCELADA')
        NOT NULL DEFAULT 'ABERTA',
    defeito_relatado VARCHAR(500) NOT NULL,
    diagnostico TEXT,
    valor_servico DECIMAL(10, 2),
    CONSTRAINT fk_ordem_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ordem_tecnico FOREIGN KEY (tecnico_id)
        REFERENCES tecnico (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ordem_equipamento FOREIGN KEY (equipamento_id)
        REFERENCES equipamento (id) ON DELETE RESTRICT,
    CONSTRAINT chk_ordem_datas CHECK (data_encerramento >= data_abertura),
    CONSTRAINT chk_ordem_valor CHECK (valor_servico >= 0),
    INDEX idx_ordem_data_abertura (data_abertura)
) ENGINE = InnoDB;

-- A listagem e o relatório compartilham a mesma consulta com JOIN.
CREATE OR REPLACE VIEW vw_ordens_servico AS
SELECT os.id AS numero_os,
       os.data_abertura,
       os.data_encerramento,
       os.status,
       c.nome AS cliente,
       t.nome AS tecnico,
       ce.nome AS categoria,
       e.marca,
       e.modelo,
       e.numero_serie,
       os.defeito_relatado,
       os.diagnostico,
       os.valor_servico
FROM ordem_servico AS os
INNER JOIN cliente AS c ON c.id = os.cliente_id
INNER JOIN tecnico AS t ON t.id = os.tecnico_id
INNER JOIN equipamento AS e ON e.id = os.equipamento_id
INNER JOIN categoria_equipamento AS ce ON ce.id = e.categoria_id;

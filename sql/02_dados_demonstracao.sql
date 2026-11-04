-- Execute uma vez em um banco recém-criado, antes de inserir dados próprios.
USE assistencia_tecnica;

START TRANSACTION;

INSERT INTO categoria_equipamento (id, nome) VALUES
    (1, 'Celular'),
    (2, 'PC'),
    (3, 'Notebook'),
    (4, 'Videogame');

INSERT INTO cliente (id, nome, telefone, email) VALUES
    (1, 'Marina Lopes', '(11) 90000-0001', 'marina@example.com'),
    (2, 'Rafael Mendes', '(11) 90000-0002', 'rafael@example.com'),
    (3, 'Beatriz Costa', '(11) 90000-0003', 'beatriz@example.com');

INSERT INTO tecnico (id, nome, telefone, email) VALUES
    (1, 'Felipe Almeida', '(11) 90000-0011', 'felipe@example.com'),
    (2, 'Camila Rocha', '(11) 90000-0012', 'camila@example.com');

INSERT INTO equipamento (id, categoria_id, marca, modelo, numero_serie) VALUES
    (1, 1, 'Samsung', 'Galaxy A55', 'DEMO-CEL-001'),
    (2, 2, 'Dell', 'OptiPlex 7010', 'DEMO-PC-002'),
    (3, 3, 'Lenovo', 'IdeaPad 3', 'DEMO-NB-003'),
    (4, 4, 'Sony', 'PlayStation 5', 'DEMO-VG-004');

INSERT INTO ordem_servico (
    cliente_id, tecnico_id, equipamento_id, data_abertura, data_encerramento,
    status, defeito_relatado, diagnostico, valor_servico
) VALUES
    (1, 1, 1, '2026-10-05 09:00:00', '2026-10-07 16:30:00',
     'ENTREGUE', 'Tela trincada após queda.', 'Tela substituída.', 420.00),
    (2, 2, 2, '2026-10-10 14:15:00', NULL,
     'EM_REPARO', 'PC desliga durante o uso.', 'Fonte com falha.', 260.00),
    (3, 2, 3, '2026-10-20 10:30:00', NULL,
     'EM_ANALISE', 'Notebook não carrega.', NULL, NULL),
    (1, 1, 4, '2026-10-31 15:40:00', NULL,
     'ABERTA', 'Videogame não mostra imagem.', NULL, NULL);

COMMIT;

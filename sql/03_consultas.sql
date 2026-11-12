USE assistencia_tecnica;

-- Listagem de ordens com os dados relacionados por JOIN.
SELECT os.id AS numero_os, os.data_abertura, os.status,
       c.nome AS cliente, t.nome AS tecnico, ce.nome AS categoria,
       e.marca, e.modelo
FROM ordem_servico AS os
INNER JOIN cliente AS c ON c.id = os.cliente_id
INNER JOIN tecnico AS t ON t.id = os.tecnico_id
INNER JOIN equipamento AS e ON e.id = os.equipamento_id
LEFT JOIN categoria_equipamento AS ce ON ce.id = e.categoria_id
ORDER BY os.data_abertura DESC, os.id DESC;

-- Relatório de ordens abertas no período, incluindo o último dia inteiro.
SET @inicio = '2026-10-01';
SET @fim = '2026-10-31';

SELECT os.id AS numero_os, os.data_abertura, os.data_encerramento,
       c.nome AS cliente, t.nome AS tecnico, ce.nome AS categoria,
       e.marca, e.modelo, os.status, os.valor_servico
FROM ordem_servico AS os
INNER JOIN cliente AS c ON c.id = os.cliente_id
INNER JOIN tecnico AS t ON t.id = os.tecnico_id
INNER JOIN equipamento AS e ON e.id = os.equipamento_id
LEFT JOIN categoria_equipamento AS ce ON ce.id = e.categoria_id
WHERE os.data_abertura >= @inicio
  AND os.data_abertura < DATE_ADD(@fim, INTERVAL 1 DAY)
ORDER BY os.data_abertura, os.id;

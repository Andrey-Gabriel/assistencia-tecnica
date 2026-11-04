USE assistencia_tecnica;

-- Listagem para a tela de ordens de serviço.
SELECT numero_os, data_abertura, status, cliente, tecnico, categoria, marca, modelo
FROM vw_ordens_servico
ORDER BY data_abertura DESC, numero_os DESC;

-- Relatório de ordens abertas no período, incluindo o último dia inteiro.
SET @inicio = '2026-10-01';
SET @fim = '2026-10-31';

SELECT numero_os, data_abertura, data_encerramento, cliente, tecnico,
       categoria, marca, modelo, status, valor_servico
FROM vw_ordens_servico
WHERE data_abertura >= @inicio
  AND data_abertura < DATE_ADD(@fim, INTERVAL 1 DAY)
ORDER BY data_abertura, numero_os;

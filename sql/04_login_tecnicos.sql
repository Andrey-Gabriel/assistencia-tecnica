-- Atualiza bancos existentes. Pode ser executado novamente.
-- Tecnicos existentes continuam sem acesso ate receberem uma senha de um administrador.
USE assistencia_tecnica;

SET @sql = IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tecnico' AND COLUMN_NAME = 'senha_hash'), 'DO 0', 'ALTER TABLE tecnico ADD COLUMN senha_hash VARCHAR(255) NULL');
PREPARE atualizacao FROM @sql;
EXECUTE atualizacao;
DEALLOCATE PREPARE atualizacao;

SET @sql = IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tecnico' AND COLUMN_NAME = 'administrador'), 'DO 0', 'ALTER TABLE tecnico ADD COLUMN administrador BOOLEAN NOT NULL DEFAULT FALSE');
PREPARE atualizacao FROM @sql;
EXECUTE atualizacao;
DEALLOCATE PREPARE atualizacao;

-- Se houver e-mails duplicados, revise-os antes de criar a restricao.
SET @sql = IF(EXISTS(SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tecnico' AND INDEX_NAME = 'uq_tecnico_email'), 'DO 0', 'ALTER TABLE tecnico ADD UNIQUE KEY uq_tecnico_email (email)');
PREPARE atualizacao FROM @sql;
EXECUTE atualizacao;
DEALLOCATE PREPARE atualizacao;

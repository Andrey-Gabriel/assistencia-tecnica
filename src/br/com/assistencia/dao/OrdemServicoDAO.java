package br.com.assistencia.dao;

import br.com.assistencia.model.OrdemServico;
import br.com.assistencia.model.StatusOrdem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoDAO {
    public List<OrdemServico> listar() throws SQLException {
        List<OrdemServico> lista = new ArrayList<>();
        String sql = "SELECT os.*, c.nome AS cliente_nome, c.telefone AS cliente_telefone, t.nome AS tecnico_nome, "
                + "e.modelo AS equipamento_modelo, CONCAT(CONCAT_WS(' / ', ce.nome, NULLIF(TRIM(CONCAT_WS(' ', e.marca, e.modelo)), '')), ' (#', e.id, ')') AS equipamento_nome "
                + "FROM ordem_servico os JOIN cliente c ON c.id=os.cliente_id "
                + "JOIN tecnico t ON t.id=os.tecnico_id JOIN equipamento e ON e.id=os.equipamento_id "
                + "LEFT JOIN categoria_equipamento ce ON ce.id=e.categoria_id "
                + "ORDER BY os.data_abertura DESC, os.id DESC";
        try (Connection c = conexao.conectar(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                OrdemServico os = new OrdemServico();
                os.setId(r.getInt("id")); os.setClienteId(r.getInt("cliente_id"));
                os.setTecnicoId(r.getInt("tecnico_id")); os.setEquipamentoId(r.getInt("equipamento_id"));
                os.setClienteNome(r.getString("cliente_nome")); os.setTecnicoNome(r.getString("tecnico_nome"));
                os.setClienteTelefone(r.getString("cliente_telefone"));
                os.setEquipamentoNome(r.getString("equipamento_nome"));
                os.setEquipamentoModelo(r.getString("equipamento_modelo"));
                os.setDataAbertura(r.getTimestamp("data_abertura").toLocalDateTime());
                Timestamp encerramento = r.getTimestamp("data_encerramento");
                os.setDataEncerramento(encerramento == null ? null : encerramento.toLocalDateTime());
                os.setStatus(StatusOrdem.valueOf(r.getString("status")));
                os.setDefeitoRelatado(r.getString("defeito_relatado"));
                os.setDiagnostico(r.getString("diagnostico")); os.setValorServico(r.getBigDecimal("valor_servico"));
                lista.add(os);
            }
        }
        return lista;
    }

    public void salvar(OrdemServico os) throws SQLException {
        try (Connection c = conexao.conectar()) {
            os.setId(salvar(c, os, os.getClienteId(), os.getEquipamentoId()));
        }
    }

    /**
     * Cada nova OS cadastra os nomes informados. Nomes iguais não comprovam que
     * sejam a mesma pessoa ou o mesmo aparelho. Na edição, vínculos inalterados
     * são preservados e os cadastros usados por outras OS não são renomeados.
     */
    public void salvarAtendimento(OrdemServico os) throws SQLException {
        try (Connection c = conexao.conectar()) {
            c.setAutoCommit(false);
            Savepoint inicio = c.setSavepoint();
            try {
                Vinculos anteriores = os.getId() == 0 ? null : vinculos(c, os.getId());
                int clienteId;
                if (anteriores != null && mesmoNome(os.getClienteNome(), anteriores.clienteNome())) {
                    clienteId = anteriores.clienteId();
                    // Telefone nulo na edição significa que o campo não foi alterado.
                    if (os.getClienteTelefone() != null) {
                        atualizarTelefone(c, clienteId, os.getClienteTelefone());
                    }
                } else {
                    clienteId = inserirCadastro(c, "INSERT INTO cliente (nome, telefone) VALUES (?, NULLIF(?, ''))",
                            os.getClienteNome(), os.getClienteTelefone());
                }
                int equipamentoId = anteriores != null && mesmoNome(os.getEquipamentoModelo(), anteriores.equipamentoModelo())
                        ? anteriores.equipamentoId() : inserirCadastro(c, "INSERT INTO equipamento (modelo) VALUES (?)", os.getEquipamentoModelo());
                int ordemId = salvar(c, os, clienteId, equipamentoId);
                c.commit();
                os.setId(ordemId); os.setClienteId(clienteId); os.setEquipamentoId(equipamentoId);
            } catch (SQLException | RuntimeException erro) {
                try { c.rollback(inicio); } catch (SQLException rollback) { erro.addSuppressed(rollback); }
                throw erro;
            }
        }
    }

    private record Vinculos(int clienteId, String clienteNome, int equipamentoId, String equipamentoModelo) { }

    private Vinculos vinculos(Connection c, int ordemId) throws SQLException {
        String sql = "SELECT os.cliente_id, c.nome, os.equipamento_id, e.modelo FROM ordem_servico os "
                + "JOIN cliente c ON c.id=os.cliente_id JOIN equipamento e ON e.id=os.equipamento_id WHERE os.id=? FOR UPDATE";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, ordemId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new SQLException("Ordem de serviço não encontrada. Atualize a lista.");
                return new Vinculos(r.getInt("cliente_id"), r.getString("nome"), r.getInt("equipamento_id"), r.getString("modelo"));
            }
        }
    }

    private boolean mesmoNome(String nome, String existente) { return nome.trim().equalsIgnoreCase(existente.trim()); }

    private void atualizarTelefone(Connection c, int clienteId, String telefone) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("UPDATE cliente SET telefone = NULLIF(?, '') WHERE id = ?")) {
            p.setString(1, telefone);
            p.setInt(2, clienteId);
            p.executeUpdate();
        }
    }

    private int inserirCadastro(Connection c, String sql, String... valores) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < valores.length; i++) {
                p.setString(i + 1, valores[i]);
            }
            p.executeUpdate();
            try (ResultSet chave = p.getGeneratedKeys()) {
                if (!chave.next()) throw new SQLException("Não foi possível identificar o novo cadastro.");
                return chave.getInt(1);
            }
        }
    }

    private int salvar(Connection c, OrdemServico os, int clienteId, int equipamentoId) throws SQLException {
        boolean novo = os.getId() == 0;
        String sql = novo
                ? "INSERT INTO ordem_servico (cliente_id,tecnico_id,equipamento_id,status,defeito_relatado,diagnostico,valor_servico,data_encerramento) "
                  + "VALUES (?,?,?,?,?,?,?,CASE WHEN ? THEN CURRENT_TIMESTAMP ELSE NULL END)"
                : "UPDATE ordem_servico SET cliente_id=?,tecnico_id=?,equipamento_id=?,status=?,defeito_relatado=?,diagnostico=?,valor_servico=?,"
                  + "data_encerramento=CASE WHEN ? THEN COALESCE(data_encerramento,GREATEST(CURRENT_TIMESTAMP,data_abertura)) ELSE NULL END WHERE id=?";
        try (PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, clienteId); p.setInt(2, os.getTecnicoId()); p.setInt(3, equipamentoId);
            p.setString(4, os.getStatus().name()); p.setString(5, os.getDefeitoRelatado());
            p.setString(6, os.getDiagnostico());
            if (os.getValorServico() == null) p.setNull(7, Types.DECIMAL);
            else p.setBigDecimal(7, os.getValorServico());
            p.setBoolean(8, os.getStatus().encerraAtendimento());
            if (!novo) p.setInt(9, os.getId());
            if (p.executeUpdate() == 0) throw new SQLException("Ordem de serviço não encontrada. Atualize a lista.");
            if (!novo) return os.getId();
            try (ResultSet chave = p.getGeneratedKeys()) {
                if (!chave.next()) throw new SQLException("Não foi possível identificar a nova ordem.");
                return chave.getInt(1);
            }
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection c = conexao.conectar();
             PreparedStatement p = c.prepareStatement("DELETE FROM ordem_servico WHERE id=?")) {
            p.setInt(1, id);
            if (p.executeUpdate() == 0) throw new SQLException("Ordem de serviço não encontrada. Atualize a lista.");
        }
    }
}

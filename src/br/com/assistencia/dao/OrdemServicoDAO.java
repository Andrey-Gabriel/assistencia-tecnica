package br.com.assistencia.dao;

import br.com.assistencia.model.OrdemServico;
import br.com.assistencia.model.StatusOrdem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoDAO {
    public List<OrdemServico> listar() throws SQLException {
        List<OrdemServico> lista = new ArrayList<>();
        String sql = "SELECT os.*, c.nome AS cliente_nome, t.nome AS tecnico_nome, "
                + "CONCAT(ce.nome, ' / ', e.marca, ' ', e.modelo, ' (#', e.id, ')') AS equipamento_nome "
                + "FROM ordem_servico os JOIN cliente c ON c.id=os.cliente_id "
                + "JOIN tecnico t ON t.id=os.tecnico_id JOIN equipamento e ON e.id=os.equipamento_id "
                + "JOIN categoria_equipamento ce ON ce.id=e.categoria_id "
                + "ORDER BY os.data_abertura DESC, os.id DESC";
        try (Connection c = conexao.conectar(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                OrdemServico os = new OrdemServico();
                os.setId(r.getInt("id")); os.setClienteId(r.getInt("cliente_id"));
                os.setTecnicoId(r.getInt("tecnico_id")); os.setEquipamentoId(r.getInt("equipamento_id"));
                os.setClienteNome(r.getString("cliente_nome")); os.setTecnicoNome(r.getString("tecnico_nome"));
                os.setEquipamentoNome(r.getString("equipamento_nome"));
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
        boolean novo = os.getId() == 0;
        String sql = novo
                ? "INSERT INTO ordem_servico (cliente_id,tecnico_id,equipamento_id,status,defeito_relatado,diagnostico,valor_servico,data_encerramento) "
                  + "VALUES (?,?,?,?,?,?,?,CASE WHEN ? THEN CURRENT_TIMESTAMP ELSE NULL END)"
                : "UPDATE ordem_servico SET cliente_id=?,tecnico_id=?,equipamento_id=?,status=?,defeito_relatado=?,diagnostico=?,valor_servico=?,"
                  + "data_encerramento=CASE WHEN ? THEN COALESCE(data_encerramento,GREATEST(CURRENT_TIMESTAMP,data_abertura)) ELSE NULL END WHERE id=?";
        try (Connection c = conexao.conectar(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, os.getClienteId()); p.setInt(2, os.getTecnicoId()); p.setInt(3, os.getEquipamentoId());
            p.setString(4, os.getStatus().name()); p.setString(5, os.getDefeitoRelatado());
            p.setString(6, os.getDiagnostico());
            if (os.getValorServico() == null) p.setNull(7, Types.DECIMAL);
            else p.setBigDecimal(7, os.getValorServico());
            p.setBoolean(8, os.getStatus().encerraAtendimento());
            if (!novo) p.setInt(9, os.getId());
            if (p.executeUpdate() == 0) throw new SQLException("Ordem de serviço não encontrada. Atualize a lista.");
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

package br.com.assistencia.dao;

import br.com.assistencia.model.LinhaRelatorio;
import br.com.assistencia.model.StatusOrdem;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {
    public List<LinhaRelatorio> consultar(LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "SELECT numero_os,data_abertura,data_encerramento,cliente,tecnico,categoria,marca,modelo,status,valor_servico "
                + "FROM vw_ordens_servico WHERE data_abertura>=? AND data_abertura<? ORDER BY data_abertura,numero_os";
        List<LinhaRelatorio> lista = new ArrayList<>();
        try (Connection c = conexao.conectar(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));
            p.setTimestamp(2, Timestamp.valueOf(fim.plusDays(1).atStartOfDay()));
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Timestamp encerramento = r.getTimestamp("data_encerramento");
                    lista.add(new LinhaRelatorio(r.getInt("numero_os"), r.getTimestamp("data_abertura").toLocalDateTime(),
                            encerramento == null ? null : encerramento.toLocalDateTime(),
                            r.getString("cliente"), r.getString("tecnico"), r.getString("categoria"),
                            r.getString("marca"), r.getString("modelo"), StatusOrdem.valueOf(r.getString("status")),
                            r.getBigDecimal("valor_servico")));
                }
            }
        }
        return lista;
    }
}

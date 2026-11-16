package br.com.assistencia.entidade;

import br.com.assistencia.controle.LinhaRelatorio;
import br.com.assistencia.controle.StatusOrdem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManterRelatorio {

    public List<LinhaRelatorio> consultar(LocalDate inicio, LocalDate fim) throws SQLException {
        Sessao.validarAcesso();
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Informe as duas datas do período.");
        }
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data final deve ser igual ou posterior à inicial.");
        }
        if (inicio.getYear() < 1000 || fim.getYear() > 9998) {
            throw new IllegalArgumentException("Informe um período entre os anos 1000 e 9998.");
        }
        String sql = "SELECT numero_os,data_abertura,data_encerramento,cliente,tecnico,categoria,marca,modelo,status,valor_servico "
                + "FROM vw_ordens_servico WHERE data_abertura>=? AND data_abertura<? ORDER BY data_abertura,numero_os";
        List<LinhaRelatorio> lista = new ArrayList<>();
        try (Connection c = DAO.abrirBanco(); PreparedStatement p = c.prepareStatement(sql)) {
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

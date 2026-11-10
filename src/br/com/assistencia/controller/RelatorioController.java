package br.com.assistencia.controller;

import br.com.assistencia.dao.RelatorioDAO;
import br.com.assistencia.model.LinhaRelatorio;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RelatorioController {
    public List<LinhaRelatorio> consultar(LocalDate inicio, LocalDate fim) throws SQLException {
        Sessao.exigirAutenticacao();
        if (inicio == null || fim == null) throw new IllegalArgumentException("Informe as duas datas do período.");
        if (fim.isBefore(inicio)) throw new IllegalArgumentException("A data final deve ser igual ou posterior à inicial.");
        if (inicio.getYear() < 1000 || fim.getYear() > 9998) {
            throw new IllegalArgumentException("Informe um período entre os anos 1000 e 9998.");
        }
        return new RelatorioDAO().consultar(inicio, fim);
    }
}

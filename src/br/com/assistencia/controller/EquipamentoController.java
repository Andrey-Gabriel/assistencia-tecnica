package br.com.assistencia.controller;

import br.com.assistencia.dao.EquipamentoDAO;
import br.com.assistencia.model.Equipamento;
import java.sql.SQLException;
import java.util.List;

public class EquipamentoController {
    private final EquipamentoDAO dao = new EquipamentoDAO();

    public List<Equipamento> listar() throws SQLException {
        Sessao.exigirAutenticacao();
        return dao.listar();
    }

    public void salvar(Equipamento e) throws SQLException {
        Sessao.exigirAutenticacao();
        if (e == null || e.getId() < 0) throw new IllegalArgumentException("Equipamento inválido.");
        if (e.getCategoriaId() <= 0) throw new IllegalArgumentException("Selecione uma categoria.");
        e.setMarca(obrigatorio(e.getMarca(), "marca", 80));
        e.setModelo(obrigatorio(e.getModelo(), "modelo", 120));
        String serie = e.getNumeroSerie() == null ? "" : e.getNumeroSerie().trim();
        if (serie.length() > 100) throw new IllegalArgumentException("O número de série deve ter até 100 caracteres.");
        e.setNumeroSerie(serie.isEmpty() ? null : serie);
        dao.salvar(e);
    }

    public void excluir(int id) throws SQLException {
        Sessao.exigirAutenticacao();
        if (id <= 0) throw new IllegalArgumentException("Selecione um equipamento válido.");
        dao.excluir(id);
    }

    private String obrigatorio(String texto, String campo, int limite) {
        if (texto == null || texto.isBlank() || texto.trim().length() > limite) {
            throw new IllegalArgumentException("Preencha a informação de " + campo + " (até " + limite + " caracteres).");
        }
        return texto.trim();
    }
}

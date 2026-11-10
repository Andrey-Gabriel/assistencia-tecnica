
package br.com.assistencia.controller;

import br.com.assistencia.dao.CategoriaDAO;
import br.com.assistencia.model.Categoria;
import java.sql.SQLException;
import java.util.List;

public class CategoriaController {

    private final CategoriaDAO dao = new CategoriaDAO();

    private String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Digite o nome da categoria."
            );
        }

        nome = nome.trim();

        if (nome.length() > 80) {
            throw new IllegalArgumentException(
                "O nome deve ter no máximo 80 caracteres."
            );
        }

        return nome;
    }

    public void cadastrar(String nome) throws SQLException {
        Sessao.validarAcesso();
        nome = validarNome(nome);

        Categoria categoria = new Categoria();
        categoria.setNome(nome);

        dao.inserir(categoria);
    }

    public List<Categoria> listar() throws SQLException {
        Sessao.validarAcesso();
        return dao.listar();
    }

    public void atualizar(int id, String nome) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        nome = validarNome(nome);

        Categoria categoria = new Categoria(id, nome);
        dao.atualizar(categoria);
    }

    public void excluir(int id) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        dao.excluir(id);
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione uma categoria válida.");
        }
    }
}

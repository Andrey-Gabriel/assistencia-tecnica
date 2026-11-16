package br.com.assistencia.entidade;

import br.com.assistencia.controle.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManterCategoria {

    // CREATE - Cadastrar
    public void inserir(String nome) throws SQLException {
        Sessao.validarAcesso();
        nome = validarNome(nome);
        String sql = "INSERT INTO categoria_equipamento (nome) VALUES (?)";

        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.executeUpdate();
        }
    }

    // READ - Listar
    public List<Categoria> listar() throws SQLException {
        Sessao.validarAcesso();
        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT id, nome FROM categoria_equipamento ORDER BY id";

        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();

                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));

                categorias.add(categoria);
            }
        }

        return categorias;
    }

    // UPDATE - Alterar
    public void alterar(int id, String nome) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        nome = validarNome(nome);
        String sql = "UPDATE categoria_equipamento SET nome = ? WHERE id = ?";

        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setInt(2, id);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Categoria não encontrada.");
            }
        }
    }

    // DELETE - Excluir
    public void excluir(int id) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        String sql = "DELETE FROM categoria_equipamento WHERE id = ?";

        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Categoria não encontrada.");
            }
        }
    }

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

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione uma categoria válida.");
        }
    }
}

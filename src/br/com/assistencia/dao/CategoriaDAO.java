
package br.com.assistencia.dao;

import br.com.assistencia.model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    // CREATE - Cadastrar
    public void inserir(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria_equipamento (nome) VALUES (?)";

        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.executeUpdate();
        }
    }

    // READ - Listar
    public List<Categoria> listar() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT id, nome FROM categoria_equipamento ORDER BY id";

        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
    public void atualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria_equipamento SET nome = ? WHERE id = ?";

        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, categoria.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Categoria não encontrada.");
            }
        }
    }

    // DELETE - Excluir
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM categoria_equipamento WHERE id = ?";

        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Categoria não encontrada.");
            }
        }
    }
}

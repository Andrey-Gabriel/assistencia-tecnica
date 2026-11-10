package br.com.assistencia.dao;

import br.com.assistencia.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT id, nome, telefone, email FROM cliente ORDER BY nome, id";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("telefone"), rs.getString("email")));
            }
        }
        return clientes;
    }

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, cliente);
            stmt.executeUpdate();
        }
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, cliente);
            stmt.setInt(4, cliente.getId());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Cliente não encontrado. Atualize a lista.");
            }
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Cliente não encontrado. Atualize a lista.");
            }
        }
    }

    private void preencher(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getTelefone());
        if (cliente.getEmail() == null) {
            stmt.setNull(3, Types.VARCHAR);
        } else {
            stmt.setString(3, cliente.getEmail());
        }
    }
}

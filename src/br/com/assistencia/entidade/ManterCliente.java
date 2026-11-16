package br.com.assistencia.entidade;

import br.com.assistencia.controle.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ManterCliente {

    public List<Cliente> listar() throws SQLException {
        Sessao.validarAcesso();
        String sql = "SELECT id, nome, telefone, email FROM cliente ORDER BY nome, id";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("telefone"), rs.getString("email")));
            }
        }
        return clientes;
    }

    public void inserir(String nome, String telefone, String email) throws SQLException {
        Sessao.validarAcesso();
        Cliente cliente = validar(0, nome, telefone, email);
        String sql = "INSERT INTO cliente (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, cliente);
            stmt.executeUpdate();
        }
    }

    public void alterar(int id, String nome, String telefone, String email) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        Cliente cliente = validar(id, nome, telefone, email);
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, cliente);
            stmt.setInt(4, cliente.getId());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Cliente não encontrado. Atualize a lista.");
            }
        }
    }

    public void excluir(int id) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    private Cliente validar(int id, String nome, String telefone, String email) {
        nome = obrigatorio(nome, "nome", 120);
        telefone = telefone == null ? "" : telefone.trim();
        if (telefone.length() > 25) {
            throw new IllegalArgumentException("O telefone deve ter no máximo 25 caracteres.");
        }
        email = email == null ? "" : email.trim();

        if (email.length() > 254) {
            throw new IllegalArgumentException("O e-mail deve ter no máximo 254 caracteres.");
        }
        if (!email.isEmpty() && !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Digite um e-mail válido ou deixe o campo em branco.");
        }
        return new Cliente(id, nome, telefone.isEmpty() ? null : telefone, email.isEmpty() ? null : email);
    }

    private String obrigatorio(String valor, String campo, int limite) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o " + campo + " do cliente.");
        }
        valor = valor.trim();
        if (valor.length() > limite) {
            throw new IllegalArgumentException("O " + campo + " deve ter no máximo "
                    + limite + " caracteres.");
        }
        return valor;
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um cliente válido.");
        }
    }
}

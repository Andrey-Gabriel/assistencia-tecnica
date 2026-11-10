package br.com.assistencia.dao;

import br.com.assistencia.model.Tecnico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TecnicoDAO {

    public record Credencial(Tecnico tecnico, String senhaHash) {
    }

    public Credencial buscarCredencial(String email) throws SQLException {
        String sql = "SELECT id, nome, telefone, email, administrador, senha_hash FROM tecnico WHERE email = ?";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Tecnico tecnico = new Tecnico(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("telefone"), rs.getString("email"));
                tecnico.setAdministrador(rs.getBoolean("administrador"));
                Credencial credencial = new Credencial(tecnico, rs.getString("senha_hash"));
                if (rs.next()) {
                    throw new SQLException("Existem técnicos com o mesmo e-mail. Revise os cadastros.");
                }
                return credencial;
            }
        }
    }

    public List<Tecnico> listar() throws SQLException {
        List<Tecnico> lista = new ArrayList<>();
        String sql = "SELECT id, nome, telefone, email, administrador FROM tecnico ORDER BY nome, id";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tecnico tecnico = new Tecnico(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("telefone"), rs.getString("email"));
                tecnico.setAdministrador(rs.getBoolean("administrador"));
                lista.add(tecnico);
            }
        }
        return lista;
    }

    public boolean ehAdministrador(int id) throws SQLException {
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT administrador FROM tecnico WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    public void inserir(Tecnico tecnico, String senhaHash) throws SQLException {
        String sql = "INSERT INTO tecnico (nome, telefone, email, administrador, senha_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, tecnico);
            stmt.setString(5, senhaHash);
            stmt.executeUpdate();
        }
    }

    public void atualizar(Tecnico tecnico, String senhaHash) throws SQLException {
        String sql = "UPDATE tecnico SET nome = ?, telefone = ?, email = ?, administrador = ?"
                + (senhaHash == null ? "" : ", senha_hash = ?") + " WHERE id = ?";
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, tecnico);
            if (senhaHash != null) {
                stmt.setString(5, senhaHash);
            }
            stmt.setInt(senhaHash == null ? 5 : 6, tecnico.getId());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Técnico não encontrado. Atualize a lista.");
            }
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tecnico WHERE id = ?")) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Técnico não encontrado. Atualize a lista.");
            }
        }
    }

    private void preencher(PreparedStatement stmt, Tecnico tecnico) throws SQLException {
        stmt.setString(1, tecnico.getNome());
        stmt.setString(2, tecnico.getTelefone());
        stmt.setString(3, tecnico.getEmail());
        stmt.setBoolean(4, tecnico.isAdministrador());
    }

    public void criarPrimeiroAdministrador(Tecnico tecnico, String senhaHash) throws SQLException {
        try (Connection conn = conexao.conectar()) {
            boolean bloqueado = false;
            try {
                try (PreparedStatement lock = conn.prepareStatement(
                        "SELECT GET_LOCK(CONCAT(DATABASE(), '_primeiro_admin'), 5)");
                     ResultSet rs = lock.executeQuery()) {
                    bloqueado = rs.next() && rs.getInt(1) == 1;
                }
                if (!bloqueado) {
                    throw new SQLException("Outra configuração inicial está em andamento. Tente novamente.");
                }
                conn.setAutoCommit(false);
                try (PreparedStatement busca = conn.prepareStatement(
                        "SELECT COUNT(*) FROM tecnico WHERE administrador = TRUE");
                     ResultSet rs = busca.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new IllegalArgumentException("Já existe um administrador. "
                                + "Use o login e solicite a ele o cadastro dos demais técnicos.");
                    }
                }
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tecnico (nome, telefone, email, administrador, senha_hash) VALUES (?, ?, ?, TRUE, ?)")) {
                    stmt.setString(1, tecnico.getNome());
                    stmt.setString(2, tecnico.getTelefone());
                    stmt.setString(3, tecnico.getEmail());
                    stmt.setString(4, senhaHash);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
                throw e;
            } finally {
                if (bloqueado) {
                    try (PreparedStatement release = conn.prepareStatement(
                            "SELECT RELEASE_LOCK(CONCAT(DATABASE(), '_primeiro_admin'))")) {
                        release.execute();
                    }
                }
            }
        }
    }
}

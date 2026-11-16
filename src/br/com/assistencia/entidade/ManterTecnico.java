package br.com.assistencia.entidade;

import br.com.assistencia.controle.Tecnico;
import br.com.assistencia.seguranca.Senha;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManterTecnico {

    public List<Tecnico> listar() throws SQLException {
        autorizar();
        return listarDadosBasicos();
    }

    public void inserir(String nome, String telefone, String email, boolean administrador,
            char[] senha, char[] confirmacao) throws SQLException {
        autorizar();
        Tecnico tecnico = validar(0, nome, telefone, email, administrador);
        conferirConfirmacao(senha, confirmacao);
        String senhaHash = Senha.gerarHash(senha);
        try (Connection conn = DAO.abrirBanco()) {
            inserirRegistro(conn, tecnico, senhaHash);
        }
    }

    public void alterar(int id, String nome, String telefone, String email, boolean administrador,
            char[] senha, char[] confirmacao) throws SQLException {
        autorizar();
        validarId(id);
        if (id == Sessao.idTecnico() && !administrador) {
            throw new IllegalArgumentException("Você não pode remover seu próprio acesso de administrador.");
        }
        Tecnico tecnico = validar(id, nome, telefone, email, administrador);
        String hash = null;
        if ((senha != null && senha.length > 0) || (confirmacao != null && confirmacao.length > 0)) {
            conferirConfirmacao(senha, confirmacao);
            hash = Senha.gerarHash(senha);
        }
        alterarRegistro(tecnico, hash);
    }

    public void excluir(int id) throws SQLException {
        autorizar();
        validarId(id);
        if (id == Sessao.idTecnico()) {
            throw new IllegalArgumentException("Você não pode excluir o usuário com que está conectado.");
        }
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM tecnico WHERE id = ?")) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Técnico não encontrado. Atualize a lista.");
            }
        }
    }

    private void autorizar() throws SQLException {
        Sessao.validarAcesso();
        Sessao.exigirAdministrador();
        if (!ehAdministrador(Sessao.idTecnico())) {
            throw new SecurityException("Seu acesso de administrador foi removido. Entre novamente no sistema.");
        }
    }

    private static Tecnico validar(int id, String nome, String telefone, String email, boolean administrador) {
        nome = campo(nome, "nome", 120);
        telefone = campo(telefone, "telefone", 25);
        email = campo(email, "e-mail", 254).toLowerCase(Locale.ROOT);
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Informe um e-mail válido. Ele será usado no login.");
        }
        Tecnico tecnico = new Tecnico(id, nome, telefone, email);
        tecnico.setAdministrador(administrador);
        return tecnico;
    }

    private static void conferirConfirmacao(char[] senha, char[] confirmacao) {
        Senha.validarNova(senha);
        if (!java.util.Arrays.equals(senha, confirmacao)) {
            throw new IllegalArgumentException("As senhas informadas não são iguais.");
        }
    }

    private static String campo(String valor, String nome, int limite) {
        if (valor == null || valor.isBlank() || valor.trim().length() > limite) {
            throw new IllegalArgumentException("Preencha o " + nome + " do técnico (até " + limite + " caracteres).");
        }
        return valor.trim();
    }

    private static void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um técnico válido.");
        }
    }

    record Credencial(Tecnico tecnico, String senhaHash) {

    }

    Credencial buscarCredencial(String email) throws SQLException {
        String sql = "SELECT id, nome, telefone, email, administrador, senha_hash FROM tecnico WHERE email = ?";
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    List<Tecnico> listarDadosBasicos() throws SQLException {
        List<Tecnico> lista = new ArrayList<>();
        String sql = "SELECT id, nome, telefone, email, administrador FROM tecnico ORDER BY nome, id";
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tecnico tecnico = new Tecnico(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("telefone"), rs.getString("email"));
                tecnico.setAdministrador(rs.getBoolean("administrador"));
                lista.add(tecnico);
            }
        }
        return lista;
    }

    boolean existe(int id) throws SQLException {
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM tecnico WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean ehAdministrador(int id) throws SQLException {
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement("SELECT administrador FROM tecnico WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    private void inserirRegistro(Connection conn, Tecnico tecnico, String senhaHash) throws SQLException {
        String sql = "INSERT INTO tecnico (nome, telefone, email, administrador, senha_hash) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, tecnico);
            stmt.setString(5, senhaHash);
            stmt.executeUpdate();
        }
    }

    private void alterarRegistro(Tecnico tecnico, String senhaHash) throws SQLException {
        String sql = "UPDATE tecnico SET nome = ?, telefone = ?, email = ?, administrador = ?"
                + (senhaHash == null ? "" : ", senha_hash = ?") + " WHERE id = ?";
        try (Connection conn = DAO.abrirBanco(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    private void preencher(PreparedStatement stmt, Tecnico tecnico) throws SQLException {
        stmt.setString(1, tecnico.getNome());
        stmt.setString(2, tecnico.getTelefone());
        stmt.setString(3, tecnico.getEmail());
        stmt.setBoolean(4, tecnico.isAdministrador());
    }

    boolean temAdministrador() throws SQLException {
        try (Connection conn = DAO.abrirBanco()) {
            return temAdministrador(conn);
        }
    }

    private boolean temAdministrador(Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM tecnico WHERE administrador = TRUE LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    }

    public void criarPrimeiroAdministrador(String nome, String telefone, String email,
            char[] senha, char[] confirmacao) throws SQLException {
        Tecnico tecnico = validar(0, nome, telefone, email, true);
        conferirConfirmacao(senha, confirmacao);
        String senhaHash = Senha.gerarHash(senha);
        try (Connection conn = DAO.abrirBanco()) {
            boolean bloqueado = false;
            try {
                try (PreparedStatement lock = conn.prepareStatement(
                        "SELECT GET_LOCK(CONCAT(DATABASE(), '_primeiro_admin'), 5)"); ResultSet rs = lock.executeQuery()) {
                    bloqueado = rs.next() && rs.getInt(1) == 1;
                }
                if (!bloqueado) {
                    throw new SQLException("Outra configuração inicial está em andamento. Tente novamente.");
                }
                conn.setAutoCommit(false);
                if (temAdministrador(conn)) {
                    throw new IllegalArgumentException("Já existe um administrador. "
                            + "Use o login e solicite a ele o cadastro dos demais técnicos.");
                }
                inserirRegistro(conn, tecnico, senhaHash);
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

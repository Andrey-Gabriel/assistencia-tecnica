package br.com.assistencia.controller;

import br.com.assistencia.dao.TecnicoDAO;
import br.com.assistencia.model.Tecnico;
import br.com.assistencia.seguranca.Senha;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class TecnicoController {

    private final TecnicoDAO dao = new TecnicoDAO();

    public List<Tecnico> listar() throws SQLException {
        autorizar();
        return dao.listar();
    }

    public void cadastrar(String nome, String telefone, String email, boolean administrador,
            char[] senha, char[] confirmacao) throws SQLException {
        autorizar();
        Tecnico tecnico = validar(0, nome, telefone, email, administrador);
        conferirConfirmacao(senha, confirmacao);
        dao.inserir(tecnico, Senha.gerarHash(senha));
    }

    public void atualizar(int id, String nome, String telefone, String email, boolean administrador,
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
        dao.atualizar(tecnico, hash);
    }

    public void excluir(int id) throws SQLException {
        autorizar();
        validarId(id);
        if (id == Sessao.idTecnico()) {
            throw new IllegalArgumentException("Você não pode excluir o usuário com que está conectado.");
        }
        dao.excluir(id);
    }

    private void autorizar() throws SQLException {
        Sessao.exigirAdministrador();
        if (!dao.ehAdministrador(Sessao.idTecnico())) {
            throw new SecurityException("Seu acesso de administrador foi removido. Entre novamente no sistema.");
        }
    }

    public static Tecnico validar(int id, String nome, String telefone, String email, boolean administrador) {
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

    public static void conferirConfirmacao(char[] senha, char[] confirmacao) {
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
}

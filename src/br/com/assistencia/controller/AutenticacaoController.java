package br.com.assistencia.controller;

import br.com.assistencia.dao.TecnicoDAO;
import br.com.assistencia.seguranca.Senha;
import java.sql.SQLException;
import java.util.Locale;

public class AutenticacaoController {

    private final TecnicoDAO dao = new TecnicoDAO();
    private static final String HASH_FICTICIO = Senha.gerarHash(
            "Verificacao local sem conta cadastrada".toCharArray());

    public void entrar(String email, char[] senha) throws SQLException {
        Sessao.encerrar();
        if (email == null || email.isBlank() || email.trim().length() > 254
                || senha == null || senha.length == 0 || senha.length > 128) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }
        TecnicoDAO.Credencial credencial = dao.buscarCredencial(email.trim().toLowerCase(Locale.ROOT));
        String hash = credencial == null || credencial.senhaHash() == null
                ? HASH_FICTICIO : credencial.senhaHash();
        boolean senhaCorreta = Senha.conferir(senha, hash);
        if (credencial == null || credencial.senhaHash() == null || !senhaCorreta) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }
        Sessao.iniciar(credencial.tecnico());
    }
}

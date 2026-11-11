package br.com.assistencia.controller;

import br.com.assistencia.dao.TecnicoDAO;
import br.com.assistencia.model.Tecnico;
import java.sql.SQLException;

/** Mantém o técnico autenticado somente durante a execução da aplicação. */
public final class Sessao {

    private static volatile Tecnico tecnico;

    private Sessao() {
    }

    static void iniciar(Tecnico autenticado) {
        tecnico = new Tecnico(autenticado.getId(), autenticado.getNome(),
                autenticado.getTelefone(), autenticado.getEmail());
        tecnico.setAdministrador(autenticado.isAdministrador());
    }

    public static boolean estaAutenticado() {
        return tecnico != null;
    }

    public static void exigirAutenticacao() {
        tecnicoAutenticado();
    }

    private static Tecnico tecnicoAutenticado() {
        Tecnico atual = tecnico;
        if (atual == null) {
            throw new SecurityException("Entre com o e-mail e a senha de um técnico cadastrado.");
        }
        return atual;
    }

    /** Revalida no banco antes de cada operação, inclusive em telas já abertas. */
    public static void validarAcesso() throws SQLException {
        Tecnico atual = tecnicoAutenticado();
        if (!new TecnicoDAO().existe(atual.getId())) {
            encerrar();
            throw new SecurityException("Seu cadastro de técnico foi excluído. "
                    + "O acesso ao sistema foi encerrado. Procure um administrador.");
        }
    }

    public static String nomeTecnico() {
        Tecnico atual = tecnico;
        return atual == null ? "" : atual.getNome();
    }

    public static boolean ehAdministrador() {
        Tecnico atual = tecnico;
        return atual != null && atual.isAdministrador();
    }

    public static int idTecnico() {
        Tecnico atual = tecnico;
        return atual == null ? 0 : atual.getId();
    }

    public static void exigirAdministrador() {
        exigirAutenticacao();
        if (!ehAdministrador()) {
            throw new SecurityException("Somente um administrador pode gerenciar técnicos e senhas.");
        }
    }

    public static void encerrar() {
        tecnico = null;
    }
}

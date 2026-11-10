package br.com.assistencia.controller;

import br.com.assistencia.dao.ClienteDAO;
import br.com.assistencia.model.Cliente;
import java.sql.SQLException;
import java.util.List;

public class ClienteController {

    private final ClienteDAO dao = new ClienteDAO();

    public List<Cliente> listar() throws SQLException {
        Sessao.validarAcesso();
        return dao.listar();
    }

    public void cadastrar(String nome, String telefone, String email) throws SQLException {
        Sessao.validarAcesso();
        dao.inserir(validar(0, nome, telefone, email));
    }

    public void atualizar(int id, String nome, String telefone, String email) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        dao.atualizar(validar(id, nome, telefone, email));
    }

    public void excluir(int id) throws SQLException {
        Sessao.validarAcesso();
        validarId(id);
        dao.excluir(id);
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

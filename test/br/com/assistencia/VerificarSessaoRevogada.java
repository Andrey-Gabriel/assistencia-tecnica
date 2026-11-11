package br.com.assistencia;

import br.com.assistencia.controller.*;
import br.com.assistencia.dao.TecnicoDAO;
import br.com.assistencia.model.*;
import br.com.assistencia.view.*;
import java.awt.Frame;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import static br.com.assistencia.VerificarFluxos.*;

/** Regressão: exclusão por outro administrador com o sistema do técnico já aberto. */
final class VerificarSessaoRevogada {
    private record Operacao(String nome, Checked tarefa) { }

    static void executar(Connection banco, String email, char[] senha,
            String emailAdmin, char[] senhaAdmin) throws Exception {
        var auth = new AutenticacaoController();
        var dao = new TecnicoDAO();
        int idTecnico = dao.buscarCredencial(email).tecnico().getId();
        int idAdmin = dao.buscarCredencial(emailAdmin).tecnico().getId();
        var clientes = new ClienteController();
        var categorias = new CategoriaController();
        var equipamentos = new EquipamentoController();
        var ordens = new OrdemServicoController();
        // Controllers são criados antes da exclusão, como nas telas já abertas.
        String nomeCliente = prefixo + " sessao";
        clientes.cadastrar(nomeCliente, "", "");
        int idCliente = buscar(clientes, nomeCliente).getId();
        var os = new OrdemServico();
        os.setClienteNome(prefixo + " cliente bloqueado");
        os.setEquipamentoModelo("Notebook bloqueado");
        os.setTecnicoId(idAdmin);
        os.setStatus(StatusOrdem.ABERTA);
        os.setDefeitoRelatado("Nao deve salvar depois da exclusao");
        List<Operacao> operacoes = List.of(
                new Operacao("consulta de clientes", () -> clientes.listar()),
                new Operacao("cadastro de cliente", () -> clientes.cadastrar(prefixo + " bloqueado", "", "")),
                new Operacao("edicao de cliente", () -> clientes.atualizar(idCliente, "Alteracao bloqueada", "", "")),
                new Operacao("exclusao de cliente", () -> clientes.excluir(idCliente)),
                new Operacao("consulta de categorias", () -> categorias.listar()),
                new Operacao("cadastro de categoria", () -> categorias.cadastrar(prefixo + " bloqueada")),
                new Operacao("consulta de equipamentos", () -> equipamentos.listar()),
                new Operacao("cadastro de equipamento", () -> equipamentos.salvar(new Equipamento(0, 0, null, "Bloqueado", null))),
                new Operacao("consulta de ordens", () -> ordens.listar()),
                new Operacao("consulta de responsaveis", () -> ordens.listarResponsaveis()),
                new Operacao("abertura de ordem", () -> ordens.salvarAtendimento(os)),
                new Operacao("relatorio", () -> new RelatorioController().consultar(LocalDate.now(), LocalDate.now())));
        try {
            auth.entrar(email, senha);
            falharConexao = true;
            try {
                ausente(() -> clientes.cadastrar("Nao deve ser salvo", "", ""), "Operou sem validar o acesso no banco");
                conferir(Sessao.estaAutenticado(), "Falha de conexao foi tratada como exclusao");
            } finally {
                falharConexao = false;
            }
            clientes.listar();
            for (Operacao operacao : operacoes) {
                auth.entrar(email, senha);
                Savepoint ponto = banco.setSavepoint();
                try {
                    dao.excluir(idTecnico); // Simula a exclusão por outro administrador.
                    negar(operacao.tarefa(), operacao.nome());
                    conferir(!Sessao.estaAutenticado() && Sessao.idTecnico() == 0,
                            "A sessao excluida continuou ativa: " + operacao.nome());
                    negar(() -> clientes.listar(), "segunda operacao apos revogacao");
                    invalido(() -> auth.entrar(email, senha), "Tecnico excluido conseguiu entrar novamente");
                } finally {
                    banco.rollback(ponto);
                    banco.releaseSavepoint(ponto);
                }
            }
            auth.entrar(emailAdmin, senhaAdmin);
            Savepoint ponto = banco.setSavepoint();
            try {
                dao.excluir(idAdmin);
                negar(() -> new TecnicoController().listar(), "administrador excluido");
                conferir(!Sessao.estaAutenticado(), "Sessao do administrador excluido continuou ativa");
            } finally {
                banco.rollback(ponto);
                banco.releaseSavepoint(ponto);
            }
            verificarTela(banco, auth, email, senha, idTecnico);
        } finally {
            falharConexao = false;
            auth.entrar(emailAdmin, senhaAdmin);
            clientes.excluir(idCliente);
        }
        System.out.println("OK: tecnico excluido bloqueado em todos os modulos, sessao encerrada, retorno ao login e falha de conexao sem falso logout.");
    }

    private static void negar(Checked tarefa, String nome) throws Exception {
        try {
            tarefa.run();
            throw new AssertionError("Tecnico excluido conseguiu executar: " + nome);
        } catch (SecurityException esperado) { }
    }

    private static void verificarTela(Connection banco, AutenticacaoController auth,
            String email, char[] senha, int idTecnico) throws Exception {
        auth.entrar(email, senha);
        TelaPrincipal[] menu = new TelaPrincipal[1];
        edt(() -> { menu[0] = new TelaPrincipal(); tela = new TelaCadastroCliente(); });
        aguardar();
        Savepoint ponto = banco.setSavepoint();
        javax.swing.Timer[] aviso = new javax.swing.Timer[1];
        boolean[] avisoFechado = new boolean[1];
        try {
            new TecnicoDAO().excluir(idTecnico);
            edt(() -> {
                aviso[0] = new javax.swing.Timer(100, evento -> {
                    for (java.awt.Window janela : java.awt.Window.getWindows()) {
                        if (janela instanceof JDialog dialogo && dialogo.isShowing()
                                && "Sessão encerrada".equals(dialogo.getTitle())) {
                            for (java.awt.Component c : dialogo.getContentPane().getComponents()) {
                                if (c instanceof JOptionPane opcao) {
                                    opcao.setValue(JOptionPane.OK_OPTION);
                                    avisoFechado[0] = true;
                                }
                            }
                        }
                    }
                });
                aviso[0].start();
                texto("txtNome", prefixo + " tela bloqueada");
                clique("btnSalvar");
            });
            long limite = System.currentTimeMillis() + 10000;
            boolean[] concluido = new boolean[1];
            while (!concluido[0] && System.currentTimeMillis() < limite) {
                edt(() -> concluido[0] = avisoFechado[0] && !tela.isDisplayable());
                if (!concluido[0]) Thread.sleep(25);
            }
            conferir(concluido[0], "A tela nao tratou a exclusao durante a gravacao");
            edt(() -> {
                conferir(!menu[0].isDisplayable(), "Menu permaneceu aberto apos revogacao");
                long logins = java.util.Arrays.stream(Frame.getFrames())
                        .filter(j -> j instanceof TelaLogin && j.isDisplayable()).count();
                conferir(logins == 1, "Nao retornou a uma unica tela de login");
                // Um segundo worker pode terminar depois do primeiro aviso.
                Formularios.tratarSessaoEncerrada(new SecurityException("Sessao encerrada"));
            });
            try (PreparedStatement p = banco.prepareStatement("SELECT COUNT(*) FROM cliente WHERE nome=?")) {
                p.setString(1, prefixo + " tela bloqueada");
                try (ResultSet r = p.executeQuery()) {
                    r.next(); conferir(r.getInt(1) == 0, "Gravacao pela tela persistiu apos exclusao");
                }
            }
        } finally {
            edt(() -> {
                if (aviso[0] != null) aviso[0].stop();
                for (Frame janela : Frame.getFrames()) {
                    if (janela instanceof TelaLogin || janela instanceof TelaGerenciada) janela.dispose();
                }
            });
            banco.rollback(ponto);
            banco.releaseSavepoint(ponto);
        }
    }
}

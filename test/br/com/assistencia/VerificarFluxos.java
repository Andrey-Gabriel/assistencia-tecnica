package br.com.assistencia;

import br.com.assistencia.controller.ClienteController;
import br.com.assistencia.model.Cliente;
import br.com.assistencia.view.TelaCadastroCliente;
import br.com.assistencia.view.TelaPrincipal;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Verifica login, perfis, cadastros e interface no MySQL configurado.
 * O driver de teste mantém todas as operações na mesma transação e suprime
 * commits dos DAOs. O rollback final descarta os registros temporários.
 * O MySQL pode consumir números de AUTO_INCREMENT mesmo com rollback.
 */
class VerificarFluxos {
    static TelaCadastroCliente tela; static br.com.assistencia.view.TelaCadastroTecnico janelaTecnico;
    static String prefixo = "TESTE-" + UUID.randomUUID();
    static void conferir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
    static Object campo(Object obj, String nome) throws Exception {
        Field f = obj.getClass().getDeclaredField(nome); f.setAccessible(true); return f.get(obj);
    }
    static void clique(String nome) throws Exception { ((JButton) campo(tela, nome)).doClick(); }
    static void texto(String campo, String valor) throws Exception { ((JTextField) campo(tela, campo)).setText(valor); }
    static String valor(String campo) throws Exception { return ((JTextField) campo(tela, campo)).getText(); }
    static void edt(Checked tarefa) throws Exception {
        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(() -> { try { tarefa.run(); } catch (Throwable e) { erro[0] = e; } });
        if (erro[0] != null) throw new RuntimeException(erro[0]);
    }
    interface Checked { void run() throws Exception; }
    static void aguardar() throws Exception {
        long limite = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < limite) {
            boolean[] ocupado = new boolean[1];
            edt(() -> ocupado[0] = (boolean) campo(tela, "ocupado"));
            if (!ocupado[0]) return;
            Thread.sleep(25);
        }
        throw new AssertionError("Tela continuou ocupada apos 10 segundos");
    }
    static void imagem(JFrame janela, String arquivo) throws Exception {
        janela.validate();
        if (janela instanceof TelaCadastroCliente || janela instanceof br.com.assistencia.view.TelaCadastroTecnico) {
            JLabel titulo = (JLabel) campo(janela,"lblTitulo");
            JTextField nome = (JTextField) campo(janela,"txtNome");
            JScrollPane lista = (JScrollPane) campo(janela, janela instanceof TelaCadastroCliente ? "scrollClientes" : "scrollTecnicos");
            conferir(nome.getY() > titulo.getY() + titulo.getHeight(), "Formulario sobreposto ao titulo");
            conferir(lista.getY() > nome.getY() + nome.getHeight(), "Tabela sobreposta ao formulario");
        }
        JRootPane root = janela.getRootPane();
        BufferedImage img = new BufferedImage(root.getWidth(), root.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics(); root.printAll(g); g.dispose();
        ImageIO.write(img, "png", new File(System.getenv("TEMP"), arquivo));
    }
    static void invalido(Checked teste, String mensagem) throws Exception {
        try { teste.run(); throw new AssertionError(mensagem); } catch (IllegalArgumentException esperado) { }
    }
    static void ausente(Checked teste, String mensagem) throws Exception {
        try { teste.run(); throw new AssertionError(mensagem); } catch (SQLException esperado) { }
    }
    static Cliente buscar(ClienteController c, String nome) throws Exception {
        return c.listar().stream().filter(x -> x.getNome().equals(nome)).findFirst().orElseThrow();
    }
    public static void main(String[] args) throws Exception {
        int exit = 1;
        Connection real = null;
        Driver wrapper = null;
        try {
            String url = System.getenv("MYSQL_TESTE_URL_REAL");
            real = DriverManager.getConnection(url, System.getenv("MYSQL_USUARIO"), System.getenv("MYSQL_SENHA"));
            real.setAutoCommit(false);
            final Connection banco = real;
            wrapper = new Driver() {
                public Connection connect(String url, Properties props) throws SQLException {
                    if (!acceptsURL(url)) return null;
                    return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class},
                        (p, m, a) -> {
                            if (java.util.Set.of("close", "commit", "rollback", "setAutoCommit").contains(m.getName())) return null;
                            try { return m.invoke(banco, a); } catch (InvocationTargetException e) { throw e.getCause(); }
                        });
                }
                public boolean acceptsURL(String url) { return "jdbc:assistencia-teste".equals(url); }
                public DriverPropertyInfo[] getPropertyInfo(String url, Properties p) { return new DriverPropertyInfo[0]; }
                public int getMajorVersion() { return 1; }
                public int getMinorVersion() { return 0; }
                public boolean jdbcCompliant() { return false; }
                public java.util.logging.Logger getParentLogger() { return java.util.logging.Logger.getGlobal(); }
            };
            DriverManager.registerDriver(wrapper);
            br.com.assistencia.controller.Sessao.encerrar();
            ClienteController bloqueado = new ClienteController();
            try { bloqueado.listar(); throw new AssertionError("Cliente acessivel sem login"); }
            catch (SecurityException esperado) { }
            try { new TelaPrincipal(); throw new AssertionError("Menu abriu sem login"); }
            catch (SecurityException esperado) { }
            VerificarModulos.semSessao();
            char[] senhaTeste = "Teste123".toCharArray();
            String hash1 = br.com.assistencia.seguranca.Senha.gerarHash(senhaTeste);
            String hash2 = br.com.assistencia.seguranca.Senha.gerarHash(senhaTeste);
            conferir(!hash1.equals(hash2), "Salt reutilizado");
            conferir(br.com.assistencia.seguranca.Senha.conferir(senhaTeste, hash1), "Hash nao confere");
            conferir(!br.com.assistencia.seguranca.Senha.conferir("errada".toCharArray(), hash1), "Hash aceitou senha errada");
            String emailAdmin = prefixo.toLowerCase() + "@example.com";
            try (PreparedStatement p = banco.prepareStatement("INSERT INTO tecnico (nome,telefone,email,senha_hash,administrador) VALUES ('Administrador teste','11999999999',?,?,TRUE)")) {
                p.setString(1, emailAdmin); p.setString(2, hash1); p.executeUpdate();
            }
            var auth = new br.com.assistencia.controller.AutenticacaoController();
            invalido(() -> auth.entrar(emailAdmin, "senha errada".toCharArray()), "Login aceitou senha errada");
            conferir(!br.com.assistencia.controller.Sessao.estaAutenticado(), "Sessao aberta apos falha");
            invalido(() -> auth.entrar("naoexiste-" + emailAdmin, senhaTeste), "Login aceitou usuario inexistente");
            try (Statement s = banco.createStatement(); ResultSet r = s.executeQuery("SELECT email FROM tecnico WHERE senha_hash IS NULL AND email IS NOT NULL LIMIT 1")) {
                if (r.next()) { String emailSemSenha = r.getString(1); invalido(() -> auth.entrar(emailSemSenha, senhaTeste), "Tecnico sem senha entrou"); }
            }
            auth.entrar(emailAdmin.toUpperCase(), senhaTeste);
            conferir(br.com.assistencia.controller.Sessao.ehAdministrador(), "Perfil admin nao carregou");
            int idAdmin = br.com.assistencia.controller.Sessao.idTecnico();
            var tecnicos = new br.com.assistencia.controller.TecnicoController();
            String emailNormal = "normal-" + emailAdmin;
            invalido(() -> tecnicos.cadastrar("Teste", "123", emailNormal, false,
                    "Teste12".toCharArray(), "Teste12".toCharArray()), "Aceitou senha com 7 caracteres");
            invalido(() -> tecnicos.cadastrar("Teste", "123", emailNormal, false, senhaTeste, "nao coincide".toCharArray()), "Aceitou confirmacao diferente");
            tecnicos.cadastrar("Tecnico teste", "123", emailNormal, false, senhaTeste, senhaTeste);
            var daoTecnicos = new br.com.assistencia.dao.TecnicoDAO();
            var registroNormal = daoTecnicos.buscarCredencial(emailNormal);
            conferir(registroNormal != null && !registroNormal.senhaHash().equals(new String(senhaTeste)), "Senha sem hash");
            int idNormal = registroNormal.tecnico().getId();
            tecnicos.atualizar(idNormal, "Tecnico alterado", "123", emailNormal, false, new char[0], new char[0]);
            conferir(daoTecnicos.buscarCredencial(emailNormal).senhaHash().equals(registroNormal.senhaHash()), "Edicao em branco apagou senha");
            invalido(() -> tecnicos.excluir(idAdmin), "Admin excluiu a propria conta");
            invalido(() -> tecnicos.atualizar(idAdmin, "Admin", "123", emailAdmin, false, new char[0], new char[0]), "Admin removeu proprio perfil");
            auth.entrar(emailNormal, senhaTeste);
            conferir(!br.com.assistencia.controller.Sessao.ehAdministrador(), "Tecnico normal virou admin");
            try { tecnicos.listar(); throw new AssertionError("Tecnico normal gerenciou usuarios"); } catch (SecurityException esperado) { }
            new ClienteController().listar();
            VerificarModulos.tecnicoComum();
            edt(() -> {
                TelaPrincipal normal = new TelaPrincipal();
                conferir(!((JButton) campo(normal, "btnTecnicos")).isVisible(), "Menu admin visivel ao tecnico");
                normal.dispose();
                try { new br.com.assistencia.view.TelaCadastroTecnico(); throw new AssertionError("Tela de tecnico abriu sem perfil admin"); }
                catch (SecurityException esperado) { }
            });
            auth.entrar(emailAdmin, senhaTeste);
            char[] novaSenha = "Nova1234".toCharArray();
            tecnicos.atualizar(idNormal, "Tecnico alterado", "123", emailNormal, false, novaSenha, novaSenha);
            invalido(() -> auth.entrar(emailNormal, senhaTeste), "Senha anterior continuou valida");
            auth.entrar(emailNormal, novaSenha);
            br.com.assistencia.controller.Sessao.encerrar();
            try { new ClienteController().listar(); throw new AssertionError("Logout deixou acesso aberto"); } catch (SecurityException esperado) { }
            auth.entrar(emailAdmin, senhaTeste);
            try (PreparedStatement p = banco.prepareStatement("UPDATE tecnico SET administrador=FALSE WHERE id=?")) { p.setInt(1,idAdmin); p.executeUpdate(); }
            try { tecnicos.listar(); throw new AssertionError("Perfil admin revogado continuou autorizado"); } catch (SecurityException esperado) { }
            try (PreparedStatement p = banco.prepareStatement("UPDATE tecnico SET administrador=TRUE WHERE id=?")) { p.setInt(1,idAdmin); p.executeUpdate(); }
            try {
                daoTecnicos.criarPrimeiroAdministrador(br.com.assistencia.controller.TecnicoController.validar(0,"Outro admin","123","outro-"+emailAdmin,true), hash1);
                throw new AssertionError("Configuracao inicial aceitou outro administrador");
            } catch (IllegalArgumentException esperado) { }
            tecnicos.excluir(idNormal);
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            edt(() -> {
                var login = new br.com.assistencia.view.TelaLogin(); imagem(login,"assistencia-login.png"); login.dispose();
                var cadTecnico = new br.com.assistencia.view.TelaCadastroTecnico();
                // Aguardar a carga fora da EDT para permitir a conclusao do SwingWorker.
                janelaTecnico = cadTecnico;
            });
            System.out.println("OK: hashes com salt, login valido/invalido, tecnico sem senha, logout, perfil admin, revogacao e troca de senha.");
            ClienteController c = new ClienteController();
            int quantidade = c.listar().size();
            invalido(() -> c.cadastrar(" ", "123", ""), "Aceitou nome vazio");
            invalido(() -> c.cadastrar("Cliente", " ", ""), "Aceitou telefone vazio");
            invalido(() -> c.cadastrar("x".repeat(121), "123", ""), "Aceitou nome maior que o banco");
            invalido(() -> c.cadastrar("Cliente", "1".repeat(26), ""), "Aceitou telefone maior que o banco");
            invalido(() -> c.cadastrar("Cliente", "123", "invalido"), "Aceitou email invalido");
            invalido(() -> c.cadastrar("Cliente", "123", "x".repeat(255)), "Aceitou email maior que o banco");
            invalido(() -> c.excluir(0), "Aceitou ID zero");
            c.cadastrar("  " + prefixo + "  ", "  11999999999  ", "");
            Cliente novo = buscar(c, prefixo);
            conferir(novo.getTelefone().equals("11999999999") && novo.getEmail() == null, "Nao normalizou campos");
            c.atualizar(novo.getId(), prefixo + " editado", "11888888888", "teste@example.com");
            conferir(buscar(c, prefixo + " editado").getEmail().equals("teste@example.com"), "Atualizacao nao persistiu");
            c.excluir(novo.getId());
            conferir(c.listar().size() == quantidade, "Exclusao nao removeu o registro de teste");
            ausente(() -> c.excluir(novo.getId()), "Nao avisou que cliente ja foi excluido");
            ausente(() -> c.atualizar(novo.getId(), "Ausente", "123", ""), "Nao avisou que cliente nao existe");
            try (Statement s = real.createStatement(); ResultSet r = s.executeQuery("SELECT cliente_id FROM ordem_servico LIMIT 1")) {
                if (r.next()) {
                    int idVinculado = r.getInt(1);
                    try { c.excluir(idVinculado); throw new AssertionError("Excluiu cliente vinculado"); }
                    catch (SQLException e) { conferir(e.getErrorCode() == 1451, "Erro inesperado de chave estrangeira"); }
                }
            }
            System.out.println("OK: validacoes, cadastro, leitura, atualizacao, exclusao e bloqueio por ordem vinculada.");
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            edt(() -> {
                TelaPrincipal principal = new TelaPrincipal();
                imagem(principal, "assistencia-menu.png"); principal.dispose();
                tela = new TelaCadastroCliente();
            });
            aguardar();
            edt(() -> {
                JTable tabela = (JTable) campo(tela, "tabelaClientes");
                conferir(tabela.getRowCount() == quantidade, "Tela nao carregou os clientes");
                tabela.getRowSorter().toggleSortOrder(1); tabela.getRowSorter().toggleSortOrder(1);
                if (quantidade > 0) {
                    tabela.setRowSelectionInterval(0, 0);
                    int esperado = (int) tabela.getValueAt(0, 0);
                    clique("btnEditar");
                    conferir((int) campo(tela, "idSelecionado") == esperado, "Editou linha errada apos ordenar");
                    clique("btnNovo");
                    conferir(valor("txtNome").isEmpty() && valor("txtTelefone").isEmpty()
                            && valor("txtEmail").isEmpty() && (int) campo(tela, "idSelecionado") == 0,
                            "Novo nao limpou o formulario ou nao saiu do modo de edicao");
                }
                texto("txtNome", "Nome ainda nao salvo");
                javax.swing.Timer cancelar = new javax.swing.Timer(120, e -> {
                    for (Window w : Window.getWindows()) {
                        if (w instanceof JDialog d && d.isShowing()) {
                            for (Component comp : d.getContentPane().getComponents()) {
                                if (comp instanceof JOptionPane pane) pane.setValue(JOptionPane.NO_OPTION);
                            }
                        }
                    }
                });
                cancelar.setRepeats(false); cancelar.start(); clique("btnNovo");
                conferir(valor("txtNome").equals("Nome ainda nao salvo"), "Novo perdeu texto ao cancelar descarte");
                javax.swing.Timer descartar = new javax.swing.Timer(120, e -> {
                    for (Window w : Window.getWindows()) {
                        if (w instanceof JDialog d && d.isShowing()) {
                            for (Component comp : d.getContentPane().getComponents()) {
                                if (comp instanceof JOptionPane pane) pane.setValue(JOptionPane.YES_OPTION);
                            }
                        }
                    }
                });
                descartar.setRepeats(false); descartar.start(); clique("btnNovo");
                conferir(valor("txtNome").isEmpty() && (int) campo(tela, "idSelecionado") == 0,
                        "Novo nao limpou texto apos confirmar descarte");
                imagem(tela, "assistencia-clientes.png");
                texto("txtNome", prefixo + " tela"); texto("txtTelefone", "11777777777");
                clique("btnSalvar");
            });
            aguardar();
            Cliente pelaTela = buscar(c, prefixo + " tela");
            edt(() -> {
                conferir((int) campo(tela, "idSelecionado") == 0 && valor("txtNome").isEmpty(), "Salvar nao limpou os campos");
                JTable tabela = (JTable) campo(tela, "tabelaClientes");
                for (int i = 0; i < tabela.getRowCount(); i++) {
                    if ((int) tabela.getValueAt(i, 0) == pelaTela.getId()) tabela.setRowSelectionInterval(i, i);
                }
                clique("btnEditar"); texto("txtNome", prefixo + " tela editada");
                texto("txtEmail", "tela@example.com"); clique("btnSalvar");
            });
            aguardar();
            conferir(buscar(c, prefixo + " tela editada").getEmail().equals("tela@example.com"), "Edicao pela tela falhou");
            System.out.println("OK: carga da tela, ordenacao, edicao da linha correta, Novo e gravacoes pelos botoes.");
            long limiteTecnico = System.currentTimeMillis() + 10000;
            while (System.currentTimeMillis() < limiteTecnico) {
                boolean[] busy = new boolean[1]; edt(() -> busy[0] = (boolean) campo(janelaTecnico,"ocupado"));
                if (!busy[0]) break;
                Thread.sleep(25);
            }
            edt(() -> {
                conferir(!((boolean) campo(janelaTecnico,"ocupado")), "Carga de tecnicos nao concluiu");
                imagem(janelaTecnico,"assistencia-tecnicos.png"); janelaTecnico.dispose();
            });
            VerificarModulos.executar(banco, prefixo);
            real.rollback();
            conferir(c.listar().size() == quantidade, "Quantidade mudou apos rollback");
            System.out.println("OK: rollback executado; registros de teste nao foram mantidos.");
            System.out.println("Imagens: assistencia-menu.png e assistencia-clientes.png na pasta TEMP.");
            exit = 0;
        } catch (Throwable erro) {
            erro.printStackTrace();
        } finally {
            if (real != null) { real.rollback(); real.close(); }
            if (wrapper != null) DriverManager.deregisterDriver(wrapper);
            SwingUtilities.invokeAndWait(() -> { for (Window w : Window.getWindows()) w.dispose(); });
        }
        System.exit(exit);
    }
}

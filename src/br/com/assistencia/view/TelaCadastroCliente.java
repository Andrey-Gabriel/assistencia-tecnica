package br.com.assistencia.view;

/**
 * Cadastro de clientes com persistência em MySQL.
 * @author Andrey
 */
public class TelaCadastroCliente extends javax.swing.JFrame implements TelaGerenciada {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(TelaCadastroCliente.class.getName());

    private final br.com.assistencia.controller.ClienteController controller =
            new br.com.assistencia.controller.ClienteController();
    private java.util.List<br.com.assistencia.model.Cliente> clientes = java.util.List.of();
    private int idSelecionado;
    private boolean ocupado;
    private String nomeOriginal = "";
    private String telefoneOriginal = "";
    private String emailOriginal = "";

    public TelaCadastroCliente() {
        br.com.assistencia.controller.Sessao.exigirAutenticacao();
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
        tabelaClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.getTableHeader().setReorderingAllowed(false);
        tabelaClientes.getColumnModel().getColumn(0).setMaxWidth(75);
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                fechar();
            }
        });
        atualizarBotoes();
        carregarClientes("");
    }

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        if (confirmarDescarte()) {
            limparFormulario();
            lblStatus.setText("Novo cliente. Preencha os dados e clique em Salvar cliente.");
        }
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        int id = idSelecionado;
        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();

        executar(() -> {
            if (id == 0) {
                controller.cadastrar(nome, telefone, email);
            } else {
                controller.atualizar(id, nome, telefone, email);
            }
            return null;
        }, resultado -> {
            limparFormulario();
            carregarClientes(id == 0 ? "Cliente cadastrado." : "Cliente atualizado.");
        }, "Salvando cliente...");
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        br.com.assistencia.model.Cliente cliente = clienteSelecionado();
        if (cliente == null || !confirmarDescarte()) {
            return;
        }
        idSelecionado = cliente.getId();
        nomeOriginal = cliente.getNome();
        telefoneOriginal = cliente.getTelefone();
        emailOriginal = cliente.getEmail() == null ? "" : cliente.getEmail();
        txtNome.setText(nomeOriginal);
        txtTelefone.setText(telefoneOriginal);
        txtEmail.setText(emailOriginal);
        lblModo.setText("Editando cliente #" + idSelecionado);
        btnSalvar.setText("Salvar alterações");
        lblStatus.setText("Altere os dados e clique em Salvar alterações.");
        txtNome.requestFocusInWindow();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        br.com.assistencia.model.Cliente cliente = clienteSelecionado();
        if (cliente == null || !confirmarDescarte()) {
            return;
        }
        int resposta = javax.swing.JOptionPane.showConfirmDialog(this,
                "Excluir o cliente \"" + cliente.getNome() + "\"?",
                "Excluir cliente", javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);
        if (resposta != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }
        executar(() -> {
            controller.excluir(cliente.getId());
            return null;
        }, resultado -> {
            limparFormulario();
            carregarClientes("Cliente excluído.");
        }, "Excluindo cliente...");
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        carregarClientes("Lista atualizada.");
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        fechar();
    }//GEN-LAST:event_btnFecharActionPerformed

    private void fechar() {
        fecharComConfirmacao();
    }

    public boolean fecharComConfirmacao() {
        if (ocupado || !confirmarDescarte()) {
            return false;
        }
        dispose();
        return true;
    }

    private boolean confirmarDescarte() {
        boolean alterado = !txtNome.getText().equals(nomeOriginal)
                || !txtTelefone.getText().equals(telefoneOriginal)
                || !txtEmail.getText().equals(emailOriginal);
        return !alterado || javax.swing.JOptionPane.showConfirmDialog(this,
                "Há alterações não salvas. Deseja descartá-las?",
                "Alterações não salvas", javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE) == javax.swing.JOptionPane.YES_OPTION;
    }

    private void limparFormulario() {
        idSelecionado = 0;
        nomeOriginal = "";
        telefoneOriginal = "";
        emailOriginal = "";
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        tabelaClientes.clearSelection();
        lblModo.setText("Novo cliente");
        btnSalvar.setText("Salvar cliente");
        txtNome.requestFocusInWindow();
    }

    private br.com.assistencia.model.Cliente clienteSelecionado() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        return clientes.get(tabelaClientes.convertRowIndexToModel(linha));
    }

    private void carregarClientes(String mensagem) {
        executar(controller::listar, lista -> {
            clientes = lista;
            javax.swing.table.DefaultTableModel modelo =
                    (javax.swing.table.DefaultTableModel) tabelaClientes.getModel();
            modelo.setRowCount(0);
            for (br.com.assistencia.model.Cliente cliente : lista) {
                modelo.addRow(new Object[]{cliente.getId(), cliente.getNome(),
                    cliente.getTelefone(), cliente.getEmail() == null ? "" : cliente.getEmail()});
            }
            String quantidade = lista.size() == 1 ? "1 cliente cadastrado."
                    : lista.size() + " clientes cadastrados.";
            lblStatus.setText(mensagem.isEmpty() ? quantidade : mensagem + " " + quantidade);
            atualizarBotoes();
        }, "Carregando clientes...");
    }

    private void atualizarBotoes() {
        boolean selecionado = tabelaClientes.getSelectedRow() >= 0;
        btnEditar.setEnabled(!ocupado && selecionado);
        btnExcluir.setEnabled(!ocupado && selecionado);
    }

    private void definirOcupado(boolean valor) {
        ocupado = valor;
        txtNome.setEnabled(!valor);
        txtTelefone.setEnabled(!valor);
        txtEmail.setEnabled(!valor);
        tabelaClientes.setEnabled(!valor);
        btnNovo.setEnabled(!valor);
        btnSalvar.setEnabled(!valor);
        btnAtualizar.setEnabled(!valor);
        btnFechar.setEnabled(!valor);
        atualizarBotoes();
        setCursor(valor ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR)
                : java.awt.Cursor.getDefaultCursor());
    }

    @FunctionalInterface
    private interface TarefaBanco<T> {
        T executar() throws java.sql.SQLException;
    }

    private <T> void executar(TarefaBanco<T> tarefa, java.util.function.Consumer<T> sucesso,
            String mensagem) {
        if (ocupado) {
            return;
        }
        definirOcupado(true);
        lblStatus.setText(mensagem);
        new javax.swing.SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarefa.executar();
            }

            @Override
            protected void done() {
                definirOcupado(false);
                try {
                    T resultado = get();
                    if (isDisplayable()) {
                        sucesso.accept(resultado);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    lblStatus.setText("Operação interrompida. Atualize a lista antes de continuar.");
                } catch (java.util.concurrent.ExecutionException e) {
                    mostrarErro(e.getCause());
                }
            }
        }.execute();
    }

    private void mostrarErro(Throwable erro) {
        logger.log(java.util.logging.Level.WARNING, "Falha no cadastro de clientes", erro);
        String mensagem = erro.getMessage();
        if (erro instanceof java.sql.SQLException sql) {
            if (sql.getErrorCode() == 1451) {
                mensagem = "Este cliente possui ordens de serviço vinculadas e não pode ser excluído.";
            } else if (sql.getSQLState() != null && sql.getSQLState().startsWith("08")) {
                mensagem = "Não foi possível conectar ao banco. Verifique se o MySQL está em execução "
                        + "e tente novamente.";
            }
        }
        if (mensagem == null || mensagem.isBlank()) {
            mensagem = "Não foi possível concluir a operação. Tente atualizar a lista.";
        }
        lblStatus.setText("Operação não concluída. Confira o aviso e tente novamente.");
        javax.swing.JOptionPane.showMessageDialog(this, mensagem, "Cadastro de clientes",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        separador = new javax.swing.JSeparator();
        lblModo = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        lblTelefone = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblAjuda = new javax.swing.JLabel();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnFechar = new javax.swing.JButton();
        lblListagem = new javax.swing.JLabel();
        scrollClientes = new javax.swing.JScrollPane();
        lblStatus = new javax.swing.JLabel();
        tabelaClientes = new javax.swing.JTable();

        setDefaultCloseOperation(0);
        setTitle("Clientes | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(940, 700));
        setPreferredSize(new java.awt.Dimension(1000, 760));
        lblTitulo.setText("Clientes");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 28));
        lblTitulo.setForeground(new java.awt.Color(25, 42, 64));
        lblDescricao.setText("Cadastre e mantenha os contatos de quem solicita os serviços.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblDescricao.setForeground(new java.awt.Color(91, 105, 124));
        lblModo.setText("Novo cliente");
        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblModo.setForeground(new java.awt.Color(25, 42, 64));
        lblNome.setText("Nome *");
        lblNome.setFont(new java.awt.Font("Segoe UI", 1, 13));
        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtNome.setToolTipText("Nome do cliente, até 120 caracteres.");
        lblTelefone.setText("Telefone *");
        lblTelefone.setFont(new java.awt.Font("Segoe UI", 1, 13));
        txtTelefone.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtTelefone.setToolTipText("Informe o telefone com DDD.");
        lblEmail.setText("E-mail");
        lblEmail.setFont(new java.awt.Font("Segoe UI", 1, 13));
        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblAjuda.setText("* Campos obrigatórios. O e-mail é opcional.");
        lblAjuda.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblAjuda.setForeground(new java.awt.Color(91, 105, 124));
        btnNovo.setText("Novo");
        btnNovo.setToolTipText("Limpa os campos para iniciar um novo cadastro.");
        btnNovo.addActionListener(this::btnNovoActionPerformed);
        btnSalvar.setText("Salvar cliente");
        btnSalvar.setBackground(new java.awt.Color(34, 76, 125));
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnSalvar.addActionListener(this::btnSalvarActionPerformed);
        btnEditar.setText("Editar selecionado");
        btnEditar.addActionListener(this::btnEditarActionPerformed);
        btnExcluir.setText("Excluir selecionado");
        btnExcluir.setForeground(new java.awt.Color(164, 40, 48));
        btnExcluir.addActionListener(this::btnExcluirActionPerformed);
        btnAtualizar.setText("Atualizar lista");
        btnAtualizar.addActionListener(this::btnAtualizarActionPerformed);
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(this::btnFecharActionPerformed);
        lblListagem.setText("Clientes cadastrados");
        lblListagem.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblListagem.setForeground(new java.awt.Color(25, 42, 64));
        lblStatus.setText("Carregando clientes...");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblStatus.setForeground(new java.awt.Color(91, 105, 124));

        tabelaClientes.setAutoCreateRowSorter(true);
        tabelaClientes.setRowHeight(30);
        tabelaClientes.setShowVerticalLines(false);
        tabelaClientes.setFillsViewportHeight(true);
        tabelaClientes.setFont(new java.awt.Font("Segoe UI", 0, 13));
        tabelaClientes.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{"ID", "Nome", "Telefone", "E-mail"}) {
            private final Class<?>[] tipos = {Integer.class, String.class, String.class, String.class};
            @Override public Class<?> getColumnClass(int coluna) { return tipos[coluna]; }
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        });
        scrollClientes.setViewportView(tabelaClientes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addGap(28, 28, 28)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTitulo, -2, -2, -2)
                .addComponent(lblDescricao, -2, -2, -2)
                .addComponent(separador, 0, -1, 32767)
                .addComponent(lblModo, -2, -2, -2)
                .addComponent(lblNome, -2, -2, -2)
                .addComponent(txtNome, 0, -1, 32767)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTelefone, -2, -2, -2)
                        .addComponent(txtTelefone, 140, 250, 250))
                    .addGap(16, 16, 16)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblEmail, -2, -2, -2)
                        .addComponent(txtEmail, 0, 600, 32767)))
                .addComponent(lblAjuda, -2, -2, -2)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnNovo, -2, 76, 76)
                    .addGap(8, 8, 8)
                    .addComponent(btnSalvar, -2, 156, 156)
                    .addGap(8, 8, 8)
                    .addComponent(btnEditar, -2, 150, 150)
                    .addGap(8, 8, 8)
                    .addComponent(btnExcluir, -2, 150, 150)
                    .addGap(8, 8, 8)
                    .addComponent(btnAtualizar, -2, 120, 120)
                    .addGap(0, 0, 32767)
                    .addComponent(btnFechar, -2, 80, 80))
                .addComponent(lblListagem, -2, -2, -2)
                .addComponent(scrollClientes, 0, -1, 32767)
                .addComponent(lblStatus, 0, -1, 32767))
            .addGap(28, 28, 28)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGap(24, 24, 24)
            .addComponent(lblTitulo, -2, 36, 36)
            .addGap(4, 4, 4)
            .addComponent(lblDescricao, -2, 22, 22)
            .addGap(18, 18, 18)
            .addComponent(separador, -2, 2, 2)
            .addGap(18, 18, 18)
            .addComponent(lblModo, -2, 24, 24)
            .addGap(14, 14, 14)
            .addComponent(lblNome, -2, 20, 20)
            .addGap(6, 6, 6)
            .addComponent(txtNome, -2, 36, 36)
            .addGap(12, 12, 12)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTelefone, -2, 20, 20)
                .addComponent(lblEmail, -2, 20, 20))
            .addGap(6, 6, 6)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txtTelefone, -2, 36, 36)
                .addComponent(txtEmail, -2, 36, 36))
            .addGap(8, 8, 8)
            .addComponent(lblAjuda, -2, 18, 18)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(btnNovo, -2, 36, 36)
                .addComponent(btnSalvar, -2, 36, 36)
                .addComponent(btnEditar, -2, 36, 36)
                .addComponent(btnExcluir, -2, 36, 36)
                .addComponent(btnAtualizar, -2, 36, 36)
                .addComponent(btnFechar, -2, 36, 36))
            .addGap(24, 24, 24)
            .addComponent(lblListagem, -2, 24, 24)
            .addGap(10, 10, 10)
            .addComponent(scrollClientes, 100, 190, 32767)
            .addGap(10, 10, 10)
            .addComponent(lblStatus, -2, 20, 20)
            .addGap(24, 24, 24)
        );
        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        TelaLogin.main(args);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JSeparator separador;
    private javax.swing.JLabel lblModo;
    private javax.swing.JLabel lblNome;
    private javax.swing.JTextField txtNome;
    private javax.swing.JLabel lblTelefone;
    private javax.swing.JTextField txtTelefone;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JLabel lblAjuda;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnFechar;
    private javax.swing.JLabel lblListagem;
    private javax.swing.JScrollPane scrollClientes;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tabelaClientes;
    // End of variables declaration//GEN-END:variables
}

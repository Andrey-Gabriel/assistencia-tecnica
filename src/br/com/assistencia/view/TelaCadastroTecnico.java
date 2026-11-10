package br.com.assistencia.view;

/**
 * Cadastro de técnicos com persistência em MySQL.
 * @author Andrey
 */
public class TelaCadastroTecnico extends javax.swing.JFrame implements TelaGerenciada {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(TelaCadastroTecnico.class.getName());

    private final br.com.assistencia.controller.TecnicoController controller =
            new br.com.assistencia.controller.TecnicoController();
    private java.util.List<br.com.assistencia.model.Tecnico> tecnicos = java.util.List.of();
    private int idSelecionado;
    private boolean ocupado;
    private String nomeOriginal = "";
    private String telefoneOriginal = "";
    private String emailOriginal = "";
    private boolean administradorOriginal;

    public TelaCadastroTecnico() {
        br.com.assistencia.controller.Sessao.exigirAdministrador();
        initComponents();
        EstiloVisual.aplicar(this);
        tabelaTecnicos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabelaTecnicos.getTableHeader().setReorderingAllowed(false);
        tabelaTecnicos.getColumnModel().getColumn(0).setMaxWidth(75);
        tabelaTecnicos.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                fechar();
            }
        });
        atualizarBotoes();
        carregarTecnicos("");
    }

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        if (confirmarDescarte()) {
            limparFormulario();
            lblStatus.setText("Novo técnico. Preencha os dados e clique em Salvar técnico.");
        }
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        int id = idSelecionado;
        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();
        boolean administrador = chkAdministrador.isSelected();
        char[] senha = txtSenha.getPassword();
        char[] confirmar = txtConfirmar.getPassword();

        executar(() -> {
            try {
                if (id == 0) controller.cadastrar(nome, telefone, email, administrador, senha, confirmar);
                else controller.atualizar(id, nome, telefone, email, administrador, senha, confirmar);
                return null;
            } finally {
                java.util.Arrays.fill(senha, '\0');
                java.util.Arrays.fill(confirmar, '\0');
            }
        }, resultado -> {
            limparFormulario();
            carregarTecnicos(id == 0 ? "Técnico cadastrado." : "Técnico atualizado.");
        }, "Salvando técnico...");
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        br.com.assistencia.model.Tecnico tecnico = tecnicoSelecionado();
        if (tecnico == null || !confirmarDescarte()) {
            return;
        }
        idSelecionado = tecnico.getId();
        nomeOriginal = tecnico.getNome();
        telefoneOriginal = tecnico.getTelefone();
        emailOriginal = tecnico.getEmail() == null ? "" : tecnico.getEmail();
        administradorOriginal = tecnico.isAdministrador();
        chkAdministrador.setSelected(administradorOriginal);
        txtSenha.setText("");
        txtConfirmar.setText("");
        txtNome.setText(nomeOriginal);
        txtTelefone.setText(telefoneOriginal);
        txtEmail.setText(emailOriginal);
        lblModo.setText("Editando técnico #" + idSelecionado);
        btnSalvar.setText("Salvar alterações");
        lblStatus.setText("Altere os dados e clique em Salvar alterações.");
        txtNome.requestFocusInWindow();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        br.com.assistencia.model.Tecnico tecnico = tecnicoSelecionado();
        if (tecnico == null || !confirmarDescarte()) {
            return;
        }
        int resposta = javax.swing.JOptionPane.showConfirmDialog(this,
                "Excluir o tecnico \"" + tecnico.getNome() + "\"?",
                "Excluir tecnico", javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);
        if (resposta != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }
        executar(() -> {
            controller.excluir(tecnico.getId());
            return null;
        }, resultado -> {
            limparFormulario();
            carregarTecnicos("Técnico excluído.");
        }, "Excluindo técnico...");
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        carregarTecnicos("Lista atualizada.");
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
                || !txtEmail.getText().equals(emailOriginal)
                || chkAdministrador.isSelected() != administradorOriginal || senhaPreenchida();
        return !alterado || javax.swing.JOptionPane.showConfirmDialog(this,
                "Há alterações não salvas. Deseja descartá-las?",
                "Alterações não salvas", javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE) == javax.swing.JOptionPane.YES_OPTION;
    }

    private boolean senhaPreenchida() {
        char[] senha = txtSenha.getPassword(), confirmar = txtConfirmar.getPassword();
        try { return senha.length > 0 || confirmar.length > 0; }
        finally { java.util.Arrays.fill(senha, '\0'); java.util.Arrays.fill(confirmar, '\0'); }
    }

    private void limparFormulario() {
        idSelecionado = 0;
        nomeOriginal = "";
        telefoneOriginal = "";
        emailOriginal = "";
        administradorOriginal = false;
        chkAdministrador.setSelected(false);
        txtSenha.setText("");
        txtConfirmar.setText("");
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        tabelaTecnicos.clearSelection();
        lblModo.setText("Novo técnico");
        btnSalvar.setText("Salvar técnico");
        txtNome.requestFocusInWindow();
    }

    private br.com.assistencia.model.Tecnico tecnicoSelecionado() {
        int linha = tabelaTecnicos.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        return tecnicos.get(tabelaTecnicos.convertRowIndexToModel(linha));
    }

    private void carregarTecnicos(String mensagem) {
        executar(controller::listar, lista -> {
            tecnicos = lista;
            javax.swing.table.DefaultTableModel modelo =
                    (javax.swing.table.DefaultTableModel) tabelaTecnicos.getModel();
            modelo.setRowCount(0);
            for (br.com.assistencia.model.Tecnico tecnico : lista) {
                modelo.addRow(new Object[]{tecnico.getId(), tecnico.getNome(),
                    tecnico.getTelefone(), tecnico.getEmail() == null ? "" : tecnico.getEmail(),
                    tecnico.isAdministrador() ? "Administrador" : "Técnico"});
            }
            String quantidade = lista.size() == 1 ? "1 técnico cadastrado."
                    : lista.size() + " técnicos cadastrados.";
            lblStatus.setText(mensagem.isEmpty() ? quantidade : mensagem + " " + quantidade);
            atualizarBotoes();
        }, "Carregando técnicos...");
    }

    private void atualizarBotoes() {
        boolean selecionado = tabelaTecnicos.getSelectedRow() >= 0;
        btnEditar.setEnabled(!ocupado && selecionado);
        btnExcluir.setEnabled(!ocupado && selecionado);
    }

    private void definirOcupado(boolean valor) {
        ocupado = valor;
        txtNome.setEnabled(!valor);
        txtTelefone.setEnabled(!valor);
        txtEmail.setEnabled(!valor);
        txtSenha.setEnabled(!valor);
        txtConfirmar.setEnabled(!valor);
        chkAdministrador.setEnabled(!valor);
        tabelaTecnicos.setEnabled(!valor);
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
        if (Formularios.tratarSessaoEncerrada(erro)) return;
        logger.log(java.util.logging.Level.WARNING, "Falha no cadastro de tecnicos", erro);
        String mensagem = erro.getMessage();
        if (erro instanceof java.sql.SQLException sql) {
            if (sql.getErrorCode() == 1062) {
                mensagem = "Já existe um técnico cadastrado com este e-mail.";
            } else if (sql.getErrorCode() == 1451) {
                mensagem = "Este tecnico possui ordens de serviço vinculadas e não pode ser excluído.";
            } else if (sql.getSQLState() != null && sql.getSQLState().startsWith("08")) {
                mensagem = "Não foi possível conectar ao banco. Verifique se o MySQL está em execução "
                        + "e tente novamente.";
            }
        }
        if (mensagem == null || mensagem.isBlank()) {
            mensagem = "Não foi possível concluir a operação. Tente atualizar a lista.";
        }
        lblStatus.setText("Operação não concluída. Confira o aviso e tente novamente.");
        javax.swing.JOptionPane.showMessageDialog(this, mensagem, "Cadastro de técnicos",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        separador = new javax.swing.JSeparator();
        separador.setBackground(new java.awt.Color(246, 247, 249));
        separador.setForeground(new java.awt.Color(201, 207, 216));
        lblModo = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblNome.setForeground(new java.awt.Color(40, 47, 56));
        txtNome = new javax.swing.JTextField();
        txtNome.setBackground(new java.awt.Color(255, 255, 255));
        txtNome.setForeground(new java.awt.Color(40, 47, 56));
        lblTelefone = new javax.swing.JLabel();
        lblTelefone.setForeground(new java.awt.Color(40, 47, 56));
        txtTelefone = new javax.swing.JTextField();
        txtTelefone.setBackground(new java.awt.Color(255, 255, 255));
        txtTelefone.setForeground(new java.awt.Color(40, 47, 56));
        lblEmail = new javax.swing.JLabel();
        lblEmail.setForeground(new java.awt.Color(40, 47, 56));
        txtEmail = new javax.swing.JTextField();
        txtEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtEmail.setForeground(new java.awt.Color(40, 47, 56));
        lblAjuda = new javax.swing.JLabel();
        lblAjuda.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnNovo = new javax.swing.JButton();
        btnNovo.setOpaque(true);
        btnNovo.setContentAreaFilled(false);
        btnNovo.setBackground(new java.awt.Color(255, 255, 255));
        btnNovo.setForeground(new java.awt.Color(40, 47, 56));
        btnNovo.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnSalvar = new javax.swing.JButton();
        btnSalvar.setOpaque(true);
        btnSalvar.setContentAreaFilled(false);
        btnEditar = new javax.swing.JButton();
        btnEditar.setOpaque(true);
        btnEditar.setContentAreaFilled(false);
        btnEditar.setBackground(new java.awt.Color(255, 255, 255));
        btnEditar.setForeground(new java.awt.Color(40, 47, 56));
        btnEditar.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnExcluir = new javax.swing.JButton();
        btnExcluir.setOpaque(true);
        btnExcluir.setContentAreaFilled(false);
        btnExcluir.setBackground(new java.awt.Color(255, 255, 255));
        btnExcluir.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnAtualizar = new javax.swing.JButton();
        btnAtualizar.setOpaque(true);
        btnAtualizar.setContentAreaFilled(false);
        btnAtualizar.setBackground(new java.awt.Color(255, 255, 255));
        btnAtualizar.setForeground(new java.awt.Color(40, 47, 56));
        btnAtualizar.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnFechar = new javax.swing.JButton();
        btnFechar.setOpaque(true);
        btnFechar.setContentAreaFilled(false);
        btnFechar.setBackground(new java.awt.Color(255, 255, 255));
        btnFechar.setForeground(new java.awt.Color(40, 47, 56));
        btnFechar.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblListagem = new javax.swing.JLabel();
        scrollTecnicos = new javax.swing.JScrollPane();
        tabelaTecnicos = new javax.swing.JTable();
        tabelaTecnicos.setBackground(new java.awt.Color(255, 255, 255));
        tabelaTecnicos.setForeground(new java.awt.Color(40, 47, 56));
        lblStatus = new javax.swing.JLabel();
        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblSenha = new javax.swing.JLabel();
        lblSenha.setForeground(new java.awt.Color(40, 47, 56));
        lblSenha.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblConfirmar = new javax.swing.JLabel();
        lblConfirmar.setForeground(new java.awt.Color(40, 47, 56));
        lblConfirmar.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtSenha = new javax.swing.JPasswordField();
        txtSenha.setBackground(new java.awt.Color(255, 255, 255));
        txtSenha.setForeground(new java.awt.Color(40, 47, 56));
        txtConfirmar = new javax.swing.JPasswordField();
        txtConfirmar.setBackground(new java.awt.Color(255, 255, 255));
        txtConfirmar.setForeground(new java.awt.Color(40, 47, 56));
        chkAdministrador = new javax.swing.JCheckBox();
        chkAdministrador.setBackground(new java.awt.Color(246, 247, 249));
        chkAdministrador.setForeground(new java.awt.Color(40, 47, 56));
        chkAdministrador.setFont(new java.awt.Font("Segoe UI", 0, 13));

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Tecnicos | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(940, 810));
        setPreferredSize(new java.awt.Dimension(1000, 860));

        lblTitulo.setText("Técnicos");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(40, 47, 56));

        lblDescricao.setText("Gerencie os técnicos, seus perfis e o acesso ao sistema.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblDescricao.setForeground(new java.awt.Color(102, 111, 123));

        lblModo.setText("Novo técnico");
        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblModo.setForeground(new java.awt.Color(40, 47, 56));

        lblNome.setText("Nome *");
        lblNome.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtNome.setToolTipText("Nome do tecnico, até 120 caracteres.");

        lblTelefone.setText("Telefone *");
        lblTelefone.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        txtTelefone.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtTelefone.setToolTipText("Informe o telefone com DDD.");

        lblEmail.setText("E-mail");
        lblEmail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblAjuda.setText("Nome, telefone e e-mail são obrigatórios. Use uma senha de 8 a 128 caracteres.");
        lblAjuda.setForeground(new java.awt.Color(102, 111, 123));

        btnNovo.setText("Novo");
        btnNovo.setToolTipText("Limpa os campos para iniciar um novo cadastro.");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnSalvar.setText("Salvar técnico");
        btnSalvar.setBackground(new java.awt.Color(43, 76, 110));
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar selecionado");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnExcluir.setText("Excluir selecionado");
        btnExcluir.setForeground(new java.awt.Color(167, 49, 49));
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnAtualizar.setText("Atualizar lista");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        btnFechar.setText("Fechar");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        lblListagem.setText("Técnicos cadastrados");
        lblListagem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblListagem.setForeground(new java.awt.Color(40, 47, 56));

        tabelaTecnicos.setAutoCreateRowSorter(true);
        tabelaTecnicos.setRowHeight(28);
        tabelaTecnicos.setFillsViewportHeight(true);
        tabelaTecnicos.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tabelaTecnicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Telefone", "E-mail", "Perfil"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollTecnicos.setViewportView(tabelaTecnicos);

        lblStatus.setText("Carregando tecnicos...");
        lblStatus.setForeground(new java.awt.Color(102, 111, 123));

        lblSenha.setText("Senha (deixe em branco para manter na edição)");

        lblConfirmar.setText("Confirmar senha");

        txtSenha.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        txtConfirmar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        chkAdministrador.setText("Administrador: pode gerenciar técnicos e senhas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addComponent(lblDescricao)
                    .addComponent(separador, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblModo)
                    .addComponent(lblNome)
                    .addComponent(txtNome, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTelefone)
                            .addComponent(txtTelefone, 140, 250, 250))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEmail)
                            .addComponent(txtEmail, 0, 600, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSenha)
                            .addComponent(txtSenha, 0, 440, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblConfirmar)
                            .addComponent(txtConfirmar, 0, 440, Short.MAX_VALUE)))
                    .addComponent(lblAjuda)
                    .addComponent(chkAdministrador)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 76, 76)
                        .addGap(8, 8, 8)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 156, 156)
                        .addGap(8, 8, 8)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, 150)
                        .addGap(8, 8, 8)
                        .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, 150)
                        .addGap(8, 8, 8)
                        .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, 120)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, 80))
                    .addComponent(lblListagem)
                    .addComponent(scrollTecnicos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, 32)
                .addGap(4, 4, 4)
                .addComponent(lblDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 22, 22)
                .addGap(18, 18, 18)
                .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 2, 2)
                .addGap(18, 18, 18)
                .addComponent(lblModo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, 24)
                .addGap(14, 14, 14)
                .addComponent(lblNome, javax.swing.GroupLayout.PREFERRED_SIZE, 20, 20)
                .addGap(6, 6, 6)
                .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 34, 34)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 20, 20)
                    .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 20, 20))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 34, 34)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 34, 34))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 20, 20)
                    .addComponent(lblConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, 20))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 34, 34)
                    .addComponent(txtConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, 34))
                .addGap(8, 8, 8)
                .addComponent(lblAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 18, 18)
                .addGap(8, 8, 8)
                .addComponent(chkAdministrador, javax.swing.GroupLayout.PREFERRED_SIZE, 24, 24)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, 36)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, 36)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, 36)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, 36)
                    .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, 36)
                    .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, 36))
                .addGap(24, 24, 24)
                .addComponent(lblListagem, javax.swing.GroupLayout.PREFERRED_SIZE, 24, 24)
                .addGap(10, 10, 10)
                .addComponent(scrollTecnicos, 100, 190, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, 20)
                .addGap(24, 24, 24))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        TelaLogin.main(args);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JCheckBox chkAdministrador;
    private javax.swing.JLabel lblAjuda;
    private javax.swing.JLabel lblConfirmar;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblListagem;
    private javax.swing.JLabel lblModo;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTelefone;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JScrollPane scrollTecnicos;
    private javax.swing.JSeparator separador;
    private javax.swing.JTable tabelaTecnicos;
    private javax.swing.JPasswordField txtConfirmar;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNome;
    private javax.swing.JPasswordField txtSenha;
    private javax.swing.JTextField txtTelefone;
    // End of variables declaration//GEN-END:variables
}

package br.com.assistencia.view;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import java.util.List;

public class TelaOrdensServico extends javax.swing.JFrame implements TelaGerenciada {

    private final OrdemServicoController controller = new OrdemServicoController();
    private CadastroSuporte<OrdemServico> suporte;
    private record DadosOpcoes(List<Tecnico> tecnicos) { }

    public TelaOrdensServico() {
        Sessao.exigirAutenticacao();
        initComponents();
        EstiloVisual.aplicar(this);
        Formularios.opcoes(cmbTecnico, List.of());
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(StatusOrdem.values()));
        Formularios.tabela(tabelaRegistros, Integer.class, java.time.LocalDateTime.class,
                String.class, String.class, String.class, StatusOrdem.class, java.math.BigDecimal.class, java.time.LocalDateTime.class);
        int[] larguras = {52, 126, 164, 206, 148, 104, 132, 126};
        for (int i = 0; i < larguras.length; i++) {
            tabelaRegistros.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        tabelaRegistros.getColumnModel().getColumn(1).setMinWidth(120);
        tabelaRegistros.getColumnModel().getColumn(6).setMinWidth(130);
        tabelaRegistros.getColumnModel().getColumn(7).setMinWidth(120);
        suporte = new CadastroSuporte<OrdemServico>(this, tabelaRegistros, lblModo, lblStatus,
                btnNovo, btnSalvar, btnEditar, btnExcluir, btnAtualizar, btnFechar,
                txtCliente, txtTelefone, txtEquipamento, cmbTecnico, cmbStatus, txtValor, txtDefeito, txtDiagnostico) {
            private String nomeClienteOriginal;
            private String telefoneOriginal;

            @Override protected List<OrdemServico> listarBanco() throws Exception { return controller.listar(); }
            @Override protected Object carregarOpcoes() throws Exception {
                return new DadosOpcoes(controller.listarResponsaveis());
            }
            @Override protected void aplicarOpcoes(Object carga) {
                Formularios.opcoes(cmbTecnico, ((DadosOpcoes) carga).tecnicos().stream()
                        .map(t -> new Opcao(t.getId(), t.getNome() + " (#" + t.getId() + ")")).toList());
            }
            @Override protected OrdemServico lerFormulario(int id) {
                OrdemServico os = new OrdemServico();
                os.setId(id); os.setClienteNome(txtCliente.getText()); os.setTecnicoId(Formularios.id(cmbTecnico));
                String telefone = txtTelefone.getText();
                if (id == 0 || !os.getClienteNome().trim().equalsIgnoreCase(nomeClienteOriginal)
                        || !telefone.equals(telefoneOriginal)) {
                    os.setClienteTelefone(telefone);
                }
                os.setEquipamentoModelo(txtEquipamento.getText()); os.setStatus((StatusOrdem) cmbStatus.getSelectedItem());
                os.setDefeitoRelatado(txtDefeito.getText()); os.setDiagnostico(txtDiagnostico.getText());
                os.setValorServico(Formularios.lerValor(txtValor.getText()));
                return os;
            }
            @Override protected void salvarBanco(OrdemServico os) throws Exception { controller.salvarAtendimento(os); }
            @Override protected void excluirBanco(int id) throws Exception { controller.excluir(id); }
            @Override protected void preencherFormulario(OrdemServico os) {
                txtCliente.setText(os.getClienteNome()); Formularios.selecionar(cmbTecnico, os.getTecnicoId());
                txtTelefone.setText(os.getClienteTelefone());
                nomeClienteOriginal = txtCliente.getText().trim();
                telefoneOriginal = txtTelefone.getText();
                txtEquipamento.setText(os.getEquipamentoModelo()); cmbStatus.setSelectedItem(os.getStatus());
                String encerramento = os.getDataEncerramento() == null
                        ? "Em andamento" : Formularios.data(os.getDataEncerramento());
                lblDatas.setText("Abertura: " + Formularios.data(os.getDataAbertura())
                        + "   |   Encerramento: " + encerramento);
                txtDefeito.setText(os.getDefeitoRelatado()); txtDiagnostico.setText(os.getDiagnostico() == null ? "" : os.getDiagnostico());
                txtValor.setText(Formularios.valorEditavel(os.getValorServico()));
            }
            @Override protected void limparFormulario() {
                txtCliente.setText(""); Formularios.selecionar(cmbTecnico, 0); txtEquipamento.setText("");
                txtTelefone.setText("");
                cmbStatus.setSelectedItem(StatusOrdem.ABERTA); txtDefeito.setText(""); txtDiagnostico.setText(""); txtValor.setText("");
                lblDatas.setText("Abertura e encerramento automáticos");
            }
            @Override protected int id(OrdemServico os) { return os.getId(); }
            @Override protected Object[] linha(OrdemServico os) {
                return new Object[]{os.getId(), os.getDataAbertura(), os.getClienteNome(), os.getEquipamentoNome(),
                    os.getTecnicoNome(), os.getStatus(), os.getValorServico(), os.getDataEncerramento()};
            }
            @Override protected String descricao(OrdemServico os) { return "a ordem de serviço #" + os.getId(); }
        };
        suporte.iniciar();
    }
    @Override public boolean fecharComConfirmacao() { return suporte.fechar(); }

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        suporte.novo();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        suporte.salvar();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        suporte.editar();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        suporte.excluir();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        suporte.atualizar();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        suporte.fechar();
    }//GEN-LAST:event_btnFecharActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblDatas = new javax.swing.JLabel();
        separador = new javax.swing.JSeparator();
        separador.setBackground(new java.awt.Color(246, 247, 249));
        separador.setForeground(new java.awt.Color(201, 207, 216));
        lblModo = new javax.swing.JLabel();
        lblTxtCliente = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        txtCliente.setBackground(new java.awt.Color(255, 255, 255));
        txtCliente.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtTelefone = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JTextField();
        txtTelefone.setBackground(new java.awt.Color(255, 255, 255));
        txtTelefone.setForeground(new java.awt.Color(40, 47, 56));
        lblCmbTecnico = new javax.swing.JLabel();
        cmbTecnico = new javax.swing.JComboBox<>();
        cmbTecnico.setBackground(new java.awt.Color(255, 255, 255));
        cmbTecnico.setForeground(new java.awt.Color(40, 47, 56));
        lblCmbStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        cmbStatus.setBackground(new java.awt.Color(255, 255, 255));
        cmbStatus.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtEquipamento = new javax.swing.JLabel();
        txtEquipamento = new javax.swing.JTextField();
        txtEquipamento.setBackground(new java.awt.Color(255, 255, 255));
        txtEquipamento.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtValor = new javax.swing.JLabel();
        txtValor = new javax.swing.JTextField();
        txtValor.setBackground(new java.awt.Color(255, 255, 255));
        txtValor.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtDefeito = new javax.swing.JLabel();
        scrolltxtDefeito = new javax.swing.JScrollPane();
        txtDefeito = new javax.swing.JTextArea();
        txtDefeito.setBackground(new java.awt.Color(255, 255, 255));
        txtDefeito.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtDiagnostico = new javax.swing.JLabel();
        scrolltxtDiagnostico = new javax.swing.JScrollPane();
        txtDiagnostico = new javax.swing.JTextArea();
        txtDiagnostico.setBackground(new java.awt.Color(255, 255, 255));
        txtDiagnostico.setForeground(new java.awt.Color(40, 47, 56));
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
        btnExcluir.setForeground(new java.awt.Color(167, 49, 49));
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
        separadorListagem = new javax.swing.JSeparator();
        separadorListagem.setBackground(new java.awt.Color(246, 247, 249));
        separadorListagem.setForeground(new java.awt.Color(201, 207, 216));
        lblListagem = new javax.swing.JLabel();
        scrollRegistros = new javax.swing.JScrollPane();
        tabelaRegistros = new javax.swing.JTable();
        tabelaRegistros.setBackground(new java.awt.Color(255, 255, 255));
        tabelaRegistros.setForeground(new java.awt.Color(40, 47, 56));
        lblStatus = new javax.swing.JLabel();
        setDefaultCloseOperation(0);
        setTitle("Ordens de serviço | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(1040, 750));
        setPreferredSize(new java.awt.Dimension(1120, 840));
        lblTitulo.setText("Ordens de serviço");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitulo.setForeground(new java.awt.Color(40, 47, 56));
        lblDatas.setText("Abertura e encerramento automáticos");
        lblDatas.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblDatas.setForeground(new java.awt.Color(102, 111, 123));
        lblModo.setText("Nova ordem de serviço");
        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblModo.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtCliente.setText("Nome do cliente *");
        lblTxtCliente.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtCliente.setForeground(new java.awt.Color(40, 47, 56));
        txtCliente.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtTelefone.setText("Telefone para contato");
        lblTxtTelefone.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtTelefone.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtTelefone.setLabelFor(txtTelefone);
        txtTelefone.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblCmbTecnico.setText("Técnico responsável *");
        lblCmbTecnico.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblCmbTecnico.setForeground(new java.awt.Color(40, 47, 56));
        cmbTecnico.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblCmbStatus.setText("Status *");
        lblCmbStatus.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblCmbStatus.setForeground(new java.awt.Color(40, 47, 56));
        cmbStatus.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtEquipamento.setText("Nome do equipamento *");
        lblTxtEquipamento.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtEquipamento.setForeground(new java.awt.Color(40, 47, 56));
        txtEquipamento.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtValor.setText("Valor do serviço (R$)");
        lblTxtValor.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtValor.setForeground(new java.awt.Color(40, 47, 56));
        txtValor.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtDefeito.setText("Defeito relatado * (até 500 caracteres)");
        lblTxtDefeito.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtDefeito.setForeground(new java.awt.Color(40, 47, 56));
        scrolltxtDefeito.setViewportView(txtDefeito);
        txtDefeito.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtDefeito.setLineWrap(true);
        txtDefeito.setWrapStyleWord(true);
        lblTxtDiagnostico.setText("Diagnóstico");
        lblTxtDiagnostico.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtDiagnostico.setForeground(new java.awt.Color(40, 47, 56));
        scrolltxtDiagnostico.setViewportView(txtDiagnostico);
        txtDiagnostico.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        btnNovo.setText("Novo");
        btnNovo.addActionListener(this::btnNovoActionPerformed);
        btnSalvar.setText("Salvar");
        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnSalvar.setBackground(new java.awt.Color(43, 76, 110));
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.addActionListener(this::btnSalvarActionPerformed);
        btnEditar.setText("Editar");
        btnEditar.addActionListener(this::btnEditarActionPerformed);
        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(this::btnExcluirActionPerformed);
        btnAtualizar.setText("Atualizar lista");
        btnAtualizar.addActionListener(this::btnAtualizarActionPerformed);
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(this::btnFecharActionPerformed);
        lblListagem.setText("Ordens cadastradas");
        lblListagem.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblListagem.setForeground(new java.awt.Color(40, 47, 56));
        scrollRegistros.setViewportView(tabelaRegistros);
        tabelaRegistros.setAutoCreateRowSorter(true);
        tabelaRegistros.setRowHeight(28);
        tabelaRegistros.setShowVerticalLines(false);
        tabelaRegistros.setFillsViewportHeight(true);
        tabelaRegistros.setFont(new java.awt.Font("Segoe UI", 0, 13));
        tabelaRegistros.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"OS","Abertura","Cliente","Equipamento","Técnico","Status","Valor","Encerramento"}) {
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        });
        lblStatus.setText("Carregando cadastros...");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblStatus.setForeground(new java.awt.Color(102, 111, 123));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitulo, -2, -2, -2)
                        .addGap(16, 16, 32767)
                        .addComponent(btnFechar, 84, 84, 84))
                    .addComponent(separador, 0, -1, 32767)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblModo, -2, -2, -2)
                        .addGap(16, 16, 32767)
                        .addComponent(lblDatas, -2, -2, -2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtCliente, -2, -2, -2)
                            .addComponent(txtCliente, 0, 560, 32767))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtTelefone, -2, -2, -2)
                            .addComponent(txtTelefone, 0, 410, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtEquipamento, -2, -2, -2)
                            .addComponent(txtEquipamento, 0, 560, 32767))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCmbTecnico, -2, -2, -2)
                            .addComponent(cmbTecnico, 0, 410, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCmbStatus, -2, -2, -2)
                            .addComponent(cmbStatus, 0, 560, 32767))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtValor, -2, -2, -2)
                            .addComponent(txtValor, 0, 410, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtDefeito, -2, -2, -2)
                            .addComponent(scrolltxtDefeito, 0, 560, 32767))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtDiagnostico, -2, -2, -2)
                            .addComponent(scrolltxtDiagnostico, 0, 410, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, 32767)
                        .addComponent(btnNovo, 86, 86, 86)
                        .addGap(8, 8, 8)
                        .addComponent(btnSalvar, 158, 158, 158))
                    .addComponent(separadorListagem, 0, -1, 32767)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblListagem, -2, -2, -2)
                        .addGap(16, 16, 32767)
                        .addComponent(btnEditar, 90, 90, 90)
                        .addGap(8, 8, 8)
                        .addComponent(btnExcluir, 90, 90, 90)
                        .addGap(8, 8, 8)
                        .addComponent(btnAtualizar, 120, 120, 120))
                    .addComponent(scrollRegistros, 0, -1, 32767)
                    .addComponent(lblStatus, 0, -1, 32767))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTitulo, 36, 36, 36)
                    .addComponent(btnFechar, 32, 32, 32))
                .addGap(12, 12, 12)
                .addComponent(separador, 1, 1, 1)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblModo, 24, 24, 24)
                    .addComponent(lblDatas, 20, 20, 20))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtCliente, 18, 18, 18)
                    .addComponent(lblTxtTelefone, 18, 18, 18))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, 34, 34, 34)
                    .addComponent(txtTelefone, 34, 34, 34))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtEquipamento, 18, 18, 18)
                    .addComponent(lblCmbTecnico, 18, 18, 18))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEquipamento, 34, 34, 34)
                    .addComponent(cmbTecnico, 34, 34, 34))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCmbStatus, 18, 18, 18)
                    .addComponent(lblTxtValor, 18, 18, 18))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbStatus, 34, 34, 34)
                    .addComponent(txtValor, 34, 34, 34))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtDefeito, 18, 18, 18)
                    .addComponent(lblTxtDiagnostico, 18, 18, 18))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrolltxtDefeito, 76, 76, 76)
                    .addComponent(scrolltxtDiagnostico, 76, 76, 76))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnNovo, 36, 36, 36)
                    .addComponent(btnSalvar, 36, 36, 36))
                .addGap(20, 20, 20)
                .addComponent(separadorListagem, 1, 1, 1)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblListagem, 24, 24, 24)
                    .addComponent(btnEditar, 32, 32, 32)
                    .addComponent(btnExcluir, 32, 32, 32)
                    .addComponent(btnAtualizar, 32, 32, 32))
                .addGap(8, 8, 8)
                .addComponent(scrollRegistros, 100, 180, 32767)
                .addGap(8, 8, 8)
                .addComponent(lblStatus, 20, 20, 20)
                .addGap(18, 18, 18))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) { TelaLogin.main(args); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblDatas;
    private javax.swing.JSeparator separador;
    private javax.swing.JLabel lblModo;
    private javax.swing.JLabel lblTxtCliente;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JLabel lblTxtTelefone;
    private javax.swing.JTextField txtTelefone;
    private javax.swing.JLabel lblCmbTecnico;
    private javax.swing.JComboBox<Opcao> cmbTecnico;
    private javax.swing.JLabel lblCmbStatus;
    private javax.swing.JComboBox<StatusOrdem> cmbStatus;
    private javax.swing.JLabel lblTxtEquipamento;
    private javax.swing.JTextField txtEquipamento;
    private javax.swing.JLabel lblTxtValor;
    private javax.swing.JTextField txtValor;
    private javax.swing.JLabel lblTxtDefeito;
    private javax.swing.JScrollPane scrolltxtDefeito;
    private javax.swing.JTextArea txtDefeito;
    private javax.swing.JLabel lblTxtDiagnostico;
    private javax.swing.JScrollPane scrolltxtDiagnostico;
    private javax.swing.JTextArea txtDiagnostico;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnFechar;
    private javax.swing.JSeparator separadorListagem;
    private javax.swing.JLabel lblListagem;
    private javax.swing.JScrollPane scrollRegistros;
    private javax.swing.JTable tabelaRegistros;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}

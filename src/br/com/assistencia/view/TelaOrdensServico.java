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
        suporte = new CadastroSuporte<OrdemServico>(this, tabelaRegistros, lblModo, lblStatus,
                btnNovo, btnSalvar, btnEditar, btnExcluir, btnAtualizar, btnFechar,
                txtCliente, cmbTecnico, txtEquipamento, cmbStatus, txtValor, txtDefeito, txtDiagnostico) {
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
                os.setEquipamentoModelo(txtEquipamento.getText()); os.setStatus((StatusOrdem) cmbStatus.getSelectedItem());
                os.setDefeitoRelatado(txtDefeito.getText()); os.setDiagnostico(txtDiagnostico.getText());
                os.setValorServico(Formularios.lerValor(txtValor.getText()));
                return os;
            }
            @Override protected void salvarBanco(OrdemServico os) throws Exception { controller.salvarAtendimento(os); }
            @Override protected void excluirBanco(int id) throws Exception { controller.excluir(id); }
            @Override protected void preencherFormulario(OrdemServico os) {
                txtCliente.setText(os.getClienteNome()); Formularios.selecionar(cmbTecnico, os.getTecnicoId());
                txtEquipamento.setText(os.getEquipamentoModelo()); cmbStatus.setSelectedItem(os.getStatus());
                txtAbertura.setText(Formularios.data(os.getDataAbertura()));
                txtEncerramento.setText(os.getDataEncerramento() == null ? "Em andamento" : Formularios.data(os.getDataEncerramento()));
                txtDefeito.setText(os.getDefeitoRelatado()); txtDiagnostico.setText(os.getDiagnostico() == null ? "" : os.getDiagnostico());
                txtValor.setText(Formularios.valorEditavel(os.getValorServico()));
            }
            @Override protected void limparFormulario() {
                txtCliente.setText(""); Formularios.selecionar(cmbTecnico, 0); txtEquipamento.setText("");
                cmbStatus.setSelectedItem(StatusOrdem.ABERTA); txtDefeito.setText(""); txtDiagnostico.setText(""); txtValor.setText("");
                txtAbertura.setText("Automática ao salvar"); txtEncerramento.setText("Automático ao encerrar");
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
        lblDescricao = new javax.swing.JLabel();
        separador = new javax.swing.JSeparator();
        separador.setBackground(new java.awt.Color(246, 247, 249));
        separador.setForeground(new java.awt.Color(201, 207, 216));
        lblModo = new javax.swing.JLabel();
        lblTxtCliente = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        txtCliente.setBackground(new java.awt.Color(255, 255, 255));
        txtCliente.setForeground(new java.awt.Color(40, 47, 56));
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
        lblTxtAbertura = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        txtAbertura.setBackground(new java.awt.Color(246, 247, 249));
        txtAbertura.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtEncerramento = new javax.swing.JLabel();
        txtEncerramento = new javax.swing.JTextField();
        txtEncerramento.setBackground(new java.awt.Color(246, 247, 249));
        txtEncerramento.setForeground(new java.awt.Color(40, 47, 56));
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
        lblAjuda = new javax.swing.JLabel();
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
        lblListagem = new javax.swing.JLabel();
        scrollRegistros = new javax.swing.JScrollPane();
        tabelaRegistros = new javax.swing.JTable();
        tabelaRegistros.setBackground(new java.awt.Color(255, 255, 255));
        tabelaRegistros.setForeground(new java.awt.Color(40, 47, 56));
        lblStatus = new javax.swing.JLabel();
        setDefaultCloseOperation(0);
        setTitle("Ordens de serviço | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(1040, 820));
        setPreferredSize(new java.awt.Dimension(1120, 900));
        lblTitulo.setText("Ordens de serviço");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitulo.setForeground(new java.awt.Color(40, 47, 56));
        lblDescricao.setText("Informe os nomes do cliente e do equipamento para abrir o atendimento.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblDescricao.setForeground(new java.awt.Color(102, 111, 123));
        lblModo.setText("Nova ordem de serviço");
        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblModo.setForeground(new java.awt.Color(40, 47, 56));
        lblTxtCliente.setText("Nome do cliente *");
        lblTxtCliente.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtCliente.setForeground(new java.awt.Color(40, 47, 56));
        txtCliente.setFont(new java.awt.Font("Segoe UI", 0, 13));
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
        lblTxtAbertura.setText("Abertura");
        lblTxtAbertura.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtAbertura.setForeground(new java.awt.Color(40, 47, 56));
        txtAbertura.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtAbertura.setEditable(false);
        lblTxtEncerramento.setText("Encerramento");
        lblTxtEncerramento.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTxtEncerramento.setForeground(new java.awt.Color(40, 47, 56));
        txtEncerramento.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtEncerramento.setEditable(false);
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
        lblAjuda.setText("Cliente e equipamento são cadastrados ao salvar a OS. Os demais dados podem ser completados nos cadastros.");
        lblAjuda.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblAjuda.setForeground(new java.awt.Color(102, 111, 123));
        btnNovo.setText("Novo");
        btnNovo.addActionListener(this::btnNovoActionPerformed);
        btnSalvar.setText("Salvar");
        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnSalvar.setBackground(new java.awt.Color(43, 76, 110));
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.addActionListener(this::btnSalvarActionPerformed);
        btnEditar.setText("Editar selecionado");
        btnEditar.addActionListener(this::btnEditarActionPerformed);
        btnExcluir.setText("Excluir selecionado");
        btnExcluir.addActionListener(this::btnExcluirActionPerformed);
        btnAtualizar.setText("Atualizar lista");
        btnAtualizar.addActionListener(this::btnAtualizarActionPerformed);
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(this::btnFecharActionPerformed);
        lblListagem.setText("Ordens de serviço");
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
                    .addComponent(lblTitulo, -2, -2, -2)
                    .addComponent(lblDescricao, -2, -2, -2)
                    .addComponent(separador, 0, -1, 32767)
                    .addComponent(lblModo, -2, -2, -2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtCliente, -2, -2, -2)
                            .addComponent(txtCliente, 0, 350, 32767))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCmbTecnico, -2, -2, -2)
                            .addComponent(cmbTecnico, 0, 350, 32767))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCmbStatus, -2, -2, -2)
                            .addComponent(cmbStatus, 0, 220, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtEquipamento, -2, -2, -2)
                            .addComponent(txtEquipamento, 0, 720, 32767))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtValor, -2, -2, -2)
                            .addComponent(txtValor, 0, 220, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtAbertura, -2, -2, -2)
                            .addComponent(txtAbertura, 0, 450, 32767))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtEncerramento, -2, -2, -2)
                            .addComponent(txtEncerramento, 0, 450, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtDefeito, -2, -2, -2)
                            .addComponent(scrolltxtDefeito, 0, 450, 32767))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtDiagnostico, -2, -2, -2)
                            .addComponent(scrolltxtDiagnostico, 0, 450, 32767)))
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
                    .addComponent(scrollRegistros, 0, -1, 32767)
                    .addComponent(lblStatus, 0, -1, 32767))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitulo, -2, 32, 32)
                .addGap(4, 4, 4)
                .addComponent(lblDescricao, -2, 22, 22)
                .addGap(16, 16, 16)
                .addComponent(separador, -2, 2, 2)
                .addGap(14, 14, 14)
                .addComponent(lblModo, -2, 24, 24)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtCliente, -2, 20, 20)
                    .addComponent(lblCmbTecnico, -2, 20, 20)
                    .addComponent(lblCmbStatus, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, -2, 34, 34)
                    .addComponent(cmbTecnico, -2, 34, 34)
                    .addComponent(cmbStatus, -2, 34, 34))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtEquipamento, -2, 20, 20)
                    .addComponent(lblTxtValor, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEquipamento, -2, 34, 34)
                    .addComponent(txtValor, -2, 34, 34))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtAbertura, -2, 20, 20)
                    .addComponent(lblTxtEncerramento, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAbertura, -2, 34, 34)
                    .addComponent(txtEncerramento, -2, 34, 34))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtDefeito, -2, 20, 20)
                    .addComponent(lblTxtDiagnostico, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrolltxtDefeito, -2, 72, 72)
                    .addComponent(scrolltxtDiagnostico, -2, 72, 72))
                .addGap(10, 10, 10)
                .addComponent(lblAjuda, -2, 20, 20)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNovo, -2, 36, 36)
                    .addComponent(btnSalvar, -2, 36, 36)
                    .addComponent(btnEditar, -2, 36, 36)
                    .addComponent(btnExcluir, -2, 36, 36)
                    .addComponent(btnAtualizar, -2, 36, 36)
                    .addComponent(btnFechar, -2, 36, 36))
                .addGap(18, 18, 18)
                .addComponent(lblListagem, -2, 24, 24)
                .addGap(8, 8, 8)
                .addComponent(scrollRegistros, 100, 170, 32767)
                .addGap(10, 10, 10)
                .addComponent(lblStatus, -2, 20, 20)
                .addGap(20, 20, 20))
        );
        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) { TelaLogin.main(args); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JSeparator separador;
    private javax.swing.JLabel lblModo;
    private javax.swing.JLabel lblTxtCliente;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JLabel lblCmbTecnico;
    private javax.swing.JComboBox<Opcao> cmbTecnico;
    private javax.swing.JLabel lblCmbStatus;
    private javax.swing.JComboBox<StatusOrdem> cmbStatus;
    private javax.swing.JLabel lblTxtEquipamento;
    private javax.swing.JTextField txtEquipamento;
    private javax.swing.JLabel lblTxtValor;
    private javax.swing.JTextField txtValor;
    private javax.swing.JLabel lblTxtAbertura;
    private javax.swing.JTextField txtAbertura;
    private javax.swing.JLabel lblTxtEncerramento;
    private javax.swing.JTextField txtEncerramento;
    private javax.swing.JLabel lblTxtDefeito;
    private javax.swing.JScrollPane scrolltxtDefeito;
    private javax.swing.JTextArea txtDefeito;
    private javax.swing.JLabel lblTxtDiagnostico;
    private javax.swing.JScrollPane scrolltxtDiagnostico;
    private javax.swing.JTextArea txtDiagnostico;
    private javax.swing.JLabel lblAjuda;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnFechar;
    private javax.swing.JLabel lblListagem;
    private javax.swing.JScrollPane scrollRegistros;
    private javax.swing.JTable tabelaRegistros;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}

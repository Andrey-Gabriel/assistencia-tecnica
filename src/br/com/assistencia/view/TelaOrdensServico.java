package br.com.assistencia.view;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import java.util.List;

public class TelaOrdensServico extends javax.swing.JFrame implements TelaGerenciada {

    private final OrdemServicoController controller = new OrdemServicoController();
    private CadastroSuporte<OrdemServico> suporte;
    private record DadosOpcoes(List<Cliente> clientes, List<Tecnico> tecnicos, List<Equipamento> equipamentos) { }

    public TelaOrdensServico() {
        Sessao.exigirAutenticacao();
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
        Formularios.opcoes(cmbCliente, List.of()); Formularios.opcoes(cmbTecnico, List.of());
        Formularios.opcoes(cmbEquipamento, List.of());
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(StatusOrdem.values()));
        Formularios.tabela(tabelaRegistros, Integer.class, java.time.LocalDateTime.class,
                String.class, String.class, String.class, StatusOrdem.class, java.math.BigDecimal.class, java.time.LocalDateTime.class);
        suporte = new CadastroSuporte<OrdemServico>(this, tabelaRegistros, lblModo, lblStatus,
                btnNovo, btnSalvar, btnEditar, btnExcluir, btnAtualizar, btnFechar,
                cmbCliente, cmbTecnico, cmbEquipamento, cmbStatus, txtValor, txtDefeito, txtDiagnostico) {
            @Override protected List<OrdemServico> listarBanco() throws Exception { return controller.listar(); }
            @Override protected Object carregarOpcoes() throws Exception {
                return new DadosOpcoes(new ClienteController().listar(), controller.listarResponsaveis(),
                        new EquipamentoController().listar());
            }
            @Override protected void aplicarOpcoes(Object carga) {
                DadosOpcoes dados = (DadosOpcoes) carga;
                Formularios.opcoes(cmbCliente, dados.clientes().stream()
                        .map(c -> new Opcao(c.getId(), c.getNome() + " (#" + c.getId() + ")")).toList());
                Formularios.opcoes(cmbTecnico, dados.tecnicos().stream()
                        .map(t -> new Opcao(t.getId(), t.getNome() + " (#" + t.getId() + ")")).toList());
                Formularios.opcoes(cmbEquipamento, dados.equipamentos().stream()
                        .map(e -> new Opcao(e.getId(), e.getCategoriaNome() + " / " + e.getMarca() + " " + e.getModelo()
                                + " (#" + e.getId() + ")")).toList());
            }
            @Override protected OrdemServico lerFormulario(int id) {
                OrdemServico os = new OrdemServico();
                os.setId(id); os.setClienteId(Formularios.id(cmbCliente)); os.setTecnicoId(Formularios.id(cmbTecnico));
                os.setEquipamentoId(Formularios.id(cmbEquipamento)); os.setStatus((StatusOrdem) cmbStatus.getSelectedItem());
                os.setDefeitoRelatado(txtDefeito.getText()); os.setDiagnostico(txtDiagnostico.getText());
                os.setValorServico(Formularios.lerValor(txtValor.getText()));
                return os;
            }
            @Override protected void salvarBanco(OrdemServico os) throws Exception { controller.salvar(os); }
            @Override protected void excluirBanco(int id) throws Exception { controller.excluir(id); }
            @Override protected void preencherFormulario(OrdemServico os) {
                Formularios.selecionar(cmbCliente, os.getClienteId()); Formularios.selecionar(cmbTecnico, os.getTecnicoId());
                Formularios.selecionar(cmbEquipamento, os.getEquipamentoId()); cmbStatus.setSelectedItem(os.getStatus());
                txtAbertura.setText(Formularios.data(os.getDataAbertura()));
                txtEncerramento.setText(os.getDataEncerramento() == null ? "Em andamento" : Formularios.data(os.getDataEncerramento()));
                txtDefeito.setText(os.getDefeitoRelatado()); txtDiagnostico.setText(os.getDiagnostico() == null ? "" : os.getDiagnostico());
                txtValor.setText(Formularios.valorEditavel(os.getValorServico()));
            }
            @Override protected void limparFormulario() {
                Formularios.selecionar(cmbCliente, 0); Formularios.selecionar(cmbTecnico, 0); Formularios.selecionar(cmbEquipamento, 0);
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
        lblModo = new javax.swing.JLabel();
        lblCmbCliente = new javax.swing.JLabel();
        cmbCliente = new javax.swing.JComboBox<>();
        lblCmbTecnico = new javax.swing.JLabel();
        cmbTecnico = new javax.swing.JComboBox<>();
        lblCmbStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        lblCmbEquipamento = new javax.swing.JLabel();
        cmbEquipamento = new javax.swing.JComboBox<>();
        lblTxtValor = new javax.swing.JLabel();
        txtValor = new javax.swing.JTextField();
        lblTxtAbertura = new javax.swing.JLabel();
        txtAbertura = new javax.swing.JTextField();
        lblTxtEncerramento = new javax.swing.JLabel();
        txtEncerramento = new javax.swing.JTextField();
        lblTxtDefeito = new javax.swing.JLabel();
        scrolltxtDefeito = new javax.swing.JScrollPane();
        txtDefeito = new javax.swing.JTextArea();
        lblTxtDiagnostico = new javax.swing.JLabel();
        scrolltxtDiagnostico = new javax.swing.JScrollPane();
        txtDiagnostico = new javax.swing.JTextArea();
        lblAjuda = new javax.swing.JLabel();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnFechar = new javax.swing.JButton();
        lblListagem = new javax.swing.JLabel();
        scrollRegistros = new javax.swing.JScrollPane();
        tabelaRegistros = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        setDefaultCloseOperation(0);
        setTitle("Ordens de serviço | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(1040, 820));
        setPreferredSize(new java.awt.Dimension(1120, 900));
        lblTitulo.setText("Ordens de serviço");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 28));
        lblTitulo.setForeground(new java.awt.Color(25, 42, 64));
        lblDescricao.setText("Acompanhe cada atendimento desde a entrada até o encerramento.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblDescricao.setForeground(new java.awt.Color(25, 42, 64));
        lblModo.setText("Nova ordem de serviço");
        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblModo.setForeground(new java.awt.Color(25, 42, 64));
        lblCmbCliente.setText("Cliente *");
        lblCmbCliente.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblCmbCliente.setForeground(new java.awt.Color(25, 42, 64));
        cmbCliente.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblCmbTecnico.setText("Técnico responsável *");
        lblCmbTecnico.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblCmbTecnico.setForeground(new java.awt.Color(25, 42, 64));
        cmbTecnico.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblCmbStatus.setText("Status *");
        lblCmbStatus.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblCmbStatus.setForeground(new java.awt.Color(25, 42, 64));
        cmbStatus.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblCmbEquipamento.setText("Equipamento *");
        lblCmbEquipamento.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblCmbEquipamento.setForeground(new java.awt.Color(25, 42, 64));
        cmbEquipamento.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblTxtValor.setText("Valor do serviço (R$)");
        lblTxtValor.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtValor.setForeground(new java.awt.Color(25, 42, 64));
        txtValor.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblTxtAbertura.setText("Abertura");
        lblTxtAbertura.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtAbertura.setForeground(new java.awt.Color(25, 42, 64));
        txtAbertura.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtAbertura.setEditable(false);
        lblTxtEncerramento.setText("Encerramento");
        lblTxtEncerramento.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtEncerramento.setForeground(new java.awt.Color(25, 42, 64));
        txtEncerramento.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtEncerramento.setEditable(false);
        lblTxtDefeito.setText("Defeito relatado * (até 500 caracteres)");
        lblTxtDefeito.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtDefeito.setForeground(new java.awt.Color(25, 42, 64));
        scrolltxtDefeito.setViewportView(txtDefeito);
        txtDefeito.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtDefeito.setLineWrap(true);
        txtDefeito.setWrapStyleWord(true);
        lblTxtDiagnostico.setText("Diagnóstico");
        lblTxtDiagnostico.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtDiagnostico.setForeground(new java.awt.Color(25, 42, 64));
        scrolltxtDiagnostico.setViewportView(txtDiagnostico);
        txtDiagnostico.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        lblAjuda.setText("Concluída, Entregue e Cancelada encerram automaticamente. Reabrir a ordem remove o encerramento.");
        lblAjuda.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblAjuda.setForeground(new java.awt.Color(25, 42, 64));
        btnNovo.setText("Novo");
        btnNovo.addActionListener(this::btnNovoActionPerformed);
        btnSalvar.setText("Salvar");
        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnSalvar.setBackground(new java.awt.Color(34, 76, 125));
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
        lblListagem.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblListagem.setForeground(new java.awt.Color(25, 42, 64));
        scrollRegistros.setViewportView(tabelaRegistros);
        tabelaRegistros.setAutoCreateRowSorter(true);
        tabelaRegistros.setRowHeight(30);
        tabelaRegistros.setShowVerticalLines(false);
        tabelaRegistros.setFillsViewportHeight(true);
        tabelaRegistros.setFont(new java.awt.Font("Segoe UI", 0, 13));
        tabelaRegistros.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"OS","Abertura","Cliente","Equipamento","Técnico","Status","Valor","Encerramento"}) {
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        });
        lblStatus.setText("Carregando cadastros...");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblStatus.setForeground(new java.awt.Color(25, 42, 64));
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
                            .addComponent(lblCmbCliente, -2, -2, -2)
                            .addComponent(cmbCliente, 0, 350, 32767))
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
                            .addComponent(lblCmbEquipamento, -2, -2, -2)
                            .addComponent(cmbEquipamento, 0, 720, 32767))
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
                .addComponent(lblTitulo, -2, 36, 36)
                .addGap(4, 4, 4)
                .addComponent(lblDescricao, -2, 22, 22)
                .addGap(16, 16, 16)
                .addComponent(separador, -2, 2, 2)
                .addGap(14, 14, 14)
                .addComponent(lblModo, -2, 24, 24)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCmbCliente, -2, 20, 20)
                    .addComponent(lblCmbTecnico, -2, 20, 20)
                    .addComponent(lblCmbStatus, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbCliente, -2, 34, 34)
                    .addComponent(cmbTecnico, -2, 34, 34)
                    .addComponent(cmbStatus, -2, 34, 34))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCmbEquipamento, -2, 20, 20)
                    .addComponent(lblTxtValor, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbEquipamento, -2, 34, 34)
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
    private javax.swing.JLabel lblCmbCliente;
    private javax.swing.JComboBox<Opcao> cmbCliente;
    private javax.swing.JLabel lblCmbTecnico;
    private javax.swing.JComboBox<Opcao> cmbTecnico;
    private javax.swing.JLabel lblCmbStatus;
    private javax.swing.JComboBox<StatusOrdem> cmbStatus;
    private javax.swing.JLabel lblCmbEquipamento;
    private javax.swing.JComboBox<Opcao> cmbEquipamento;
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

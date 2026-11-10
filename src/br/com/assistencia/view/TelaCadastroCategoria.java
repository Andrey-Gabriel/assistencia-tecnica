package br.com.assistencia.view;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import java.util.List;

public class TelaCadastroCategoria extends javax.swing.JFrame implements TelaGerenciada {

    private final CategoriaController controller = new CategoriaController();
    private CadastroSuporte<Categoria> suporte;

    public TelaCadastroCategoria() {
        Sessao.exigirAutenticacao();
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
        Formularios.tabela(tabelaRegistros, Integer.class, String.class);
        suporte = new CadastroSuporte<Categoria>(this, tabelaRegistros, lblModo, lblStatus,
                btnNovo, btnSalvar, btnEditar, btnExcluir, btnAtualizar, btnFechar, txtNome) {
            @Override protected List<Categoria> listarBanco() throws Exception { return controller.listar(); }
            @Override protected Categoria lerFormulario(int id) { return new Categoria(id, txtNome.getText()); }
            @Override protected void salvarBanco(Categoria c) throws Exception {
                if (c.getId() == 0) controller.cadastrar(c.getNome());
                else controller.atualizar(c.getId(), c.getNome());
            }
            @Override protected void excluirBanco(int id) throws Exception { controller.excluir(id); }
            @Override protected void preencherFormulario(Categoria c) { txtNome.setText(c.getNome()); }
            @Override protected void limparFormulario() { txtNome.setText(""); }
            @Override protected int id(Categoria c) { return c.getId(); }
            @Override protected Object[] linha(Categoria c) { return new Object[]{c.getId(), c.getNome()}; }
            @Override protected String descricao(Categoria c) { return "a categoria " + c.getNome(); }
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
        lblTxtNome = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
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
        setTitle("Categorias | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(940, 580));
        setPreferredSize(new java.awt.Dimension(1000, 630));
        lblTitulo.setText("Categorias");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 28));
        lblTitulo.setForeground(new java.awt.Color(25, 42, 64));
        lblDescricao.setText("Organize os tipos de equipamentos atendidos pela assistência.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblDescricao.setForeground(new java.awt.Color(25, 42, 64));
        lblModo.setText("Nova categoria");
        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblModo.setForeground(new java.awt.Color(25, 42, 64));
        lblTxtNome.setText("Nome da categoria *");
        lblTxtNome.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtNome.setForeground(new java.awt.Color(25, 42, 64));
        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblAjuda.setText("Informe um nome de até 80 caracteres. Novo limpa os campos; Salvar grava o cadastro.");
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
        lblListagem.setText("Categorias cadastradas");
        lblListagem.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblListagem.setForeground(new java.awt.Color(25, 42, 64));
        scrollRegistros.setViewportView(tabelaRegistros);
        tabelaRegistros.setAutoCreateRowSorter(true);
        tabelaRegistros.setRowHeight(30);
        tabelaRegistros.setShowVerticalLines(false);
        tabelaRegistros.setFillsViewportHeight(true);
        tabelaRegistros.setFont(new java.awt.Font("Segoe UI", 0, 13));
        tabelaRegistros.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"ID","Categoria"}) {
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
                            .addComponent(lblTxtNome, -2, -2, -2)
                            .addComponent(txtNome, 0, 450, 32767)))
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
                    .addComponent(lblTxtNome, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNome, -2, 34, 34))
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
                .addComponent(scrollRegistros, 100, 180, 32767)
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
    private javax.swing.JLabel lblTxtNome;
    private javax.swing.JTextField txtNome;
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

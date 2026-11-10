package br.com.assistencia.view;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import java.util.List;

public class TelaCadastroEquipamento extends javax.swing.JFrame implements TelaGerenciada {

    private final EquipamentoController controller = new EquipamentoController();
    private final CategoriaController categorias = new CategoriaController();
    private CadastroSuporte<Equipamento> suporte;
    private record DadosOpcoes(List<Categoria> categorias) { }

    public TelaCadastroEquipamento() {
        Sessao.exigirAutenticacao();
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
        Formularios.opcoes(cmbCategoria, List.of());
        Formularios.tabela(tabelaRegistros, Integer.class, String.class, String.class, String.class, String.class);
        suporte = new CadastroSuporte<Equipamento>(this, tabelaRegistros, lblModo, lblStatus,
                btnNovo, btnSalvar, btnEditar, btnExcluir, btnAtualizar, btnFechar,
                cmbCategoria, txtMarca, txtModelo, txtSerie) {
            @Override protected List<Equipamento> listarBanco() throws Exception { return controller.listar(); }
            @Override protected Object carregarOpcoes() throws Exception { return new DadosOpcoes(categorias.listar()); }
            @Override protected void aplicarOpcoes(Object dados) {
                Formularios.opcoes(cmbCategoria, ((DadosOpcoes) dados).categorias().stream()
                        .map(c -> new Opcao(c.getId(), c.getNome())).toList());
            }
            @Override protected Equipamento lerFormulario(int id) {
                return new Equipamento(id, Formularios.id(cmbCategoria), txtMarca.getText(), txtModelo.getText(), txtSerie.getText());
            }
            @Override protected void salvarBanco(Equipamento e) throws Exception { controller.salvar(e); }
            @Override protected void excluirBanco(int id) throws Exception { controller.excluir(id); }
            @Override protected void preencherFormulario(Equipamento e) {
                Formularios.selecionar(cmbCategoria, e.getCategoriaId());
                txtMarca.setText(e.getMarca()); txtModelo.setText(e.getModelo());
                txtSerie.setText(e.getNumeroSerie() == null ? "" : e.getNumeroSerie());
            }
            @Override protected void limparFormulario() {
                Formularios.selecionar(cmbCategoria, 0);
                txtMarca.setText(""); txtModelo.setText(""); txtSerie.setText("");
            }
            @Override protected int id(Equipamento e) { return e.getId(); }
            @Override protected Object[] linha(Equipamento e) {
                return new Object[]{e.getId(), e.getCategoriaNome(), e.getMarca(), e.getModelo(), e.getNumeroSerie()};
            }
            @Override protected String descricao(Equipamento e) { return "o equipamento #" + e.getId() + " (" + e.getMarca() + " " + e.getModelo() + ")"; }
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
        lblCmbCategoria = new javax.swing.JLabel();
        cmbCategoria = new javax.swing.JComboBox<>();
        lblTxtMarca = new javax.swing.JLabel();
        txtMarca = new javax.swing.JTextField();
        lblTxtModelo = new javax.swing.JLabel();
        txtModelo = new javax.swing.JTextField();
        lblTxtSerie = new javax.swing.JLabel();
        txtSerie = new javax.swing.JTextField();
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
        setTitle("Equipamentos | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(940, 660));
        setPreferredSize(new java.awt.Dimension(1000, 720));
        lblTitulo.setText("Equipamentos");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 28));
        lblTitulo.setForeground(new java.awt.Color(25, 42, 64));
        lblDescricao.setText("Cadastre os aparelhos e vincule cada um à sua categoria.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblDescricao.setForeground(new java.awt.Color(25, 42, 64));
        lblModo.setText("Novo equipamento");
        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblModo.setForeground(new java.awt.Color(25, 42, 64));
        lblCmbCategoria.setText("Categoria *");
        lblCmbCategoria.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblCmbCategoria.setForeground(new java.awt.Color(25, 42, 64));
        cmbCategoria.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblTxtMarca.setText("Marca *");
        lblTxtMarca.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtMarca.setForeground(new java.awt.Color(25, 42, 64));
        txtMarca.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblTxtModelo.setText("Modelo *");
        lblTxtModelo.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtModelo.setForeground(new java.awt.Color(25, 42, 64));
        txtModelo.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblTxtSerie.setText("Número de série");
        lblTxtSerie.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTxtSerie.setForeground(new java.awt.Color(25, 42, 64));
        txtSerie.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblAjuda.setText("Categoria, marca e modelo são obrigatórios. O número de série é opcional.");
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
        lblListagem.setText("Equipamentos cadastrados");
        lblListagem.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblListagem.setForeground(new java.awt.Color(25, 42, 64));
        scrollRegistros.setViewportView(tabelaRegistros);
        tabelaRegistros.setAutoCreateRowSorter(true);
        tabelaRegistros.setRowHeight(30);
        tabelaRegistros.setShowVerticalLines(false);
        tabelaRegistros.setFillsViewportHeight(true);
        tabelaRegistros.setFont(new java.awt.Font("Segoe UI", 0, 13));
        tabelaRegistros.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"ID","Categoria","Marca","Modelo","Número de série"}) {
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
                            .addComponent(lblCmbCategoria, -2, -2, -2)
                            .addComponent(cmbCategoria, 0, 450, 32767))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtMarca, -2, -2, -2)
                            .addComponent(txtMarca, 0, 450, 32767)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtModelo, -2, -2, -2)
                            .addComponent(txtModelo, 0, 450, 32767))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtSerie, -2, -2, -2)
                            .addComponent(txtSerie, 0, 450, 32767)))
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
                    .addComponent(lblCmbCategoria, -2, 20, 20)
                    .addComponent(lblTxtMarca, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbCategoria, -2, 34, 34)
                    .addComponent(txtMarca, -2, 34, 34))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTxtModelo, -2, 20, 20)
                    .addComponent(lblTxtSerie, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtModelo, -2, 34, 34)
                    .addComponent(txtSerie, -2, 34, 34))
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
    private javax.swing.JLabel lblCmbCategoria;
    private javax.swing.JComboBox<Opcao> cmbCategoria;
    private javax.swing.JLabel lblTxtMarca;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JLabel lblTxtModelo;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JLabel lblTxtSerie;
    private javax.swing.JTextField txtSerie;
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

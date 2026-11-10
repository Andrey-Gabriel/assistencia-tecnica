package br.com.assistencia.view;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import java.util.List;

public class TelaRelatorio extends javax.swing.JFrame implements TelaGerenciada {

    private final RelatorioController controller = new RelatorioController();
    private boolean ocupado;

    public TelaRelatorio() {
        Sessao.exigirAutenticacao();
        initComponents();
        EstiloVisual.aplicar(this);
        Formularios.tabela(tabelaRegistros, Integer.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class,
                String.class, String.class, String.class, StatusOrdem.class, java.math.BigDecimal.class);
        txtInicio.setText(Formularios.dia(java.time.LocalDate.now().withDayOfMonth(1)));
        txtFim.setText(Formularios.dia(java.time.LocalDate.now()));
        getRootPane().setDefaultButton(btnConsultar);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { fecharComConfirmacao(); }
        });
        consultar();
    }
    private void ocupado(boolean valor) {
        ocupado = valor;
        txtInicio.setEnabled(!valor); txtFim.setEnabled(!valor);
        btnConsultar.setEnabled(!valor); btnFechar.setEnabled(!valor);
    }
    private void consultar() {
        if (ocupado) return;
        java.time.LocalDate inicio, fim;
        try { inicio = Formularios.lerDia(txtInicio.getText()); fim = Formularios.lerDia(txtFim.getText()); }
        catch (IllegalArgumentException e) { Formularios.erro(this, e); return; }
        ocupado(true);
        ((javax.swing.table.DefaultTableModel) tabelaRegistros.getModel()).setRowCount(0);
        lblResumo.setText("Consultando...");
        lblStatus.setText("Consultando ordens de serviço...");
        new javax.swing.SwingWorker<List<LinhaRelatorio>, Void>() {
            @Override protected List<LinhaRelatorio> doInBackground() throws Exception { return controller.consultar(inicio, fim); }
            @Override protected void done() {
                ocupado(false);
                try {
                    List<LinhaRelatorio> linhas = get();
                    javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tabelaRegistros.getModel();
                    java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                    for (LinhaRelatorio r : linhas) {
                        modelo.addRow(new Object[]{r.numeroOs(), r.abertura(), r.encerramento(), r.cliente(),
                            Formularios.equipamento(r.categoria(), r.marca(), r.modelo()), r.tecnico(), r.status(), r.valor()});
                        if (r.valor() != null) total = total.add(r.valor());
                    }
                    lblResumo.setText(linhas.size() + " ordem(ns) | Valor dos serviços: " + Formularios.moeda(total));
                    lblStatus.setText(linhas.isEmpty() ? "Nenhuma ordem aberta neste período."
                            : "Período: " + Formularios.dia(inicio) + " a " + Formularios.dia(fim) + ".");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); lblResumo.setText("Consulta interrompida.");
                } catch (java.util.concurrent.ExecutionException e) {
                    lblResumo.setText("Consulta não concluída."); lblStatus.setText("Confira o aviso e tente novamente.");
                    Formularios.erro(TelaRelatorio.this, e.getCause());
                }
            }
        }.execute();
    }
    @Override public boolean fecharComConfirmacao() {
        if (ocupado) return false;
        dispose(); return true;
    }


    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        consultar();
    }//GEN-LAST:event_btnConsultarActionPerformed
    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        fecharComConfirmacao();
    }//GEN-LAST:event_btnFecharActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        separador = new javax.swing.JSeparator();
        separador.setBackground(new java.awt.Color(246, 247, 249));
        separador.setForeground(new java.awt.Color(201, 207, 216));
        lblPeriodo = new javax.swing.JLabel();
        lblInicio = new javax.swing.JLabel();
        txtInicio = new javax.swing.JTextField();
        txtInicio.setBackground(new java.awt.Color(255, 255, 255));
        txtInicio.setForeground(new java.awt.Color(40, 47, 56));
        lblFim = new javax.swing.JLabel();
        txtFim = new javax.swing.JTextField();
        txtFim.setBackground(new java.awt.Color(255, 255, 255));
        txtFim.setForeground(new java.awt.Color(40, 47, 56));
        btnConsultar = new javax.swing.JButton();
        btnConsultar.setOpaque(true);
        btnConsultar.setContentAreaFilled(false);
        btnFechar = new javax.swing.JButton();
        btnFechar.setOpaque(true);
        btnFechar.setContentAreaFilled(false);
        btnFechar.setBackground(new java.awt.Color(255, 255, 255));
        btnFechar.setForeground(new java.awt.Color(40, 47, 56));
        btnFechar.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblAjuda = new javax.swing.JLabel();
        scrollRegistros = new javax.swing.JScrollPane();
        tabelaRegistros = new javax.swing.JTable();
        tabelaRegistros.setBackground(new java.awt.Color(255, 255, 255));
        tabelaRegistros.setForeground(new java.awt.Color(40, 47, 56));
        lblResumo = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        setDefaultCloseOperation(0);
        setTitle("Relatório por período | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(1040, 600));
        setPreferredSize(new java.awt.Dimension(1120, 700));
        lblTitulo.setText("Relatório por período");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitulo.setForeground(new java.awt.Color(40, 47, 56));
        lblDescricao.setText("Consulte as ordens pela data de abertura e acompanhe o valor dos serviços.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblDescricao.setForeground(new java.awt.Color(102, 111, 123));
        lblPeriodo.setText("Período de abertura");
        lblPeriodo.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblPeriodo.setForeground(new java.awt.Color(40, 47, 56));
        lblInicio.setText("Data inicial (dd/mm/aaaa)");
        lblInicio.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblInicio.setForeground(new java.awt.Color(40, 47, 56));
        txtInicio.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblFim.setText("Data final (dd/mm/aaaa)");
        lblFim.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblFim.setForeground(new java.awt.Color(40, 47, 56));
        txtFim.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnConsultar.setText("Consultar");
        btnConsultar.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnConsultar.setBackground(new java.awt.Color(43, 76, 110));
        btnConsultar.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultar.addActionListener(this::btnConsultarActionPerformed);
        btnFechar.setText("Fechar");
        btnFechar.addActionListener(this::btnFecharActionPerformed);
        lblAjuda.setText("A consulta inclui todo o último dia do período informado.");
        lblAjuda.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblAjuda.setForeground(new java.awt.Color(102, 111, 123));
        scrollRegistros.setViewportView(tabelaRegistros);
        tabelaRegistros.setAutoCreateRowSorter(true);
        tabelaRegistros.setRowHeight(28);
        tabelaRegistros.setShowVerticalLines(false);
        tabelaRegistros.setFillsViewportHeight(true);
        tabelaRegistros.setFont(new java.awt.Font("Segoe UI", 0, 13));
        tabelaRegistros.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"OS","Abertura","Encerramento","Cliente","Equipamento","Técnico","Status","Valor"}) {
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        });
        lblResumo.setText("Nenhuma consulta realizada.");
        lblResumo.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblResumo.setForeground(new java.awt.Color(40, 47, 56));
        lblStatus.setText("Informe o período e clique em Consultar.");
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
                    .addComponent(lblPeriodo, -2, -2, -2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblInicio, -2, -2, -2)
                            .addComponent(txtInicio, 0, 220, 220))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFim, -2, -2, -2)
                            .addComponent(txtFim, 0, 220, 220))
                        .addGap(16, 16, 16)
                        .addComponent(btnConsultar, -2, 136, 136)
                        .addGap(0, 0, 32767)
                        .addComponent(btnFechar, -2, 80, 80))
                    .addComponent(lblAjuda, -2, -2, -2)
                    .addComponent(scrollRegistros, 0, -1, 32767)
                    .addComponent(lblResumo, 0, -1, 32767)
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
                .addGap(18, 18, 18)
                .addComponent(separador, -2, 2, 2)
                .addGap(18, 18, 18)
                .addComponent(lblPeriodo, -2, 24, 24)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInicio, -2, 20, 20)
                    .addComponent(lblFim, -2, 20, 20))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtInicio, -2, 34, 34)
                    .addComponent(txtFim, -2, 34, 34)
                    .addComponent(btnConsultar, -2, 36, 36)
                    .addComponent(btnFechar, -2, 36, 36))
                .addGap(10, 10, 10)
                .addComponent(lblAjuda, -2, 20, 20)
                .addGap(18, 18, 18)
                .addComponent(scrollRegistros, 160, 320, 32767)
                .addGap(14, 14, 14)
                .addComponent(lblResumo, -2, 24, 24)
                .addGap(8, 8, 8)
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
    private javax.swing.JLabel lblPeriodo;
    private javax.swing.JLabel lblInicio;
    private javax.swing.JTextField txtInicio;
    private javax.swing.JLabel lblFim;
    private javax.swing.JTextField txtFim;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnFechar;
    private javax.swing.JLabel lblAjuda;
    private javax.swing.JScrollPane scrollRegistros;
    private javax.swing.JTable tabelaRegistros;
    private javax.swing.JLabel lblResumo;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}

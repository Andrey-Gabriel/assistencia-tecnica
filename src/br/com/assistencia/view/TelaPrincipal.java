package br.com.assistencia.view;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import java.util.List;

public class TelaPrincipal extends javax.swing.JFrame implements TelaGerenciada {

    private final java.util.Map<String, javax.swing.JFrame> janelas = new java.util.LinkedHashMap<>();

    public TelaPrincipal() {
        Sessao.exigirAutenticacao();
        initComponents();
        boolean admin = Sessao.ehAdministrador();
        btnTecnicos.setVisible(admin); lblTecnicos.setVisible(admin); menuTecnicos.setVisible(admin);
        lblRodape.setText("Conectado: " + Sessao.nomeTecnico() + (admin ? " | Administrador" : " | Técnico"));
        getContentPane().setBackground(new java.awt.Color(246, 248, 252));
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { fecharComConfirmacao(); }
        });
    }
    private void abrir(String chave, java.util.function.Supplier<? extends javax.swing.JFrame> fabrica) {
        Sessao.exigirAutenticacao();
        javax.swing.JFrame janela = janelas.get(chave);
        if (janela == null || !janela.isDisplayable()) {
            javax.swing.JFrame nova = fabrica.get();
            nova.setLocationRelativeTo(this);
            nova.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent e) { janelas.remove(chave, nova); }
            });
            janelas.put(chave, nova);
            janela = nova;
        }
        janela.setVisible(true); janela.toFront();
    }
    @Override public boolean fecharComConfirmacao() {
        for (javax.swing.JFrame janela : new java.util.ArrayList<>(janelas.values())) {
            if (janela.isDisplayable() && janela instanceof TelaGerenciada gerenciada
                    && !gerenciada.fecharComConfirmacao()) return false;
        }
        Sessao.encerrar(); dispose(); return true;
    }

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        abrir("Clientes", TelaCadastroCliente::new);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void menuClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuClientesActionPerformed
        abrir("Clientes", TelaCadastroCliente::new);
    }//GEN-LAST:event_menuClientesActionPerformed

    private void btnCategoriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriasActionPerformed
        abrir("Categorias", TelaCadastroCategoria::new);
    }//GEN-LAST:event_btnCategoriasActionPerformed

    private void menuCategoriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCategoriasActionPerformed
        abrir("Categorias", TelaCadastroCategoria::new);
    }//GEN-LAST:event_menuCategoriasActionPerformed

    private void btnEquipamentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEquipamentosActionPerformed
        abrir("Equipamentos", TelaCadastroEquipamento::new);
    }//GEN-LAST:event_btnEquipamentosActionPerformed

    private void menuEquipamentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEquipamentosActionPerformed
        abrir("Equipamentos", TelaCadastroEquipamento::new);
    }//GEN-LAST:event_menuEquipamentosActionPerformed

    private void btnOrdensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdensActionPerformed
        abrir("Ordens", TelaOrdensServico::new);
    }//GEN-LAST:event_btnOrdensActionPerformed

    private void menuOrdensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOrdensActionPerformed
        abrir("Ordens", TelaOrdensServico::new);
    }//GEN-LAST:event_menuOrdensActionPerformed

    private void btnRelatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRelatorioActionPerformed
        abrir("Relatorio", TelaRelatorio::new);
    }//GEN-LAST:event_btnRelatorioActionPerformed

    private void menuRelatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRelatorioActionPerformed
        abrir("Relatorio", TelaRelatorio::new);
    }//GEN-LAST:event_menuRelatorioActionPerformed

    private void btnTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTecnicosActionPerformed
        if (!Sessao.ehAdministrador()) return;
        abrir("Tecnicos", TelaCadastroTecnico::new);
    }//GEN-LAST:event_btnTecnicosActionPerformed

    private void menuTecnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuTecnicosActionPerformed
        if (!Sessao.ehAdministrador()) return;
        abrir("Tecnicos", TelaCadastroTecnico::new);
    }//GEN-LAST:event_menuTecnicosActionPerformed

    private void menuSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSairActionPerformed
        if (fecharComConfirmacao()) new TelaLogin().setVisible(true);
    }//GEN-LAST:event_menuSairActionPerformed


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        barraMenu = new javax.swing.JMenuBar();
        menuCadastros = new javax.swing.JMenu();
        menuClientes = new javax.swing.JMenuItem();
        menuCategorias = new javax.swing.JMenuItem();
        menuEquipamentos = new javax.swing.JMenuItem();
        menuTecnicos = new javax.swing.JMenuItem();
        menuAtendimento = new javax.swing.JMenu();
        menuOrdens = new javax.swing.JMenuItem();
        menuRelatorio = new javax.swing.JMenuItem();
        menuSistema = new javax.swing.JMenu();
        menuSair = new javax.swing.JMenuItem();

        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        separador = new javax.swing.JSeparator();
        lblSecao = new javax.swing.JLabel();
        btnClientes = new javax.swing.JButton();
        lblClientes = new javax.swing.JLabel();
        btnCategorias = new javax.swing.JButton();
        lblCategorias = new javax.swing.JLabel();
        btnEquipamentos = new javax.swing.JButton();
        lblEquipamentos = new javax.swing.JLabel();
        btnOrdens = new javax.swing.JButton();
        lblOrdens = new javax.swing.JLabel();
        btnRelatorio = new javax.swing.JButton();
        lblRelatorio = new javax.swing.JLabel();
        btnTecnicos = new javax.swing.JButton();
        lblTecnicos = new javax.swing.JLabel();
        lblRodape = new javax.swing.JLabel();
        setDefaultCloseOperation(0);
        setTitle("Assistência Técnica | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(1040, 580));
        setPreferredSize(new java.awt.Dimension(1120, 620));
        lblTitulo.setText("Assistência Técnica");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 30));
        lblTitulo.setForeground(new java.awt.Color(25, 42, 64));
        lblDescricao.setText("Organize os aparelhos, os cadastros e os atendimentos.");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblDescricao.setForeground(new java.awt.Color(25, 42, 64));
        lblSecao.setText("ATENDIMENTO E CADASTROS");
        lblSecao.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblSecao.setForeground(new java.awt.Color(25, 42, 64));
        btnClientes.setText("Gerenciar clientes");
        btnClientes.addActionListener(this::btnClientesActionPerformed);
        lblClientes.setText("Dados e contatos dos clientes.");
        lblClientes.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblClientes.setForeground(new java.awt.Color(25, 42, 64));
        btnCategorias.setText("Categorias");
        btnCategorias.addActionListener(this::btnCategoriasActionPerformed);
        lblCategorias.setText("Tipos de aparelhos atendidos.");
        lblCategorias.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblCategorias.setForeground(new java.awt.Color(25, 42, 64));
        btnEquipamentos.setText("Equipamentos");
        btnEquipamentos.addActionListener(this::btnEquipamentosActionPerformed);
        lblEquipamentos.setText("Identificação dos aparelhos.");
        lblEquipamentos.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblEquipamentos.setForeground(new java.awt.Color(25, 42, 64));
        btnOrdens.setText("Ordens de serviço");
        btnOrdens.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOrdens.setBackground(new java.awt.Color(34, 76, 125));
        btnOrdens.setForeground(new java.awt.Color(255, 255, 255));
        btnOrdens.addActionListener(this::btnOrdensActionPerformed);
        lblOrdens.setText("Abertura e andamento dos serviços.");
        lblOrdens.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblOrdens.setForeground(new java.awt.Color(25, 42, 64));
        btnRelatorio.setText("Relatório por período");
        btnRelatorio.addActionListener(this::btnRelatorioActionPerformed);
        lblRelatorio.setText("Consulta e valores por período.");
        lblRelatorio.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblRelatorio.setForeground(new java.awt.Color(25, 42, 64));
        btnTecnicos.setText("Gerenciar técnicos");
        btnTecnicos.addActionListener(this::btnTecnicosActionPerformed);
        lblTecnicos.setText("Usuários, senhas e perfis.");
        lblTecnicos.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblTecnicos.setForeground(new java.awt.Color(25, 42, 64));
        lblRodape.setText("Conectado");
        lblRodape.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblRodape.setForeground(new java.awt.Color(25, 42, 64));
        menuCadastros.setText("Cadastros");
        menuClientes.setText("Clientes");
        menuClientes.addActionListener(this::menuClientesActionPerformed);
        menuCadastros.add(menuClientes);
        menuCategorias.setText("Categorias");
        menuCategorias.addActionListener(this::menuCategoriasActionPerformed);
        menuCadastros.add(menuCategorias);
        menuEquipamentos.setText("Equipamentos");
        menuEquipamentos.addActionListener(this::menuEquipamentosActionPerformed);
        menuCadastros.add(menuEquipamentos);
        menuTecnicos.setText("Técnicos");
        menuTecnicos.addActionListener(this::menuTecnicosActionPerformed);
        menuCadastros.add(menuTecnicos);
        barraMenu.add(menuCadastros);
        menuAtendimento.setText("Atendimento");
        menuOrdens.setText("Ordens de serviço");
        menuOrdens.addActionListener(this::menuOrdensActionPerformed);
        menuAtendimento.add(menuOrdens);
        menuRelatorio.setText("Relatório por período");
        menuRelatorio.addActionListener(this::menuRelatorioActionPerformed);
        menuAtendimento.add(menuRelatorio);
        barraMenu.add(menuAtendimento);
        menuSistema.setText("Sistema");
        menuSair.setText("Sair da conta");
        menuSair.addActionListener(this::menuSairActionPerformed);
        menuSistema.add(menuSair);
        barraMenu.add(menuSistema);
        setJMenuBar(barraMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo, -2, -2, -2)
                    .addComponent(lblDescricao, -2, -2, -2)
                    .addComponent(separador, 0, -1, 32767)
                    .addComponent(lblSecao, -2, -2, -2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClientes, 0, 300, 32767)
                            .addComponent(lblClientes, 0, 300, 32767)
                            .addComponent(btnOrdens, 0, 300, 32767)
                            .addComponent(lblOrdens, 0, 300, 32767))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCategorias, 0, 300, 32767)
                            .addComponent(lblCategorias, 0, 300, 32767)
                            .addComponent(btnRelatorio, 0, 300, 32767)
                            .addComponent(lblRelatorio, 0, 300, 32767))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEquipamentos, 0, 300, 32767)
                            .addComponent(lblEquipamentos, 0, 300, 32767)
                            .addComponent(btnTecnicos, 0, 300, 32767)
                            .addComponent(lblTecnicos, 0, 300, 32767)))
                    .addComponent(lblRodape, 0, -1, 32767))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblTitulo, -2, 42, 42)
                .addGap(6, 6, 6)
                .addComponent(lblDescricao, -2, 22, 22)
                .addGap(22, 22, 22)
                .addComponent(separador, -2, 2, 2)
                .addGap(22, 22, 22)
                .addComponent(lblSecao, -2, 20, 20)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClientes, -2, 70, 70)
                    .addComponent(btnCategorias, -2, 70, 70)
                    .addComponent(btnEquipamentos, -2, 70, 70))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClientes, -2, 24, 24)
                    .addComponent(lblCategorias, -2, 24, 24)
                    .addComponent(lblEquipamentos, -2, 24, 24))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOrdens, -2, 70, 70)
                    .addComponent(btnRelatorio, -2, 70, 70)
                    .addComponent(btnTecnicos, -2, 70, 70))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblOrdens, -2, 24, 24)
                    .addComponent(lblRelatorio, -2, 24, 24)
                    .addComponent(lblTecnicos, -2, 24, 24))
                .addGap(30, 30, 32767)
                .addComponent(lblRodape, -2, 20, 20)
                .addGap(26, 26, 26))
        );
        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) { TelaLogin.main(args); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JSeparator separador;
    private javax.swing.JLabel lblSecao;
    private javax.swing.JButton btnClientes;
    private javax.swing.JLabel lblClientes;
    private javax.swing.JButton btnCategorias;
    private javax.swing.JLabel lblCategorias;
    private javax.swing.JButton btnEquipamentos;
    private javax.swing.JLabel lblEquipamentos;
    private javax.swing.JButton btnOrdens;
    private javax.swing.JLabel lblOrdens;
    private javax.swing.JButton btnRelatorio;
    private javax.swing.JLabel lblRelatorio;
    private javax.swing.JButton btnTecnicos;
    private javax.swing.JLabel lblTecnicos;
    private javax.swing.JLabel lblRodape;
    private javax.swing.JMenuBar barraMenu;
    private javax.swing.JMenu menuCadastros;
    private javax.swing.JMenuItem menuClientes;
    private javax.swing.JMenuItem menuCategorias;
    private javax.swing.JMenuItem menuEquipamentos;
    private javax.swing.JMenuItem menuTecnicos;
    private javax.swing.JMenu menuAtendimento;
    private javax.swing.JMenuItem menuOrdens;
    private javax.swing.JMenuItem menuRelatorio;
    private javax.swing.JMenu menuSistema;
    private javax.swing.JMenuItem menuSair;
    // End of variables declaration//GEN-END:variables
}

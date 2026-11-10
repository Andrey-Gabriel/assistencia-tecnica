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
        btnTecnicos.setVisible(admin); menuTecnicos.setVisible(admin);
        lblRodape.setText("Conectado: " + Sessao.nomeTecnico() + (admin ? " | Administrador" : " | Técnico"));
        EstiloVisual.aplicar(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { fecharComConfirmacao(); }
        });
    }
    private void abrir(String chave, java.util.function.Supplier<? extends javax.swing.JFrame> fabrica) {
        try {
            Sessao.exigirAutenticacao();
        } catch (SecurityException e) {
            Formularios.erro(this, e);
            return;
        }
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


    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        menuSairActionPerformed(evt);
    }//GEN-LAST:event_btnSairActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        separador = new javax.swing.JSeparator();
        separador.setBackground(new java.awt.Color(246, 247, 249));
        lblAtendimento = new javax.swing.JLabel();
        btnOrdens = new javax.swing.JButton();
        btnRelatorio = new javax.swing.JButton();
        separadorCadastros = new javax.swing.JSeparator();
        separadorCadastros.setBackground(new java.awt.Color(246, 247, 249));
        lblCadastros = new javax.swing.JLabel();
        btnClientes = new javax.swing.JButton();
        btnCategorias = new javax.swing.JButton();
        btnEquipamentos = new javax.swing.JButton();
        btnTecnicos = new javax.swing.JButton();
        separadorRodape = new javax.swing.JSeparator();
        separadorRodape.setBackground(new java.awt.Color(246, 247, 249));
        lblRodape = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();
        barraMenu = new javax.swing.JMenuBar();
        barraMenu.setBackground(new java.awt.Color(255, 255, 255));
        barraMenu.setForeground(new java.awt.Color(40, 47, 56));
        barraMenu.setFont(new java.awt.Font("Segoe UI", 0, 13));
        menuCadastros = new javax.swing.JMenu();
        menuClientes = new javax.swing.JMenuItem();
        menuCategorias = new javax.swing.JMenuItem();
        menuEquipamentos = new javax.swing.JMenuItem();
        menuTecnicos = new javax.swing.JMenuItem();
        menuAtendimento = new javax.swing.JMenu();
        menuAtendimento.setBackground(new java.awt.Color(255, 255, 255));
        menuAtendimento.setForeground(new java.awt.Color(40, 47, 56));
        menuAtendimento.setFont(new java.awt.Font("Segoe UI", 0, 13));
        menuOrdens = new javax.swing.JMenuItem();
        menuRelatorio = new javax.swing.JMenuItem();
        menuSistema = new javax.swing.JMenu();
        menuSistema.setBackground(new java.awt.Color(255, 255, 255));
        menuSistema.setForeground(new java.awt.Color(40, 47, 56));
        menuSistema.setFont(new java.awt.Font("Segoe UI", 0, 13));
        menuSair = new javax.swing.JMenuItem();
        setDefaultCloseOperation(0);
        setTitle("Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(800, 460));
        setPreferredSize(new java.awt.Dimension(840, 480));
        lblTitulo.setText("Assistência Técnica");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitulo.setForeground(new java.awt.Color(40, 47, 56));
        lblDescricao.setText("Menu principal");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblDescricao.setForeground(new java.awt.Color(102, 111, 123));
        separador.setForeground(new java.awt.Color(201, 207, 216));
        lblAtendimento.setText("Atendimento");
        lblAtendimento.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblAtendimento.setForeground(new java.awt.Color(40, 47, 56));
        btnOrdens.setText("Ordens de serviço");
        btnOrdens.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOrdens.setBackground(new java.awt.Color(43, 76, 110));
        btnOrdens.setForeground(new java.awt.Color(255, 255, 255));
        btnOrdens.setContentAreaFilled(false);
        btnOrdens.setOpaque(true);
        btnOrdens.addActionListener(this::btnOrdensActionPerformed);
        btnRelatorio.setText("Relatório por período");
        btnRelatorio.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnRelatorio.setBackground(new java.awt.Color(255, 255, 255));
        btnRelatorio.setForeground(new java.awt.Color(40, 47, 56));
        btnRelatorio.setContentAreaFilled(false);
        btnRelatorio.setOpaque(true);
        btnRelatorio.addActionListener(this::btnRelatorioActionPerformed);
        separadorCadastros.setForeground(new java.awt.Color(201, 207, 216));
        lblCadastros.setText("Cadastros");
        lblCadastros.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblCadastros.setForeground(new java.awt.Color(40, 47, 56));
        btnClientes.setText("Clientes");
        btnClientes.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnClientes.setBackground(new java.awt.Color(255, 255, 255));
        btnClientes.setForeground(new java.awt.Color(40, 47, 56));
        btnClientes.setContentAreaFilled(false);
        btnClientes.setOpaque(true);
        btnClientes.addActionListener(this::btnClientesActionPerformed);
        btnCategorias.setText("Categorias");
        btnCategorias.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnCategorias.setBackground(new java.awt.Color(255, 255, 255));
        btnCategorias.setForeground(new java.awt.Color(40, 47, 56));
        btnCategorias.setContentAreaFilled(false);
        btnCategorias.setOpaque(true);
        btnCategorias.addActionListener(this::btnCategoriasActionPerformed);
        btnEquipamentos.setText("Equipamentos");
        btnEquipamentos.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnEquipamentos.setBackground(new java.awt.Color(255, 255, 255));
        btnEquipamentos.setForeground(new java.awt.Color(40, 47, 56));
        btnEquipamentos.setContentAreaFilled(false);
        btnEquipamentos.setOpaque(true);
        btnEquipamentos.addActionListener(this::btnEquipamentosActionPerformed);
        btnTecnicos.setText("Técnicos");
        btnTecnicos.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnTecnicos.setBackground(new java.awt.Color(255, 255, 255));
        btnTecnicos.setForeground(new java.awt.Color(40, 47, 56));
        btnTecnicos.setContentAreaFilled(false);
        btnTecnicos.setOpaque(true);
        btnTecnicos.addActionListener(this::btnTecnicosActionPerformed);
        separadorRodape.setForeground(new java.awt.Color(201, 207, 216));
        lblRodape.setText("Conectado");
        lblRodape.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblRodape.setForeground(new java.awt.Color(102, 111, 123));
        btnSair.setText("Sair da conta");
        btnSair.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnSair.setBackground(new java.awt.Color(255, 255, 255));
        btnSair.setForeground(new java.awt.Color(40, 47, 56));
        btnSair.setContentAreaFilled(false);
        btnSair.setOpaque(true);
        btnSair.addActionListener(this::btnSairActionPerformed);
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
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo, -2, -2, -2)
                    .addComponent(lblDescricao, -2, -2, -2)
                    .addComponent(separador, 0, -1, 32767)
                    .addComponent(lblAtendimento, -2, -2, -2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOrdens, 0, 360, 32767)
                        .addGap(12, 12, 12)
                        .addComponent(btnRelatorio, 0, 360, 32767))
                    .addComponent(separadorCadastros, 0, -1, 32767)
                    .addComponent(lblCadastros, -2, -2, -2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClientes, 0, 360, 32767)
                            .addComponent(btnEquipamentos, 0, 360, 32767))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCategorias, 0, 360, 32767)
                            .addComponent(btnTecnicos, 0, 360, 32767)))
                    .addComponent(separadorRodape, 0, -1, 32767)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblRodape, 0, -1, 32767)
                        .addGap(16, 16, 16)
                        .addComponent(btnSair, -2, 120, 120)))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitulo, -2, 32, 32)
                .addGap(4, 4, 4)
                .addComponent(lblDescricao, -2, 18, 18)
                .addGap(12, 12, 12)
                .addComponent(separador, -2, 1, 1)
                .addGap(14, 14, 14)
                .addComponent(lblAtendimento, -2, 20, 20)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOrdens, -2, 36, 36)
                    .addComponent(btnRelatorio, -2, 36, 36))
                .addGap(18, 18, 18)
                .addComponent(separadorCadastros, -2, 1, 1)
                .addGap(14, 14, 14)
                .addComponent(lblCadastros, -2, 20, 20)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClientes, -2, 36, 36)
                    .addComponent(btnCategorias, -2, 36, 36))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEquipamentos, -2, 36, 36)
                    .addComponent(btnTecnicos, -2, 36, 36))
                .addGap(20, 20, 32767)
                .addComponent(separadorRodape, -2, 1, 1)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRodape, -2, 32, 32)
                    .addComponent(btnSair, -2, 32, 32))
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
    private javax.swing.JLabel lblAtendimento;
    private javax.swing.JButton btnOrdens;
    private javax.swing.JButton btnRelatorio;
    private javax.swing.JSeparator separadorCadastros;
    private javax.swing.JLabel lblCadastros;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnCategorias;
    private javax.swing.JButton btnEquipamentos;
    private javax.swing.JButton btnTecnicos;
    private javax.swing.JSeparator separadorRodape;
    private javax.swing.JLabel lblRodape;
    private javax.swing.JButton btnSair;
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

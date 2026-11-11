package br.com.assistencia.view;

import br.com.assistencia.controller.AutenticacaoController;
import br.com.assistencia.controller.Sessao;
import br.com.assistencia.setup.PrimeiroAdministrador;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

public class TelaLogin extends javax.swing.JFrame {

    private final AutenticacaoController controller = new AutenticacaoController();
    private boolean ocupado;

    public TelaLogin() {
        initComponents();
        EstiloVisual.aplicar(this);
        getRootPane().setDefaultButton(btnEntrar);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                if (!ocupado) {
                    Sessao.encerrar();
                    dispose();
                }
            }
        });
    }

    private void definirOcupado(boolean valor) {
        ocupado = valor;
        txtEmail.setEnabled(!valor);
        txtSenha.setEnabled(!valor);
        btnEntrar.setEnabled(!valor);
        btnPrimeiroAcesso.setEnabled(!valor);
    }

    private void btnPrimeiroAcessoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimeiroAcessoActionPerformed
        if (ocupado) return;
        definirOcupado(true);
        lblStatus.setText("Verificando primeiro acesso...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                return controller.primeiroAcessoDisponivel();
            }

            @Override protected void done() {
                try {
                    if (get()) {
                        lblStatus.setText(" ");
                        PrimeiroAdministrador.abrir(TelaLogin.this);
                    } else {
                        lblStatus.setText("O primeiro acesso já foi configurado.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    lblStatus.setText("Verificação interrompida. Tente novamente.");
                } catch (ExecutionException e) {
                    lblStatus.setText("Não foi possível verificar o primeiro acesso.");
                    Formularios.erro(TelaLogin.this, e.getCause());
                } finally {
                    definirOcupado(false);
                }
            }
        }.execute();
    }//GEN-LAST:event_btnPrimeiroAcessoActionPerformed

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrarActionPerformed
        if (ocupado) return;
        String email = txtEmail.getText();
        char[] senha = txtSenha.getPassword();
        txtSenha.setText("");
        definirOcupado(true);
        lblStatus.setText("Verificando acesso...");
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                try {
                    controller.entrar(email, senha);
                    return null;
                } finally {
                    Arrays.fill(senha, '\0');
                }
            }

            @Override protected void done() {
                try {
                    get();
                    new TelaPrincipal().setVisible(true);
                    dispose();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Sessao.encerrar();
                    lblStatus.setText("Acesso interrompido. Tente novamente.");
                    definirOcupado(false);
                } catch (ExecutionException e) {
                    Sessao.encerrar();
                    Throwable causa = e.getCause();
                    String mensagem = causa instanceof IllegalArgumentException
                            ? "E-mail ou senha incorretos."
                            : "Não foi possível acessar o sistema. Verifique o MySQL ou contate o administrador.";
                    lblStatus.setText("<html>" + mensagem + "</html>");
                    if (!(causa instanceof IllegalArgumentException)) {
                        java.util.logging.Logger.getLogger(TelaLogin.class.getName())
                                .log(java.util.logging.Level.WARNING, "Falha de acesso", causa);
                    }
                    javax.swing.Timer espera = new javax.swing.Timer(1500, evento -> {
                        definirOcupado(false);
                        txtSenha.requestFocusInWindow();
                    });
                    espera.setRepeats(false);
                    espera.start();
                }
            }
        }.execute();
    }//GEN-LAST:event_btnEntrarActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 0, 12));
        separador = new javax.swing.JSeparator();
        separador.setBackground(new java.awt.Color(246, 247, 249));
        separador.setForeground(new java.awt.Color(201, 207, 216));
        lblEmail = new javax.swing.JLabel();
        lblEmail.setForeground(new java.awt.Color(40, 47, 56));
        lblEmail.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtEmail = new javax.swing.JTextField();
        txtEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtEmail.setForeground(new java.awt.Color(40, 47, 56));
        lblSenha = new javax.swing.JLabel();
        lblSenha.setForeground(new java.awt.Color(40, 47, 56));
        lblSenha.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtSenha = new javax.swing.JPasswordField();
        txtSenha.setBackground(new java.awt.Color(255, 255, 255));
        txtSenha.setForeground(new java.awt.Color(40, 47, 56));
        btnEntrar = new javax.swing.JButton();
        btnEntrar.setOpaque(true);
        btnEntrar.setContentAreaFilled(false);
        lblStatus = new javax.swing.JLabel();
        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblRodape = new javax.swing.JLabel();
        lblRodape.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnPrimeiroAcesso = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Entrar | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(500, 490));
        setPreferredSize(new java.awt.Dimension(520, 510));

        lblTitulo.setText("Assistência Técnica");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(40, 47, 56));

        lblDescricao.setText("Entre com o e-mail e a senha do seu cadastro.");
        lblDescricao.setForeground(new java.awt.Color(102, 111, 123));

        lblEmail.setText("E-mail");

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblSenha.setText("Senha");

        txtSenha.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        btnEntrar.setText("Entrar");
        btnEntrar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnEntrar.setBackground(new java.awt.Color(43, 76, 110));
        btnEntrar.setForeground(new java.awt.Color(255, 255, 255));
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrarActionPerformed(evt);
            }
        });

        lblStatus.setText(" ");
        lblStatus.setForeground(new java.awt.Color(167, 49, 49));

        lblRodape.setText("Acesso exclusivo para técnicos cadastrados.");
        lblRodape.setForeground(new java.awt.Color(102, 111, 123));

        btnPrimeiroAcesso.setText("Primeiro Acesso");
        btnPrimeiroAcesso.addActionListener(this::btnPrimeiroAcessoActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addComponent(lblDescricao)
                    .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSenha)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblRodape)
                        .addGap(12, 12, Short.MAX_VALUE)
                        .addComponent(btnPrimeiroAcesso)))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblTitulo, 32, 32, 32)
                .addGap(8, 8, 8)
                .addComponent(lblDescricao, 22, 22, 22)
                .addGap(22, 22, 22)
                .addComponent(separador, 2, 2, 2)
                .addGap(22, 22, 22)
                .addComponent(lblEmail, 20, 20, 20)
                .addGap(6, 6, 6)
                .addComponent(txtEmail, 34, 34, 34)
                .addGap(16, 16, 16)
                .addComponent(lblSenha, 20, 20, 20)
                .addGap(6, 6, 6)
                .addComponent(txtSenha, 34, 34, 34)
                .addGap(24, 24, 24)
                .addComponent(btnEntrar, 36, 36, 36)
                .addGap(8, 8, 8)
                .addComponent(lblStatus, 48, 48, 48)
                .addGap(8, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblRodape, 20, 20, 20)
                    .addComponent(btnPrimeiroAcesso, 30, 30, 30))
                .addGap(24, 24, 24))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        EstiloVisual.iniciar();
        java.awt.EventQueue.invokeLater(() -> new TelaLogin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntrar;
    private javax.swing.JButton btnPrimeiroAcesso;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblRodape;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JSeparator separador;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtSenha;
    // End of variables declaration//GEN-END:variables
}

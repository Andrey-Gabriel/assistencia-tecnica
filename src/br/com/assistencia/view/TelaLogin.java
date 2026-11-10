package br.com.assistencia.view;

import br.com.assistencia.controller.AutenticacaoController;
import br.com.assistencia.controller.Sessao;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

public class TelaLogin extends javax.swing.JFrame {

    private final AutenticacaoController controller = new AutenticacaoController();
    private boolean ocupado;

    public TelaLogin() {
        initComponents();
        getContentPane().setBackground(java.awt.Color.WHITE);
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
    }

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
        separador = new javax.swing.JSeparator();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblSenha = new javax.swing.JLabel();
        txtSenha = new javax.swing.JPasswordField();
        btnEntrar = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        lblRodape = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Entrar | Assistência Técnica");
        setMinimumSize(new java.awt.Dimension(500, 530));
        setPreferredSize(new java.awt.Dimension(520, 550));

        lblTitulo.setText("Assistência Técnica");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 27)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(25, 42, 64));

        lblDescricao.setText("Entre com o e-mail e a senha do seu cadastro.");
        lblDescricao.setForeground(new java.awt.Color(91, 105, 124));

        lblEmail.setText("E-mail");

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblSenha.setText("Senha");

        txtSenha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnEntrar.setText("Entrar");
        btnEntrar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEntrar.setBackground(new java.awt.Color(34, 76, 125));
        btnEntrar.setForeground(new java.awt.Color(255, 255, 255));
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrarActionPerformed(evt);
            }
        });

        lblStatus.setText(" ");
        lblStatus.setForeground(new java.awt.Color(164, 40, 48));

        lblRodape.setText("Acesso exclusivo para técnicos cadastrados.");
        lblRodape.setForeground(new java.awt.Color(91, 105, 124));

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
                    .addComponent(lblRodape))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblTitulo, 38, 38, 38)
                .addGap(8, 8, 8)
                .addComponent(lblDescricao, 22, 22, 22)
                .addGap(22, 22, 22)
                .addComponent(separador, 2, 2, 2)
                .addGap(22, 22, 22)
                .addComponent(lblEmail, 20, 20, 20)
                .addGap(6, 6, 6)
                .addComponent(txtEmail, 38, 38, 38)
                .addGap(16, 16, 16)
                .addComponent(lblSenha, 20, 20, 20)
                .addGap(6, 6, 6)
                .addComponent(txtSenha, 38, 38, 38)
                .addGap(24, 24, 24)
                .addComponent(btnEntrar, 42, 42, 42)
                .addGap(8, 8, 8)
                .addComponent(lblStatus, 48, 48, 48)
                .addGap(8, 16, Short.MAX_VALUE)
                .addComponent(lblRodape, 20, 20, 20)
                .addGap(24, 24, 24))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException e) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName())
                    .log(java.util.logging.Level.WARNING, "Aparência padrão do sistema", e);
        }
        java.awt.EventQueue.invokeLater(() -> new TelaLogin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntrar;
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

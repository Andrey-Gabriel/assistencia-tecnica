package br.com.assistencia.visao;

import br.com.assistencia.entidade.ManterTecnico;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

/** Cadastro inicial do gerente, acessível pela tela de login. */
public final class PrimeiroAdministrador {

    public static void main(String[] args) {
        EstiloVisual.iniciar();
        SwingUtilities.invokeLater(() -> abrir(null));
    }

    public static void abrir(JFrame login) {
        JDialog janela = new JDialog(login, "Cadastro do gerente", true);
        janela.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel painel = new JPanel(new java.awt.GridLayout(0, 2, 12, 12));
        painel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JTextField nome = new JTextField(24);
        JTextField telefone = new JTextField();
        JTextField email = new JTextField();
        JPasswordField senha = new JPasswordField();
        JPasswordField confirmar = new JPasswordField();
        painel.add(new JLabel("Nome")); painel.add(nome);
        painel.add(new JLabel("Telefone")); painel.add(telefone);
        painel.add(new JLabel("E-mail para entrar")); painel.add(email);
        painel.add(new JLabel("Senha (8 a 128 caracteres)")); painel.add(senha);
        painel.add(new JLabel("Confirmar senha")); painel.add(confirmar);
        JLabel status = new JLabel("Configuração única neste banco.");
        JButton criar = new JButton("Cadastrar gerente");
        painel.add(status); painel.add(criar);
        criar.addActionListener(e -> {
            char[] textoSenha = senha.getPassword();
            char[] textoConfirmar = confirmar.getPassword();
            String nomeGerente = nome.getText();
            String telefoneGerente = telefone.getText();
            String emailGerente = email.getText();
            criar.setEnabled(false);
            janela.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            status.setText("Criando acesso...");
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    try {
                        new ManterTecnico().criarPrimeiroAdministrador(
                                nomeGerente, telefoneGerente, emailGerente, textoSenha, textoConfirmar);
                        return null;
                    } finally {
                        Arrays.fill(textoSenha, '\0'); Arrays.fill(textoConfirmar, '\0');
                    }
                }
                @Override protected void done() {
                    senha.setText(""); confirmar.setText("");
                    janela.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    try {
                        get();
                        JOptionPane.showMessageDialog(janela, "Gerente cadastrado. "
                                + "Use o e-mail e a senha informados na tela de login.");
                        janela.dispose();
                    } catch (InterruptedException erro) {
                        Thread.currentThread().interrupt();
                        status.setText("Cadastro interrompido.");
                    } catch (ExecutionException erro) {
                        Throwable causa = erro.getCause();
                        String mensagem = causa instanceof SQLException sql && sql.getErrorCode() == 1062
                                ? "Este e-mail já pertence a um técnico. Informe outro e-mail."
                                : causa.getMessage();
                        JOptionPane.showMessageDialog(janela, mensagem, "Configuração inicial", JOptionPane.ERROR_MESSAGE);
                        status.setText("Confira os dados e tente novamente.");
                    } finally {
                        criar.setEnabled(true);
                    }
                }
            }.execute();
        });
        janela.setContentPane(painel);
        criar.setForeground(java.awt.Color.WHITE);
        EstiloVisual.aplicar(janela);
        janela.getRootPane().setDefaultButton(criar);
        janela.pack();
        janela.setLocationRelativeTo(login);
        janela.setVisible(true);
    }
}

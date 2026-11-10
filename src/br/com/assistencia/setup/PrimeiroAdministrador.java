package br.com.assistencia.setup;

import br.com.assistencia.controller.TecnicoController;
import br.com.assistencia.dao.TecnicoDAO;
import br.com.assistencia.model.Tecnico;
import br.com.assistencia.seguranca.Senha;
import java.util.Arrays;
import javax.swing.*;

/** Utilitário local de instalação. Não permite criar outro administrador após a configuração. */
public final class PrimeiroAdministrador {

    public static void main(String[] args) {
        try {
            br.com.assistencia.view.EstiloVisual.iniciar();
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(PrimeiroAdministrador::abrir);
    }

    private static void abrir() {
        JFrame janela = new JFrame("Configurar primeiro administrador");
        janela.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel painel = new JPanel(new java.awt.GridLayout(0, 2, 12, 12));
        painel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JTextField nome = new JTextField(24);
        JTextField telefone = new JTextField();
        JTextField email = new JTextField();
        JPasswordField senha = new JPasswordField();
        JPasswordField confirmar = new JPasswordField();
        painel.add(new JLabel("Nome:")); painel.add(nome);
        painel.add(new JLabel("Telefone:")); painel.add(telefone);
        painel.add(new JLabel("E-mail para entrar:")); painel.add(email);
        painel.add(new JLabel("Senha (8 a 128 caracteres):")); painel.add(senha);
        painel.add(new JLabel("Confirmar senha:")); painel.add(confirmar);
        JLabel status = new JLabel("Configuração única neste banco.");
        JButton criar = new JButton("Criar administrador");
        painel.add(status); painel.add(criar);
        criar.addActionListener(e -> {
            char[] textoSenha = senha.getPassword();
            char[] textoConfirmar = confirmar.getPassword();
            Tecnico tecnico;
            try {
                tecnico = TecnicoController.validar(0, nome.getText(), telefone.getText(), email.getText(), true);
                TecnicoController.conferirConfirmacao(textoSenha, textoConfirmar);
            } catch (IllegalArgumentException erro) {
                Arrays.fill(textoSenha, '\0'); Arrays.fill(textoConfirmar, '\0');
                JOptionPane.showMessageDialog(janela, erro.getMessage());
                return;
            }
            criar.setEnabled(false);
            janela.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            status.setText("Criando acesso...");
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    try {
                        new TecnicoDAO().criarPrimeiroAdministrador(tecnico, Senha.gerarHash(textoSenha));
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
                        JOptionPane.showMessageDialog(janela, "Administrador criado. "
                                + "Use o e-mail e a senha informados na tela de login.");
                        janela.dispose();
                    } catch (Exception erro) {
                        Throwable causa = erro.getCause() == null ? erro : erro.getCause();
                        String mensagem = causa instanceof java.sql.SQLException sql && sql.getErrorCode() == 1062
                                ? "Este e-mail já pertence a um técnico. Informe outro e-mail."
                                : causa.getMessage();
                        JOptionPane.showMessageDialog(janela, mensagem, "Configuração inicial", JOptionPane.ERROR_MESSAGE);
                        criar.setEnabled(true); status.setText("Confira os dados e tente novamente.");
                    }
                }
            }.execute();
        });
        janela.setContentPane(painel);
        criar.setForeground(java.awt.Color.WHITE);
        criar.setBackground(br.com.assistencia.view.EstiloVisual.AZUL);
        br.com.assistencia.view.EstiloVisual.aplicar(janela);
        janela.getRootPane().setDefaultButton(criar);
        janela.pack(); janela.setLocationRelativeTo(null); janela.setVisible(true);
    }
}

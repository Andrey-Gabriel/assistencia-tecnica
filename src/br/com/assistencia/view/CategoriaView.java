
package br.com.assistencia.view;

import br.com.assistencia.controller.CategoriaController;
import br.com.assistencia.model.Categoria;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CategoriaView extends JFrame {

    private final CategoriaController controller =
            new CategoriaController();

    private final JTextField txtNome = new JTextField(25);

    private final JTable tabela = new JTable();

    private final DefaultTableModel modelo =
            new DefaultTableModel(
                new Object[]{"ID", "Nome da Categoria"}, 0
            ) {
                @Override
                public boolean isCellEditable(int linha, int coluna) {
                    return false;
                }
            };

    private int idSelecionado = -1;

    public CategoriaView() {

        setTitle("Assistência Técnica - Categorias");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // Campos de cadastro
        JPanel painelSuperior = new JPanel(new GridLayout(2, 1));

        painelSuperior.add(new JLabel("Nome da Categoria:"));
        painelSuperior.add(txtNome);

        add(painelSuperior, BorderLayout.NORTH);

        // Tabela
        tabela.setModel(modelo);
        tabela.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar Lista");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);

        add(painelBotoes, BorderLayout.SOUTH);

        // Eventos dos botões
        btnNovo.addActionListener(e -> limparCampos());

        btnSalvar.addActionListener(e -> salvar());

        btnEditar.addActionListener(e -> editar());

        btnExcluir.addActionListener(e -> excluir());

        btnAtualizar.addActionListener(e -> carregarTabela());

        carregarTabela();
    }

    private void carregarTabela() {

        modelo.setRowCount(0);

        try {
            List<Categoria> categorias = controller.listar();

            for (Categoria c : categorias) {
                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNome()
                });
            }

        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void salvar() {

        try {
            String nome = txtNome.getText();

            if (idSelecionado == -1) {
                controller.cadastrar(nome);

                JOptionPane.showMessageDialog(
                    this, "Categoria cadastrada com sucesso!"
                );

            } else {
                controller.atualizar(idSelecionado, nome);

                JOptionPane.showMessageDialog(
                    this, "Categoria atualizada com sucesso!"
                );
            }

            limparCampos();
            carregarTabela();

        } catch (SQLException | IllegalArgumentException e) {
            mostrarErro(e);
        }
    }

    private void editar() {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(
                this, "Selecione uma categoria na tabela."
            );
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linha);

        idSelecionado = (int) modelo.getValueAt(linhaModelo, 0);

        txtNome.setText(
            modelo.getValueAt(linhaModelo, 1).toString()
        );

        txtNome.requestFocusInWindow();
    }

    private void excluir() {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(
                this, "Selecione uma categoria para excluir."
            );
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linha);

        int id = (int) modelo.getValueAt(linhaModelo, 0);

        int resposta = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir esta categoria?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.excluir(id);

            JOptionPane.showMessageDialog(
                this, "Categoria excluída com sucesso!"
            );

            limparCampos();
            carregarTabela();

        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void limparCampos() {

        idSelecionado = -1;
        txtNome.setText("");
        tabela.clearSelection();
        txtNome.requestFocusInWindow();
    }

    private void mostrarErro(Exception e) {

        String mensagem = e.getMessage();

        if (e instanceof SQLException) {
            SQLException erro = (SQLException) e;

            if (erro.getErrorCode() == 1062) {
                mensagem = "Já existe uma categoria com esse nome.";
            } else if (erro.getErrorCode() == 1451) {
                mensagem =
                    "Não é possível excluir: existem equipamentos "
                    + "vinculados a esta categoria.";
            }
        }

        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Aviso",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new CategoriaView().setVisible(true);
        });
    }
}

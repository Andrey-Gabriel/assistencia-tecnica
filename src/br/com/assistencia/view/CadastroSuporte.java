package br.com.assistencia.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

/** Comportamento compartilhado; os componentes e layouts permanecem nos arquivos .form. */
public abstract class CadastroSuporte<T> {
    private final JFrame tela;
    private final JTable tabela;
    private final JLabel modo;
    private final JLabel status;
    private final String modoNovo;
    private final JButton novo, salvar, editar, excluir, atualizar, fechar;
    private final List<JComponent> campos;
    private List<T> registros = List.of();
    private List<String> estadoInicial = List.of();
    private int idEdicao;
    private boolean ocupado;

    protected CadastroSuporte(JFrame tela, JTable tabela, JLabel modo, JLabel status,
            JButton novo, JButton salvar, JButton editar, JButton excluir, JButton atualizar, JButton fechar,
            JComponent... campos) {
        this.tela = tela; this.tabela = tabela; this.modo = modo; this.status = status;
        this.modoNovo = modo.getText();
        this.novo = novo; this.salvar = salvar; this.editar = editar; this.excluir = excluir;
        this.atualizar = atualizar; this.fechar = fechar; this.campos = List.of(campos);
        tabela.getSelectionModel().addListSelectionListener(e -> botoes());
        tela.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { fechar(); }
        });
    }
    protected abstract List<T> listarBanco() throws Exception;
    protected abstract T lerFormulario(int id);
    protected abstract void salvarBanco(T registro) throws Exception;
    protected abstract void excluirBanco(int id) throws Exception;
    protected abstract void preencherFormulario(T registro);
    protected abstract void limparFormulario();
    protected abstract int id(T registro);
    protected abstract Object[] linha(T registro);
    protected abstract String descricao(T registro);
    protected Object carregarOpcoes() throws Exception { return null; }
    protected void aplicarOpcoes(Object opcoes) { }

    public void iniciar() { limpar(); atualizar(""); }
    public boolean ocupado() { return ocupado; }
    private List<String> estado() {
        List<String> estado = new ArrayList<>();
        for (JComponent campo : campos) {
            if (campo instanceof JTextComponent texto) estado.add(texto.getText());
            else if (campo instanceof JComboBox<?> combo) {
                Object opcao = combo.getSelectedItem();
                estado.add(opcao instanceof Opcao o ? Integer.toString(o.id()) : String.valueOf(opcao));
            }
        }
        return estado;
    }
    private boolean descartar() {
        return estado().equals(estadoInicial) || JOptionPane.showConfirmDialog(tela,
                "Há alterações não salvas. Deseja descartá-las?", "Alterações não salvas",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
    private void limpar() {
        idEdicao = 0; limparFormulario(); tabela.clearSelection();
        modo.setText(modoNovo); salvar.setText("Salvar"); estadoInicial = estado();
        if (!campos.isEmpty()) campos.get(0).requestFocusInWindow();
    }
    public void novo() {
        if (!ocupado && descartar()) { limpar(); status.setText("Preencha os campos e clique em Salvar."); }
    }
    public void editar() {
        if (ocupado || tabela.getSelectedRow() < 0 || !descartar()) return;
        T registro = registros.get(tabela.convertRowIndexToModel(tabela.getSelectedRow()));
        idEdicao = id(registro); preencherFormulario(registro); estadoInicial = estado();
        modo.setText("Editando #" + idEdicao); salvar.setText("Salvar alterações");
        status.setText("Altere os dados e clique em Salvar alterações.");
    }
    public void salvar() {
        if (ocupado) return;
        T dados;
        try { dados = lerFormulario(idEdicao); }
        catch (IllegalArgumentException e) { Formularios.erro(tela, e); return; }
        executar(() -> { salvarBanco(dados); return null; }, nada -> {
            limpar(); atualizar("Cadastro salvo.");
        }, "Salvando...");
    }
    public void excluir() {
        if (ocupado || tabela.getSelectedRow() < 0 || !descartar()) return;
        T dados = registros.get(tabela.convertRowIndexToModel(tabela.getSelectedRow()));
        if (JOptionPane.showConfirmDialog(tela, "Excluir " + descricao(dados) + "?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        executar(() -> { excluirBanco(id(dados)); return null; }, nada -> {
            limpar(); atualizar("Cadastro excluído.");
        }, "Excluindo...");
    }
    public boolean fechar() {
        if (ocupado || !descartar()) return false;
        tela.dispose(); return true;
    }
    public void atualizar() { atualizar("Lista atualizada."); }
    private record Carga<R>(List<R> registros, Object opcoes) { }
    private void atualizar(String mensagem) {
        executar(() -> new Carga<>(listarBanco(), carregarOpcoes()), carga -> {
            aplicarOpcoes(carga.opcoes()); registros = carga.registros();
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel(); modelo.setRowCount(0);
            for (T registro : registros) modelo.addRow(linha(registro));
            status.setText((mensagem.isEmpty() ? "" : mensagem + " ") + registros.size() + " registro(s).");
            botoes();
        }, "Carregando cadastros...");
    }
    private void botoes() {
        boolean selecionado = tabela.getSelectedRow() >= 0;
        editar.setEnabled(!ocupado && selecionado); excluir.setEnabled(!ocupado && selecionado);
    }
    private void ocupar(boolean valor) {
        ocupado = valor;
        for (JComponent campo : campos) campo.setEnabled(!valor);
        for (JButton botao : List.of(novo, salvar, atualizar, fechar)) botao.setEnabled(!valor);
        tabela.setEnabled(!valor); botoes();
        tela.setCursor(valor ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR) : java.awt.Cursor.getDefaultCursor());
    }
    @FunctionalInterface private interface Tarefa<R> { R executar() throws Exception; }
    private <R> void executar(Tarefa<R> tarefa, Consumer<R> sucesso, String mensagem) {
        if (ocupado) return;
        ocupar(true); status.setText(mensagem);
        new SwingWorker<R, Void>() {
            @Override protected R doInBackground() throws Exception { return tarefa.executar(); }
            @Override protected void done() {
                ocupar(false);
                try { R resultado = get(); if (tela.isDisplayable()) sucesso.accept(resultado); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Operação interrompida. Atualize a lista."); }
                catch (ExecutionException e) { status.setText("Operação não concluída. Confira o aviso."); Formularios.erro(tela, e.getCause()); }
            }
        }.execute();
    }
}

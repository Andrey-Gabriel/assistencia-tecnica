package br.com.assistencia.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class Formularios {
    private static boolean encerrandoSessao;
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private Formularios() { }
    public static String data(LocalDateTime valor) { return valor == null ? "" : DATA_HORA.format(valor); }
    public static String dia(LocalDate valor) { return DIA.format(valor); }
    public static LocalDate lerDia(String valor) {
        try { return LocalDate.parse(valor.trim(), DIA); }
        catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Informe uma data válida no formato dia/mês/ano, por exemplo 31/10/2026.");
        }
    }
    public static String moeda(BigDecimal valor) {
        return valor == null ? "" : NumberFormat.getCurrencyInstance(BRASIL).format(valor);
    }
    public static String equipamento(String categoria, String marca, String modelo) {
        String aparelho = java.util.stream.Stream.of(marca, modelo)
                .filter(s -> s != null && !s.isBlank()).collect(java.util.stream.Collectors.joining(" "));
        return categoria == null || categoria.isBlank() ? aparelho : categoria + " / " + aparelho;
    }
    public static String valorEditavel(BigDecimal valor) {
        return valor == null ? "" : valor.toPlainString().replace('.', ',');
    }
    public static BigDecimal lerValor(String texto) {
        texto = texto.trim();
        if (texto.isEmpty()) return null;
        if (texto.matches("\\d+(?:\\.\\d{1,2})?")) return new BigDecimal(texto);
        if (texto.matches("(?:\\d+|\\d{1,3}(?:\\.\\d{3})+)(?:,\\d{1,2})?")) {
            return new BigDecimal(texto.replace(".", "").replace(',', '.'));
        }
        throw new IllegalArgumentException("Informe o valor como 150,00 ou deixe em branco se ainda não houver orçamento.");
    }
    public static int id(JComboBox<Opcao> combo) {
        return combo.getSelectedItem() instanceof Opcao opcao ? opcao.id() : 0;
    }
    public static void selecionar(JComboBox<Opcao> combo, int id) {
        combo.setSelectedIndex(-1);
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id() == id) { combo.setSelectedIndex(i); return; }
        }
        if (combo.getItemCount() > 0) combo.setSelectedIndex(0);
    }
    public static void opcoes(JComboBox<Opcao> combo, List<Opcao> opcoes) {
        int anterior = id(combo);
        DefaultComboBoxModel<Opcao> modelo = new DefaultComboBoxModel<>();
        modelo.addElement(new Opcao(0, "Selecione..."));
        for (Opcao opcao : opcoes) modelo.addElement(opcao);
        combo.setModel(modelo);
        selecionar(combo, anterior);
    }
    public static void tabela(JTable tabela, Class<?>... tipos) {
        String[] titulos = new String[tabela.getColumnCount()];
        for (int i = 0; i < titulos.length; i++) titulos[i] = tabela.getColumnName(i);
        tabela.setModel(new DefaultTableModel(titulos, 0) {
            @Override public Class<?> getColumnClass(int coluna) { return tipos[coluna]; }
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        });
        tabela.setAutoCreateRowSorter(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setDefaultRenderer(LocalDateTime.class, new EstiloVisual.RenderizadorTabela() {
            @Override protected void setValue(Object valor) { super.setValue(data((LocalDateTime) valor)); }
        });
        tabela.setDefaultRenderer(BigDecimal.class, new EstiloVisual.RenderizadorTabela() {
            @Override protected void setValue(Object valor) { setHorizontalAlignment(RIGHT); super.setValue(moeda((BigDecimal) valor)); }
        });
        if (tipos.length > 0) tabela.getColumnModel().getColumn(0).setMaxWidth(70);
    }
    public static boolean tratarSessaoEncerrada(Throwable erro) {
        if (!(erro instanceof SecurityException)
                || br.com.assistencia.controller.Sessao.estaAutenticado()) return false;
        // Outros SwingWorkers podem terminar enquanto o aviso modal está aberto.
        if (encerrandoSessao) return true;
        encerrandoSessao = true;
        try {
            TelaLogin login = null;
            for (java.awt.Frame janela : java.awt.Frame.getFrames()) {
                if (janela instanceof TelaGerenciada) janela.dispose();
                else if (janela instanceof TelaLogin existente && existente.isDisplayable()) login = existente;
            }
            if (login == null) {
                login = new TelaLogin();
                login.setVisible(true);
                JOptionPane.showMessageDialog(login, erro.getMessage(), "Sessão encerrada", JOptionPane.WARNING_MESSAGE);
            }
            login.toFront();
        } finally {
            encerrandoSessao = false;
        }
        return true;
    }

    public static void erro(java.awt.Component pai, Throwable erro) {
        if (tratarSessaoEncerrada(erro)) return;
        java.util.logging.Logger.getLogger(Formularios.class.getName()).log(java.util.logging.Level.WARNING, "Falha na operação", erro);
        String mensagem = erro.getMessage();
        if (erro instanceof java.sql.SQLException sql) {
            if (sql.getErrorCode() == 1062) mensagem = "Já existe um cadastro com esses dados. Confira o nome informado.";
            else if (sql.getErrorCode() == 1451) mensagem = "Este cadastro possui registros vinculados e não pode ser excluído.";
            else if (sql.getErrorCode() == 1452) mensagem = "Um dos cadastros selecionados não existe mais. Atualize a lista e selecione novamente.";
            else if (sql.getSQLState() != null && sql.getSQLState().startsWith("08")) mensagem = "Não foi possível conectar ao banco. Verifique o MySQL e tente novamente.";
        } else if (erro instanceof LinkageError) {
            mensagem = "Feche a aplicação e execute Limpar e Construir no NetBeans antes de abrir novamente.";
        }
        JOptionPane.showMessageDialog(pai, mensagem == null ? "Não foi possível concluir a operação." : mensagem,
                "Confira os dados", JOptionPane.ERROR_MESSAGE);
    }
}

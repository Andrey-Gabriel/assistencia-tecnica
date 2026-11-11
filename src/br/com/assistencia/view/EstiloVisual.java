package br.com.assistencia.view;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;

/** Padrão visual compartilhado por todas as janelas da aplicação. */
public final class EstiloVisual {
    public static final Color FUNDO = new Color(246, 247, 249);
    public static final Color TEXTO = new Color(40, 47, 56);
    public static final Color SECUNDARIO = new Color(102, 111, 123);
    public static final Color BORDA = new Color(201, 207, 216);
    public static final Color AZUL = new Color(43, 76, 110);
    public static final Color VERMELHO = new Color(167, 49, 49);
    private static final Font FONTE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Border ESPACO_CELULA = BorderFactory.createEmptyBorder(0, 8, 0, 8);

    public static class RenderizadorTabela extends DefaultTableCellRenderer {
        public RenderizadorTabela() { }
        public RenderizadorTabela(int alinhamento) { setHorizontalAlignment(alinhamento); }
        @Override public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionado, boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
            setBorder(foco ? borda(AZUL, 1, 0, 7) : ESPACO_CELULA);
            return this;
        }
    }

    private EstiloVisual() { }

    public static void iniciar() {
        String padrao = UIManager.getCrossPlatformLookAndFeelClassName();
        if (!UIManager.getLookAndFeel().getClass().getName().equals(padrao)) {
            try { UIManager.setLookAndFeel(padrao); }
            catch (Exception e) { throw new IllegalStateException("Não foi possível carregar a aparência do sistema.", e); }
        }
        for (String chave : new String[]{"Label.font", "Button.font", "TextField.font", "PasswordField.font",
                "TextArea.font", "ComboBox.font", "Table.font", "TableHeader.font", "CheckBox.font",
                "Menu.font", "MenuItem.font", "ToolTip.font", "OptionPane.messageFont", "OptionPane.buttonFont"}) {
            UIManager.put(chave, FONTE);
        }
        UIManager.put("control", FUNDO);
        UIManager.put("Panel.background", FUNDO);
        UIManager.put("OptionPane.background", FUNDO);
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.foreground", TEXTO);
        UIManager.put("Button.gradient", null);
        UIManager.put("MenuBar.gradient", null);
        UIManager.put("Button.border", borda(BORDA, 1, 6, 14));
        UIManager.put("ComboBox.selectionBackground", new Color(224, 234, 244));
        UIManager.put("ComboBox.selectionForeground", TEXTO);
    }

    public static void aplicar(JFrame janela) {
        aplicarConteudo(janela, janela.getContentPane());
        JMenuBar barra = janela.getJMenuBar();
        if (barra != null) {
            barra.setBackground(Color.WHITE);
            barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA));
            for (int i = 0; i < barra.getMenuCount(); i++) menu(barra.getMenu(i));
        }
    }

    public static void aplicar(JDialog janela) {
        aplicarConteudo(janela, janela.getContentPane());
    }

    private static void aplicarConteudo(Window janela, Container conteudo) {
        String anterior = UIManager.getLookAndFeel().getClass().getName();
        iniciar();
        if (!anterior.equals(UIManager.getLookAndFeel().getClass().getName())) SwingUtilities.updateComponentTreeUI(janela);
        configurar(conteudo);
    }

    private static void menu(JMenu menu) {
        if (menu == null) return;
        menu.setFont(FONTE); menu.setForeground(TEXTO); menu.setBackground(Color.WHITE);
        for (Component item : menu.getMenuComponents()) {
            item.setFont(FONTE); item.setForeground(TEXTO); item.setBackground(Color.WHITE);
        }
    }

    private static Border borda(Color cor, int espessura, int vertical, int horizontal) {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(cor, espessura),
                BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal));
    }

    private static boolean vermelho(Color cor) {
        return cor.getRed() > 130 && cor.getGreen() < 95 && cor.getBlue() < 95;
    }

    private static void configurar(Component componente) {
        if (componente instanceof JLabel label) {
            int tamanho = label.getFont().getSize();
            boolean erro = vermelho(label.getForeground());
            boolean auxiliar = tamanho <= 12;
            label.setFont(tamanho >= 20 ? FONTE.deriveFont(Font.BOLD, 22f)
                    : tamanho >= 14 && label.getFont().isBold() ? FONTE.deriveFont(Font.BOLD, 14f)
                    : auxiliar ? FONTE.deriveFont(12f) : FONTE);
            label.setForeground(erro ? VERMELHO : auxiliar ? SECUNDARIO : TEXTO);
            return;
        }
        if (componente instanceof JButton botao) {
            boolean primaria = Color.WHITE.equals(botao.getForeground());
            boolean exclusao = vermelho(botao.getForeground());
            botao.setFont(primaria ? FONTE.deriveFont(Font.BOLD) : FONTE);
            botao.setContentAreaFilled(false); botao.setOpaque(true);
            botao.setBackground(primaria ? AZUL : Color.WHITE);
            botao.setForeground(primaria ? Color.WHITE : exclusao ? VERMELHO : TEXTO);
            botao.setBorder(borda(primaria ? AZUL : BORDA, 1, 6, 14));
            botao.setFocusPainted(false);
            botao.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { botao.setBorder(borda(AZUL.darker(), 2, 5, 13)); }
                @Override public void focusLost(FocusEvent e) { botao.setBorder(borda(primaria ? AZUL : BORDA, 1, 6, 14)); }
            });
            return;
        }
        if (componente instanceof JTextComponent campo) {
            campo.setFont(FONTE); campo.setForeground(TEXTO); campo.setCaretColor(TEXTO);
            campo.setBackground(campo.isEditable() ? Color.WHITE : FUNDO);
            campo.setDisabledTextColor(SECUNDARIO);
            if (campo instanceof JTextArea) campo.setBorder(BorderFactory.createEmptyBorder(7, 9, 7, 9));
            else {
                campo.setBorder(borda(BORDA, 1, 5, 8));
                campo.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { campo.setBorder(borda(AZUL, 1, 5, 8)); }
                    @Override public void focusLost(FocusEvent e) { campo.setBorder(borda(BORDA, 1, 5, 8)); }
                });
            }
            return;
        }
        if (componente instanceof JComboBox<?> combo) {
            combo.setUI(new BasicComboBoxUI());
            combo.setFont(FONTE); combo.setBackground(Color.WHITE); combo.setForeground(TEXTO);
            combo.setBorder(BorderFactory.createLineBorder(BORDA));
            return;
        }
        if (componente instanceof JTable tabela) {
            tabela.setFont(FONTE); tabela.setRowHeight(28);
            tabela.setBackground(Color.WHITE); tabela.setForeground(TEXTO);
            tabela.setSelectionBackground(new Color(224, 234, 244)); tabela.setSelectionForeground(TEXTO);
            tabela.setGridColor(new Color(231, 234, 238));
            tabela.setShowHorizontalLines(true); tabela.setShowVerticalLines(false);
            tabela.setIntercellSpacing(new Dimension(0, 1));
            tabela.setDefaultRenderer(Object.class, new RenderizadorTabela());
            tabela.setDefaultRenderer(Number.class, new RenderizadorTabela(SwingConstants.RIGHT));
            tabela.getTableHeader().setFont(FONTE.deriveFont(Font.BOLD));
            tabela.getTableHeader().setBackground(new Color(235, 238, 242));
            tabela.getTableHeader().setForeground(TEXTO);
            tabela.getTableHeader().setPreferredSize(new Dimension(tabela.getTableHeader().getPreferredSize().width, 28));
            return;
        }
        if (componente instanceof JScrollPane scroll) {
            scroll.setBorder(BorderFactory.createLineBorder(BORDA));
            scroll.getViewport().setBackground(Color.WHITE);
            Component conteudo = scroll.getViewport().getView();
            if (conteudo != null) configurar(conteudo);
            return;
        }
        if (componente instanceof JCheckBox caixa) {
            caixa.setFont(FONTE); caixa.setForeground(TEXTO); caixa.setBackground(FUNDO);
            return;
        }
        if (componente instanceof JSeparator separador) {
            separador.setForeground(BORDA); separador.setBackground(FUNDO); return;
        }
        if (componente instanceof Container grupo) {
            grupo.setBackground(FUNDO);
            for (Component filho : grupo.getComponents()) configurar(filho);
        }
    }
}

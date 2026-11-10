package br.com.assistencia;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import br.com.assistencia.view.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import static br.com.assistencia.VerificarFluxos.*;

/** Executado dentro da transação de VerificarFluxos, sem manter dados de teste. */
final class VerificarModulos {
    private static void negado(Checked tarefa) throws Exception {
        try { tarefa.run(); throw new AssertionError("Operação permitida sem login"); }
        catch (SecurityException esperado) { }
    }
    static void semSessao() throws Exception {
        negado(() -> new CategoriaController().listar());
        negado(() -> new EquipamentoController().listar());
        negado(() -> new OrdemServicoController().listar());
        negado(() -> new RelatorioController().consultar(LocalDate.now(), LocalDate.now()));
    }
    static void tecnicoComum() throws Exception {
        new CategoriaController().listar(); new EquipamentoController().listar();
        new OrdemServicoController().listar(); new OrdemServicoController().listarResponsaveis();
        new RelatorioController().consultar(LocalDate.now(), LocalDate.now());
    }
    private static void aguardarNova(JFrame janela) throws Exception {
        long limite = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < limite) {
            boolean[] busy = new boolean[1];
            edt(() -> busy[0] = janela instanceof TelaRelatorio
                    ? (boolean) campo(janela, "ocupado") : ((CadastroSuporte<?>) campo(janela, "suporte")).ocupado());
            if (!busy[0]) return;
            Thread.sleep(20);
        }
        throw new AssertionError("Carga da tela não terminou: " + janela.getClass().getSimpleName());
    }
    private static void clique(JFrame janela, String nome) throws Exception { ((JButton) campo(janela, nome)).doClick(); }
    private static void texto(JFrame janela, String nome, String valor) throws Exception { ((javax.swing.text.JTextComponent) campo(janela, nome)).setText(valor); }
    @SuppressWarnings("unchecked")
    private static void escolher(JFrame janela, String nome, int id) throws Exception {
        Formularios.selecionar((JComboBox<Opcao>) campo(janela, nome), id);
    }
    private static void selecionar(JFrame janela, int id) throws Exception {
        JTable tabela = (JTable) campo(janela, "tabelaRegistros");
        tabela.getRowSorter().toggleSortOrder(1);
        for (int i = 0; i < tabela.getRowCount(); i++) {
            if (((Number) tabela.getValueAt(i, 0)).intValue() == id) { tabela.setRowSelectionInterval(i, i); return; }
        }
        throw new AssertionError("Registro não encontrado na tabela: " + id);
    }
    private static void confirmar(int resposta) {
        Timer timer = new Timer(100, e -> {
            for (Window w : Window.getWindows()) if (w instanceof JDialog dialogo && dialogo.isShowing()) {
                for (Component c : dialogo.getContentPane().getComponents()) if (c instanceof JOptionPane p) p.setValue(resposta);
            }
        });
        timer.setRepeats(false); timer.start();
    }
    private static void imagemNova(JFrame janela, String arquivo) throws Exception {
        janela.validate();
        JLabel titulo = (JLabel) campo(janela, "lblTitulo");
        JTable tabela = (JTable) campo(janela, "tabelaRegistros");
        Point posicao = SwingUtilities.convertPoint(tabela, 0, 0, janela.getContentPane());
        conferir(posicao.y > titulo.getY() + titulo.getHeight(), "Tabela sobreposta ao título");
        imagem(janela, arquivo);
    }
    private static OrdemServico novaOrdem(int cliente, int tecnico, int equipamento, String defeito) {
        OrdemServico os = new OrdemServico();
        os.setClienteId(cliente); os.setTecnicoId(tecnico); os.setEquipamentoId(equipamento);
        os.setStatus(StatusOrdem.ABERTA); os.setDefeitoRelatado(defeito);
        return os;
    }
    private static OrdemServico ordem(OrdemServicoController c, String defeito) throws Exception {
        return c.listar().stream().filter(os -> os.getDefeitoRelatado().equals(defeito)).findFirst().orElseThrow();
    }
    private static void moverAbertura(Connection banco, int id, LocalDateTime data) throws Exception {
        try (PreparedStatement p = banco.prepareStatement("UPDATE ordem_servico SET data_abertura=?,data_encerramento=NULL,status='ABERTA' WHERE id=?")) {
            p.setTimestamp(1, Timestamp.valueOf(data)); p.setInt(2, id); p.executeUpdate();
        }
    }
    static void executar(Connection banco, String prefixo) throws Exception {
        CategoriaController categorias = new CategoriaController();
        EquipamentoController equipamentos = new EquipamentoController();
        OrdemServicoController ordens = new OrdemServicoController();
        RelatorioController relatorios = new RelatorioController();
        ClienteController clientes = new ClienteController();
        TecnicoController tecnicos = new TecnicoController();
        String nomeCategoria = prefixo + " categoria";
        invalido(() -> categorias.cadastrar(" "), "Aceitou categoria vazia");
        invalido(() -> categorias.cadastrar("a".repeat(81)), "Aceitou categoria longa");
        categorias.cadastrar(nomeCategoria);
        int categoriaId = categorias.listar().stream().filter(c -> c.getNome().equals(nomeCategoria)).findFirst().orElseThrow().getId();
        ausente(() -> categorias.cadastrar(nomeCategoria), "Aceitou categoria duplicada");
        categorias.atualizar(categoriaId, nomeCategoria + " editada");
        conferir(categorias.listar().stream().anyMatch(c -> c.getId() == categoriaId && c.getNome().endsWith("editada")), "Edição de categoria falhou");

        Equipamento aparelho = new Equipamento(0, categoriaId, " Marca teste ", prefixo, " ");
        Equipamento invalido = new Equipamento(0, 0, "Marca", "Modelo", "");
        invalido(() -> equipamentos.salvar(invalido), "Aceitou equipamento sem categoria");
        equipamentos.salvar(aparelho);
        aparelho = equipamentos.listar().stream().filter(e -> e.getModelo().equals(prefixo)).findFirst().orElseThrow();
        int equipamentoId = aparelho.getId();
        conferir(aparelho.getMarca().equals("Marca teste") && aparelho.getNumeroSerie() == null, "Não normalizou equipamento");
        aparelho.setNumeroSerie("SERIE-TESTE"); equipamentos.salvar(aparelho);
        ausente(() -> categorias.excluir(categoriaId), "Excluiu categoria com equipamento");
        clientes.cadastrar(prefixo + " cliente", "11999999999", "");
        int clienteId = clientes.listar().stream().filter(c -> c.getNome().equals(prefixo + " cliente")).findFirst().orElseThrow().getId();
        char[] senha = "Teste123".toCharArray();
        String email = "os-" + prefixo.toLowerCase() + "@example.com";
        tecnicos.cadastrar("Técnico da OS", "123", email, false, senha, senha);
        int tecnicoId = tecnicos.listar().stream().filter(t -> t.getEmail().equals(email)).findFirst().orElseThrow().getId();

        OrdemServico dados = novaOrdem(clienteId, tecnicoId, equipamentoId, prefixo + " defeito");
        dados.setValorServico(new BigDecimal("-1"));
        invalido(() -> ordens.salvar(dados), "Aceitou valor negativo");
        dados.setValorServico(new BigDecimal("12.345"));
        invalido(() -> ordens.salvar(dados), "Aceitou três casas decimais");
        dados.setValorServico(new BigDecimal("120.50"));
        dados.setDataAbertura(LocalDateTime.of(2000, 1, 1, 0, 0));
        dados.setDataEncerramento(LocalDateTime.of(2000, 1, 1, 1, 0));
        ordens.salvar(dados);
        OrdemServico registrada = ordem(ordens, prefixo + " defeito");
        int ordemId = registrada.getId();
        conferir(registrada.getDataAbertura().getYear() != 2000 && registrada.getDataEncerramento() == null,
                "Abertura ou encerramento confiaram nas datas enviadas pela tela");
        for (StatusOrdem status : List.of(StatusOrdem.CONCLUIDA, StatusOrdem.ENTREGUE, StatusOrdem.CANCELADA)) {
            registrada.setStatus(status); ordens.salvar(registrada); registrada = ordem(ordens, prefixo + " defeito");
            conferir(registrada.getDataEncerramento() != null && !registrada.getDataEncerramento().isBefore(registrada.getDataAbertura()), "Encerramento automático falhou");
        }
        registrada.setStatus(StatusOrdem.EM_REPARO); ordens.salvar(registrada);
        conferir(ordem(ordens, prefixo + " defeito").getDataEncerramento() == null, "Reabertura não limpou encerramento");
        ausente(() -> equipamentos.excluir(equipamentoId), "Excluiu equipamento com OS");
        ausente(() -> clientes.excluir(clienteId), "Excluiu cliente com OS");
        ausente(() -> tecnicos.excluir(tecnicoId), "Excluiu técnico com OS");

        List<Integer> idsOrdens = new ArrayList<>(); idsOrdens.add(ordemId);
        moverAbertura(banco, ordemId, LocalDateTime.of(2026, 10, 31, 23, 59, 59));
        OrdemServico inicioDia = novaOrdem(clienteId, tecnicoId, equipamentoId, prefixo + " inicio");
        ordens.salvar(inicioDia); int idInicio = ordem(ordens, prefixo + " inicio").getId(); idsOrdens.add(idInicio);
        moverAbertura(banco, idInicio, LocalDateTime.of(2026, 10, 31, 0, 0));
        OrdemServico diaSeguinte = novaOrdem(clienteId, tecnicoId, equipamentoId, prefixo + " seguinte");
        ordens.salvar(diaSeguinte); int idSeguinte = ordem(ordens, prefixo + " seguinte").getId(); idsOrdens.add(idSeguinte);
        moverAbertura(banco, idSeguinte, LocalDateTime.of(2026, 11, 1, 0, 0));
        LocalDate dia = LocalDate.of(2026, 10, 31);
        List<LinhaRelatorio> resultado = relatorios.consultar(dia, dia);
        conferir(resultado.stream().anyMatch(r -> r.numeroOs() == ordemId) && resultado.stream().anyMatch(r -> r.numeroOs() == idInicio)
                && resultado.stream().noneMatch(r -> r.numeroOs() == idSeguinte), "Relatório não respeita os limites do dia");
        invalido(() -> relatorios.consultar(dia.plusDays(1), dia), "Aceitou intervalo invertido");
        invalido(() -> Formularios.lerDia("31/02/2026"), "Aceitou data inexistente");
        conferir(relatorios.consultar(LocalDate.of(9998,12,25), LocalDate.of(9998,12,25)).isEmpty(), "Período vazio trouxe ordens");
        conferir(Formularios.lerValor("1.234,56").compareTo(new BigDecimal("1234.56")) == 0, "Valor em reais foi interpretado incorretamente");
        System.out.println("OK: categorias, equipamentos, vínculos da OS, valores, encerramento e relatório com último dia inteiro.");

        JFrame[] telas = new JFrame[4];
        edt(() -> telas[0] = new TelaCadastroCategoria()); aguardarNova(telas[0]);
        String nomeTela = prefixo + " tela categoria";
        edt(() -> { texto(telas[0], "txtNome", nomeTela); clique(telas[0], "btnSalvar"); }); aguardarNova(telas[0]);
        int categoriaTela = categorias.listar().stream().filter(c -> c.getNome().equals(nomeTela)).findFirst().orElseThrow().getId();
        edt(() -> {
            selecionar(telas[0], categoriaTela); clique(telas[0], "btnEditar");
            conferir(((JTextField) campo(telas[0], "txtNome")).getText().equals(nomeTela), "Categoria errada após ordenar");
            clique(telas[0], "btnNovo"); texto(telas[0], "txtNome", "Sem salvar");
            confirmar(JOptionPane.NO_OPTION); clique(telas[0], "btnNovo");
            conferir(((JTextField) campo(telas[0], "txtNome")).getText().equals("Sem salvar"), "Novo descartou sem confirmação");
            confirmar(JOptionPane.YES_OPTION); clique(telas[0], "btnNovo");
            conferir(((JTextField) campo(telas[0], "txtNome")).getText().isEmpty(), "Novo não limpou categoria");
            selecionar(telas[0], categoriaTela); confirmar(JOptionPane.YES_OPTION); clique(telas[0], "btnExcluir");
        }); aguardarNova(telas[0]);
        conferir(categorias.listar().stream().noneMatch(c -> c.getId() == categoriaTela), "Exclusão pela tela falhou");
        edt(() -> imagemNova(telas[0], "assistencia-categorias.png"));

        edt(() -> telas[1] = new TelaCadastroEquipamento()); aguardarNova(telas[1]);
        edt(() -> {
            escolher(telas[1], "cmbCategoria", categoriaId); texto(telas[1], "txtMarca", "Marca tela");
            texto(telas[1], "txtModelo", prefixo + " equipamento tela"); clique(telas[1], "btnSalvar");
        }); aguardarNova(telas[1]);
        int equipamentoTela = equipamentos.listar().stream().filter(e -> e.getModelo().equals(prefixo + " equipamento tela")).findFirst().orElseThrow().getId();
        edt(() -> imagemNova(telas[1], "assistencia-equipamentos.png"));

        edt(() -> telas[2] = new TelaOrdensServico()); aguardarNova(telas[2]);
        edt(() -> {
            escolher(telas[2], "cmbCliente", clienteId); escolher(telas[2], "cmbTecnico", tecnicoId);
            escolher(telas[2], "cmbEquipamento", equipamentoTela); texto(telas[2], "txtDefeito", prefixo + " OS tela");
            texto(telas[2], "txtValor", "89,90"); clique(telas[2], "btnSalvar");
        }); aguardarNova(telas[2]);
        OrdemServico osTela = ordem(ordens, prefixo + " OS tela"); idsOrdens.add(osTela.getId());
        conferir(osTela.getEquipamentoId() == equipamentoTela && osTela.getValorServico().compareTo(new BigDecimal("89.90")) == 0, "OS da tela gravou IDs ou valor incorretos");
        edt(() -> {
            selecionar(telas[2], osTela.getId()); clique(telas[2], "btnEditar");
            ((JComboBox<?>) campo(telas[2], "cmbStatus")).setSelectedItem(StatusOrdem.ENTREGUE);
            clique(telas[2], "btnSalvar");
        }); aguardarNova(telas[2]);
        conferir(ordem(ordens, prefixo + " OS tela").getDataEncerramento() != null, "Encerramento pela tela não foi salvo");
        edt(() -> imagemNova(telas[2], "assistencia-ordens.png"));

        edt(() -> telas[3] = new TelaRelatorio()); aguardarNova(telas[3]);
        edt(() -> { texto(telas[3], "txtInicio", "31/10/2026"); texto(telas[3], "txtFim", "31/10/2026"); clique(telas[3], "btnConsultar"); });
        aguardarNova(telas[3]);
        edt(() -> {
            JTable tabela = (JTable) campo(telas[3], "tabelaRegistros");
            conferir(tabela.getRowCount() == resultado.size(), "Tela do relatório divergiu da consulta");
            imagemNova(telas[3], "assistencia-relatorio.png");
            for (JFrame janela : telas) ((TelaGerenciada) janela).fecharComConfirmacao();
        });
        for (int id : idsOrdens) ordens.excluir(id);
        equipamentos.excluir(equipamentoTela); equipamentos.excluir(equipamentoId);
        categorias.excluir(categoriaId); clientes.excluir(clienteId); tecnicos.excluir(tecnicoId);
        System.out.println("OK: telas novas, Novo com confirmação, IDs nos combos, encerramento pela tela e relatório integrado.");
    }
}

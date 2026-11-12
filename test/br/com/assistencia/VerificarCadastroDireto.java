package br.com.assistencia;

import br.com.assistencia.controller.*;
import br.com.assistencia.model.*;
import br.com.assistencia.view.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import static br.com.assistencia.VerificarFluxos.*;

/** Confere cadastro por nomes, edição e rollback sem alterar os registros reais. */
final class VerificarCadastroDireto {
    private static List<Integer> quantidades(Connection banco) throws Exception {
        List<Integer> valores = new ArrayList<>();
        for (String tabela : List.of("cliente", "equipamento", "ordem_servico")) {
            try (Statement s = banco.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
                r.next(); valores.add(r.getInt(1));
            }
        }
        return valores;
    }
    private static OrdemServico nova(String cliente, String equipamento, String defeito) {
        OrdemServico os = new OrdemServico();
        os.setClienteNome(cliente); os.setEquipamentoModelo(equipamento);
        os.setTecnicoId(Sessao.idTecnico()); os.setStatus(StatusOrdem.ABERTA); os.setDefeitoRelatado(defeito);
        return os;
    }
    private static OrdemServico carregar(OrdemServicoController controller, int id) throws Exception {
        return controller.listar().stream().filter(o -> o.getId() == id).findFirst().orElseThrow();
    }
    private static void aguardar(JFrame frame, boolean suporte) throws Exception {
        long fim = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < fim) {
            boolean[] busy = new boolean[1];
            edt(() -> busy[0] = suporte ? ((CadastroSuporte<?>) campo(frame, "suporte")).ocupado()
                    : (boolean) campo(frame, "ocupado"));
            if (!busy[0]) return;
            Thread.sleep(20);
        }
        throw new AssertionError("A tela não concluiu o carregamento.");
    }
    private static void selecionar(JTable tabela, int id) {
        for (int i = 0; i < tabela.getRowCount(); i++) if (((Number) tabela.getValueAt(i, 0)).intValue() == id) {
            tabela.setRowSelectionInterval(i, i); return;
        }
        throw new AssertionError("Registro não encontrado na tela.");
    }
    static void executar(Connection banco, String prefixo) throws Exception {
        OrdemServicoController controller = new OrdemServicoController();
        List<Integer> antes = quantidades(banco);
        OrdemServico os = nova("  " + prefixo + " direto  ", "  " + prefixo + " notebook  ", prefixo + " só nomes");
        controller.salvarAtendimento(os);
        conferir(os.getId() > 0 && os.getClienteId() > 0 && os.getEquipamentoId() > 0, "Cadastros ou OS não foram criados");
        conferir(quantidades(banco).equals(antes.stream().map(n -> n + 1).toList()), "Cadastro direto não inseriu os três registros");
        int clienteOriginal = os.getClienteId(), equipamentoOriginal = os.getEquipamentoId();
        Cliente cliente = new ClienteController().listar().stream().filter(c -> c.getId() == clienteOriginal).findFirst().orElseThrow();
        Equipamento equipamento = new EquipamentoController().listar().stream().filter(e -> e.getId() == equipamentoOriginal).findFirst().orElseThrow();
        conferir(cliente.getTelefone() == null && cliente.getEmail() == null, "Inventou dados de contato");
        conferir(equipamento.getCategoriaId() == 0 && equipamento.getMarca() == null && equipamento.getNumeroSerie() == null,
                "Inventou dados do aparelho");
        new ClienteController().atualizar(clienteOriginal, cliente.getNome(), "", "");
        new EquipamentoController().salvar(equipamento);

        List<Integer> aposCadastro = quantidades(banco);
        os.setStatus(StatusOrdem.ENTREGUE); controller.salvarAtendimento(os);
        conferir(quantidades(banco).equals(aposCadastro) && os.getClienteId() == clienteOriginal
                && os.getEquipamentoId() == equipamentoOriginal, "Editar status duplicou os cadastros");
        OrdemServico lida = carregar(controller, os.getId());
        conferir(lida.getDataEncerramento() != null && lida.getEquipamentoModelo().equals(equipamento.getModelo()),
                "OS parcial não apareceu corretamente na listagem");
        LocalDate dia = lida.getDataAbertura().toLocalDate();
        LinhaRelatorio linha = new RelatorioController().consultar(dia, dia).stream()
                .filter(r -> r.numeroOs() == os.getId()).findFirst().orElseThrow();
        conferir(Formularios.equipamento(linha.categoria(), linha.marca(), linha.modelo()).equals(equipamento.getModelo()),
                "Relatório não apresentou aparelho sem marca/categoria");

        OrdemServico falha = nova(prefixo + " falha", prefixo + " falha equipamento", "Falha controlada");
        falha.setClienteTelefone("(61) 91234-5678");
        falha.setTecnicoId(Integer.MAX_VALUE);
        ausente(() -> controller.salvarAtendimento(falha), "Aceitou técnico inexistente");
        conferir(quantidades(banco).equals(aposCadastro), "Erro na OS deixou cliente ou aparelho órfãos");
        OrdemServico invalida = nova(" ", "Equipamento", "Defeito");
        invalido(() -> controller.salvarAtendimento(invalida), "Aceitou nome de cliente vazio");
        invalida.setClienteNome("Cliente"); invalida.setEquipamentoModelo("x".repeat(121));
        invalido(() -> controller.salvarAtendimento(invalida), "Aceitou nome de aparelho longo");
        invalida.setEquipamentoModelo("Equipamento");
        invalida.setClienteTelefone("1".repeat(26));
        invalido(() -> controller.salvarAtendimento(invalida), "Aceitou telefone com mais de 25 caracteres");
        conferir(quantidades(banco).equals(aposCadastro), "Validação deixou registros incompletos");

        OrdemServico homonima = nova(cliente.getNome(), equipamento.getModelo(), prefixo + " nomes iguais");
        controller.salvarAtendimento(homonima);
        conferir(homonima.getClienteId() != clienteOriginal && homonima.getEquipamentoId() != equipamentoOriginal,
                "Nomes iguais foram tratados como comprovação de identidade");
        os.setClienteNome(prefixo + " outro cliente"); controller.salvarAtendimento(os);
        conferir(os.getClienteId() != clienteOriginal && os.getEquipamentoId() == equipamentoOriginal,
                "Troca do cliente alterou o equipamento");
        os.setEquipamentoModelo(prefixo + " outro aparelho"); controller.salvarAtendimento(os);
        conferir(os.getEquipamentoId() != equipamentoOriginal, "Troca do nome do aparelho não criou o cadastro correspondente");
        conferir(new ClienteController().listar().stream().anyMatch(c -> c.getId() == clienteOriginal && c.getNome().equals(cliente.getNome())),
                "Renomeou um cadastro anterior ao editar a OS");

        JFrame[] frame = new JFrame[3];
        edt(() -> frame[0] = new TelaCadastroCliente()); aguardar(frame[0], false);
        edt(() -> {
            selecionar((JTable) campo(frame[0], "tabelaClientes"), clienteOriginal);
            ((JButton) campo(frame[0], "btnEditar")).doClick();
            conferir(((JTextField) campo(frame[0], "txtTelefone")).getText().isEmpty(), "Não carregou contato opcional");
            Timer guarda = new Timer(150, evento -> {
                for (java.awt.Window w : java.awt.Window.getWindows()) if (w instanceof JDialog d && d.isShowing())
                    for (java.awt.Component c : d.getContentPane().getComponents()) if (c instanceof JOptionPane p) p.setValue(JOptionPane.NO_OPTION);
            });
            guarda.setRepeats(false); guarda.start();
            conferir(((TelaGerenciada) frame[0]).fecharComConfirmacao(), "Edição sem mudanças foi marcada como alterada por contato nulo");
            guarda.stop();
        });
        edt(() -> frame[1] = new TelaRelatorio()); aguardar(frame[1], false);
        edt(() -> {
            ((JTextField) campo(frame[1], "txtInicio")).setText(Formularios.dia(dia));
            ((JTextField) campo(frame[1], "txtFim")).setText(Formularios.dia(dia));
            ((JButton) campo(frame[1], "btnConsultar")).doClick();
        }); aguardar(frame[1], false);
        edt(() -> {
            JTable tabela = (JTable) campo(frame[1], "tabelaRegistros");
            for (int i = 0; i < tabela.getRowCount(); i++) if (((Number) tabela.getValueAt(i, 0)).intValue() == os.getId()) {
                conferir(tabela.getValueAt(i, 4).equals(os.getEquipamentoModelo()), "Texto do relatório mostrou dados nulos");
            }
            frame[1].dispose();
            frame[2] = new TelaOrdensServico();
        }); aguardar(frame[2], true);
        String defeitoContato = prefixo + " contato pela tela";
        edt(() -> {
            conferir(campo(frame[2], "txtCliente") instanceof JTextField && campo(frame[2], "txtEquipamento") instanceof JTextField,
                    "A OS ainda exige selecionar cliente/aparelho");
            imagem(frame[2], "assistencia-ordens-direto.png");
            ((JTextField) campo(frame[2], "txtCliente")).setText(prefixo + " cliente com telefone");
            ((JTextField) campo(frame[2], "txtTelefone")).setText("  (61) 91234-5678  ");
            ((JTextField) campo(frame[2], "txtEquipamento")).setText("Notebook");
            ((JComboBox<?>) campo(frame[2], "cmbTecnico")).setSelectedIndex(1);
            ((JTextArea) campo(frame[2], "txtDefeito")).setText(defeitoContato);
            ((JButton) campo(frame[2], "btnSalvar")).doClick();
        });
        aguardar(frame[2], true);
        OrdemServico comContato = controller.listar().stream()
                .filter(o -> defeitoContato.equals(o.getDefeitoRelatado())).findFirst().orElseThrow();
        conferir("(61) 91234-5678".equals(comContato.getClienteTelefone()), "Telefone não foi gravado no cadastro do cliente");
        edt(() -> {
            var telefone = (JTextField) campo(frame[2], "txtTelefone");
            conferir(telefone.getText().isEmpty(), "Telefone não foi limpo após salvar");
            selecionar((JTable) campo(frame[2], "tabelaRegistros"), comContato.getId());
            ((JButton) campo(frame[2], "btnEditar")).doClick();
            conferir("(61) 91234-5678".equals(telefone.getText()), "Telefone não foi carregado na edição da OS");
            telefone.setText("(61) 99876-5432");
            ((JButton) campo(frame[2], "btnSalvar")).doClick();
        });
        aguardar(frame[2], true);
        OrdemServico atualizada = carregar(controller, comContato.getId());
        conferir("(61) 99876-5432".equals(atualizada.getClienteTelefone())
                && atualizada.getClienteId() == comContato.getClienteId(),
                "Editar telefone pela OS não atualizou o mesmo cliente");
        edt(() -> frame[2].dispose());
        System.out.println("OK: OS com telefone opcional, edição de contato, rollback sem cadastros órfãos e relatório com dados opcionais.");
    }
}

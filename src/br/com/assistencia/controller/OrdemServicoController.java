package br.com.assistencia.controller;

import br.com.assistencia.dao.OrdemServicoDAO;
import br.com.assistencia.dao.TecnicoDAO;
import br.com.assistencia.model.OrdemServico;
import br.com.assistencia.model.Tecnico;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class OrdemServicoController {
    private final OrdemServicoDAO dao = new OrdemServicoDAO();

    public List<OrdemServico> listar() throws SQLException {
        Sessao.validarAcesso();
        return dao.listar();
    }

    /** A seleção do responsável não concede acesso ao gerenciamento de técnicos. */
    public List<Tecnico> listarResponsaveis() throws SQLException {
        Sessao.validarAcesso();
        return new TecnicoDAO().listar();
    }

    public void salvar(OrdemServico os) throws SQLException {
        Sessao.validarAcesso();
        if (os == null || os.getId() < 0) throw new IllegalArgumentException("Ordem de serviço inválida.");
        if (os.getClienteId() <= 0) throw new IllegalArgumentException("Selecione o cliente.");
        if (os.getEquipamentoId() <= 0) throw new IllegalArgumentException("Selecione o equipamento.");
        validarConteudo(os);
        dao.salvar(os);
    }

    public void salvarAtendimento(OrdemServico os) throws SQLException {
        Sessao.validarAcesso();
        if (os == null || os.getId() < 0) throw new IllegalArgumentException("Ordem de serviço inválida.");
        os.setClienteNome(nomeObrigatorio(os.getClienteNome(), "cliente"));
        os.setEquipamentoModelo(nomeObrigatorio(os.getEquipamentoModelo(), "equipamento"));
        validarConteudo(os);
        dao.salvarAtendimento(os);
    }

    private String nomeObrigatorio(String nome, String campo) {
        if (nome == null || nome.isBlank() || nome.trim().length() > 120) {
            throw new IllegalArgumentException("Informe o nome do " + campo + " (até 120 caracteres).");
        }
        return nome.trim();
    }

    private void validarConteudo(OrdemServico os) {
        if (os.getTecnicoId() <= 0) throw new IllegalArgumentException("Selecione o técnico responsável.");
        if (os.getStatus() == null) throw new IllegalArgumentException("Selecione o status.");
        String defeito = os.getDefeitoRelatado() == null ? "" : os.getDefeitoRelatado().trim();
        if (defeito.isEmpty() || defeito.length() > 500) {
            throw new IllegalArgumentException("Descreva o defeito relatado em até 500 caracteres.");
        }
        os.setDefeitoRelatado(defeito);
        String diagnostico = os.getDiagnostico() == null ? "" : os.getDiagnostico().trim();
        if (diagnostico.getBytes(StandardCharsets.UTF_8).length > 65535) {
            throw new IllegalArgumentException("O diagnóstico excede a capacidade do campo. Reduza o texto.");
        }
        os.setDiagnostico(diagnostico.isEmpty() ? null : diagnostico);
        BigDecimal valor = os.getValorServico();
        if (valor != null) {
            if (valor.signum() < 0 || valor.compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new IllegalArgumentException("O valor deve estar entre R$ 0,00 e R$ 99.999.999,99.");
            }
            try { os.setValorServico(valor.setScale(2, RoundingMode.UNNECESSARY)); }
            catch (ArithmeticException e) { throw new IllegalArgumentException("Informe o valor com até duas casas decimais."); }
        }
        // Datas são calculadas no banco; os cadastros diretos usam a mesma transação da OS.
    }

    public void excluir(int id) throws SQLException {
        Sessao.validarAcesso();
        if (id <= 0) throw new IllegalArgumentException("Selecione uma ordem válida.");
        dao.excluir(id);
    }
}

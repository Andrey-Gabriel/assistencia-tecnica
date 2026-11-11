package br.com.assistencia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdemServico {
    private int id;
    private int clienteId;
    private int tecnicoId;
    private int equipamentoId;
    private String clienteNome;
    private String tecnicoNome;
    private String equipamentoNome;
    private String equipamentoModelo;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataEncerramento;
    private StatusOrdem status;
    private String defeitoRelatado;
    private String diagnostico;
    private BigDecimal valorServico;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int id) { clienteId = id; }
    public int getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(int id) { tecnicoId = id; }
    public int getEquipamentoId() { return equipamentoId; }
    public void setEquipamentoId(int id) { equipamentoId = id; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String nome) { clienteNome = nome; }
    public String getTecnicoNome() { return tecnicoNome; }
    public void setTecnicoNome(String nome) { tecnicoNome = nome; }
    public String getEquipamentoNome() { return equipamentoNome; }
    public void setEquipamentoNome(String nome) { equipamentoNome = nome; }
    public String getEquipamentoModelo() { return equipamentoModelo; }
    public void setEquipamentoModelo(String nome) { equipamentoModelo = nome; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime data) { dataAbertura = data; }
    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime data) { dataEncerramento = data; }
    public StatusOrdem getStatus() { return status; }
    public void setStatus(StatusOrdem status) { this.status = status; }
    public String getDefeitoRelatado() { return defeitoRelatado; }
    public void setDefeitoRelatado(String texto) { defeitoRelatado = texto; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String texto) { diagnostico = texto; }
    public BigDecimal getValorServico() { return valorServico; }
    public void setValorServico(BigDecimal valor) { valorServico = valor; }
}

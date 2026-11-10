package br.com.assistencia.model;

public enum StatusOrdem {
    ABERTA("Aberta", false), EM_ANALISE("Em análise", false), EM_REPARO("Em reparo", false),
    CONCLUIDA("Concluída", true), ENTREGUE("Entregue", true), CANCELADA("Cancelada", true);

    private final String descricao;
    private final boolean encerrada;
    StatusOrdem(String descricao, boolean encerrada) {
        this.descricao = descricao;
        this.encerrada = encerrada;
    }
    public boolean encerraAtendimento() { return encerrada; }
    @Override public String toString() { return descricao; }
}

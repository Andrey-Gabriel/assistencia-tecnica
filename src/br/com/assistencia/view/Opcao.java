package br.com.assistencia.view;

/** O texto é apresentado ao usuário; somente o ID é enviado ao banco. */
public record Opcao(int id, String descricao) {
    @Override public String toString() { return descricao; }
}

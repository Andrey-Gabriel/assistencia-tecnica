package br.com.assistencia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LinhaRelatorio(int numeroOs, LocalDateTime abertura, LocalDateTime encerramento,
        String cliente, String tecnico, String categoria, String marca, String modelo,
        StatusOrdem status, BigDecimal valor) {
}

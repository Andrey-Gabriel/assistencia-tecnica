package br.com.assistencia.seguranca;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Hash de senha com salt aleatório; a senha original não é armazenada. */
public final class Senha {

    private static final int ITERACOES = 600_000;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Senha() {
    }

    public static String gerarHash(char[] senha) {
        validarNova(senha);
        byte[] salt = new byte[16];
        ALEATORIO.nextBytes(salt);
        byte[] hash = derivar(senha, salt, ITERACOES);
        return "pbkdf2-sha256$" + ITERACOES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean conferir(char[] senha, String registro) {
        if (senha == null || senha.length == 0 || senha.length > 128 || registro == null) {
            return false;
        }
        try {
            String[] partes = registro.split("\\$", -1);
            if (partes.length != 4 || !partes[0].equals("pbkdf2-sha256")) {
                return false;
            }
            int iteracoes = Integer.parseInt(partes[1]);
            if (iteracoes < ITERACOES || iteracoes > 2_000_000) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            if (salt.length != 16 || esperado.length != 32) {
                return false;
            }
            byte[] recebido = derivar(senha, salt, iteracoes);
            try {
                return MessageDigest.isEqual(esperado, recebido);
            } finally {
                Arrays.fill(recebido, (byte) 0);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void validarNova(char[] senha) {
        if (senha == null || senha.length < 8 || senha.length > 128) {
            throw new IllegalArgumentException("Use uma senha de 8 a 128 caracteres. "
                    + "Você pode usar uma frase fácil de lembrar.");
        }
    }

    private static byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec especificacao = new PBEKeySpec(senha, salt, iteracoes, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacao).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível verificar a senha neste Java.", e);
        } finally {
            especificacao.clearPassword();
        }
    }
}

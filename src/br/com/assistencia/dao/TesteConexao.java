
package br.com.assistencia.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection conexao = br.com.assistencia.dao.conexao.conectar()) {

            System.out.println(
                "Conexão com o MySQL realizada com sucesso!"
            );

        } catch (SQLException e) {

            System.out.println(
                "Erro ao conectar: " + e.getMessage()
            );
        }
    }
}

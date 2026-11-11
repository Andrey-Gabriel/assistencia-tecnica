
package br.com.assistencia.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection conexao = br.com.assistencia.dao.conexao.conectar();
             Statement consulta = conexao.createStatement();
             ResultSet resultado = consulta.executeQuery(
                 "SELECT DATABASE() AS banco, CURRENT_USER() AS usuario, VERSION() AS versao"
             )) {
            resultado.next();
            System.out.println("Conexão com o MySQL realizada com sucesso!");
            System.out.println("Banco: " + resultado.getString("banco"));
            System.out.println("Usuário: " + resultado.getString("usuario"));
            System.out.println("MySQL: " + resultado.getString("versao"));

            try (ResultSet categorias = consulta.executeQuery(
                    "SELECT COUNT(*) AS total FROM categoria_equipamento")) {
                categorias.next();
                System.out.println("Categorias cadastradas: " + categorias.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("Erro no teste do banco: " + e.getMessage());
            System.exit(1);
        }
    }
}

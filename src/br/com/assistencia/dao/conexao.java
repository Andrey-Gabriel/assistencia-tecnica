package br.com.assistencia.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexao {

    private static final String URL =
        "jdbc:mysql://127.0.0.1:3306/assistencia_tecnica";

    private static final String USUARIO = "assistencia";

    private static final String SENHA = System.getenv("MYSQL_SENHA");
//private static final String SENHA = "senha"; (você precisa configurar essa
//variável com a senha correta e reiniciar o NetBeans. Caso contrário, a conexão poderá falhar.)
    
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}

package br.com.assistencia.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class conexao {

    public static Connection conectar() throws SQLException {
        String url = System.getenv("MYSQL_URL");
        if (url == null || url.isBlank()) {
            url = "jdbc:mysql://127.0.0.1:3306/assistencia_tecnica";
        }

        String usuario = System.getenv("MYSQL_USUARIO");
        if (usuario == null || usuario.isBlank()) {
            usuario = "assistencia";
        }

        String senha = System.getenv("MYSQL_SENHA");
        if (senha == null) {
            throw new SQLException(
                "Configure MYSQL_SENHA com a senha do MySQL e reinicie o NetBeans. "
                + "Configure MYSQL_USUARIO para usar outro usuario (ex.: root)."
            );
        }

        Properties configuracao = new Properties();
        configuracao.setProperty("user", usuario);
        configuracao.setProperty("password", senha);
        configuracao.setProperty("connectTimeout", "5000");
        configuracao.setProperty("socketTimeout", "15000");
        return DriverManager.getConnection(url, configuracao);
    }
}

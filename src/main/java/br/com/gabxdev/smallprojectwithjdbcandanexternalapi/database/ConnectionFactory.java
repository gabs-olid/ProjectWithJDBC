package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/projectwithjdbc";
        String username = "root";
        String password = "root";

        return DriverManager.getConnection(url, username, password);
    }
}
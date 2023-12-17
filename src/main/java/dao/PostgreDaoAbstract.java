package dao;

import entities.Secret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public abstract class PostgreDaoAbstract implements Dao {

    private static final ResourceBundle resources = ResourceBundle.getBundle("constant");
    private final Secret secret = Secret.getInstance();

    @Override
    public abstract <T> T get();

    @Override
    public abstract  <T> T put(String string);

    protected String getConnectionString() {
        String host;
        String port;
        String database;
            host = secret.getValue("host");
            port = secret.getValue("port");
            database = "marathon";

        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }

    protected String getUser() {
            return secret.getValue("username");
    }

    protected String getPassword() {
            return secret.getValue("password");
    }


    public Connection getConnection() throws SQLException {
        String clazz = resources.getString("jdbcClass");
        try {
            Class.forName(clazz);
        } catch (ClassNotFoundException exception) {
        }
        String connectionString = getConnectionString();
        String userName = getUser();
        String password = getPassword();
        return DriverManager.getConnection(connectionString,
                userName, password);
    }
}

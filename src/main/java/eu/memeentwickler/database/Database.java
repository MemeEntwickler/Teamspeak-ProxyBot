package eu.memeentwickler.database;

import lombok.SneakyThrows;

import java.sql.*;

public class Database {

    private String host;
    private String database;
    private String user;
    private String password;
    private Connection connection;

    public Database(String host, String database, String user, String password) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.connect();
    }

    private void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://"
                            + this.host + ":3306/"
                            + this.database
                            + "?autoReconnect=true",
                    this.user, this.password);
            System.out.println("ProxyBot :: Die Verbindung zur Datenbank war erfolgreich.");
        } catch (SQLException e) {
            System.out.println("ProxyBot :: Die Verbindung zur Datenbank ist fehlgeschlagen: " + e.getErrorCode());
        }
    }

    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
                System.out.println("ProxyBot :: Die Verbindung zur Datenbank wurde erfolgreich beendet.");
            }
        } catch (SQLException e) {
            System.out.println("ProxyBot :: Die Verbindung zur Datenbank konnte nicht aufgelöst werden: " + e.getMessage());
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        if (resultSet != null)
            try {
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @SneakyThrows
    public void update(String qry) {
        Statement st = this.connection.createStatement();
        st.executeUpdate(qry);
        st.close();
    }

    @SneakyThrows
    public ResultSet query(String qry) {
        ResultSet rs;
        Statement st = this.connection.createStatement();
        rs = st.executeQuery(qry);
        return rs;
    }
}

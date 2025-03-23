package com.gastro.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Klasa zarządzająca połączeniem z bazą danych MySQL
 */
public class DatabaseConnector {
    private static DatabaseConnector instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    // Konstruktor prywatny - wzorzec Singleton
    private DatabaseConnector() {
        try {
            // Wczytywanie konfiguracji z pliku properties
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));

            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");

            // Alternatywnie można ustawić wartości bezpośrednio (nie zalecane dla produkcji)
            // this.url = "jdbc:mysql://localhost:3306/gastro_management";
            // this.username = "root";
            // this.password = "password";

        } catch (IOException e) {
            System.err.println("Nie można wczytać konfiguracji: " + e.getMessage());
            // Wartości domyślne w przypadku błędu
            this.url = "jdbc:mysql://localhost:3306/gastro_management";
            this.username = "root";
            this.password = "";
        }
    }

    /**
     * Metoda zwracająca instancję klasy DatabaseConnector (wzorzec Singleton)
     * @return instancja DatabaseConnector
     */
    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    /**
     * Metoda tworząca i zwracająca połączenie z bazą danych
     * @return połączenie z bazą danych
     * @throws SQLException w przypadku problemów z połączeniem
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Załadowanie sterownika JDBC (opcjonalne dla nowszych wersji Javy)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Utworzenie połączenia
                connection = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Nie znaleziono sterownika MySQL JDBC: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Metoda zamykająca połączenie z bazą danych
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Błąd podczas zamykania połączenia: " + e.getMessage());
            }
        }
    }
}
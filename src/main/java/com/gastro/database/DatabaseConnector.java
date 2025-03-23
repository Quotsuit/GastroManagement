package com.gastro.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

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
        loadConfiguration();
    }

    /**
     * Wczytuje konfigurację połączenia z pliku lub ustawia wartości domyślne
     */
    private void loadConfiguration() {
        // Ścieżki, gdzie może znajdować się plik konfiguracyjny
        String[] configPaths = {
                "config.properties",
                "src/main/resources/config.properties",
                System.getProperty("user.dir") + "/config.properties"
        };

        boolean configLoaded = false;

        // Próba wczytania pliku z różnych lokalizacji
        for (String path : configPaths) {
            File configFile = new File(path);
            if (configFile.exists()) {
                try {
                    Properties props = new Properties();
                    props.load(new FileInputStream(configFile));

                    this.url = props.getProperty("db.url");
                    this.username = props.getProperty("db.username");
                    this.password = props.getProperty("db.password");

                    System.out.println("Wczytano konfigurację z: " + path);
                    configLoaded = true;
                    break;
                } catch (IOException e) {
                    System.err.println("Błąd podczas wczytywania " + path + ": " + e.getMessage());
                }
            }
        }

        // Jeśli nie udało się wczytać konfiguracji, używamy wartości twardych
        if (!configLoaded) {
            System.err.println("Nie znaleziono pliku config.properties. Używam wartości domyślnych.");
            this.url = "jdbc:mysql://localhost:3306/gastro_management?useSSL=false&serverTimezone=UTC";
            this.username = "root";
            this.password = ""; // puste hasło - prawdopodobnie to jest przyczyną błędu

            // Wypisz instrukcję dla użytkownika
            System.out.println("Utwórz plik config.properties w katalogu głównym projektu z następującą zawartością:");
            System.out.println("db.url=jdbc:mysql://localhost:3306/gastro_management?useSSL=false&serverTimezone=UTC");
            System.out.println("db.username=root");
            System.out.println("db.password=twoje_haslo");
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

                // Wyświetlenie informacji o próbie połączenia (bez hasła)
                System.out.println("Próba połączenia z bazą danych: " + url);
                System.out.println("Użytkownik: " + username);

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
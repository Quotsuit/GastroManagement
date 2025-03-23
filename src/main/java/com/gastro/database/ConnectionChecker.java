package com.gastro.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Prosta klasa do testowania połączenia z bazą danych
 */
public class ConnectionChecker {

    public static void main(String[] args) {
        System.out.println("Test połączenia z bazą danych MySQL");
        System.out.println("===================================");

        // Jeśli config.properties nie istnieje, poproś użytkownika o dane
        Scanner scanner = new Scanner(System.in);

        System.out.println("Czy chcesz ręcznie wprowadzić dane połączenia? (T/N)");
        String answer = scanner.nextLine().trim().toUpperCase();

        if (answer.equals("T")) {
            System.out.print("Podaj URL bazy danych (np. jdbc:mysql://localhost:3306/gastro_management): ");
            String url = scanner.nextLine();

            System.out.print("Podaj nazwę użytkownika: ");
            String username = scanner.nextLine();

            System.out.print("Podaj hasło: ");
            String password = scanner.nextLine();

            // Próba nawiązania połączenia z podanymi parametrami
            testConnectionWithParams(url, username, password);
        } else {
            // Próba standardowego połączenia
            testDefaultConnection();
        }

        scanner.close();
    }

    private static void testDefaultConnection() {
        System.out.println("Próba nawiązania połączenia z bazą danych z użyciem DatabaseConnector...");

        try {
            Connection conn = DatabaseConnector.getInstance().getConnection();
            System.out.println("Połączenie nawiązane pomyślnie!");

            // Wypisz szczegóły połączenia
            System.out.println("Informacje o połączeniu:");
            System.out.println("URL: " + conn.getMetaData().getURL());
            System.out.println("Użytkownik: " + conn.getMetaData().getUserName());
            System.out.println("Wersja serwera: " + conn.getMetaData().getDatabaseProductVersion());

            // Zamknij połączenie
            conn.close();
            System.out.println("Połączenie zamknięte.");
        } catch (SQLException e) {
            System.err.println("Błąd podczas połączenia z bazą danych:");
            System.err.println(e.getMessage());
            System.err.println("Kod błędu: " + e.getErrorCode());

            // Dodatkowe wskazówki w zależności od kodu błędu
            if (e.getMessage().contains("Access denied")) {
                System.err.println("\nPrawdopodobnie użyto nieprawidłowej nazwy użytkownika lub hasła.");
                System.err.println("Sprawdź plik config.properties i upewnij się, że dane są poprawne.");
            } else if (e.getMessage().contains("Unknown database")) {
                System.err.println("\nBaza danych 'gastro_management' nie istnieje.");
                System.err.println("Uruchom skrypt SQL, aby utworzyć bazę danych.");
            } else if (e.getMessage().contains("Communications link failure")) {
                System.err.println("\nNie można nawiązać połączenia z serwerem MySQL.");
                System.err.println("Upewnij się, że serwer MySQL jest uruchomiony.");
            }
        }
    }

    private static void testConnectionWithParams(String url, String username, String password) {
        System.out.println("Próba nawiązania połączenia z bazą danych z podanymi parametrami...");

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Połączenie nawiązane pomyślnie!");

            // Wypisz szczegóły połączenia
            System.out.println("Informacje o połączeniu:");
            System.out.println("URL: " + conn.getMetaData().getURL());
            System.out.println("Użytkownik: " + conn.getMetaData().getUserName());
            System.out.println("Wersja serwera: " + conn.getMetaData().getDatabaseProductVersion());

            // Sugestia zapisania konfiguracji
            System.out.println("\nPodane parametry działają poprawnie. Zapisz je w pliku config.properties:");
            System.out.println("db.url=" + url);
            System.out.println("db.username=" + username);
            System.out.println("db.password=" + password);

            // Zamknij połączenie
            conn.close();
            System.out.println("Połączenie zamknięte.");
        } catch (SQLException e) {
            System.err.println("Błąd podczas połączenia z bazą danych:");
            System.err.println(e.getMessage());
        }
    }
}
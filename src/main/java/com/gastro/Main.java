package com.gastro;

import com.gastro.database.DatabaseConnector;
import com.gastro.inventory.InventoryManager;
import com.gastro.inventory.Product;
import com.gastro.finance.FinancialReportManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Test połączenia z bazą danych
        try {
            Connection conn = DatabaseConnector.getInstance().getConnection();
            System.out.println("Połączenie z bazą danych nawiązane pomyślnie!");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Błąd podczas łączenia z bazą danych: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Test menedżera magazynu
        InventoryManager inventoryManager = new InventoryManager();
        List<Product> products = inventoryManager.getAllProducts();
        System.out.println("Liczba produktów w magazynie: " + products.size());

        // Test menedżera raportów finansowych
        FinancialReportManager financialReportManager = new FinancialReportManager();
        // Tutaj można dodać testy funkcji raportowania

        System.out.println("Testy zakończone pomyślnie!");
    }
}
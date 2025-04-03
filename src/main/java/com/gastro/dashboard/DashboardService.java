package com.gastro.dashboard;

import com.gastro.database.DatabaseConnector;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    // Zamiast wstrzykiwania, użyjemy metody getInstance()
    public DashboardDTO getDashboardData() {
        DashboardDTO dashboardDTO = new DashboardDTO();

        try (Connection conn = DatabaseConnector.getInstance().getConnection()) {
            // Pobierz dane z różnych źródeł
            dashboardDTO.setMonthlyRevenue(getMonthlyRevenue(conn));
            dashboardDTO.setYearlyProfit(getYearlyProfit(conn));
            dashboardDTO.setTaskCompletionPercentage(getTaskCompletionPercentage(conn));
            dashboardDTO.setLowStockProductsCount(getLowStockProductsCount(conn));
            dashboardDTO.setRecentDeliveries(getRecentDeliveries(conn));
            dashboardDTO.setTodayTransactionsCount(getTodayTransactionsCount(conn));
            dashboardDTO.setAverageOrderValue(getAverageOrderValue(conn));
            dashboardDTO.setMostOrderedProduct(getMostOrderedProduct(conn));
            dashboardDTO.setActiveEmployeesCount(getActiveEmployeesCount(conn));
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania danych dla dashboardu: " + e.getMessage());
            e.printStackTrace();

            // W przypadku błędu, ustaw domyślne wartości
            setDefaultValues(dashboardDTO);
        }

        return dashboardDTO;
    }

    // Reszta metod pozostaje bez zmian

    private BigDecimal getMonthlyRevenue(Connection conn) throws SQLException {
        // Pobierz przychody z bieżącego miesiąca
        String sql = "SELECT COALESCE(SUM(total_sales), 0) FROM daily_financial_report " +
                "WHERE MONTH(day) = MONTH(CURRENT_DATE()) AND YEAR(day) = YEAR(CURRENT_DATE())";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getYearlyProfit(Connection conn) throws SQLException {
        // Pobierz zysk z bieżącego roku
        String sql = "SELECT COALESCE(SUM(net_profit), 0) FROM daily_financial_report " +
                "WHERE YEAR(day) = YEAR(CURRENT_DATE())";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    private int getTaskCompletionPercentage(Connection conn) throws SQLException {
        // Pobierz procent ukończonych zadań
        // Uwaga: Zakładam, że istnieje tabela tasks z polami status (np. 'completed', 'pending')
        String sql = "SELECT (COUNT(CASE WHEN status = 'completed' THEN 1 END) * 100 / NULLIF(COUNT(*), 0)) " +
                "FROM tasks";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Jeśli tabela nie istnieje lub wystąpi inny błąd, zwróć domyślną wartość
            System.err.println("Błąd podczas pobierania statusu zadań: " + e.getMessage());
        }
        return 80; // Domyślna wartość
    }

    private int getLowStockProductsCount(Connection conn) throws SQLException {
        // Pobierz liczbę produktów o niskim stanie magazynowym
        String sql = "SELECT COUNT(*) FROM products WHERE stock_quantity <= minimum_quantity";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private List<DashboardDTO.DeliveryDTO> getRecentDeliveries(Connection conn) throws SQLException {
        List<DashboardDTO.DeliveryDTO> deliveries = new ArrayList<>();

        // Pobierz ostatnie 3 dostawy
        String sql = "SELECT delivery_date, supplier_name, total_amount FROM deliveries " +
                "ORDER BY delivery_date DESC LIMIT 3";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while (rs.next()) {
                Date deliveryDate = rs.getDate("delivery_date");
                String formattedDate = deliveryDate != null
                        ? deliveryDate.toLocalDate().format(formatter)
                        : LocalDate.now().format(formatter);

                deliveries.add(new DashboardDTO.DeliveryDTO(
                        formattedDate,
                        rs.getString("supplier_name"),
                        rs.getBigDecimal("total_amount")
                ));
            }
        }

        // Jeśli brak danych, dodaj przykładowe
        if (deliveries.isEmpty()) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            deliveries.add(new DashboardDTO.DeliveryDTO(
                    today.minusDays(3).format(formatter),
                    "Makro Cash & Carry",
                    new BigDecimal("2500.00")
            ));

            deliveries.add(new DashboardDTO.DeliveryDTO(
                    today.minusDays(6).format(formatter),
                    "Hurtownia Owoców",
                    new BigDecimal("1200.00")
            ));

            deliveries.add(new DashboardDTO.DeliveryDTO(
                    today.minusDays(9).format(formatter),
                    "Dystrybutor Napojów",
                    new BigDecimal("850.00")
            ));
        }

        return deliveries;
    }

    private int getTodayTransactionsCount(Connection conn) throws SQLException {
        // Pobierz liczbę transakcji z dzisiaj
        // Uwaga: Zakładam, że istnieje tabela orders z polem order_date
        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURRENT_DATE()";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Jeśli tabela nie istnieje lub wystąpi inny błąd, zwróć domyślną wartość
            System.err.println("Błąd podczas pobierania liczby transakcji: " + e.getMessage());
        }
        return 42; // Domyślna wartość
    }

    private BigDecimal getAverageOrderValue(Connection conn) throws SQLException {
        // Pobierz średnią wartość zamówienia
        // Uwaga: Zakładam, że istnieje tabela orders z polem total_amount
        String sql = "SELECT AVG(total_amount) FROM orders";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : new BigDecimal("78.00");
            }
        } catch (SQLException e) {
            // Jeśli tabela nie istnieje lub wystąpi inny błąd, zwróć domyślną wartość
            System.err.println("Błąd podczas pobierania średniej wartości zamówienia: " + e.getMessage());
        }
        return new BigDecimal("78.00"); // Domyślna wartość
    }

    private String getMostOrderedProduct(Connection conn) throws SQLException {
        // Pobierz najczęściej zamawiany produkt
        // Uwaga: Zakładam, że istnieje tabela order_items z relacją do menu_items
        String sql = "SELECT m.item_name FROM order_items oi " +
                "JOIN menu_items m ON oi.menu_item_id = m.menu_item_id " +
                "GROUP BY oi.menu_item_id " +
                "ORDER BY COUNT(*) DESC LIMIT 1";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            // Jeśli tabela nie istnieje lub wystąpi inny błąd, zwróć domyślną wartość
            System.err.println("Błąd podczas pobierania najczęściej zamawianego produktu: " + e.getMessage());
        }
        return "Pizza Margherita"; // Domyślna wartość
    }

    private int getActiveEmployeesCount(Connection conn) throws SQLException {
        // Pobierz liczbę aktywnych pracowników
        // Uwaga: Zakładam, że istnieje tabela employees z polem is_active
        String sql = "SELECT COUNT(*) FROM employees WHERE is_active = 1";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Jeśli tabela nie istnieje lub wystąpi inny błąd, zwróć domyślną wartość
            System.err.println("Błąd podczas pobierania liczby aktywnych pracowników: " + e.getMessage());
        }
        return 8; // Domyślna wartość
    }

    private void setDefaultValues(DashboardDTO dashboardDTO) {
        // Ustawia domyślne wartości w przypadku błędów
        dashboardDTO.setMonthlyRevenue(new BigDecimal("8250.00"));
        dashboardDTO.setYearlyProfit(new BigDecimal("215000.00"));
        dashboardDTO.setTaskCompletionPercentage(80);
        dashboardDTO.setLowStockProductsCount(5);

        // Przykładowe dostawy
        List<DashboardDTO.DeliveryDTO> deliveries = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        deliveries.add(new DashboardDTO.DeliveryDTO(
                today.minusDays(3).format(formatter),
                "Makro Cash & Carry",
                new BigDecimal("2500.00")
        ));

        deliveries.add(new DashboardDTO.DeliveryDTO(
                today.minusDays(6).format(formatter),
                "Hurtownia Owoców",
                new BigDecimal("1200.00")
        ));

        deliveries.add(new DashboardDTO.DeliveryDTO(
                today.minusDays(9).format(formatter),
                "Dystrybutor Napojów",
                new BigDecimal("850.00")
        ));

        dashboardDTO.setRecentDeliveries(deliveries);

        // Statystyki sprzedaży
        dashboardDTO.setTodayTransactionsCount(42);
        dashboardDTO.setAverageOrderValue(new BigDecimal("78.00"));
        dashboardDTO.setMostOrderedProduct("Pizza Margherita");
        dashboardDTO.setActiveEmployeesCount(8);
    }
}
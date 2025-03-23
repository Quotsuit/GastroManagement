package com.gastro.finance;

import com.gastro.database.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Klasa odpowiedzialna za generowanie raportów finansowych
 */
public class FinancialReportManager {
    private DatabaseConnector dbConnector;

    public FinancialReportManager() {
        this.dbConnector = DatabaseConnector.getInstance();
    }

    /**
     * Pobiera raport finansowy dzienny dla określonego dnia
     * @param date data w formacie 'YYYY-MM-DD'
     * @return obiekt raportu finansowego lub null w przypadku błędu
     */
    public DailyFinancialReport getDailyReport(String date) {
        String sql = "SELECT * FROM daily_financial_report WHERE day = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DailyFinancialReport report = new DailyFinancialReport();
                    report.setDay(rs.getDate("day"));
                    report.setTotalOrders(rs.getInt("total_orders"));
                    report.setTotalSales(rs.getBigDecimal("total_sales"));
                    report.setTotalExpenses(rs.getBigDecimal("total_expenses"));
                    report.setNetProfit(rs.getBigDecimal("net_profit"));
                    return report;
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania raportu dziennego: " + e.getMessage());
        }

        return null;
    }

    /**
     * Pobiera raport finansowy miesięczny dla określonego miesiąca i roku
     * @param year rok
     * @param month miesiąc (1-12)
     * @return obiekt raportu finansowego lub null w przypadku błędu
     */
    public MonthlyFinancialReport getMonthlyReport(int year, int month) {
        String sql = "SELECT * FROM monthly_financial_report WHERE year = ? AND month = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MonthlyFinancialReport report = new MonthlyFinancialReport();
                    report.setYear(rs.getInt("year"));
                    report.setMonth(rs.getInt("month"));
                    report.setTotalOrders(rs.getInt("total_orders"));
                    report.setTotalSales(rs.getBigDecimal("total_sales"));
                    report.setTotalExpenses(rs.getBigDecimal("total_expenses"));
                    report.setNetProfit(rs.getBigDecimal("net_profit"));
                    return report;
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania raportu miesięcznego: " + e.getMessage());
        }

        return null;
    }

    /**
     * Pobiera raporty finansowe dzienne dla określonego zakresu dat
     * @param startDate data początkowa w formacie 'YYYY-MM-DD'
     * @param endDate data końcowa w formacie 'YYYY-MM-DD'
     * @return lista raportów dziennych
     */
    public List<DailyFinancialReport> getDailyReportRange(String startDate, String endDate) {
        List<DailyFinancialReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM daily_financial_report WHERE day BETWEEN ? AND ? ORDER BY day";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DailyFinancialReport report = new DailyFinancialReport();
                    report.setDay(rs.getDate("day"));
                    report.setTotalOrders(rs.getInt("total_orders"));
                    report.setTotalSales(rs.getBigDecimal("total_sales"));
                    report.setTotalExpenses(rs.getBigDecimal("total_expenses"));
                    report.setNetProfit(rs.getBigDecimal("net_profit"));
                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania zakresu raportów dziennych: " + e.getMessage());
        }

        return reports;
    }

    /**
     * Pobiera szczegółowy raport sprzedaży dla określonego dnia
     * @param date data w formacie 'YYYY-MM-DD'
     * @return lista pozycji sprzedaży
     */
    public List<SalesItem> getDailySalesDetails(String date) {
        List<SalesItem> salesItems = new ArrayList<>();
        String sql = "SELECT m.item_name, SUM(oi.quantity) AS total_quantity, " +
                "SUM(oi.quantity * oi.unit_price) AS total_amount " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN menu_items m ON oi.menu_item_id = m.menu_item_id " +
                "WHERE DATE(o.order_date) = ? " +
                "GROUP BY m.menu_item_id " +
                "ORDER BY total_amount DESC";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SalesItem item = new SalesItem();
                    item.setItemName(rs.getString("item_name"));
                    item.setQuantity(rs.getInt("total_quantity"));
                    item.setAmount(rs.getBigDecimal("total_amount"));
                    salesItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania szczegółów sprzedaży: " + e.getMessage());
        }

        return salesItems;
    }

    /**
     * Pobiera szczegółowy raport wydatków dla określonego dnia
     * @param date data w formacie 'YYYY-MM-DD'
     * @return lista pozycji wydatków
     */
    public List<ExpenseItem> getDailyExpensesDetails(String date) {
        List<ExpenseItem> expenseItems = new ArrayList<>();
        String sql = "SELECT category, SUM(amount) AS total_amount, COUNT(*) AS count " +
                "FROM expenses " +
                "WHERE DATE(expense_date) = ? " +
                "GROUP BY category " +
                "ORDER BY total_amount DESC";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ExpenseItem item = new ExpenseItem();
                    item.setCategory(rs.getString("category"));
                    item.setAmount(rs.getBigDecimal("total_amount"));
                    item.setCount(rs.getInt("count"));
                    expenseItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania szczegółów wydatków: " + e.getMessage());
        }

        return expenseItems;
    }

    /**
     * Dodaje nowy wydatek
     * @param expense obiekt wydatku
     * @return ID dodanego wydatku lub -1 w przypadku błędu
     */
    public int addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (expense_date, category, amount, description, " +
                "payment_method, employee_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, new java.sql.Date(expense.getExpenseDate().getTime()));
            pstmt.setString(2, expense.getCategory());
            pstmt.setBigDecimal(3, expense.getAmount());
            pstmt.setString(4, expense.getDescription());
            pstmt.setString(5, expense.getPaymentMethod());
            pstmt.setInt(6, expense.getEmployeeId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania wydatku: " + e.getMessage());
        }

        return -1;
    }
}
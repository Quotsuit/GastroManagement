package com.gastro.inventory;

import com.gastro.database.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa odpowiedzialna za zarządzanie zapasami produktów
 */
public class InventoryManager {
    private DatabaseConnector dbConnector;

    public InventoryManager() {
        this.dbConnector = DatabaseConnector.getInstance();
    }

    /**
     * Pobiera wszystkie produkty z bazy danych
     * @return lista produktów
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                "LEFT JOIN product_categories c ON p.category_id = c.category_id " +
                "ORDER BY p.product_name";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(rs.getString("category_name"));
                product.setProductName(rs.getString("product_name"));
                product.setDescription(rs.getString("description"));
                product.setUnit(rs.getString("unit"));
                product.setPurchasePrice(rs.getBigDecimal("purchase_price"));
                product.setStockQuantity(rs.getBigDecimal("stock_quantity"));
                product.setMinimumQuantity(rs.getBigDecimal("minimum_quantity"));
                product.setExpiryDate(rs.getDate("expiry_date"));

                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania produktów: " + e.getMessage());
        }

        return products;
    }

    /**
     * Pobiera produkty o niskim stanie zapasów
     * @return lista produktów o niskim stanie zapasów
     */
    public List<Product> getLowStockProducts() {
        List<Product> lowStockProducts = new ArrayList<>();
        String sql = "SELECT * FROM low_stock_alerts";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setStockQuantity(rs.getBigDecimal("stock_quantity"));
                product.setMinimumQuantity(rs.getBigDecimal("minimum_quantity"));
                product.setUnit(rs.getString("unit"));

                lowStockProducts.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania produktów o niskim stanie: " + e.getMessage());
        }

        return lowStockProducts;
    }

    /**
     * Dodaje nowy produkt do bazy danych
     * @param product produkt do dodania
     * @return ID dodanego produktu lub -1 w przypadku błędu
     */
    public int addProduct(Product product) {
        String sql = "INSERT INTO products (category_id, product_name, description, unit, " +
                "purchase_price, stock_quantity, minimum_quantity, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setObject(1, product.getCategoryId(), Types.INTEGER);
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getUnit());
            pstmt.setBigDecimal(5, product.getPurchasePrice());
            pstmt.setBigDecimal(6, product.getStockQuantity());
            pstmt.setBigDecimal(7, product.getMinimumQuantity());
            pstmt.setDate(8, product.getExpiryDate() != null ? new java.sql.Date(product.getExpiryDate().getTime()) : null);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania produktu: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Aktualizuje istniejący produkt w bazie danych
     * @param product produkt do aktualizacji
     * @return true jeśli aktualizacja się powiodła, false w przeciwnym razie
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET category_id = ?, product_name = ?, description = ?, " +
                "unit = ?, purchase_price = ?, stock_quantity = ?, minimum_quantity = ?, " +
                "expiry_date = ? WHERE product_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, product.getCategoryId(), Types.INTEGER);
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getUnit());
            pstmt.setBigDecimal(5, product.getPurchasePrice());
            pstmt.setBigDecimal(6, product.getStockQuantity());
            pstmt.setBigDecimal(7, product.getMinimumQuantity());
            pstmt.setDate(8, product.getExpiryDate() != null ? new java.sql.Date(product.getExpiryDate().getTime()) : null);
            pstmt.setInt(9, product.getProductId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas aktualizacji produktu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Aktualizuje stan magazynowy produktu
     * @param productId ID produktu
     * @param newQuantity nowa ilość
     * @return true jeśli aktualizacja się powiodła, false w przeciwnym razie
     */
    public boolean updateProductStock(int productId, double newQuantity) {
        String sql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newQuantity);
            pstmt.setInt(2, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas aktualizacji stanu magazynowego: " + e.getMessage());
            return false;
        }
    }

    /**
     * Usuwa produkt z bazy danych
     * @param productId ID produktu do usunięcia
     * @return true jeśli usunięcie się powiodło, false w przeciwnym razie
     */
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas usuwania produktu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tworzy nowy proces inwentaryzacji
     * @param employeeId ID pracownika przeprowadzającego inwentaryzację
     * @return ID utworzonej inwentaryzacji lub -1 w przypadku błędu
     */
    public int createInventoryCount(int employeeId, String notes) {
        String sql = "INSERT INTO inventory_counts (count_date, employee_id, notes, status) " +
                "VALUES (CURDATE(), ?, ?, 'W trakcie')";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, employeeId);
            pstmt.setString(2, notes);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int countId = generatedKeys.getInt(1);

                        // Automatycznie dodaj wszystkie produkty do inwentaryzacji
                        String itemsSql = "INSERT INTO inventory_count_items (count_id, product_id, expected_quantity, actual_quantity, notes) " +
                                "SELECT ?, product_id, stock_quantity, stock_quantity, '' FROM products";

                        try (PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
                            itemsStmt.setInt(1, countId);
                            itemsStmt.executeUpdate();
                        }

                        return countId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas tworzenia inwentaryzacji: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Aktualizuje faktyczną ilość produktu podczas inwentaryzacji
     * @param countId ID inwentaryzacji
     * @param productId ID produktu
     * @param actualQuantity faktyczna ilość
     * @return true jeśli aktualizacja się powiodła, false w przeciwnym razie
     */
    public boolean updateInventoryCountItem(int countId, int productId, double actualQuantity, String notes) {
        String sql = "UPDATE inventory_count_items SET actual_quantity = ?, notes = ? " +
                "WHERE count_id = ? AND product_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, actualQuantity);
            pstmt.setString(2, notes);
            pstmt.setInt(3, countId);
            pstmt.setInt(4, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas aktualizacji pozycji inwentaryzacji: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finalizuje proces inwentaryzacji i aktualizuje rzeczywiste stany magazynowe
     * @param countId ID inwentaryzacji
     * @return true jeśli finalizacja się powiodła, false w przeciwnym razie
     */
    public boolean finalizeInventoryCount(int countId) {
        Connection conn = null;
        try {
            conn = dbConnector.getConnection();
            conn.setAutoCommit(false);

            // Aktualizuj stany magazynowe na podstawie inwentaryzacji
            String updateStockSql = "UPDATE products p " +
                    "JOIN inventory_count_items i ON p.product_id = i.product_id " +
                    "SET p.stock_quantity = i.actual_quantity " +
                    "WHERE i.count_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(updateStockSql)) {
                pstmt.setInt(1, countId);
                pstmt.executeUpdate();
            }

            // Oznacz inwentaryzację jako zakończoną
            String updateCountSql = "UPDATE inventory_counts SET status = 'Zakończona' WHERE count_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(updateCountSql)) {
                pstmt.setInt(1, countId);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Błąd podczas finalizacji inwentaryzacji: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Błąd podczas cofania transakcji: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Błąd podczas ustawiania auto-commit: " + e.getMessage());
                }
            }
        }
    }
}
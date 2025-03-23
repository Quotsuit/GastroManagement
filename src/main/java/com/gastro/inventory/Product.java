package com.gastro.inventory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca produkt w magazynie
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "purchase_price", nullable = false)
    private BigDecimal purchasePrice;

    @Column(name = "stock_quantity")
    private BigDecimal stockQuantity;

    @Column(name = "minimum_quantity")
    private BigDecimal minimumQuantity;

    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    // Konstruktor domyślny
    public Product() {
    }

    // Konstruktor z parametrami
    public Product(ProductCategory category, String productName, String description, String unit,
                   BigDecimal purchasePrice, BigDecimal stockQuantity, BigDecimal minimumQuantity,
                   Date expiryDate) {
        this.category = category;
        this.productName = productName;
        this.description = description;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.stockQuantity = stockQuantity;
        this.minimumQuantity = minimumQuantity;
        this.expiryDate = expiryDate;
    }

    // Gettery i settery
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    // Metody pomocnicze dla kompatybilności ze starszym kodem
    public Integer getCategoryId() {
        return category != null ? category.getCategoryId() : null;
    }

    public void setCategoryId(Integer categoryId) {
        // Ta metoda zostaje dla kompatybilności, ale rzeczywiste ustawienie
        // kategorii powinno odbywać się przez setCategory()
    }

    public String getCategoryName() {
        return category != null ? category.getCategoryName() : null;
    }

    // Dodajemy brakującą metodę setCategoryName
    public void setCategoryName(String categoryName) {
        // Ta metoda jest tylko dla kompatybilności
        // W rzeczywistości kategoria powinna być ustawiana przez setCategory()
        // Możesz dodać logikę wyszukiwania kategorii jeśli potrzebujesz, ale to powinno być obsługiwane w serwisie
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(BigDecimal minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Sprawdza, czy stan magazynowy jest niski (poniżej minimum)
     * @return true jeśli stan jest niski, false w przeciwnym razie
     */
    public boolean isLowStock() {
        return stockQuantity != null && minimumQuantity != null &&
                stockQuantity.compareTo(minimumQuantity) <= 0;
    }

    /**
     * Oblicza wartość zapasów (ilość * cena zakupu)
     * @return wartość zapasów
     */
    public BigDecimal getStockValue() {
        return stockQuantity != null && purchasePrice != null ?
                stockQuantity.multiply(purchasePrice) : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unit='" + unit + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", minimumQuantity=" + minimumQuantity +
                '}';
    }
}
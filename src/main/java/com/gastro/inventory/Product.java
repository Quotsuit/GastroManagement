package com.gastro.inventory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca produkt w magazynie
 */
public class Product {
    private int productId;
    private Integer categoryId;
    private String categoryName;
    private String productName;
    private String description;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal stockQuantity;
    private BigDecimal minimumQuantity;
    private Date expiryDate;

    // Konstruktor domyślny
    public Product() {
    }

    // Konstruktor z parametrami
    public Product(Integer categoryId, String productName, String description, String unit,
                   BigDecimal purchasePrice, BigDecimal stockQuantity, BigDecimal minimumQuantity,
                   Date expiryDate) {
        this.categoryId = categoryId;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
        return stockQuantity.compareTo(minimumQuantity) <= 0;
    }

    /**
     * Oblicza wartość zapasów (ilość * cena zakupu)
     * @return wartość zapasów
     */
    public BigDecimal getStockValue() {
        return stockQuantity.multiply(purchasePrice);
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
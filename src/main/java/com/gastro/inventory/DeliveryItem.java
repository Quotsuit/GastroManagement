package com.gastro.inventory;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Klasa reprezentująca pozycję dostawy (pojedynczy produkt w dostawie)
 */
@Entity
@Table(name = "delivery_items")
public class DeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private int itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "already_added_to_stock", nullable = false)
    private boolean alreadyAddedToStock;

    // Konstruktor domyślny
    public DeliveryItem() {
        this.alreadyAddedToStock = false;
        this.quantity = BigDecimal.ZERO;
        this.unitPrice = BigDecimal.ZERO;
    }

    // Konstruktor z parametrami
    public DeliveryItem(Product product, BigDecimal quantity, BigDecimal unitPrice) {
        this();
        this.product = product;
        this.quantity = quantity != null ? quantity : BigDecimal.ZERO;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }

    // Konstruktor z parametrami - pełny
    public DeliveryItem(Delivery delivery, Product product, BigDecimal quantity, BigDecimal unitPrice, String notes) {
        this(product, quantity, unitPrice);
        this.delivery = delivery;
        this.notes = notes;
    }

    // Gettery i settery
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity != null ? quantity : BigDecimal.ZERO;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAlreadyAddedToStock() {
        return alreadyAddedToStock;
    }

    public void setAlreadyAddedToStock(boolean alreadyAddedToStock) {
        this.alreadyAddedToStock = alreadyAddedToStock;
    }

    /**
     * Oblicza łączną wartość pozycji (quantity * unitPrice)
     * @return łączna wartość pozycji
     */
    public BigDecimal getTotalValue() {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(unitPrice);
    }

    @Override
    public String toString() {
        return "DeliveryItem{" +
                "itemId=" + itemId +
                ", product=" + (product != null ? product.getProductName() : "null") +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", alreadyAddedToStock=" + alreadyAddedToStock +
                '}';
    }
}
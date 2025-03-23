package com.gastro.inventory;

import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Klasa reprezentująca dostawę produktów
 */
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private int deliveryId;

    @Column(name = "delivery_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeliveryItem> deliveryItems = new ArrayList<>();

    /**
     * Status dostawy
     */
    public enum DeliveryStatus {
        PLANOWANA("Planowana"),
        W_TRAKCIE("W trakcie"),
        ZAKONCZONA("Zakończona");

        private final String displayName;

        DeliveryStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Konstruktor domyślny
    public Delivery() {
        this.deliveryDate = new Date();
        this.status = DeliveryStatus.PLANOWANA;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.totalAmount = BigDecimal.ZERO;
    }

    // Konstruktor z parametrami
    public Delivery(Date deliveryDate, String supplierName, String invoiceNumber, String notes) {
        this();
        this.deliveryDate = deliveryDate != null ? deliveryDate : new Date();
        this.supplierName = supplierName;
        this.invoiceNumber = invoiceNumber;
        this.notes = notes;
    }

    // Gettery i settery
    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate != null ? deliveryDate : new Date();
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status != null ? status : DeliveryStatus.PLANOWANA;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt != null ? createdAt : new Date();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt != null ? updatedAt : new Date();
    }

    public List<DeliveryItem> getDeliveryItems() {
        return deliveryItems;
    }

    public void setDeliveryItems(List<DeliveryItem> deliveryItems) {
        this.deliveryItems = deliveryItems != null ? deliveryItems : new ArrayList<>();
    }

    /**
     * Dodaje nową pozycję dostawy
     * @param item pozycja dostawy
     */
    public void addDeliveryItem(DeliveryItem item) {
        if (item == null) return;

        deliveryItems.add(item);
        item.setDelivery(this);
        recalculateTotalAmount();
    }

    /**
     * Usuwa pozycję dostawy
     * @param item pozycja dostawy
     */
    public void removeDeliveryItem(DeliveryItem item) {
        if (item == null) return;

        deliveryItems.remove(item);
        item.setDelivery(null);
        recalculateTotalAmount();
    }

    /**
     * Przelicza łączną wartość dostawy
     */
    public void recalculateTotalAmount() {
        // Resetujemy sumę przed przeliczeniem
        this.totalAmount = BigDecimal.ZERO;

        // Sumujemy wartości wszystkich pozycji
        if (deliveryItems != null && !deliveryItems.isEmpty()) {
            this.totalAmount = deliveryItems.stream()
                    .map(item -> {
                        if (item.getQuantity() != null && item.getUnitPrice() != null) {
                            return item.getQuantity().multiply(item.getUnitPrice());
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    /**
     * Zmienia status dostawy na "W trakcie"
     */
    public void startDelivery() {
        this.status = DeliveryStatus.W_TRAKCIE;
        this.updatedAt = new Date();
    }

    /**
     * Zmienia status dostawy na "Zakończona"
     */
    public void completeDelivery() {
        this.status = DeliveryStatus.ZAKONCZONA;
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
        if (this.updatedAt == null) {
            this.updatedAt = new Date();
        }
        if (this.status == null) {
            this.status = DeliveryStatus.PLANOWANA;
        }
        if (this.totalAmount == null) {
            this.totalAmount = BigDecimal.ZERO;
        }
        if (this.deliveryDate == null) {
            this.deliveryDate = new Date();
        }
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId=" + deliveryId +
                ", deliveryDate=" + deliveryDate +
                ", supplierName='" + supplierName + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
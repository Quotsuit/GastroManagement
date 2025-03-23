package com.gastro.finance;

import java.math.BigDecimal;

/**
 * Klasa reprezentująca pozycję sprzedaży w raporcie
 */
public class SalesItem {
    private String itemName;
    private int quantity;
    private BigDecimal amount;

    // Konstruktor domyślny
    public SalesItem() {
    }

    // Konstruktor z parametrami
    public SalesItem(String itemName, int quantity, BigDecimal amount) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.amount = amount;
    }

    // Gettery i settery
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "SalesItem{" +
                "itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                '}';
    }
}
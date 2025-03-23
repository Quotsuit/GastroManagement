package com.gastro.finance;

import java.math.BigDecimal;

/**
 * Klasa reprezentująca pozycję wydatku w raporcie
 */
public class ExpenseItem {
    private String category;
    private BigDecimal amount;
    private int count;

    // Konstruktor domyślny
    public ExpenseItem() {
    }

    // Konstruktor z parametrami
    public ExpenseItem(String category, BigDecimal amount, int count) {
        this.category = category;
        this.amount = amount;
        this.count = count;
    }

    // Gettery i settery
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ExpenseItem{" +
                "category='" + category + '\'' +
                ", amount=" + amount +
                ", count=" + count +
                '}';
    }
}
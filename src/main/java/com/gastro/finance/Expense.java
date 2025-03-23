package com.gastro.finance;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca wydatek
 */
public class Expense {
    private int expenseId;
    private Date expenseDate;
    private String category;
    private BigDecimal amount;
    private String description;
    private String paymentMethod;
    private int employeeId;

    // Konstruktor domyślny
    public Expense() {
    }

    // Konstruktor z parametrami
    public Expense(Date expenseDate, String category, BigDecimal amount, String description,
                   String paymentMethod, int employeeId) {
        this.expenseDate = expenseDate;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.employeeId = employeeId;
    }

    // Gettery i settery
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", expenseDate=" + expenseDate +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", employeeId=" + employeeId +
                '}';
    }
}
package com.gastro.finance;

import java.math.BigDecimal;

/**
 * Klasa reprezentująca miesięczny raport finansowy
 */
public class MonthlyFinancialReport {
    private int year;
    private int month;
    private int totalOrders;
    private BigDecimal totalSales;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;

    // Konstruktor domyślny
    public MonthlyFinancialReport() {
    }

    // Konstruktor z parametrami
    public MonthlyFinancialReport(int year, int month, int totalOrders,
                                  BigDecimal totalSales, BigDecimal totalExpenses,
                                  BigDecimal netProfit) {
        this.year = year;
        this.month = month;
        this.totalOrders = totalOrders;
        this.totalSales = totalSales;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
    }

    // Gettery i settery
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    @Override
    public String toString() {
        return "MonthlyFinancialReport{" +
                "year=" + year +
                ", month=" + month +
                ", totalOrders=" + totalOrders +
                ", totalSales=" + totalSales +
                ", totalExpenses=" + totalExpenses +
                ", netProfit=" + netProfit +
                '}';
    }
}
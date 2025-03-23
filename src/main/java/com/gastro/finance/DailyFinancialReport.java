package com.gastro.finance;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca dzienny raport finansowy
 */
public class DailyFinancialReport {
    private Date day;
    private int totalOrders;
    private BigDecimal totalSales;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;

    // Konstruktor domyślny
    public DailyFinancialReport() {
    }

    // Konstruktor z parametrami
    public DailyFinancialReport(Date day, int totalOrders, BigDecimal totalSales,
                                BigDecimal totalExpenses, BigDecimal netProfit) {
        this.day = day;
        this.totalOrders = totalOrders;
        this.totalSales = totalSales;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
    }

    // Gettery i settery
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
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
        return "DailyFinancialReport{" +
                "day=" + day +
                ", totalOrders=" + totalOrders +
                ", totalSales=" + totalSales +
                ", totalExpenses=" + totalExpenses +
                ", netProfit=" + netProfit +
                '}';
    }
}
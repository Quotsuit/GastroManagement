package com.gastro.dashboard;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {

    // Statystyki kart
    private BigDecimal monthlyRevenue; // Przychody miesięczne
    private BigDecimal yearlyProfit;   // Zysk roczny
    private int taskCompletionPercentage; // Procent ukończonych zadań
    private int lowStockProductsCount;   // Liczba produktów o niskim stanie

    // Dostawy
    private List<DeliveryDTO> recentDeliveries;

    // Statystyki sprzedaży
    private int todayTransactionsCount;
    private BigDecimal averageOrderValue;
    private String mostOrderedProduct;
    private int activeEmployeesCount;

    // Gettery i settery

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public BigDecimal getYearlyProfit() {
        return yearlyProfit;
    }

    public void setYearlyProfit(BigDecimal yearlyProfit) {
        this.yearlyProfit = yearlyProfit;
    }

    public int getTaskCompletionPercentage() {
        return taskCompletionPercentage;
    }

    public void setTaskCompletionPercentage(int taskCompletionPercentage) {
        this.taskCompletionPercentage = taskCompletionPercentage;
    }

    public int getLowStockProductsCount() {
        return lowStockProductsCount;
    }

    public void setLowStockProductsCount(int lowStockProductsCount) {
        this.lowStockProductsCount = lowStockProductsCount;
    }

    public List<DeliveryDTO> getRecentDeliveries() {
        return recentDeliveries;
    }

    public void setRecentDeliveries(List<DeliveryDTO> recentDeliveries) {
        this.recentDeliveries = recentDeliveries;
    }

    public int getTodayTransactionsCount() {
        return todayTransactionsCount;
    }

    public void setTodayTransactionsCount(int todayTransactionsCount) {
        this.todayTransactionsCount = todayTransactionsCount;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public String getMostOrderedProduct() {
        return mostOrderedProduct;
    }

    public void setMostOrderedProduct(String mostOrderedProduct) {
        this.mostOrderedProduct = mostOrderedProduct;
    }

    public int getActiveEmployeesCount() {
        return activeEmployeesCount;
    }

    public void setActiveEmployeesCount(int activeEmployeesCount) {
        this.activeEmployeesCount = activeEmployeesCount;
    }

    // Klasa dla dostawy
    public static class DeliveryDTO {
        private String date;
        private String supplierName;
        private BigDecimal value;

        public DeliveryDTO(String date, String supplierName, BigDecimal value) {
            this.date = date;
            this.supplierName = supplierName;
            this.value = value;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}
package com.gastro.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    // Znajduje dostawy o określonym statusie
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);

    // Znajduje dostawy dla określonego dostawcy
    List<Delivery> findBySupplierNameContainingIgnoreCase(String supplierName);

    // Znajduje dostawy o numerze faktury zawierającym podany ciąg znaków
    List<Delivery> findByInvoiceNumberContainingIgnoreCase(String invoiceNumber);

    // Znajduje dostawy z określonego zakresu dat
    List<Delivery> findByDeliveryDateBetween(Date startDate, Date endDate);

    // Znajduje dostawy z ostatnich X dni
    @Query("SELECT d FROM Delivery d WHERE d.deliveryDate >= ?1 ORDER BY d.deliveryDate DESC")
    List<Delivery> findRecentDeliveries(Date startDate);

    // Znajduje dostawy oczekujące (planowane lub w trakcie)
    @Query("SELECT d FROM Delivery d WHERE d.status = 'PLANOWANA' OR d.status = 'W_TRAKCIE' ORDER BY d.deliveryDate ASC")
    List<Delivery> findPendingDeliveries();

    // Znajduje zakończone dostawy posortowane od najnowszej
    @Query("SELECT d FROM Delivery d WHERE d.status = 'ZAKONCZONA' ORDER BY d.deliveryDate DESC")
    List<Delivery> findCompletedDeliveries();
}
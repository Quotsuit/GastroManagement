package com.gastro.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Znajdź produkty z niskim stanem magazynowym
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minimumQuantity")
    List<Product> findLowStockProducts();

    // Znajdź produkty z określonej kategorii
    List<Product> findByCategoryId(Integer categoryId);

    // Wyszukaj produkty po nazwie (zawierającej frazę)
    List<Product> findByProductNameContainingIgnoreCase(String productName);

    // Znajdź produkty, których termin ważności zbliża się lub minął
    @Query("SELECT p FROM Product p WHERE p.expiryDate <= CURRENT_DATE")
    List<Product> findExpiredProducts();

    // Sortuj produkty po nazwie
    List<Product> findAllByOrderByProductNameAsc();
}
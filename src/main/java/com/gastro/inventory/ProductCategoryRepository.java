package com.gastro.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    // Znajdź kategorię po nazwie
    Optional<ProductCategory> findByCategoryName(String categoryName);

    // Znajdź kategorie zawierające podaną frazę w nazwie (ignorując wielkość liter)
    List<ProductCategory> findByCategoryNameContainingIgnoreCase(String categoryName);

    // Posortuj kategorie po nazwie
    List<ProductCategory> findAllByOrderByCategoryNameAsc();

    // Sprawdź, czy istnieje kategoria o podanej nazwie
    boolean existsByCategoryName(String categoryName);
}
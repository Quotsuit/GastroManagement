package com.gastro.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    @Autowired
    public ProductCategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Pobierz wszystkie kategorie posortowane alfabetycznie
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAllByOrderByCategoryNameAsc();
    }

    // Pobierz kategorię po ID
    public Optional<ProductCategory> getCategoryById(int id) {
        return categoryRepository.findById(id);
    }

    // Pobierz kategorię po nazwie
    public Optional<ProductCategory> getCategoryByName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    // Zapisz lub zaktualizuj kategorię
    @Transactional
    public ProductCategory saveCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    // Usuń kategorię
    @Transactional
    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }

    // Sprawdź, czy nazwa kategorii jest już używana
    public boolean isCategoryNameTaken(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }

    // Sprawdź, czy nazwa kategorii jest już używana, z wyłączeniem podanej kategorii
    public boolean isCategoryNameTaken(String categoryName, int excludeCategoryId) {
        Optional<ProductCategory> existingCategory = categoryRepository.findByCategoryName(categoryName);
        return existingCategory.isPresent() && existingCategory.get().getCategoryId() != excludeCategoryId;
    }

    // Wyszukaj kategorie po nazwie
    public List<ProductCategory> searchCategories(String query) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(query);
    }
}
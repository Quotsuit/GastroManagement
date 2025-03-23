package com.gastro.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Pobierz wszystkie produkty
    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByProductNameAsc();
    }

    // Pobierz produkt po ID
    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    // Pobierz produkty o niskim stanie magazynowym
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    // Dodaj nowy produkt
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Aktualizuj produkt
    @Transactional
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    // Usuń produkt
    @Transactional
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    // Aktualizuj stan magazynowy produktu
    @Transactional
    public void updateProductStock(int id, double newQuantity) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(product -> {
            product.setStockQuantity(java.math.BigDecimal.valueOf(newQuantity));
            productRepository.save(product);
        });
    }

    // Wyszukaj produkty po nazwie
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }

    // Pobierz produkty z określonej kategorii
    public List<Product> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}
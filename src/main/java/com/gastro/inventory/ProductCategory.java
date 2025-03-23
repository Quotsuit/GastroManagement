package com.gastro.inventory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca kategorię produktów
 */
@Entity
@Table(name = "product_categories")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    // Konstruktor domyślny
    public ProductCategory() {
    }

    // Konstruktor z parametrami
    public ProductCategory(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    // Gettery i settery
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // Metoda pomocnicza do dodawania produktu do kategorii
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }

    // Metoda pomocnicza do usuwania produktu z kategorii
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
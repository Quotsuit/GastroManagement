package com.gastro.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/inventory")
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, ProductCategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Wyświetl listę wszystkich produktów
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "inventory/product-list";
    }

    // Wyświetl produkty o niskim stanie magazynowym
    @GetMapping("/low-stock")
    public String listLowStockProducts(Model model) {
        List<Product> lowStockProducts = productService.getLowStockProducts();
        model.addAttribute("products", lowStockProducts);
        model.addAttribute("lowStockView", true);
        return "inventory/product-list";
    }

    // Formularz dodawania nowego produktu
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "inventory/product-form";
    }

    // Obsługa dodawania produktu
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam(name = "category.categoryId", required = false) Integer categoryId,
                             RedirectAttributes redirectAttributes) {
        try {
            // Ustawienie kategorii, jeśli wybrano
            if (categoryId != null && categoryId > 0) {
                Optional<ProductCategory> category = categoryService.getCategoryById(categoryId);
                if (category.isPresent()) {
                    product.setCategory(category.get());
                }
            } else {
                product.setCategory(null);
            }

            // Upewnij się, że żadne z wartości nie jest null
            if (product.getStockQuantity() == null) {
                product.setStockQuantity(java.math.BigDecimal.ZERO);
            }
            if (product.getMinimumQuantity() == null) {
                product.setMinimumQuantity(java.math.BigDecimal.ZERO);
            }
            if (product.getPurchasePrice() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Proszę podać cenę zakupu.");
                return "redirect:/inventory/add";
            }

            // Zapisz produkt
            Product savedProduct = productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie dodany.");
            return "redirect:/inventory";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas dodawania produktu: " + e.getMessage());
            return "redirect:/inventory/add";
        }
    }

    // Formularz edycji produktu
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("allCategories", categoryService.getAllCategories());
            return "inventory/product-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono produktu o podanym ID.");
            return "redirect:/inventory";
        }
    }

    // Obsługa aktualizacji produktu
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable int id,
                                @ModelAttribute Product product,
                                @RequestParam(name = "category.categoryId", required = false) Integer categoryId,
                                RedirectAttributes redirectAttributes) {
        try {
            product.setProductId(id);

            // Ustawienie kategorii, jeśli wybrano
            if (categoryId != null && categoryId > 0) {
                Optional<ProductCategory> category = categoryService.getCategoryById(categoryId);
                if (category.isPresent()) {
                    product.setCategory(category.get());
                }
            } else {
                product.setCategory(null);
            }

            // Upewnij się, że żadne z wartości nie jest null
            if (product.getStockQuantity() == null) {
                product.setStockQuantity(java.math.BigDecimal.ZERO);
            }
            if (product.getMinimumQuantity() == null) {
                product.setMinimumQuantity(java.math.BigDecimal.ZERO);
            }
            if (product.getPurchasePrice() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Proszę podać cenę zakupu.");
                return "redirect:/inventory/edit/" + id;
            }

            // Zaktualizuj produkt
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie zaktualizowany.");
            return "redirect:/inventory";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas aktualizacji produktu: " + e.getMessage());
            return "redirect:/inventory/edit/" + id;
        }
    }

    // Usuwanie produktu
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie usunięty.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania produktu: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    // Formularz aktualizacji stanu magazynowego
    @GetMapping("/update-stock/{id}")
    public String showUpdateStockForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "inventory/stock-update-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono produktu o podanym ID.");
            return "redirect:/inventory";
        }
    }

    // Obsługa aktualizacji stanu magazynowego
    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable int id, @RequestParam("stockQuantity") double stockQuantity, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductStock(id, stockQuantity);
            redirectAttributes.addFlashAttribute("successMessage", "Stan magazynowy został pomyślnie zaktualizowany.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas aktualizacji stanu magazynowego: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    // Wyszukiwanie produktów
    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        List<Product> products = productService.searchProductsByName(query);
        model.addAttribute("products", products);
        model.addAttribute("searchQuery", query);
        return "inventory/product-list";
    }

    // Filtrowanie produktów według kategorii
    @GetMapping("/category/{categoryId}")
    public String filterByCategory(@PathVariable int categoryId, Model model, RedirectAttributes redirectAttributes) {
        Optional<ProductCategory> category = categoryService.getCategoryById(categoryId);

        if (category.isPresent()) {
            List<Product> products = productService.getProductsByCategory(categoryId);
            model.addAttribute("products", products);
            model.addAttribute("categoryFilter", category.get().getCategoryName());
            return "inventory/product-list";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono kategorii o podanym ID.");
            return "redirect:/inventory";
        }
    }

    // Dodanie listy kategorii do wszystkich widoków
    @ModelAttribute("allCategories")
    public List<ProductCategory> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
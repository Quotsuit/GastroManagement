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

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
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
        return "inventory/product-form";
    }

    // Obsługa dodawania produktu
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product, RedirectAttributes redirectAttributes) {
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie dodany.");
        return "redirect:/inventory";
    }

    // Formularz edycji produktu
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "inventory/product-form";
        } else {
            return "redirect:/inventory";
        }
    }

    // Obsługa aktualizacji produktu
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable int id, @ModelAttribute("product") Product product, RedirectAttributes redirectAttributes) {
        product.setProductId(id);
        productService.updateProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie zaktualizowany.");
        return "redirect:/inventory";
    }

    // Usuwanie produktu
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie usunięty.");
        return "redirect:/inventory";
    }

    // Formularz aktualizacji stanu magazynowego
    @GetMapping("/update-stock/{id}")
    public String showUpdateStockForm(@PathVariable int id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "inventory/stock-update-form";
        } else {
            return "redirect:/inventory";
        }
    }

    // Obsługa aktualizacji stanu magazynowego
    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable int id, @RequestParam("stockQuantity") double stockQuantity, RedirectAttributes redirectAttributes) {
        productService.updateProductStock(id, stockQuantity);
        redirectAttributes.addFlashAttribute("successMessage", "Stan magazynowy został pomyślnie zaktualizowany.");
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
}
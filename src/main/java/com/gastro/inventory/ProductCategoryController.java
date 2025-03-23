package com.gastro.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/inventory/categories")
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Wyświetl listę wszystkich kategorii
    @GetMapping
    public String listCategories(Model model) {
        List<ProductCategory> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "inventory/category-list";
    }

    // Formularz dodawania nowej kategorii
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new ProductCategory());
        model.addAttribute("isNew", true);
        return "inventory/category-form";
    }

    // Obsługa dodawania kategorii
    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") ProductCategory category, RedirectAttributes redirectAttributes) {
        // Sprawdź, czy nazwa kategorii jest już używana
        if (categoryService.isCategoryNameTaken(category.getCategoryName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kategoria o takiej nazwie już istnieje.");
            redirectAttributes.addFlashAttribute("category", category);
            return "redirect:/inventory/categories/add";
        }

        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategoria została pomyślnie dodana.");
        return "redirect:/inventory/categories";
    }

    // Formularz edycji kategorii
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ProductCategory> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            model.addAttribute("isNew", false);
            return "inventory/category-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono kategorii o podanym ID.");
            return "redirect:/inventory/categories";
        }
    }

    // Obsługa aktualizacji kategorii
    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable int id, @ModelAttribute("category") ProductCategory category, RedirectAttributes redirectAttributes) {
        // Sprawdź, czy nazwa kategorii jest już używana przez inną kategorię
        if (categoryService.isCategoryNameTaken(category.getCategoryName(), id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kategoria o takiej nazwie już istnieje.");
            redirectAttributes.addFlashAttribute("category", category);
            return "redirect:/inventory/categories/edit/" + id;
        }

        category.setCategoryId(id);
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategoria została pomyślnie zaktualizowana.");
        return "redirect:/inventory/categories";
    }

    // Usuwanie kategorii
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<ProductCategory> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            // Sprawdź, czy kategoria ma przypisane produkty
            if (!category.get().getProducts().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Nie można usunąć kategorii, ponieważ są do niej przypisane produkty. " +
                                "Najpierw przypisz te produkty do innej kategorii.");
                return "redirect:/inventory/categories";
            }

            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kategoria została pomyślnie usunięta.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono kategorii o podanym ID.");
        }
        return "redirect:/inventory/categories";
    }

    // Wyszukiwanie kategorii
    @GetMapping("/search")
    public String searchCategories(@RequestParam("query") String query, Model model) {
        List<ProductCategory> categories = categoryService.searchCategories(query);
        model.addAttribute("categories", categories);
        model.addAttribute("searchQuery", query);
        return "inventory/category-list";
    }

    // Metoda pomocnicza do pobierania wszystkich kategorii dla innych kontrolerów
    @ModelAttribute("allCategories")
    public List<ProductCategory> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
package com.gastro.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/inventory/deliveries")
public class DeliveryController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryController.class);

    private final DeliveryService deliveryService;
    private final ProductService productService;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryItemRepository deliveryItemRepository;

    @Autowired
    public DeliveryController(DeliveryService deliveryService,
                              ProductService productService,
                              DeliveryRepository deliveryRepository,
                              DeliveryItemRepository deliveryItemRepository) {
        this.deliveryService = deliveryService;
        this.productService = productService;
        this.deliveryRepository = deliveryRepository;
        this.deliveryItemRepository = deliveryItemRepository;
    }

    // Wyświetlanie listy dostaw
    @GetMapping
    public String listDeliveries(Model model) {
        List<Delivery> pendingDeliveries = deliveryService.getPendingDeliveries();
        List<Delivery> completedDeliveries = deliveryService.getRecentDeliveries(30);

        model.addAttribute("pendingDeliveries", pendingDeliveries);
        model.addAttribute("completedDeliveries", completedDeliveries);
        return "inventory/delivery-list";
    }

    // Formularz nowej dostawy
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("delivery", new Delivery());
        return "inventory/delivery-form";
    }

    // Zapisywanie nowej dostawy
    @PostMapping("/add")
    public String addDelivery(@ModelAttribute Delivery delivery, RedirectAttributes redirectAttributes) {
        try {
            // Ustawienie domyślnych wartości
            if (delivery.getStatus() == null) {
                delivery.setStatus(Delivery.DeliveryStatus.PLANOWANA);
            }

            if (delivery.getTotalAmount() == null) {
                delivery.setTotalAmount(BigDecimal.ZERO);
            }

            if (delivery.getDeliveryDate() == null) {
                delivery.setDeliveryDate(new Date());
            }

            delivery.setCreatedAt(new Date());
            delivery.setUpdatedAt(new Date());

            // Zapisz pustą dostawę bez elementów
            Delivery savedDelivery = deliveryRepository.save(delivery);

            redirectAttributes.addFlashAttribute("successMessage", "Dostawa została pomyślnie utworzona.");
            return "redirect:/inventory/deliveries/edit/" + savedDelivery.getDeliveryId();
        } catch (Exception e) {
            // Logowanie błędu
            logger.error("Błąd podczas dodawania dostawy", e);

            // Przekierowanie z komunikatem błędu
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas dodawania dostawy: " + e.getMessage());
            return "redirect:/inventory/deliveries/add";
        }
    }

    // Wyświetlanie szczegółów dostawy
    @GetMapping("/{id}")
    public String viewDelivery(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Delivery> deliveryOpt = deliveryService.getDeliveryById(id);
        if (deliveryOpt.isPresent()) {
            model.addAttribute("delivery", deliveryOpt.get());
            return "inventory/delivery-details";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono dostawy o podanym ID.");
            return "redirect:/inventory/deliveries";
        }
    }

    // Formularz edycji dostawy
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Delivery> deliveryOpt = deliveryService.getDeliveryById(id);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();

            // Sprawdź czy można edytować dostawę
            if (delivery.getStatus() == Delivery.DeliveryStatus.ZAKONCZONA) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nie można edytować zakończonej dostawy.");
                return "redirect:/inventory/deliveries/" + id;
            }

            model.addAttribute("delivery", delivery);
            model.addAttribute("allProducts", productService.getAllProducts());
            return "inventory/delivery-edit";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono dostawy o podanym ID.");
            return "redirect:/inventory/deliveries";
        }
    }

    // Aktualizacja dostawy
    @PostMapping("/edit/{id}")
    public String updateDelivery(@PathVariable int id, @ModelAttribute Delivery delivery, RedirectAttributes redirectAttributes) {
        try {
            Optional<Delivery> existingDeliveryOpt = deliveryService.getDeliveryById(id);
            if (existingDeliveryOpt.isPresent()) {
                Delivery existingDelivery = existingDeliveryOpt.get();

                // Sprawdź czy można edytować dostawę
                if (existingDelivery.getStatus() == Delivery.DeliveryStatus.ZAKONCZONA) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Nie można edytować zakończonej dostawy.");
                    return "redirect:/inventory/deliveries/" + id;
                }

                // Aktualizuj tylko podstawowe informacje o dostawie
                existingDelivery.setDeliveryDate(delivery.getDeliveryDate());
                existingDelivery.setSupplierName(delivery.getSupplierName());
                existingDelivery.setInvoiceNumber(delivery.getInvoiceNumber());
                existingDelivery.setNotes(delivery.getNotes());

                deliveryService.saveDelivery(existingDelivery);
                redirectAttributes.addFlashAttribute("successMessage", "Dostawa została pomyślnie zaktualizowana.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono dostawy o podanym ID.");
            }
            return "redirect:/inventory/deliveries/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas aktualizacji dostawy: " + e.getMessage());
            return "redirect:/inventory/deliveries/edit/" + id;
        }
    }

    // Dodawanie produktu do dostawy
    @PostMapping("/{deliveryId}/items/add")
    public String addItemToDelivery(@PathVariable int deliveryId,
                                    @RequestParam("productId") int productId,
                                    @RequestParam("quantity") BigDecimal quantity,
                                    @RequestParam("unitPrice") BigDecimal unitPrice,
                                    @RequestParam(value = "notes", required = false) String notes,
                                    RedirectAttributes redirectAttributes) {
        try {
            logger.info("Próba dodania produktu ID: {} do dostawy ID: {}, ilość: {}, cena: {}",
                    productId, deliveryId, quantity, unitPrice);

            Optional<Product> productOpt = productService.getProductById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                logger.info("Produkt znaleziony: {}", product.getProductName());

                // Pobierz dostawę bezpośrednio
                Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
                if (!deliveryOpt.isPresent()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono dostawy o ID: " + deliveryId);
                    return "redirect:/inventory/deliveries";
                }

                Delivery delivery = deliveryOpt.get();

                // Utwórz pozycję dostawy
                DeliveryItem item = new DeliveryItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);
                item.setNotes(notes);
                item.setDelivery(delivery);
                item.setAlreadyAddedToStock(false);

                // Zapisz pozycję bezpośrednio
                DeliveryItem savedItem = deliveryItemRepository.save(item);
                logger.info("Pozycja zapisana z ID: {}", savedItem.getItemId());

                // Przelicz sumę i zaktualizuj dostawę
                delivery.getDeliveryItems().add(savedItem);
                delivery.recalculateTotalAmount();
                deliveryRepository.save(delivery);

                redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie dodany do dostawy.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono produktu o podanym ID.");
            }
            return "redirect:/inventory/deliveries/edit/" + deliveryId;
        } catch (Exception e) {
            logger.error("Błąd podczas dodawania produktu do dostawy", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas dodawania produktu do dostawy: " + e.getMessage());
            return "redirect:/inventory/deliveries/edit/" + deliveryId;
        }
    }

    // Usuwanie produktu z dostawy
    @GetMapping("/{deliveryId}/items/delete/{itemId}")
    public String removeItemFromDelivery(@PathVariable int deliveryId,
                                         @PathVariable int itemId,
                                         RedirectAttributes redirectAttributes) {
        try {
            // Pobierz element z bazy
            Optional<DeliveryItem> itemOpt = deliveryItemRepository.findById(itemId);
            if (!itemOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono pozycji o ID: " + itemId);
                return "redirect:/inventory/deliveries/edit/" + deliveryId;
            }

            DeliveryItem item = itemOpt.get();

            // Sprawdź, czy produkt nie został już dodany do magazynu
            if (item.isAlreadyAddedToStock()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nie można usunąć pozycji, która została już dodana do magazynu");
                return "redirect:/inventory/deliveries/edit/" + deliveryId;
            }

            // Pobierz dostawę
            Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
            if (!deliveryOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono dostawy o ID: " + deliveryId);
                return "redirect:/inventory/deliveries";
            }

            Delivery delivery = deliveryOpt.get();

            // Usuń pozycję z listy w dostawie
            delivery.getDeliveryItems().removeIf(i -> i.getItemId() == itemId);

            // Usuń z bazy danych
            deliveryItemRepository.delete(item);

            // Przelicz sumę i zapisz dostawę
            delivery.recalculateTotalAmount();
            deliveryRepository.save(delivery);

            redirectAttributes.addFlashAttribute("successMessage", "Produkt został pomyślnie usunięty z dostawy.");
        } catch (Exception e) {
            logger.error("Błąd podczas usuwania produktu z dostawy", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania produktu z dostawy: " + e.getMessage());
        }
        return "redirect:/inventory/deliveries/edit/" + deliveryId;
    }

    // Zmiana statusu dostawy na "W trakcie"
    @GetMapping("/{id}/start")
    public String startDelivery(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            deliveryService.startDelivery(id);
            redirectAttributes.addFlashAttribute("successMessage", "Status dostawy został zmieniony na \"W trakcie\".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas zmiany statusu dostawy: " + e.getMessage());
        }
        return "redirect:/inventory/deliveries/" + id;
    }

    // Finalizacja dostawy i aktualizacja stanów magazynowych
    @GetMapping("/{id}/complete")
    public String completeDelivery(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            deliveryService.completeDelivery(id);
            redirectAttributes.addFlashAttribute("successMessage", "Dostawa została zakończona a produkty dodane do magazynu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas finalizacji dostawy: " + e.getMessage());
        }
        return "redirect:/inventory/deliveries/" + id;
    }

    // Usuwanie dostawy
    @GetMapping("/{id}/delete")
    public String deleteDelivery(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            deliveryService.deleteDelivery(id);
            redirectAttributes.addFlashAttribute("successMessage", "Dostawa została pomyślnie usunięta.");
            return "redirect:/inventory/deliveries";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/deliveries/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania dostawy: " + e.getMessage());
            return "redirect:/inventory/deliveries/" + id;
        }
    }

    // Cofnięcie aktualizacji stanów magazynowych (tylko dla testów)
    @GetMapping("/{id}/undo-stock")
    public String undoStockUpdate(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            deliveryService.undoStockUpdate(id);
            redirectAttributes.addFlashAttribute("successMessage", "Aktualizacja stanów magazynowych została cofnięta.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas cofania aktualizacji stanów: " + e.getMessage());
        }
        return "redirect:/inventory/deliveries/" + id;
    }
}
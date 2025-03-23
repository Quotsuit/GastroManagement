package com.gastro.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryItemRepository deliveryItemRepository;
    private final ProductService productService;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository,
                           DeliveryItemRepository deliveryItemRepository,
                           ProductService productService) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryItemRepository = deliveryItemRepository;
        this.productService = productService;
    }

    // Znajdź wszystkie dostawy
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    // Znajdź dostawę po ID
    public Optional<Delivery> getDeliveryById(int id) {
        return deliveryRepository.findById(id);
    }

    // Znajdź dostawy o określonym statusie
    public List<Delivery> getDeliveriesByStatus(Delivery.DeliveryStatus status) {
        return deliveryRepository.findByStatus(status);
    }

    // Znajdź dostawy oczekujące (planowane lub w trakcie)
    public List<Delivery> getPendingDeliveries() {
        return deliveryRepository.findPendingDeliveries();
    }

    // Znajdź zakończone dostawy
    public List<Delivery> getCompletedDeliveries() {
        return deliveryRepository.findCompletedDeliveries();
    }

    // Znajdź dostawy z ostatnich X dni
    public List<Delivery> getRecentDeliveries(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return deliveryRepository.findRecentDeliveries(calendar.getTime());
    }

    // Utwórz nową dostawę - uproszczona implementacja
    @Transactional
    public Delivery createDelivery(Delivery delivery) {
        logger.debug("Tworzenie nowej dostawy: {}", delivery);

        // Upewnij się, że wszystkie wymagane pola są ustawione
        if (delivery.getDeliveryDate() == null) {
            delivery.setDeliveryDate(new Date());
        }

        if (delivery.getStatus() == null) {
            delivery.setStatus(Delivery.DeliveryStatus.PLANOWANA);
        }

        if (delivery.getTotalAmount() == null) {
            delivery.setTotalAmount(BigDecimal.ZERO);
        }

        delivery.setCreatedAt(new Date());
        delivery.setUpdatedAt(new Date());

        // Zapisz najpierw samą dostawę bez pozycji
        logger.debug("Zapisywanie dostawy w bazie danych...");
        Delivery savedDelivery = deliveryRepository.save(delivery);
        logger.debug("Dostawa zapisana z ID: {}", savedDelivery.getDeliveryId());

        return savedDelivery;
    }

    // Zapisz lub zaktualizuj dostawę
    @Transactional
    public Delivery saveDelivery(Delivery delivery) {
        logger.debug("Zapisywanie/aktualizowanie dostawy: {}", delivery);

        delivery.recalculateTotalAmount();
        delivery.setUpdatedAt(new Date());

        return deliveryRepository.save(delivery);
    }

    // Dodaj pozycję do dostawy
    @Transactional
    public DeliveryItem addItemToDelivery(int deliveryId, DeliveryItem item) {
        logger.debug("Dodawanie pozycji do dostawy ID: {}", deliveryId);

        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();

            // Upewnij się, że pozycja ma ustawioną dostawę
            item.setDelivery(delivery);

            // Upewnij się, że wszystkie wartości są ustawione poprawnie
            if (item.getQuantity() == null) {
                item.setQuantity(BigDecimal.ZERO);
            }

            if (item.getUnitPrice() == null) {
                item.setUnitPrice(BigDecimal.ZERO);
            }

            // Zapisz pozycję
            DeliveryItem savedItem = deliveryItemRepository.save(item);

            // Odświeżenie listy pozycji w dostawie
            delivery = deliveryRepository.findById(deliveryId).get(); // Ponowne pobranie dostawy z bazy

            // Ważne: zastosowanie świeżo obliczonej sumy
            delivery.recalculateTotalAmount();

            // Zapisanie dostawy z zaktualizowaną wartością
            deliveryRepository.save(delivery);

            logger.debug("Pozycja dodana z ID: {}, nowa wartość dostawy: {}", savedItem.getItemId(), delivery.getTotalAmount());
            return savedItem;
        }

        logger.error("Nie znaleziono dostawy o ID: {}", deliveryId);
        throw new IllegalArgumentException("Nie znaleziono dostawy o ID: " + deliveryId);
    }

    // Usuń pozycję z dostawy
    @Transactional
    public void removeItemFromDelivery(int deliveryId, int itemId) {
        logger.debug("Usuwanie pozycji ID: {} z dostawy ID: {}", itemId, deliveryId);

        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        Optional<DeliveryItem> itemOpt = deliveryItemRepository.findById(itemId);

        if (deliveryOpt.isPresent() && itemOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();
            DeliveryItem item = itemOpt.get();

            // Sprawdź czy pozycja należy do tej dostawy
            if (item.getDelivery().getDeliveryId() == deliveryId) {
                // Sprawdź, czy produkt nie został już dodany do magazynu
                if (item.isAlreadyAddedToStock()) {
                    logger.error("Nie można usunąć pozycji, która została już dodana do magazynu");
                    throw new IllegalStateException("Nie można usunąć pozycji, która została już dodana do magazynu");
                }

                // Usuń pozycję z listy w dostawie
                delivery.getDeliveryItems().removeIf(i -> i.getItemId() == itemId);

                // Usuń z bazy danych
                deliveryItemRepository.delete(item);

                // Przelicz sumę i zapisz dostawę
                delivery.recalculateTotalAmount();
                deliveryRepository.save(delivery);

                logger.debug("Pozycja została usunięta");
            }
        }
    }

    // Zmień status dostawy na "W trakcie"
    @Transactional
    public Delivery startDelivery(int deliveryId) {
        logger.debug("Zmiana statusu dostawy ID: {} na W_TRAKCIE", deliveryId);

        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();
            delivery.startDelivery();
            return deliveryRepository.save(delivery);
        }

        logger.error("Nie znaleziono dostawy o ID: {}", deliveryId);
        throw new IllegalArgumentException("Nie znaleziono dostawy o ID: " + deliveryId);
    }

    // Zmień status dostawy na "Zakończona" i dodaj produkty do stanu magazynowego
    @Transactional
    public Delivery completeDelivery(int deliveryId) {
        logger.debug("Zmiana statusu dostawy ID: {} na ZAKONCZONA i aktualizacja stanów magazynowych", deliveryId);

        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();

            // Aktualizuj stany magazynowe produktów
            for (DeliveryItem item : delivery.getDeliveryItems()) {
                if (!item.isAlreadyAddedToStock()) {
                    Product product = item.getProduct();
                    BigDecimal newQuantity = product.getStockQuantity().add(item.getQuantity());

                    // Aktualizuj cenę zakupu produktu
                    product.setPurchasePrice(item.getUnitPrice());

                    // Aktualizuj stan magazynowy
                    product.setStockQuantity(newQuantity);
                    productService.updateProduct(product);

                    // Oznacz jako dodane do magazynu
                    item.setAlreadyAddedToStock(true);
                    deliveryItemRepository.save(item);
                }
            }

            // Zmień status dostawy
            delivery.completeDelivery();
            return deliveryRepository.save(delivery);
        }

        logger.error("Nie znaleziono dostawy o ID: {}", deliveryId);
        throw new IllegalArgumentException("Nie znaleziono dostawy o ID: " + deliveryId);
    }

    // Anuluj dodanie dostawy do magazynu (dla testowania)
    @Transactional
    public Delivery undoStockUpdate(int deliveryId) {
        logger.debug("Cofanie aktualizacji stanów magazynowych dla dostawy ID: {}", deliveryId);

        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();

            // Usuń produkty ze stanu magazynowego
            for (DeliveryItem item : delivery.getDeliveryItems()) {
                if (item.isAlreadyAddedToStock()) {
                    Product product = item.getProduct();
                    BigDecimal newQuantity = product.getStockQuantity().subtract(item.getQuantity());

                    // Aktualizuj stan magazynowy
                    product.setStockQuantity(newQuantity);
                    productService.updateProduct(product);

                    // Oznacz jako nie dodane do magazynu
                    item.setAlreadyAddedToStock(false);
                    deliveryItemRepository.save(item);
                }
            }

            // Zmień status dostawy z powrotem na "W trakcie"
            delivery.startDelivery();
            return deliveryRepository.save(delivery);
        }

        logger.error("Nie znaleziono dostawy o ID: {}", deliveryId);
        throw new IllegalArgumentException("Nie znaleziono dostawy o ID: " + deliveryId);
    }

    // Usuń dostawę
    @Transactional
    public void deleteDelivery(int deliveryId) {
        logger.debug("Usuwanie dostawy ID: {}", deliveryId);

        Optional<Delivery> deliveryOpt = deliveryRepository.findById(deliveryId);
        if (deliveryOpt.isPresent()) {
            Delivery delivery = deliveryOpt.get();

            // Sprawdź, czy żadna z pozycji nie została już dodana do magazynu
            boolean anyItemAddedToStock = delivery.getDeliveryItems().stream()
                    .anyMatch(DeliveryItem::isAlreadyAddedToStock);

            if (anyItemAddedToStock) {
                logger.error("Nie można usunąć dostawy, ponieważ produkty zostały już dodane do magazynu");
                throw new IllegalStateException("Nie można usunąć dostawy, ponieważ produkty zostały już dodane do magazynu");
            }

            deliveryRepository.delete(delivery);
            logger.debug("Dostawa została usunięta");
        }
    }

    // Pobierz pozycje dostawy
    public List<DeliveryItem> getDeliveryItems(int deliveryId) {
        return deliveryItemRepository.findByDeliveryDeliveryId(deliveryId);
    }
}
package com.gastro.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Integer> {

    // Znajduje pozycje dla określonej dostawy
    List<DeliveryItem> findByDeliveryDeliveryId(int deliveryId);

    // Znajduje pozycje dla określonego produktu
    List<DeliveryItem> findByProductProductId(int productId);

    // Znajduje pozycje, które jeszcze nie zostały dodane do stanu magazynowego
    List<DeliveryItem> findByAlreadyAddedToStockFalseAndDeliveryDeliveryId(int deliveryId);

    // Znajduje ostatnie 5 dostaw dla określonego produktu
    @Query("SELECT di FROM DeliveryItem di JOIN di.delivery d WHERE di.product.productId = ?1 ORDER BY d.deliveryDate DESC")
    List<DeliveryItem> findRecentDeliveriesForProduct(int productId);
}
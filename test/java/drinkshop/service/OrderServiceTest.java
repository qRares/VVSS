package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("WhiteBox")
public class OrderServiceTest {

    private OrderService orderService;
    private Repository<Integer, Order> orderRepo;
    private Repository<Integer, Product> productRepo;



    @BeforeEach
    void setUp() {
        // Folosim clasele interne in-memory pentru a izola testele
        orderRepo = new InMemoryOrderRepository();
        productRepo = new InMemoryProductRepository();
        orderService = new OrderService(orderRepo, productRepo);
    }

    @Test
    @DisplayName("WBT TC01 (Path 1): Comanda este null -> Aruncă excepție")
    @Timeout(1) // Testul pică dacă durează mai mult de 1 secundă
    void processOrder_WBT_NullOrder() {
        // Arrange
        Order nullOrder = null;

        // Act + Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.processOrder(nullOrder));
        assertEquals("Comanda nu poate fi null", exception.getMessage());
    }

    @Test
    @DisplayName("WBT TC02 (Path 2): Comandă fără produse (0 iterații buclă) -> Se salvează cu succes")
    void processOrder_WBT_EmptyItems() {
        // Arrange
        Order order = new Order(100);
        // Niciun produs în listă

        // Act
        orderService.processOrder(order);

        // Assert
        Order savedOrder = orderRepo.findOne(100);
        assertNotNull(savedOrder, "Comanda ar fi trebuit salvată cu succes");
        assertTrue(savedOrder.getItems().isEmpty());
    }

    @Test
    @DisplayName("WBT TC03 (Path 3): Produsul nu există în repository -> Aruncă excepție")
    void processOrder_WBT_ProductNotFound() {
        // Arrange
        Order order = new Order(101);

        Product fakeProduct = new Product(999, "Produs Inexistent", 10.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED);
        // NU salvăm produsul în productRepo intenționat

        OrderItem item = new OrderItem(fakeProduct, 2);
        order.getItems().add(item);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.processOrder(order));
        assertEquals("Produs inexistent", exception.getMessage());
    }

    @Test
    @DisplayName("WBT TC04 (Path 4): Cantitate invalidă (<= 0) -> Aruncă excepție")
    void processOrder_WBT_InvalidQuantity() {
        // Arrange
        Order order = new Order(102);

        // Creăm și salvăm un produs valid în repo
        Product validProduct = new Product(1, "Cola", 5.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED);
        productRepo.save(validProduct);

        // Creăm un item cu cantitate invalidă (-5)
        OrderItem item = new OrderItem(validProduct, -5);
        order.getItems().add(item);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.processOrder(order));
        assertEquals("Cantitate invalida", exception.getMessage());
    }

    @Test
    @DisplayName("WBT TC05 (Path 5): Comandă complet validă -> Se salvează cu succes")
    void processOrder_WBT_ValidOrder() {
        // Arrange
        Order order = new Order(103);

        Product validProduct = new Product(1, "Cola", 5.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        productRepo.save(validProduct);

        OrderItem item = new OrderItem(validProduct, 5);
        order.getItems().add(item);

        // Act
        orderService.processOrder(order);

        // Assert
        Order savedOrder = orderRepo.findOne(103);
        assertNotNull(savedOrder, "Comanda complet validă ar fi trebuit salvată.");
    }

    // --- CLASE INTERNE PENTRU REPOSITORY (Mocks in-memory) ---

    private static class InMemoryOrderRepository extends AbstractRepository<Integer, Order> {
        @Override
        protected Integer getId(Order entity) {
            return entity.getId();
        }
    }

    private static class InMemoryProductRepository extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }
}
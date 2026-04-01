package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        Repository<Integer, Product> repo = new InMemoryProductRepository();
        productService = new ProductService(repo);
    }

    @DisplayName("ECP valid: produsul se adauga pentru valori valide")
    @ParameterizedTest
    @CsvSource({
            "1,Cappuccino,10.0",
            "7,Espresso,15.5"
    })
    void addProduct_ECP_valid(int id, String nume, double pret) {
        // Arrange
        Product product = new Product(id, nume, pret,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        // Act
        productService.addProduct(product);

        // Assert
        Product added = productService.findById(id);
        assertNotNull(added);
        assertEquals(nume, added.getNume());
        assertEquals(pret, added.getPret());
    }

    @DisplayName("ECP invalid: id negativ sau zero")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void addProduct_ECP_invalid_id(int id) {
        // Arrange
        Product product = new Product(id, "Cafea", 10.0,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        // Act + Assert
        assertThrows(ValidationException.class, () -> productService.addProduct(product));
    }

    @DisplayName("ECP invalid: pret negativ sau zero")
    @ParameterizedTest
    @ValueSource(doubles = {0.0, -5.0})
    void addProduct_ECP_invalid_price(double pret) {
        // Arrange
        Product product = new Product(1, "Cafea", pret,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        // Act + Assert
        assertThrows(ValidationException.class, () -> productService.addProduct(product));
    }

    @DisplayName("BVA pentru id: 0 invalid, 1 valid")
    @ParameterizedTest
    @CsvSource({
            "0,false",
            "1,true"
    })
    void addProduct_BVA_id(int id, boolean shouldBeValid) {
        // Arrange
        Product product = new Product(id, "Latte", 12.0,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        // Act + Assert
        if (shouldBeValid) {
            productService.addProduct(product);
            assertNotNull(productService.findById(id));
        } else {
            assertThrows(ValidationException.class, () -> productService.addProduct(product));
        }
    }

    @DisplayName("BVA pentru pret: 0 invalid, 0.01 valid")
    @ParameterizedTest
    @CsvSource({
            "0.0,false",
            "0.01,true"
    })
    void addProduct_BVA_price(double pret, boolean shouldBeValid) {
        // Arrange
        Product product = new Product(10, "Mocha", pret,
                CategorieBautura.CLASSIC_COFFEE,
                TipBautura.BASIC);

        // Act + Assert
        if (shouldBeValid) {
            productService.addProduct(product);
            assertNotNull(productService.findById(10));
        } else {
            assertThrows(ValidationException.class, () -> productService.addProduct(product));
        }
    }

    private static class InMemoryProductRepository extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }
}
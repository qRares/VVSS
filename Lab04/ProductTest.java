package drinkshop.ut.domain;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductTest {

    Product product;

    @BeforeEach
    void setUp() {
        product =new Product(100, "Limonada", 10.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
    }

    @AfterEach
    void tearDown() {
        product = null;
    }

    @Test
    void test_getId() {
        assert 100 == product.getId();
    }

    @Test
    void setNume() {
        product.setNume("Limonade");
        assert "Limonade".equals(product.getNume());
    }

    @Test
    void getNume() {
        assert "Limonada".equals(product.getNume());
    }

}
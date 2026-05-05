package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileStocRepository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceIntTest {
    private Stoc stoc;
    private StocValidator stocValidator; // REAL
    private Repository<Integer, Stoc> stocRepo;

    private StocService stocService;

    @BeforeEach
    void setUp() {
        stocValidator = new StocValidator();
        stocRepo = new FileStocRepository("data/stocuri.txt");
        stoc = null; // integram al doilea nivel (top down breadth first)
        stocService = new StocService(stocRepo, stocValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealRepo() {
        Stoc stoc = new Stoc(1, "Apa", 5.0, 1.0);

        //apelam metoda add si evaluam apelul cu fail
        try{
            stocService.add(stoc);
        }catch (Exception e){
            fail("Invalid add operation " + e);
        }

        assert 10 == stocRepo.findAll().size();
        assert 10 == stocService.getAll().size();
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealRepo() {
        Stoc stoc = new Stoc(-1, "Apa", 5.0, 10.0);

        //apelam metoda si evaluam invalidarea obiectului
        Assertions.assertThrows(ValidationException.class, () -> {
            stocService.add(stoc);
        });

        assert 10 == stocRepo.findAll().size();
        assert 10 == stocService.getAll().size();
    }

    @Test
    @Disabled
    void add() {
    }

    @Test
    @Disabled
    void update() {
    }

    @Test
    @Disabled
    void delete() {
    }

    @Test
    @Disabled
    void areSuficient() {
    }

    @Test
    @Disabled
    void consuma() {
    }
}
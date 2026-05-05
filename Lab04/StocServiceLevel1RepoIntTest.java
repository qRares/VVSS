package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileStocRepository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceLevel1RepoIntTest {
    private Stoc stoc;
    private StocValidator stocValidator;
    private Repository<Integer, Stoc> stocRepo;

    private StocService stocService;

    @BeforeEach
    void setUp() {
        stocValidator = new StocValidator();//deja integrat
        stocRepo = new FileStocRepository("data/stocuri.txt"); // integram primul nivel (top down breadth first)
        stoc = mock(Stoc.class);
        stocService = new StocService(stocRepo, stocValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealRepo() {
        //simulam o adaugare valida cu Stoc s = new Stoc(1, "Apa", 5, 1);
        //asociem comportamentul pentru obiectul mock stoc
        when(stoc.getId()).thenReturn(1);
        when(stoc.getIngredient()).thenReturn("Apa");
        when(stoc.getCantitate()).thenReturn(5.00);
        when(stoc.getStocMinim()).thenReturn(1.00);

        //apelam (metoda add) si evaluam apelul (cu fail)
        try{
            stocService.add(stoc);
        }catch (Exception e){
            fail("Invalid add operation " + e);
        }

        // verificam interactiunea doar cu obiectul mock ramas, i.e., stoc
        verify(stoc, times(3)).getCantitate();//apelat de 3 ori, la validare simpla, la comparatie cu stocul minim, la salvare in fisier

    }

    @Test
    @Order(2)
    void testAddInvalid_withRealRepo() {
        //asociem comportamente obiectelor mock
        when(stoc.getId()).thenReturn(-1);

        //apelam metoda si invalidarea obiectului
        Assertions.assertThrows(ValidationException.class, () -> {
            stocService.add(stoc);
        });

        //verificam interactiunea cu obiectele mock ramase, i.e., stoc
        verify(stoc, times(1)).getId();
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
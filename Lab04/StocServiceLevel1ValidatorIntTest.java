package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceLevel1ValidatorIntTest {
    private Stoc stoc;
    private StocValidator stocValidator;
    private Repository<Integer, Stoc> stocRepo;

    private StocService stocService;

    @BeforeEach
    void setUp() {
        stoc = mock(Stoc.class);
        stocValidator = new StocValidator(); // integram primul nivel (top down breadth first)
        stocRepo = mock(Repository.class);//mock

        stocService = new StocService(stocRepo, stocValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealValidator() {
        //simulam o adaugare valida cu Stoc s = new Stoc(1, "Apa", 5, 1);
        //asociem comportamentul pentru obiectele mock
        when(stoc.getId()).thenReturn(1);
        when(stoc.getIngredient()).thenReturn("Apa");
        when(stoc.getCantitate()).thenReturn(5.00);
        when(stoc.getStocMinim()).thenReturn(1.00);
        when(stocRepo.save(stoc)).thenReturn(stoc);

        //apelam metoda add si evaluam apelul cu fail
        try{
            stocService.add(stoc);
        }catch (Exception e){
            fail("Invalid add operation");
        }

        // verificam interactiunea obiectului testat cu obiectele mock ramase, i.e., repository si stoc
        verify(stocRepo, times(1)).save(stoc);
        verify(stoc, times(2)).getCantitate();//apelat de 2 ori, la validare simpla si la comparatia cu stocul minim
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealValidator() {
        //asociem comportamente obiectelor mock
        when(stoc.getId()).thenReturn(-1);

        //apelam metoda si evaluam invalidarea obiectului
        Assertions.assertThrows(ValidationException.class, () -> {
            stocService.add(stoc);
        });

        //verificam interactiunea obiectului testat cu obiectele mock ramase, i.e., repository si stoc
        verify(stocRepo, never()).save(any());
        verify(stoc, times(1)).getId();
    }
}
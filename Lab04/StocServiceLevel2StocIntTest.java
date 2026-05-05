package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceLevel2StocIntTest {
    private Stoc stoc;
    private StocValidator stocValidator; // REAL
    private Repository<Integer, Stoc> stocRepo;

    private StocService stocService;

    @BeforeEach
    void setUp() {
        stocValidator = new StocValidator();
        stocRepo = mock(Repository.class);
        stoc = null; // integram primul branch, al doilea nivel (top down depth first)
        stocService = new StocService(stocRepo, stocValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealStoc() {
        Stoc stoc = new Stoc(1, "Apa", 5.0, 1.0);
        //asociem comportamentul pentru obiectele mock
        when(stocRepo.save(stoc)).thenReturn(stoc);

        //apelam metoda add si evaluam apelul cu fail
        try{
            stocService.add(stoc);
        }catch (Exception e){
            fail("Invalid add operation " + e);
        }

        // verificam interactiunea obiectului testat cu obiectele mock ramase, i.e., repository
        verify(stocRepo, times(1)).save(stoc);
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealStoc() {
        Stoc stoc = new Stoc(-1, "Apa", 5, 10);

        //asociem comportamente obiectelor mock
        when(stocRepo.save(stoc)).thenReturn(stoc);//nu se va ajunge la apelul metodei save

        //apelam metoda si evaluam invalidarea obiectului
        Assertions.assertThrows(ValidationException.class, () -> {
            stocService.add(stoc);
        });

        //verificam interactiunea obiectului testat cu obiectele mock ramase, i.e., repository, dar nu se ajunge la verificarea interactiunii
        verify(stocRepo, never()).save(any());
    }
}
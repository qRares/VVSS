package drinkshop.ut.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceMockitoTest {

    private Stoc stoc;
    private Validator<Stoc> stocValidator;
    private Repository<Integer, Stoc> stocRepo;

    private StocService stocService;

    @BeforeEach
    public void setUp(){
        //cream obiecte mock
        stoc = mock(Stoc.class);// in mod intentionat folosim obiecte mock pentru obiecte din pachetul domain sau model
        stocValidator = mock (Validator.class);
        stocRepo = mock( Repository.class);
        //cream obiectul testat
        stocService = new StocService(stocRepo, stocValidator);
    }

    @AfterEach
    public void tearDown(){
        stocService = null;
        stocRepo = null;
        stocValidator = null;
        stoc = null;
    }

    @Test
    @Order(1)
    public void testGetAllValid() {
        //cream obiecte mock suplimentare
        Stoc stoc1 = mock(Stoc.class);
        Stoc stoc2 = mock(Stoc.class);
        //asociem comportamente obiectelor mock
        when(stocRepo.findAll()).thenReturn(Arrays.asList(stoc1, stoc2));

        //testam metoda getAll din StocService cu assert
        assert 2 == stocService.getAll().size();

        //verificam interactiunile obiectului testat cu obiectele mock
        verify(stocValidator, never()).validate(stoc1);
        verify(stocRepo).findAll();
        verify(stocRepo, times(1)).findAll();
    }

    @Test
    @Order(2)
    void testAddInvalid() {
        //simulam adaugarea unui stoc invalid, i.e., Stoc s = new Stoc(-1, "", -5.0, -1.0);

        //stabilim comportamente pentru obiectele mock: Stoc, Validator, Repository
        when(stoc.getId()).thenReturn(-1);
        doThrow(new ValidationException("ID invalid!\n")).when(stocValidator).validate(stoc);
        when(stocRepo.save(stoc)).thenReturn(stoc);//nu este obligatoriu sa facem aceasta asociere, nu se va ajunge la apelul save

        //testam metoda add si evaluam apelul cu assert
        try{
            stocService.add(stoc);
        }catch (Exception e){
            assert e.getClass().equals(ValidationException.class);
        }

        //verificam interactiunile oiectului testat cu obiectele mock
        verify(stoc, never()).getId();
        verify(stocValidator, times(1)).validate(stoc);
        verify(stocRepo, never()).save(any());
    }

    @Test
    @Order(3)
    void testAddValid() {
        //cream obiecte mock suplimentare nefolosite in asocierea comportamentelor obiectelor mock
        Stoc stoc1= mock(Stoc.class);
        //asociem comportament obiectului mock, i.e., cazul in care nu se arunca exceptie
        doNothing().when(stocValidator).validate(stoc);
        when(stocRepo.save(stoc)).thenReturn(stoc);

        // apelam metoda add si evaluam rezultatul cu fail
        try{
            stocService.add(stoc);
        }catch (Exception e){
            fail("Invalid add operation");
        }

        assert 0==stocService.getAll().size();

        // verificari ale interactiunii obiectului testat cu obiectele mock
        verify(stocValidator, times(1)).validate(stoc);
        verify(stocRepo, times(1)).save(stoc);

        //verificari ale interactiunii obiectului testat cu obiecte mock nefolosite propriu-zis, i.e., stoc1
        verify(stocValidator, times(0)).validate(stoc1);
        verify(stocRepo, never()).save(stoc1);
    }

}

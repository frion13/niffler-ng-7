package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.ex.SpendNotFoundException;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SpendServiceTest {

    @Test
    void getSpendForUserShouldThrowExceptionInCaseThatIdIsIncorrectFormat(@Mock SpendRepository spendRepository,
                                                                          @Mock CategoryService categoryService) {
        final String incorrectId = "incorrect_id";
        SpendService spendService = new SpendService(spendRepository, categoryService);
        SpendNotFoundException ex = assertThrows(
                SpendNotFoundException.class,
                () -> spendService.getSpendForUser(incorrectId, "user")
        );
        assertEquals(
                "Can`t find spend by given id: " + incorrectId,
                ex.getMessage()
        );
    }

    @Test
    void getSpendForUserShouldThrowExceptionInCaseThatIdNotFoundInDb(@Mock SpendRepository spendRepository,
                                                                     @Mock CategoryService categoryService) {
        final UUID correctId = UUID.randomUUID();
        final String correctUsername = "user";

        Mockito.when(spendRepository.findByIdAndUsername(eq(correctId), eq(correctUsername)))
                .thenReturn(Optional.empty());

        SpendService spendService = new SpendService(spendRepository, categoryService);
        SpendNotFoundException ex = assertThrows(
                SpendNotFoundException.class,
                () -> spendService.getSpendForUser(correctId.toString(), correctUsername)
        );
        assertEquals(
                "Can`t find spend by given id: " + correctId,
                ex.getMessage()
        );
    }

    @Test
    void getSpendForUserShouldReturnCorrectJsonObject(@Mock SpendRepository spendRepository,
                                                      @Mock CategoryService categoryService) {
        final UUID correctId = UUID.randomUUID();
        final String correctUsername = "user";

        SpendEntity spend = new SpendEntity();
        CategoryEntity category = new CategoryEntity();
        spend.setId(correctId);
        spend.setUsername(correctUsername);
        spend.setCurrency(CurrencyValues.USD);
        spend.setAmount(150.15);
        spend.setDescription("unit-test description");
        spend.setSpendDate(new Date(0));

        category.setUsername(correctUsername);
        category.setName("Unit-test category");
        category.setArchived(false);
        category.setId(UUID.randomUUID());

        spend.setCategory(category);

        Mockito.when(spendRepository.findByIdAndUsername(eq(correctId), eq(correctUsername)))
                .thenReturn(Optional.of(
                        spend
                ));

        SpendService spendService = new SpendService(spendRepository, categoryService);

        final SpendJson result = spendService.getSpendForUser(correctId.toString(), correctUsername);

        Mockito.verify(spendRepository, Mockito.times(1))
                .findByIdAndUsername(eq(correctId), eq(correctUsername));

        assertEquals(
                "unit-test description",
                result.description()
        );
    }

}
package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final int MAX_CATEGORIES_SIZE = 7;

    @Test
    void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
        final String username = "not_found";
        final UUID id = UUID.randomUUID();

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.empty());

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "",
                username,
                true
        );

        CategoryNotFoundException ex = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t find category by id: '" + id + "'",
                ex.getMessage()
        );
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
    @ParameterizedTest
    void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                catName,
                username,
                true
        );

        InvalidCategoryNameException ex = assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t add category with name: '" + catName + "'",
                ex.getMessage()
        );
    }

    @Test
    void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));
        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "Бары",
                username,
                true
        );

        categoryService.update(categoryJson);
        ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
        verify(categoryRepository).save(argumentCaptor.capture());
        assertEquals("Бары", argumentCaptor.getValue().getName());
        assertEquals("duck", argumentCaptor.getValue().getUsername());
        assertTrue(argumentCaptor.getValue().isArchived());
        assertEquals(id, argumentCaptor.getValue().getId());
    }

    @Test
    void getAllCategoriesShouldFilterlExcludeArchived(@Mock CategoryRepository categoryRepository) {
        String username = "testUser";
        CategoryEntity activeCategory1 = new CategoryEntity();
        activeCategory1.setName("Food");
        activeCategory1.setUsername(username);
        activeCategory1.setArchived(false);

        CategoryEntity activeCategory2 = new CategoryEntity();
        activeCategory2.setName("Transport");
        activeCategory2.setUsername(username);
        activeCategory2.setArchived(false);

        CategoryEntity archivedCategory = new CategoryEntity();
        archivedCategory.setName("Old");
        archivedCategory.setUsername(username);
        archivedCategory.setArchived(true);

        when(categoryRepository.findAllByUsernameOrderByName(username))
                .thenReturn(List.of(activeCategory1, archivedCategory, activeCategory2));

        CategoryService categoryService = new CategoryService(categoryRepository);

        List<CategoryJson> resultExcludingArchived = categoryService.getAllCategories(username, true);

        assertEquals(2, resultExcludingArchived.size());
        assertEquals("Food", resultExcludingArchived.get(0).name());
        assertEquals("Transport", resultExcludingArchived.get(1).name());
    }

    @Test
    void getAllCategoriesShouldFilterArchived(@Mock CategoryRepository categoryRepository) {
        String username = "testUser";
        CategoryEntity activeCategory1 = new CategoryEntity();
        activeCategory1.setName("Food");
        activeCategory1.setUsername(username);
        activeCategory1.setArchived(false);

        CategoryEntity activeCategory2 = new CategoryEntity();
        activeCategory2.setName("Transport");
        activeCategory2.setUsername(username);
        activeCategory2.setArchived(false);

        CategoryEntity archivedCategory = new CategoryEntity();
        archivedCategory.setName("Old");
        archivedCategory.setUsername(username);
        archivedCategory.setArchived(true);

        when(categoryRepository.findAllByUsernameOrderByName(username))
                .thenReturn(List.of(activeCategory1, archivedCategory, activeCategory2));

        CategoryService categoryService = new CategoryService(categoryRepository);

        List<CategoryJson> resultIncludingAll = categoryService.getAllCategories(username, false);

        assertEquals(3, resultIncludingAll.size());
        assertEquals("Food", resultIncludingAll.get(0).name());
        assertEquals("Old", resultIncludingAll.get(1).name());
        assertEquals("Transport", resultIncludingAll.get(2).name());
    }

    @Test
    void updateShouldRejectUnarchiveWhenAboveLimit(@Mock CategoryRepository categoryRepository) {
        UUID id = UUID.randomUUID();
        String username = "user";
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setName("category");
        entity.setArchived(true);
        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.findByUsernameAndId(username, id))
                .thenReturn(Optional.of(entity));
        when(categoryRepository.countByUsernameAndArchived(username, false))
                .thenReturn((long) (MAX_CATEGORIES_SIZE + 1));

        CategoryJson input = new CategoryJson(id, "category", username, false);

        TooManyCategoriesException exception = assertThrows(TooManyCategoriesException.class,
                () -> categoryService.update(input));

        assertEquals("Can`t unarchive category for user: '" + entity.getUsername() + "'", exception.getMessage());
    }

    @Test
    void updateShouldNotCheckLimitWhenArchiving(@Mock CategoryRepository categoryRepository) {
        UUID id = UUID.randomUUID();
        String username = "user";
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setName("category");
        entity.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.findByUsernameAndId(username, id))
                .thenReturn(Optional.of(entity));
        when(categoryRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        CategoryJson input = new CategoryJson(id, "category", username, true);
        CategoryJson result = categoryService.update(input);

        assertTrue(result.archived());
    }

    @Test
    void updateShouldNotCheckLimitWhenNoArchiveStatusChange(@Mock CategoryRepository categoryRepository) {
        UUID id = UUID.randomUUID();
        String username = "user";
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setName("category");
        entity.setArchived(false);
        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.findByUsernameAndId(username, id))
                .thenReturn(Optional.of(entity));
        when(categoryRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        CategoryJson input = new CategoryJson(id, "newName", username, false);
        categoryService.update(input);
    }


    @Test
    void saveShouldSuccessfullyCreateNewCategory(@Mock CategoryRepository categoryRepository) {
        String username = "user";
        String categoryName = "Food";

        when(categoryRepository.countByUsernameAndArchived(username, false))
                .thenReturn((long) (MAX_CATEGORIES_SIZE - 1));
        when(categoryRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson input = new CategoryJson(null, categoryName, username, false);
        CategoryEntity result = categoryService.save(input);

        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        assertEquals(username, result.getUsername());
        assertFalse(result.isArchived());

        verify(categoryRepository).save(argThat(e ->
                e.getName().equals(categoryName) &&
                        !e.isArchived() &&
                        e.getUsername().equals(username)
        ));
    }

  @Test
  void saveShouldRejectWhenMaxCategoriesReached(@Mock CategoryRepository categoryRepository) {
    String username = "user";
    String categoryName = "Food";
    CategoryService categoryService = new CategoryService(categoryRepository);

    when(categoryRepository.countByUsernameAndArchived(username, false))
            .thenReturn((long) (MAX_CATEGORIES_SIZE+1));

    CategoryJson input = new CategoryJson(null, categoryName, username, false);

    assertThrows(TooManyCategoriesException.class,
            () -> categoryService.save(input));

    verify(categoryRepository, never()).save(any());
  }
}
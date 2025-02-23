package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private CategoryService testedObject;

    private final String username = "Ivan";

    private CategoryEntity nonArchivedCategory;
    private final UUID nonArchivedCategoryId = UUID.randomUUID();
    private final String nonArchivedCategoryName = "Non archived category";
    private final boolean nonArchivedCategoryState = false;

    private CategoryEntity firstArchivedCategory;
    private final UUID firstArchivedCategoryId = UUID.randomUUID();
    private final String firstArchivedCategoryName = "First archived category";
    private final boolean firstArchivedCategoryState = true;

    private CategoryEntity secondArchivedCategory;
    private final UUID secondArchivedCategoryId = UUID.randomUUID();
    private final String secondArchivedCategoryName = "Second archived category";
    private final boolean secondArchivedCategoryState = true;


    @BeforeEach
    void init() {
        nonArchivedCategory = new CategoryEntity();
        nonArchivedCategory.setId(nonArchivedCategoryId);
        nonArchivedCategory.setName(nonArchivedCategoryName);
        nonArchivedCategory.setUsername(username);
        nonArchivedCategory.setArchived(nonArchivedCategoryState);

        firstArchivedCategory = new CategoryEntity();
        firstArchivedCategory.setId(firstArchivedCategoryId);
        firstArchivedCategory.setName(firstArchivedCategoryName);
        firstArchivedCategory.setUsername(username);
        firstArchivedCategory.setArchived(firstArchivedCategoryState);

        secondArchivedCategory = new CategoryEntity();
        secondArchivedCategory.setId(secondArchivedCategoryId);
        secondArchivedCategory.setName(secondArchivedCategoryName);
        secondArchivedCategory.setUsername(username);
        secondArchivedCategory.setArchived(secondArchivedCategoryState);

    }

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

        CategoryNotFoundException ex = Assertions.assertThrows(
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

        InvalidCategoryNameException ex = Assertions.assertThrows(
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
    void shouldFilterCategoriesByArchived(@Mock CategoryRepository categoryRepository) {
        List<CategoryEntity> categoryEntities = new ArrayList<>(
                List.of(nonArchivedCategory, firstArchivedCategory, secondArchivedCategory)
        );

        when(categoryRepository.findAllByUsernameOrderByName(eq(username))).thenReturn(categoryEntities);

        testedObject = new CategoryService(categoryRepository);
        List<CategoryJson> result = testedObject.getAllCategories(username, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(nonArchivedCategoryName, result.getFirst().name());
    }

    @Test
    void categoryShouldNotBeUnarchived(@Mock CategoryRepository categoryRepository) {
        CategoryJson categoryJson = new CategoryJson(
                firstArchivedCategoryId,
                firstArchivedCategoryName,
                username,
                false
        );

        when(categoryRepository.findByUsernameAndId(
                eq(categoryJson.username()),
                eq(categoryJson.id()))
        ).thenReturn(Optional.of(firstArchivedCategory));

        when(categoryRepository.countByUsernameAndArchived(
                eq(categoryJson.username()),
                eq(false))
        ).thenReturn(8L);


        testedObject = new CategoryService(categoryRepository);
        TooManyCategoriesException exception = assertThrows(
                TooManyCategoriesException.class, () -> testedObject.update(categoryJson));

        assertNotNull(exception);
        assertEquals(
                "Can`t unarchive category for user: '" + categoryJson.username() + "'",
                exception.getMessage()
        );
    }

//    @Test
    void categoryShouldBeSave(@Mock CategoryRepository categoryRepository) {
        CategoryJson categoryJson = new CategoryJson(
                firstArchivedCategoryId,
                firstArchivedCategoryName,
                username,
                false
        );

        when(categoryRepository.countByUsernameAndArchived(
                eq(categoryJson.username()),
                eq(false))
        ).thenReturn(7L);


        testedObject = new CategoryService(categoryRepository);
        TooManyCategoriesException exception = assertThrows(
                TooManyCategoriesException.class, () -> testedObject.save(categoryJson));

        assertNotNull(exception);
        assertEquals(
                "Can`t add over than 8 categories for user: '" + categoryJson.username() + "'",
                exception.getMessage()
        );
    }
}

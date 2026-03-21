package com.jobly.service;

import com.jobly.dao.CategoryDao;
import com.jobly.gen.model.Category;
import com.jobly.gen.model.CategoryCreateRequest;
import com.jobly.gen.model.CategoryUpdateRequest;
import com.jobly.gen.model.GetAllCategoriesResponse;
import com.jobly.model.CategoryEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.jobly.util.TestEntityFactory.buildCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findById_mapsEntityToCategory() {
        Long categoryId = 5L;
        CategoryEntity entity = buildCategory(categoryId, "Engineering");
        entity.setDescription("Core engineering roles");

        when(categoryDao.findById(categoryId)).thenReturn(entity);

        Category response = categoryService.findById(categoryId);

        assertEquals(categoryId, response.getId());
        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getDescription(), response.getDescription());
    }

    @Test
    void findAll_returnsTotalAndCategories() {
        CategoryEntity first = buildCategory(1L, "Engineering");
        CategoryEntity second = buildCategory(2L, "Design");

        when(categoryDao.findAll()).thenReturn(List.of(first, second));

        GetAllCategoriesResponse response = categoryService.findAll();

        assertEquals(2, response.getTotal());
        assertNotNull(response.getCategories());
        assertEquals(2, response.getCategories().size());
        assertEquals(first.getId(), response.getCategories().get(0).getId());
    }

    @Test
    void createCategory_savesAndReturnsMappedCategory() {
        CategoryCreateRequest request = new CategoryCreateRequest()
                .name("Product")
                .description("Product management");

        CategoryEntity saved = buildCategory(9L, "Product");
        saved.setDescription("Product management");

        when(categoryDao.save(org.mockito.ArgumentMatchers.any(CategoryEntity.class))).thenReturn(saved);

        Category response = categoryService.createCategory(request);

        assertEquals(saved.getId(), response.getId());
        assertEquals(saved.getName(), response.getName());
        assertEquals(saved.getDescription(), response.getDescription());
    }

    @Test
    void updateCategory_updatesEntityAndReturnsMappedCategory() {
        Long categoryId = 10L;
        CategoryUpdateRequest request = new CategoryUpdateRequest()
                .name("Marketing")
                .description("Growth marketing");

        CategoryEntity existing = buildCategory(categoryId, "Old name");

        when(categoryDao.findById(categoryId)).thenReturn(existing);
        when(categoryDao.save(existing)).thenReturn(existing);

        Category response = categoryService.updateCategory(categoryId, request);

        assertEquals(categoryId, response.getId());
        assertEquals("Marketing", response.getName());
        assertEquals("Growth marketing", response.getDescription());
        verify(categoryDao).save(existing);
    }

    @Test
    void deleteCategoryById_delegatesToDao() {
        Long categoryId = 11L;

        categoryService.deleteCategoryById(categoryId);

        verify(categoryDao).deleteById(categoryId);
    }

    @Test
    void findEntityById_returnsEntity() {
        Long categoryId = 12L;
        CategoryEntity entity = buildCategory(categoryId, "Legal");

        when(categoryDao.findById(categoryId)).thenReturn(entity);

        CategoryEntity response = categoryService.findEntityById(categoryId);

        assertEquals(entity, response);
    }
}


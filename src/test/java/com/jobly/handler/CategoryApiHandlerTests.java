package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.CategoriesApiController;
import com.jobly.gen.model.Category;
import com.jobly.gen.model.CategoryCreateRequest;
import com.jobly.gen.model.CategoryUpdateRequest;
import com.jobly.gen.model.GetAllCategoriesResponse;
import com.jobly.security.filter.JwtAuthenticationFilter;
import com.jobly.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CategoriesApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(CategoryApiHandler.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class CategoryApiHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getCategoryById_returnsCategory() throws Exception {
        Long categoryId = 10L;
        Category response = new Category()
                .id(categoryId)
                .name("Engineering")
                .description("Core engineering roles");

        when(categoryService.findById(categoryId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Engineering"));

        verify(categoryService).findById(categoryId);
    }

    @Test
    void getAllCategories_returnsList() throws Exception {
        Category first = new Category().id(1L).name("Engineering");
        Category second = new Category().id(2L).name("Design");
        GetAllCategoriesResponse response = new GetAllCategoriesResponse()
                .total(2)
                .categories(List.of(first, second));

        when(categoryService.findAll()).thenReturn(response);

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.categories[0].id").value(1))
                .andExpect(jsonPath("$.categories[1].id").value(2));

        verify(categoryService).findAll();
    }

    @Test
    void createCategory_returnsCreated() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest()
                .name("Marketing")
                .description("Growth marketing roles");
        Category response = new Category()
                .id(55L)
                .name("Marketing")
                .description("Growth marketing roles");

        when(categoryService.createCategory(any(CategoryCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/categories/55"))
                .andExpect(jsonPath("$.id").value(55))
                .andExpect(jsonPath("$.name").value("Marketing"));

        verify(categoryService).createCategory(any(CategoryCreateRequest.class));
    }

    @Test
    void updateCategory_returnsUpdated() throws Exception {
        Long categoryId = 12L;
        CategoryUpdateRequest request = new CategoryUpdateRequest()
                .name("Product")
                .description("Product roles");
        Category response = new Category()
                .id(categoryId)
                .name("Product")
                .description("Product roles");

        when(categoryService.updateCategory(categoryId, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Product"));

        verify(categoryService).updateCategory(categoryId, request);
    }

    @Test
    void deleteCategory_returnsNoContent() throws Exception {
        Long categoryId = 13L;

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategoryById(categoryId);
    }
}

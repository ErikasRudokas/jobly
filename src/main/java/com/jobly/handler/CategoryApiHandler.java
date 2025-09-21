package com.jobly.handler;

import com.jobly.gen.api.CategoriesApiDelegate;
import com.jobly.gen.model.Category;
import com.jobly.gen.model.CategoryCreateRequest;
import com.jobly.gen.model.CategoryUpdateRequest;
import com.jobly.gen.model.GetAllCategoriesResponse;
import com.jobly.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryApiHandler implements CategoriesApiDelegate {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<Category> getCategoryById(Long id) {
        log.info("Getting category with id {}", id);
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @Override
    public ResponseEntity<GetAllCategoriesResponse> getAllCategories() {
        log.info("Getting all categories");
        return ResponseEntity.ok(categoryService.findAll());
    }

    @Override
    public ResponseEntity<Category> createCategory(CategoryCreateRequest categoryCreateRequest) {
        log.info("Initiating creation of category with name {}", categoryCreateRequest.getName());
        var createdCategory = categoryService.createCategory(categoryCreateRequest);
        return ResponseEntity.created(URI.create("/api/v1/categories/" + createdCategory.getId()))
                .body(createdCategory);
    }

    @Override
    public ResponseEntity<Category> updateCategory(Long id, CategoryUpdateRequest categoryUpdateRequest){
        log.info("Updating category with id {}", id);
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryUpdateRequest));
    }

    @Override
    public ResponseEntity<Void> deleteCategoryById(Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}

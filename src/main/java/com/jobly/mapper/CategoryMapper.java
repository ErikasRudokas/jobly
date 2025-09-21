package com.jobly.mapper;

import com.jobly.gen.model.Category;
import com.jobly.gen.model.CategoryCreateRequest;
import com.jobly.gen.model.CategoryUpdateRequest;
import com.jobly.model.CategoryEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static Category toCategory(CategoryEntity categoryEntity) {
        var category = new Category();
        category.setId(categoryEntity.getId());
        category.setName(categoryEntity.getName());
        category.setDescription(categoryEntity.getDescription());
        category.setCreatedAt(categoryEntity.getCreatedAt());
        category.setUpdatedAt(categoryEntity.getUpdatedAt());
        return category;
    }

    public static CategoryEntity toCategoryEntity(CategoryCreateRequest categoryCreateRequest) {
        var categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryCreateRequest.getName());
        categoryEntity.setDescription(categoryCreateRequest.getDescription());
        return categoryEntity;
    }

    public static void updateEntity(CategoryEntity existingCategory, CategoryUpdateRequest categoryUpdateRequest) {
        existingCategory.setName(categoryUpdateRequest.getName());
        existingCategory.setDescription(categoryUpdateRequest.getDescription());
    }
}

package com.jobly.service;

import com.jobly.dao.CategoryDao;
import com.jobly.gen.model.Category;
import com.jobly.gen.model.CategoryCreateRequest;
import com.jobly.gen.model.CategoryUpdateRequest;
import com.jobly.gen.model.GetAllCategoriesResponse;
import com.jobly.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryDao categoryDao;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Category findById(Long id) {
        return CategoryMapper.toCategory(categoryDao.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'EMPLOYER')")
    public GetAllCategoriesResponse findAll() {
        var categories = categoryDao.findAll()
                .stream()
                .map(CategoryMapper::toCategory)
                .toList();

        return new GetAllCategoriesResponse().categories(categories);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Category createCategory(CategoryCreateRequest categoryCreateRequest) {
        var categoryEntity = CategoryMapper.toCategoryEntity(categoryCreateRequest);
        return CategoryMapper.toCategory(categoryDao.save(categoryEntity));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Category updateCategory(Long id, CategoryUpdateRequest categoryUpdateRequest) {
        var existingCategory = categoryDao.findById(id);
        CategoryMapper.updateEntity(existingCategory, categoryUpdateRequest);
        return CategoryMapper.toCategory(categoryDao.save(existingCategory));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteCategoryById(Long id) {
        categoryDao.deleteById(id);
    }
}

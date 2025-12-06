package com.whu.pet.controller;

import com.whu.pet.common.Result;
import com.whu.pet.entity.PetCategory;
import com.whu.pet.service.PetCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class PetCategoryController {

    @Autowired
    private PetCategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping
    public Result<List<PetCategory>> getAllCategories() {
        return Result.success(categoryService.list());
    }

    /**
     * 添加分类（管理员/救助站）
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER')")
    public Result<Void> addCategory(@RequestBody PetCategory category) {
        if (categoryService.save(category)) {
            return Result.success();
        }
        return Result.error("添加失败");
    }

    /**
     * 更新分类（管理员/救助站）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER')")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody PetCategory category) {
        category.setId(id);
        if (categoryService.updateById(category)) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        if (categoryService.removeById(id)) {
            return Result.success();
        }
        return Result.error("删除失败");
    }
}

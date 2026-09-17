package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.NewsCategory;
import com.shouyang.service.NewsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台资讯分类管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/category")
public class CategoryAdminController {

    @Autowired
    private NewsCategoryService newsCategoryService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<NewsCategory>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<NewsCategory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(NewsCategory::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(NewsCategory::getStatus, status);
        }
        wrapper.orderByAsc(NewsCategory::getSort).orderByDesc(NewsCategory::getCreateTime);
        IPage<NewsCategory> result = newsCategoryService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 全部列表（下拉选用）
     */
    @GetMapping("/all")
    public Result<java.util.List<NewsCategory>> all() {
        LambdaQueryWrapper<NewsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NewsCategory::getStatus, 1).orderByAsc(NewsCategory::getSort);
        return Result.success(newsCategoryService.list(wrapper));
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<NewsCategory> detail(@PathVariable Long id) {
        return Result.success(newsCategoryService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody NewsCategory category) {
        newsCategoryService.save(category);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody NewsCategory category) {
        newsCategoryService.updateById(category);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        newsCategoryService.removeById(id);
        return Result.success("删除成功", null);
    }
}

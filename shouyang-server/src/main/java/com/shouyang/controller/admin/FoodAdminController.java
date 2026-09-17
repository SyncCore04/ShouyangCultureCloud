package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Food;
import com.shouyang.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台美食管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/food")
public class FoodAdminController {

    @Autowired
    private FoodService foodService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<Food>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Food::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(Food::getStatus, status);
        }
        wrapper.orderByDesc(Food::getCreateTime);
        IPage<Food> result = foodService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Food> detail(@PathVariable Long id) {
        return Result.success(foodService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Food food) {
        foodService.save(food);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Food food) {
        foodService.updateById(food);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        foodService.removeById(id);
        return Result.success("删除成功", null);
    }
}

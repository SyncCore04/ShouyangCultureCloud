package com.shouyang.controller.api;

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
 * 前台美食 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/food")
public class ApiFoodController {

    @Autowired
    private FoodService foodService;

    /**
     * 美食列表（分页）
     *
     * @param page     当前页
     * @param size     每页条数
     * @param keyword  关键词（名称模糊搜索）
     * @param category 类型筛选（主食/小吃/菜肴等）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Food>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Food::getName, keyword.trim());
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Food::getCategory, category.trim());
        }
        wrapper.orderByDesc(Food::getCreateTime);

        IPage<Food> result = foodService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 美食详情（浏览量 +1）
     *
     * @param id 美食ID
     * @return 美食详情
     */
    @GetMapping("/{id}")
    public Result<Food> detail(@PathVariable Long id) {
        Food food = foodService.getById(id);
        if (food == null || food.getStatus() != 1) {
            return Result.error(404, "美食不存在或已下架");
        }
        // 浏览量 +1
        foodService.lambdaUpdate()
                .eq(Food::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
        return Result.success(food);
    }
}

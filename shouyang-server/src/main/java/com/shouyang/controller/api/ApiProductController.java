package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Product;
import com.shouyang.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 前台文创商品 Controller（仅展示，不做交易）
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/product")
public class ApiProductController {

    @Autowired
    private ProductService productService;

    /**
     * 商品列表（分页，支持关键词搜索和价格排序）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param keyword 关键词（名称模糊搜索）
     * @param sort    排序：default默认 / price_asc价格升序 / price_desc价格降序
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "default") String sort) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword.trim());
        }

        // 排序
        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }

        IPage<Product> result = productService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 商品详情（含多图）
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null || product.getStatus() != 1) {
            return Result.error(404, "商品不存在或已下架");
        }
        return Result.success(product);
    }
}

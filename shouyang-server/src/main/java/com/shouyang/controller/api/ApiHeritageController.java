package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Heritage;
import com.shouyang.service.HeritageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 前台非遗文化 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/heritage")
public class ApiHeritageController {

    @Autowired
    private HeritageService heritageService;

    /**
     * 非遗列表（分页，支持级别和类别筛选）
     *
     * @param page     当前页
     * @param size     每页条数
     * @param level    级别（国家级/省级/市级/县级）
     * @param category 类别（传统技艺/民俗/传统音乐等）
     * @param keyword  关键词（名称模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Heritage>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Heritage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Heritage::getStatus, 1);
        if (StringUtils.hasText(level)) {
            wrapper.eq(Heritage::getLevel, level.trim());
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Heritage::getCategory, category.trim());
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Heritage::getName, keyword.trim());
        }
        wrapper.orderByDesc(Heritage::getCreateTime);

        IPage<Heritage> result = heritageService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 非遗详情
     *
     * @param id 非遗ID
     * @return 非遗详情
     */
    @GetMapping("/{id}")
    public Result<Heritage> detail(@PathVariable Long id) {
        Heritage heritage = heritageService.getById(id);
        if (heritage == null || heritage.getStatus() != 1) {
            return Result.error(404, "非遗项目不存在或已下架");
        }
        return Result.success(heritage);
    }
}

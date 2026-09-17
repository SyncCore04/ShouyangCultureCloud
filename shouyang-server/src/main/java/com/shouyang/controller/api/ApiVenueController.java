package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Venue;
import com.shouyang.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 前台场馆 Controller（仅展示）
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/venue")
public class ApiVenueController {

    @Autowired
    private VenueService venueService;

    /**
     * 场馆列表（分页）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param keyword 关键词（名称模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Venue>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Venue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Venue::getStatus, 1); // 只查开放的
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Venue::getName, keyword.trim());
        }
        wrapper.orderByDesc(Venue::getCreateTime);

        IPage<Venue> result = venueService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 场馆详情
     *
     * @param id 场馆ID
     * @return 场馆详情
     */
    @GetMapping("/{id}")
    public Result<Venue> detail(@PathVariable Long id) {
        Venue venue = venueService.getById(id);
        if (venue == null || venue.getStatus() != 1) {
            return Result.error(404, "场馆不存在或已关闭");
        }
        return Result.success(venue);
    }
}

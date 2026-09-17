package com.shouyang.controller.admin;

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
 * 后台场馆管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/venue")
public class VenueAdminController {

    @Autowired
    private VenueService venueService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<Venue>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Venue> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Venue::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(Venue::getStatus, status);
        }
        wrapper.orderByDesc(Venue::getCreateTime);
        IPage<Venue> result = venueService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Venue> detail(@PathVariable Long id) {
        return Result.success(venueService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Venue venue) {
        venueService.save(venue);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Venue venue) {
        venueService.updateById(venue);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        venueService.removeById(id);
        return Result.success("删除成功", null);
    }
}

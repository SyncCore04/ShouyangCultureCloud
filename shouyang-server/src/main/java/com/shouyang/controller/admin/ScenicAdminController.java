package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.ScenicSpot;
import com.shouyang.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台景点管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/scenic")
public class ScenicAdminController {

    @Autowired
    private ScenicSpotService scenicSpotService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<ScenicSpot>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ScenicSpot::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(ScenicSpot::getStatus, status);
        }
        wrapper.orderByDesc(ScenicSpot::getCreateTime);
        IPage<ScenicSpot> result = scenicSpotService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<ScenicSpot> detail(@PathVariable Long id) {
        return Result.success(scenicSpotService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody ScenicSpot scenicSpot) {
        scenicSpotService.save(scenicSpot);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody ScenicSpot scenicSpot) {
        scenicSpotService.updateById(scenicSpot);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scenicSpotService.removeById(id);
        return Result.success("删除成功", null);
    }
}

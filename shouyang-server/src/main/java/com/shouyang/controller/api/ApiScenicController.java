package com.shouyang.controller.api;

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
 * 前台景点 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/scenic")
public class ApiScenicController {

    @Autowired
    private ScenicSpotService scenicSpotService;

    /**
     * 景点列表（分页）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param keyword 关键词（名称模糊搜索）
     * @param level   等级筛选（5A/4A/3A）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<ScenicSpot>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level) {
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicSpot::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ScenicSpot::getName, keyword.trim());
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(ScenicSpot::getLevel, level.trim());
        }
        wrapper.orderByAsc(ScenicSpot::getSort)
                .orderByDesc(ScenicSpot::getCreateTime);

        IPage<ScenicSpot> result = scenicSpotService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 景点详情（浏览量 +1）
     *
     * @param id 景点ID
     * @return 景点详情
     */
    @GetMapping("/{id}")
    public Result<ScenicSpot> detail(@PathVariable Long id) {
        ScenicSpot scenic = scenicSpotService.getById(id);
        if (scenic == null || scenic.getStatus() != 1) {
            return Result.error(404, "景点不存在或已下架");
        }
        // 浏览量 +1
        scenicSpotService.lambdaUpdate()
                .eq(ScenicSpot::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
        return Result.success(scenic);
    }
}

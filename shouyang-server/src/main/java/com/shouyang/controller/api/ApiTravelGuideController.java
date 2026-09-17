package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.TravelGuide;
import com.shouyang.service.TravelGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 前台旅游攻略 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/guide")
public class ApiTravelGuideController {

    @Autowired
    private TravelGuideService travelGuideService;

    /**
     * 攻略列表（分页）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param keyword 关键词（标题模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<TravelGuide>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TravelGuide::getTitle, keyword.trim());
        }
        wrapper.orderByDesc(TravelGuide::getCreateTime);

        IPage<TravelGuide> result = travelGuideService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 攻略详情（浏览量 +1）
     *
     * @param id 攻略ID
     * @return 攻略详情
     */
    @GetMapping("/{id}")
    public Result<TravelGuide> detail(@PathVariable Long id) {
        TravelGuide guide = travelGuideService.getById(id);
        if (guide == null || guide.getStatus() != 1) {
            return Result.error(404, "攻略不存在或已下架");
        }
        // 浏览量 +1
        travelGuideService.lambdaUpdate()
                .eq(TravelGuide::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
        return Result.success(guide);
    }
}

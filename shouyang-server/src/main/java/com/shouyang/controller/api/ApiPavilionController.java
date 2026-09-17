package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Pavilion;
import com.shouyang.service.PavilionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 前台数字展馆 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/pavilion")
public class ApiPavilionController {

    @Autowired
    private PavilionService pavilionService;

    /**
     * 数字展馆列表（分页，按 sort 排序）
     *
     * @param page 当前页
     * @param size 每页条数
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Pavilion>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        LambdaQueryWrapper<Pavilion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pavilion::getStatus, 1);
        wrapper.orderByAsc(Pavilion::getSort)
                .orderByDesc(Pavilion::getCreateTime);

        IPage<Pavilion> result = pavilionService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 数字展馆详情（浏览量 +1）
     *
     * @param id 展馆ID
     * @return 展馆详情
     */
    @GetMapping("/{id}")
    public Result<Pavilion> detail(@PathVariable Long id) {
        Pavilion pavilion = pavilionService.getById(id);
        if (pavilion == null || pavilion.getStatus() != 1) {
            return Result.error(404, "展馆不存在或已下架");
        }
        // 浏览量 +1
        pavilionService.lambdaUpdate()
                .eq(Pavilion::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
        return Result.success(pavilion);
    }
}

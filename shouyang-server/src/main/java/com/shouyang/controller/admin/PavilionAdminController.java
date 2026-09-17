package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Pavilion;
import com.shouyang.service.PavilionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台数字展馆管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/pavilion")
public class PavilionAdminController {

    @Autowired
    private PavilionService pavilionService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<Pavilion>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Pavilion> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Pavilion::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(Pavilion::getStatus, status);
        }
        wrapper.orderByAsc(Pavilion::getSort).orderByDesc(Pavilion::getCreateTime);
        IPage<Pavilion> result = pavilionService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Pavilion> detail(@PathVariable Long id) {
        return Result.success(pavilionService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Pavilion pavilion) {
        pavilionService.save(pavilion);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Pavilion pavilion) {
        pavilionService.updateById(pavilion);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        pavilionService.removeById(id);
        return Result.success("删除成功", null);
    }
}

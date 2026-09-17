package com.shouyang.controller.admin;

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
 * 后台非遗文化管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/heritage")
public class HeritageAdminController {

    @Autowired
    private HeritageService heritageService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<Heritage>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Heritage> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Heritage::getName, keyword.trim());
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(Heritage::getLevel, level.trim());
        }
        if (status != null) {
            wrapper.eq(Heritage::getStatus, status);
        }
        wrapper.orderByDesc(Heritage::getCreateTime);
        IPage<Heritage> result = heritageService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Heritage> detail(@PathVariable Long id) {
        return Result.success(heritageService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Heritage heritage) {
        heritageService.save(heritage);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Heritage heritage) {
        heritageService.updateById(heritage);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        heritageService.removeById(id);
        return Result.success("删除成功", null);
    }
}

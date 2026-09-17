package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Organization;
import com.shouyang.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台文旅单位管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/org")
public class OrgAdminController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<Organization>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Organization::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(Organization::getStatus, status);
        }
        wrapper.orderByDesc(Organization::getCreateTime);
        IPage<Organization> result = organizationService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Organization> detail(@PathVariable Long id) {
        return Result.success(organizationService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Organization org) {
        organizationService.save(org);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Organization org) {
        organizationService.updateById(org);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        organizationService.removeById(id);
        return Result.success("删除成功", null);
    }
}

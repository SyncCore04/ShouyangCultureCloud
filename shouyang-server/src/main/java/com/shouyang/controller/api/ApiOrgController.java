package com.shouyang.controller.api;

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
 * 前台文旅单位 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/org")
public class ApiOrgController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * 文旅单位列表（分页）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param keyword 关键词（名称模糊搜索）
     * @param type    类型筛选（图书馆/文化馆/文化站/文化中心）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Organization>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Organization::getName, keyword.trim());
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Organization::getType, type.trim());
        }
        wrapper.orderByAsc(Organization::getSort)
                .orderByDesc(Organization::getCreateTime);

        IPage<Organization> result = organizationService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 文旅单位详情
     *
     * @param id 单位ID
     * @return 单位详情
     */
    @GetMapping("/{id}")
    public Result<Organization> detail(@PathVariable Long id) {
        Organization org = organizationService.getById(id);
        if (org == null || org.getStatus() != 1) {
            return Result.error(404, "文旅单位不存在或已下架");
        }
        return Result.success(org);
    }
}

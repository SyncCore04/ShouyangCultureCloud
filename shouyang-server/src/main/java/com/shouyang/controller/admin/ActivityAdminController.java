package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Activity;
import com.shouyang.entity.ActivityRegister;
import com.shouyang.service.ActivityRegisterService;
import com.shouyang.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台活动管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/activity")
public class ActivityAdminController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRegisterService activityRegisterService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<Activity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Activity::getTitle, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        wrapper.orderByDesc(Activity::getCreateTime);
        IPage<Activity> result = activityService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 报名记录列表
     */
    @GetMapping("/register/list")
    public Result<IPage<ActivityRegister>> registerList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<ActivityRegister> wrapper = new LambdaQueryWrapper<>();
        if (activityId != null) {
            wrapper.eq(ActivityRegister::getActivityId, activityId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ActivityRegister::getName, keyword.trim())
                    .or().like(ActivityRegister::getPhone, keyword.trim()));
        }
        wrapper.orderByDesc(ActivityRegister::getCreateTime);
        IPage<ActivityRegister> result = activityRegisterService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Activity> detail(@PathVariable Long id) {
        return Result.success(activityService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Activity activity) {
        activityService.save(activity);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Activity activity) {
        activityService.updateById(activity);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.removeById(id);
        return Result.success("删除成功", null);
    }
}

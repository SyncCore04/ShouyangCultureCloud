package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.dto.ActivityRegisterDTO;
import com.shouyang.entity.Activity;
import com.shouyang.entity.ActivityRegister;
import com.shouyang.service.ActivityRegisterService;
import com.shouyang.service.ActivityService;
import com.shouyang.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台活动 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/activity")
public class ApiActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRegisterService activityRegisterService;

    /**
     * 活动列表（分页）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param status  状态筛选（0未开始 1进行中 2已结束，可选）
     * @param keyword 关键词（标题模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Activity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Activity::getTitle, keyword.trim());
        }
        // 按开始时间倒序（最新的活动在前）
        wrapper.orderByDesc(Activity::getStartTime);

        IPage<Activity> result = activityService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 活动详情
     *
     * @param id 活动ID
     * @return 活动详情（含当前用户是否已报名）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error(404, "活动不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("activity", activity);

        // 如果用户已登录，返回是否已报名
        if (UserContext.isLogin()) {
            Long userId = UserContext.getUserId();
            boolean isRegistered = activityRegisterService.isRegistered(id, userId);
            result.put("isRegistered", isRegistered);
        } else {
            result.put("isRegistered", false);
        }

        return Result.success(result);
    }

    /**
     * 活动报名（需登录）
     *
     * @param dto 报名参数
     * @return 报名记录ID
     */
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody ActivityRegisterDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Long registerId = activityRegisterService.register(
                dto.getActivityId(),
                userId,
                dto.getName(),
                dto.getPhone(),
                dto.getRemark()
        );
        return Result.success(registerId);
    }

    /**
     * 我的报名列表（需登录）
     *
     * @param page 当前页
     * @param size 每页条数
     * @return 报名记录列表（含活动信息）
     */
    @GetMapping("/my/list")
    public Result<IPage<ActivityRegister>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        LambdaQueryWrapper<ActivityRegister> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegister::getUserId, userId)
                .eq(ActivityRegister::getStatus, 1) // 只查有效报名
                .orderByDesc(ActivityRegister::getCreateTime);

        IPage<ActivityRegister> result = activityRegisterService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }
}

package com.shouyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shouyang.common.exception.BusinessException;
import com.shouyang.entity.Activity;
import com.shouyang.entity.ActivityRegister;
import com.shouyang.mapper.ActivityRegisterMapper;
import com.shouyang.service.ActivityRegisterService;
import com.shouyang.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 活动报名 Service 实现类
 *
 * @author shouyang
 */
@Service
public class ActivityRegisterServiceImpl extends ServiceImpl<ActivityRegisterMapper, ActivityRegister> implements ActivityRegisterService {

    @Autowired
    private ActivityService activityService;

    /**
     * 活动报名（含完整校验）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(Long activityId, Long userId, String name, String phone, String remark) {
        // 1. 校验活动是否存在
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 2. 校验活动状态（已结束的活动不能报名）
        if (activity.getStatus() != null && activity.getStatus() == 2) {
            throw new BusinessException("活动已结束，无法报名");
        }

        // 3. 校验是否在报名时间内
        if (activity.getSignupDeadline() != null
                && LocalDateTime.now().isAfter(activity.getSignupDeadline())) {
            throw new BusinessException("报名已截止");
        }

        // 4. 校验人数是否已满（maxPeople=0 表示不限）
        if (activity.getMaxPeople() != null && activity.getMaxPeople() > 0) {
            int currentCount = activity.getSignupCount() != null ? activity.getSignupCount() : 0;
            if (currentCount >= activity.getMaxPeople()) {
                throw new BusinessException("报名人数已满");
            }
        }

        // 5. 校验是否已报名（同一用户不能重复报名同一活动）
        if (isRegistered(activityId, userId)) {
            throw new BusinessException("您已报名该活动，请勿重复报名");
        }

        // 6. 创建报名记录
        ActivityRegister register = new ActivityRegister();
        register.setActivityId(activityId);
        register.setUserId(userId);
        register.setName(name);
        register.setPhone(phone);
        register.setRemark(remark);
        register.setStatus((byte) 1); // 1=已报名
        this.save(register);

        // 7. 活动报名人数 +1
        activityService.lambdaUpdate()
                .eq(Activity::getId, activityId)
                .setSql("signup_count = signup_count + 1")
                .update();

        return register.getId();
    }

    /**
     * 检查用户是否已报名该活动
     */
    @Override
    public boolean isRegistered(Long activityId, Long userId) {
        long count = this.count(new LambdaQueryWrapper<ActivityRegister>()
                .eq(ActivityRegister::getActivityId, activityId)
                .eq(ActivityRegister::getUserId, userId)
                .eq(ActivityRegister::getStatus, 1)); // 只统计有效报名
        return count > 0;
    }
}

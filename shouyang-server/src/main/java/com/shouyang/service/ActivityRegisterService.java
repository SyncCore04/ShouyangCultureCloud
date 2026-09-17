package com.shouyang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shouyang.entity.ActivityRegister;

/**
 * 活动报名 Service 接口
 *
 * @author shouyang
 */
public interface ActivityRegisterService extends IService<ActivityRegister> {

    /**
     * 活动报名
     *
     * @param activityId 活动ID
     * @param userId     用户ID
     * @param name       报名人姓名
     * @param phone      联系电话
     * @param remark     备注
     * @return 报名记录ID
     */
    Long register(Long activityId, Long userId, String name, String phone, String remark);

    /**
     * 检查用户是否已报名该活动
     *
     * @param activityId 活动ID
     * @param userId     用户ID
     * @return 是否已报名
     */
    boolean isRegistered(Long activityId, Long userId);
}

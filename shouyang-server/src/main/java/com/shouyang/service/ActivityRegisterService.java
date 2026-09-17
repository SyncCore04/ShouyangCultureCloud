package com.shouyang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shouyang.entity.ActivityRegister;
import com.shouyang.vo.ActivityRegisterVO;

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

    /**
     * 我的报名列表（含活动信息）
     *
     * @param userId 用户ID
     * @param page   当前页
     * @param size   每页条数
     * @return 分页结果
     */
    IPage<ActivityRegisterVO> getMyRegisterList(Long userId, int page, int size);

    /**
     * 取消报名（仅未开始的活动可取消）
     *
     * @param userId     用户ID
     * @param registerId 报名记录ID
     */
    void cancelRegister(Long userId, Long registerId);
}

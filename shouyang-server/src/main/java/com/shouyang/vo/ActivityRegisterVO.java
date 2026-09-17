package com.shouyang.vo;

import com.shouyang.entity.Activity;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 我的报名 VO（报名记录 + 活动信息）
 *
 * @author shouyang
 */
@Data
public class ActivityRegisterVO {

    /** 报名记录ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 活动ID */
    private Long activityId;

    /** 报名人姓名 */
    private String name;

    /** 报名人电话 */
    private String phone;

    /** 备注 */
    private String remark;

    /** 报名时间 */
    private LocalDateTime createTime;

    /** 活动名称 */
    private String activityName;

    /** 活动封面图 */
    private String coverImage;

    /** 活动开始时间 */
    private LocalDateTime startTime;

    /** 活动结束时间 */
    private LocalDateTime endTime;

    /** 活动地点 */
    private String location;

    /** 活动状态（0未开始 1进行中 2已结束） */
    private Integer activityStatus;
}

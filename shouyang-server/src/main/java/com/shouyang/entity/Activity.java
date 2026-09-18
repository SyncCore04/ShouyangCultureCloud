package com.shouyang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 活动表
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Getter
@Setter
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动标题
     */
    @TableField("title")
    private String title;

    /**
     * 封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 活动地点
     */
    @TableField("venue")
    private String venue;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 报名截止时间
     */
    @TableField("signup_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signupDeadline;

    /**
     * 最大人数（0=不限）
     */
    @TableField("max_people")
    private Integer maxPeople;

    /**
     * 已报名人数
     */
    @TableField("signup_count")
    private Integer signupCount;

    /**
     * 活动详情
     */
    @TableField("description")
    private String description;

    /**
     * 状态 0未开始 1进行中 2已结束
     */
    @TableField("status")
    private Byte status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}

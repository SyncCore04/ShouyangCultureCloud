package com.shouyang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 旅游攻略表
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Getter
@Setter
@TableName("travel_guide")
public class TravelGuide implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 攻略ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 攻略标题
     */
    @TableField("title")
    private String title;

    /**
     * 封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 作者/来源
     */
    @TableField("author")
    private String author;

    /**
     * 游玩天数
     */
    @TableField("days")
    private String days;

    /**
     * 路线概要
     */
    @TableField("route")
    private String route;

    /**
     * 简介
     */
    @TableField("description")
    private String description;

    /**
     * 详细攻略
     */
    @TableField("content")
    private String content;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 状态 0下架 1上架
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

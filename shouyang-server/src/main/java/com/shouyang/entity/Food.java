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
 * 美食表
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Getter
@Setter
@TableName("food")
public class Food implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 美食ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 美食名称
     */
    @TableField("name")
    private String name;

    /**
     * 封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 类型（主食/小吃/菜肴等）
     */
    @TableField("category")
    private String category;

    /**
     * 简介
     */
    @TableField("description")
    private String description;

    /**
     * 详细介绍（可含做法）
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

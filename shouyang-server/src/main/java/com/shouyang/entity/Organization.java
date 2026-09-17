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
 * 文旅单位表
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Getter
@Setter
@TableName("organization")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单位ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 单位名称
     */
    @TableField("name")
    private String name;

    /**
     * 封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 类型（文化馆/图书馆/分馆等）
     */
    @TableField("type")
    private String type;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 简介
     */
    @TableField("description")
    private String description;

    /**
     * 详细介绍
     */
    @TableField("content")
    private String content;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

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

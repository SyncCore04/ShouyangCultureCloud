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
 * 场馆表
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Getter
@Setter
@TableName("venue")
public class Venue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场馆ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场馆名称
     */
    @TableField("name")
    private String name;

    /**
     * 封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 开放时间
     */
    @TableField("open_time")
    private String openTime;

    /**
     * 联系电话
     */
    @TableField("contact")
    private String contact;

    /**
     * 场馆介绍
     */
    @TableField("description")
    private String description;

    /**
     * 状态 0关闭 1开放
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

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
 * 门票预订订单表
 * </p>
 *
 * @author shouyang
 * @since 2026-10-27
 */
@Getter
@Setter
@TableName("ticket_order")
public class TicketOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 票务ID
     */
    @TableField("ticket_id")
    private Long ticketId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 取票人姓名
     */
    @TableField("name")
    private String name;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 预订数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 状态 0已取消 1有效
     */
    @TableField("status")
    private Byte status;

    /**
     * 下单时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}

package com.shouyang.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 我的门票 VO（订单记录 + 票务信息）
 *
 * @author shouyang
 */
@Data
public class TicketOrderVO {

    /** 订单ID */
    private Long id;

    /** 票务ID */
    private Long ticketId;

    /** 用户ID */
    private Long userId;

    /** 取票人姓名 */
    private String name;

    /** 联系电话 */
    private String phone;

    /** 预订数量 */
    private Integer quantity;

    /** 订单状态（0已取消 1有效） */
    private Byte status;

    /** 下单时间 */
    private LocalDateTime createTime;

    /** 票务名称 */
    private String ticketName;

    /** 票务封面图 */
    private String coverImage;

    /** 场馆/地点 */
    private String venue;

    /** 票价（0=免费） */
    private BigDecimal price;

    /** 票务开始时间 */
    private LocalDateTime startTime;

    /** 票务结束时间 */
    private LocalDateTime endTime;
}

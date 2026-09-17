package com.shouyang.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏列表 VO（收藏记录 + 目标简要信息）
 *
 * @author shouyang
 */
@Data
public class FavoriteVO {

    /** 收藏ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 收藏类型（news/scenic/food/hotel/activity/product等） */
    private String targetType;

    /** 目标ID */
    private Long targetId;

    /** 收藏时间 */
    private LocalDateTime createTime;

    /** 目标标题/名称 */
    private String title;

    /** 目标封面图 */
    private String coverImage;

    /** 目标简介/描述 */
    private String description;

    /** 目标状态（活动状态等） */
    private String status;
}

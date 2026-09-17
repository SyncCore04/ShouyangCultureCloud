package com.shouyang.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 全站搜索结果 VO
 * 统一各表搜索结果的返回格式
 *
 * @author shouyang
 */
@Data
public class SearchResultVO {

    /**
     * 目标ID
     */
    private Long id;

    /**
     * 类型：news/scenic/food/hotel/activity/product/heritage/pavilion/guide
     */
    private String type;

    /**
     * 类型名称：资讯/景点/美食/酒店/活动/文创/非遗/展馆/攻略
     */
    private String typeName;

    /**
     * 标题/名称
     */
    private String title;

    /**
     * 封面图
     */
    private String coverImage;

    /**
     * 摘要/描述
     */
    private String summary;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

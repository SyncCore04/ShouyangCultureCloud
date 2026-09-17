package com.shouyang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shouyang.entity.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shouyang.vo.FavoriteVO;

/**
 * <p>
 * 收藏表 服务类
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 添加收藏
     *
     * @param userId     用户ID
     * @param targetType 收藏类型
     * @param targetId   目标ID
     */
    void addFavorite(Long userId, String targetType, Long targetId);

    /**
     * 取消收藏
     *
     * @param userId     用户ID
     * @param targetType 收藏类型
     * @param targetId   目标ID
     */
    void deleteFavorite(Long userId, String targetType, Long targetId);

    /**
     * 我的收藏列表（含目标简要信息）
     *
     * @param userId     用户ID
     * @param targetType 收藏类型（null表示全部）
     * @param page       当前页
     * @param size       每页条数
     * @return 分页结果
     */
    IPage<FavoriteVO> getFavoriteList(Long userId, String targetType, int page, int size);

    /**
     * 检查是否已收藏
     *
     * @param userId     用户ID
     * @param targetType 收藏类型
     * @param targetId   目标ID
     * @return 是否已收藏
     */
    boolean checkFavorite(Long userId, String targetType, Long targetId);
}

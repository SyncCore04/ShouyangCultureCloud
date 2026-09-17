package com.shouyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shouyang.common.exception.BusinessException;
import com.shouyang.entity.*;
import com.shouyang.mapper.FavoriteMapper;
import com.shouyang.service.*;
import com.shouyang.vo.FavoriteVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private NewsService newsService;
    @Autowired
    private ScenicSpotService scenicSpotService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ProductService productService;

    /**
     * 添加收藏
     */
    @Override
    public void addFavorite(Long userId, String targetType, Long targetId) {
        // 先检查是否已收藏
        if (checkFavorite(userId, targetType, targetId)) {
            throw new BusinessException("已收藏，请勿重复操作");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        try {
            this.save(favorite);
        } catch (DuplicateKeyException e) {
            // 唯一索引冲突，说明已收藏
            throw new BusinessException("已收藏，请勿重复操作");
        }
    }

    /**
     * 取消收藏
     */
    @Override
    public void deleteFavorite(Long userId, String targetType, Long targetId) {
        this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId));
    }

    /**
     * 我的收藏列表（含目标简要信息）
     */
    @Override
    public IPage<FavoriteVO> getFavoriteList(Long userId, String targetType, int page, int size) {
        // 1. 分页查询收藏记录
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        if (targetType != null && !targetType.isEmpty()) {
            wrapper.eq(Favorite::getTargetType, targetType);
        }
        wrapper.orderByDesc(Favorite::getCreateTime);

        IPage<Favorite> favoritePage = this.page(new Page<>(page, size), wrapper);
        List<Favorite> records = favoritePage.getRecords();

        // 2. 组装目标信息
        List<FavoriteVO> voList = new ArrayList<>();
        if (records.isEmpty()) {
            IPage<FavoriteVO> result = new Page<>(page, size, favoritePage.getTotal());
            result.setRecords(voList);
            return result;
        }

        // 按 targetType 分组，批量查询目标信息
        Map<String, List<Favorite>> grouped = records.stream()
                .collect(Collectors.groupingBy(Favorite::getTargetType));

        for (Favorite favorite : records) {
            FavoriteVO vo = new FavoriteVO();
            BeanUtils.copyProperties(favorite, vo);
            // 根据类型填充目标信息
            fillTargetInfo(vo, favorite.getTargetType(), favorite.getTargetId());
            voList.add(vo);
        }

        IPage<FavoriteVO> result = new Page<>(page, size, favoritePage.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 根据收藏类型填充目标简要信息
     */
    private void fillTargetInfo(FavoriteVO vo, String targetType, Long targetId) {
        try {
            switch (targetType) {
                case "news":
                    News news = newsService.getById(targetId);
                    if (news != null) {
                        vo.setTitle(news.getTitle());
                        vo.setCoverImage(news.getCoverImage());
                        vo.setDescription(news.getSummary());
                    }
                    break;
                case "scenic":
                    ScenicSpot scenic = scenicSpotService.getById(targetId);
                    if (scenic != null) {
                        vo.setTitle(scenic.getName());
                        vo.setCoverImage(scenic.getCoverImage());
                        vo.setDescription(scenic.getDescription());
                    }
                    break;
                case "food":
                    Food food = foodService.getById(targetId);
                    if (food != null) {
                        vo.setTitle(food.getName());
                        vo.setCoverImage(food.getCoverImage());
                        vo.setDescription(food.getDescription());
                    }
                    break;
                case "hotel":
                    Hotel hotel = hotelService.getById(targetId);
                    if (hotel != null) {
                        vo.setTitle(hotel.getName());
                        vo.setCoverImage(hotel.getCoverImage());
                        vo.setDescription(hotel.getDescription());
                    }
                    break;
                case "activity":
                    Activity activity = activityService.getById(targetId);
                    if (activity != null) {
                        vo.setTitle(activity.getTitle());
                        vo.setCoverImage(activity.getCoverImage());
                        vo.setDescription(activity.getDescription());
                        vo.setStatus(activity.getStatus() != null ? String.valueOf(activity.getStatus()) : null);
                    }
                    break;
                case "product":
                    Product product = productService.getById(targetId);
                    if (product != null) {
                        vo.setTitle(product.getName());
                        vo.setCoverImage(product.getCoverImage());
                        vo.setDescription(product.getDescription());
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // 目标信息查询失败时忽略，收藏记录仍正常返回
        }
    }

    /**
     * 检查是否已收藏
     */
    @Override
    public boolean checkFavorite(Long userId, String targetType, Long targetId) {
        long count = this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId));
        return count > 0;
    }
}

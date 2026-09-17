package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shouyang.common.result.Result;
import com.shouyang.service.FavoriteService;
import com.shouyang.utils.UserContext;
import com.shouyang.vo.FavoriteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台收藏 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/favorite")
public class ApiFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     *
     * @param params 包含 targetType, targetId
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        Long userId = UserContext.getUserId();
        String targetType = (String) params.get("targetType");
        Long targetId = Long.valueOf(params.get("targetId").toString());
        favoriteService.addFavorite(userId, targetType, targetId);
        return Result.success("收藏成功", null);
    }

    /**
     * 取消收藏
     *
     * @param params 包含 targetType, targetId
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Object> params) {
        Long userId = UserContext.getUserId();
        String targetType = (String) params.get("targetType");
        Long targetId = Long.valueOf(params.get("targetId").toString());
        favoriteService.deleteFavorite(userId, targetType, targetId);
        return Result.success("取消收藏成功", null);
    }

    /**
     * 我的收藏列表（分页，按收藏时间倒序）
     *
     * @param page       当前页
     * @param size       每页条数
     * @param targetType 收藏类型（null表示全部）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<FavoriteVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String targetType) {
        Long userId = UserContext.getUserId();
        IPage<FavoriteVO> result = favoriteService.getFavoriteList(userId, targetType, page, size);
        return Result.success(result);
    }

    /**
     * 检查是否已收藏
     *
     * @param targetType 收藏类型
     * @param targetId   目标ID
     * @return 是否已收藏
     */
    @GetMapping("/check")
    public Result<Map<String, Boolean>> check(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        Long userId = UserContext.getUserId();
        boolean favorited = favoriteService.checkFavorite(userId, targetType, targetId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("favorited", favorited);
        return Result.success(result);
    }
}

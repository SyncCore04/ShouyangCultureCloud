package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Hotel;
import com.shouyang.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 前台酒店 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/hotel")
public class ApiHotelController {

    @Autowired
    private HotelService hotelService;

    /**
     * 酒店列表（分页）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param keyword 关键词（名称模糊搜索）
     * @param star    星级筛选（1-5）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Hotel>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer star) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Hotel::getName, keyword.trim());
        }
        if (star != null) {
            wrapper.eq(Hotel::getStar, star);
        }
        wrapper.orderByDesc(Hotel::getCreateTime);

        IPage<Hotel> result = hotelService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 酒店详情（浏览量 +1）
     *
     * @param id 酒店ID
     * @return 酒店详情
     */
    @GetMapping("/{id}")
    public Result<Hotel> detail(@PathVariable Long id) {
        Hotel hotel = hotelService.getById(id);
        if (hotel == null || hotel.getStatus() != 1) {
            return Result.error(404, "酒店不存在或已下架");
        }
        // 浏览量 +1
        hotelService.lambdaUpdate()
                .eq(Hotel::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
        return Result.success(hotel);
    }
}

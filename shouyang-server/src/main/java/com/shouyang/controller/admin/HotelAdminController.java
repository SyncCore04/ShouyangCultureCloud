package com.shouyang.controller.admin;

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
 * 后台酒店管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/hotel")
public class HotelAdminController {

    @Autowired
    private HotelService hotelService;

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<IPage<Hotel>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Hotel::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(Hotel::getStatus, status);
        }
        wrapper.orderByDesc(Hotel::getCreateTime);
        IPage<Hotel> result = hotelService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Hotel> detail(@PathVariable Long id) {
        return Result.success(hotelService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody Hotel hotel) {
        hotelService.save(hotel);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Hotel hotel) {
        hotelService.updateById(hotel);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hotelService.removeById(id);
        return Result.success("删除成功", null);
    }
}

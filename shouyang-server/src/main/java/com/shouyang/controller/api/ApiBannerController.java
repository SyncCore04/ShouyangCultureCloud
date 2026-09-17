package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Banner;
import com.shouyang.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台轮播图 Controller
 * 提供轮播图列表查询接口
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/banner")
public class ApiBannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取轮播图列表
     * 只返回已启用的，按 sort 排序
     */
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        List<Banner> list = bannerService.list(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort)
                .orderByDesc(Banner::getCreateTime));
        return Result.success(list);
    }
}

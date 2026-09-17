package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Activity;
import com.shouyang.entity.ActivityRegister;
import com.shouyang.entity.News;
import com.shouyang.entity.ScenicSpot;
import com.shouyang.entity.SysUser;
import com.shouyang.service.ActivityRegisterService;
import com.shouyang.service.ActivityService;
import com.shouyang.service.NewsService;
import com.shouyang.service.ScenicSpotService;
import com.shouyang.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台仪表盘 Controller
 * 提供统计数据、趋势图、热门资讯等接口
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ScenicSpotService scenicSpotService;
    @Autowired
    private ActivityRegisterService activityRegisterService;

    /**
     * 统计数据
     * 用户总数、资讯总数、活动总数、景点总数、今日新增用户、今日发布资讯、活动报名总次数
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户总数
        long userTotal = sysUserService.count();
        stats.put("userTotal", userTotal);

        // 资讯总数
        long newsTotal = newsService.count();
        stats.put("newsTotal", newsTotal);

        // 活动总数
        long activityTotal = activityService.count();
        stats.put("activityTotal", activityTotal);

        // 景点总数
        long scenicTotal = scenicSpotService.count();
        stats.put("scenicTotal", scenicTotal);

        // 今日新增用户
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayNewUsers = sysUserService.count(new LambdaQueryWrapper<SysUser>()
                .ge(SysUser::getCreateTime, todayStart));
        stats.put("todayNewUsers", todayNewUsers);

        // 今日发布资讯
        long todayNews = newsService.count(new LambdaQueryWrapper<News>()
                .ge(News::getCreateTime, todayStart));
        stats.put("todayNews", todayNews);

        // 活动报名总次数
        long registerTotal = activityRegisterService.count();
        stats.put("registerTotal", registerTotal);

        return Result.success(stats);
    }

    /**
     * 资讯发布趋势（最近 N 天）
     * 返回日期数组 + 每天发布数量数组
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestParam(defaultValue = "7") int days) {
        // 生成最近 N 天的日期列表
        List<String> dateList = new ArrayList<>();
        List<Long> countList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dateList.add(date.format(formatter));

            // 查询当天发布的资讯数量
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            long count = newsService.count(new LambdaQueryWrapper<News>()
                    .ge(News::getCreateTime, dayStart)
                    .lt(News::getCreateTime, dayEnd));
            countList.add(count);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dateList);
        result.put("counts", countList);
        return Result.success(result);
    }

    /**
     * 热门资讯 TOP10（按浏览量排序）
     */
    @GetMapping("/hot-news")
    public Result<List<Map<String, Object>>> hotNews(@RequestParam(defaultValue = "10") int limit) {
        List<News> newsList = newsService.list(new LambdaQueryWrapper<News>()
                .eq(News::getStatus, 1)
                .orderByDesc(News::getViewCount)
                .last("LIMIT " + limit));

        List<Map<String, Object>> result = newsList.stream().map(news -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", news.getId());
            item.put("title", news.getTitle());
            item.put("viewCount", news.getViewCount());
            item.put("coverImage", news.getCoverImage());
            item.put("createTime", news.getCreateTime());
            return item;
        }).collect(Collectors.toList());

        return Result.success(result);
    }
}

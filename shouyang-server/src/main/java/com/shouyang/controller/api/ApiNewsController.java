package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shouyang.common.result.Result;
import com.shouyang.entity.News;
import com.shouyang.entity.NewsCategory;
import com.shouyang.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台资讯 Controller
 * 提供资讯列表、详情、分类、热门、相关资讯等公开接口
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/news")
public class ApiNewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 资讯列表（分页）
     *
     * @param page       当前页，默认 1
     * @param size       每页条数，默认 10
     * @param categoryId 分类ID（可选）
     * @param keyword    关键词（可选，标题模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<News>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        IPage<News> result = newsService.getNewsList(page, size, categoryId, keyword);
        return Result.success(result);
    }

    /**
     * 资讯详情
     *
     * @param id 资讯ID
     * @return 资讯详情（浏览量自动 +1）
     */
    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        News news = newsService.getNewsDetail(id);
        if (news == null) {
            return Result.error(404, "资讯不存在或已下架");
        }
        return Result.success(news);
    }

    /**
     * 资讯分类列表
     *
     * @return 所有启用的分类
     */
    @GetMapping("/category")
    public Result<List<NewsCategory>> category() {
        List<NewsCategory> list = newsService.getCategoryList();
        return Result.success(list);
    }

    /**
     * 热门资讯（浏览量前 10）
     *
     * @return 热门资讯列表
     */
    @GetMapping("/hot")
    public Result<List<News>> hot() {
        List<News> list = newsService.getHotNews();
        return Result.success(list);
    }

    /**
     * 相关资讯（同分类下的其他资讯，6 条）
     *
     * @param id 当前资讯ID
     * @return 相关资讯列表
     */
    @GetMapping("/related/{id}")
    public Result<List<News>> related(@PathVariable Long id) {
        List<News> list = newsService.getRelatedNews(id);
        return Result.success(list);
    }
}

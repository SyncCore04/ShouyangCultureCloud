package com.shouyang.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.News;
import com.shouyang.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台资讯管理 Controller
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/news")
public class NewsAdminController {

    @Autowired
    private NewsService newsService;

    /**
     * 分页列表（支持关键词、分类、状态筛选）
     */
    @GetMapping("/list")
    public Result<IPage<News>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword.trim());
        }
        if (categoryId != null) {
            wrapper.eq(News::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(News::getStatus, status);
        }
        wrapper.orderByDesc(News::getIsTop).orderByDesc(News::getCreateTime);
        IPage<News> result = newsService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        return Result.success(newsService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> add(@RequestBody News news) {
        newsService.save(news);
        return Result.success("新增成功", null);
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody News news) {
        newsService.updateById(news);
        return Result.success("修改成功", null);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        newsService.removeById(id);
        return Result.success("删除成功", null);
    }
}

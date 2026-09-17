package com.shouyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shouyang.entity.News;
import com.shouyang.entity.NewsCategory;
import com.shouyang.mapper.NewsMapper;
import com.shouyang.service.NewsCategoryService;
import com.shouyang.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资讯 Service 实现类
 *
 * @author shouyang
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsCategoryService newsCategoryService;

    /**
     * 分页查询资讯列表
     */
    @Override
    public IPage<News> getNewsList(int page, int size, Long categoryId, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        // 只查询已发布的
        wrapper.eq(News::getStatus, 1);
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(News::getCategoryId, categoryId);
        }
        // 关键词搜索（标题模糊匹配）
        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword.trim());
        }
        // 排序：置顶优先，然后按创建时间倒序
        wrapper.orderByDesc(News::getIsTop)
                .orderByDesc(News::getCreateTime);

        // 分页查询
        Page<News> pageParam = new Page<>(page, size);
        IPage<News> result = this.page(pageParam, wrapper);

        // 填充分类名称
        fillCategoryName(result.getRecords());

        return result;
    }

    /**
     * 获取资讯详情（浏览量 +1）
     */
    @Override
    public News getNewsDetail(Long id) {
        // 查询详情
        News news = this.getById(id);
        if (news == null) {
            return null;
        }

        // 浏览量 +1（用 update 语句避免并发问题）
        this.lambdaUpdate()
                .eq(News::getId, id)
                .setSql("view_count = view_count + 1")
                .update();

        // 填充分类名称
        fillCategoryName(List.of(news));

        return news;
    }

    /**
     * 获取所有启用的资讯分类
     */
    @Override
    public List<NewsCategory> getCategoryList() {
        return newsCategoryService.list(
                new LambdaQueryWrapper<NewsCategory>()
                        .eq(NewsCategory::getStatus, 1)
                        .orderByAsc(NewsCategory::getSort)
                        .orderByAsc(NewsCategory::getId)
        );
    }

    /**
     * 获取热门资讯（浏览量前 10）
     */
    @Override
    public List<News> getHotNews() {
        List<News> list = this.list(
                new LambdaQueryWrapper<News>()
                        .eq(News::getStatus, 1)
                        .orderByDesc(News::getViewCount)
                        .orderByDesc(News::getCreateTime)
                        .last("LIMIT 10")
        );
        fillCategoryName(list);
        return list;
    }

    /**
     * 获取相关资讯（同分类下的其他资讯，6 条）
     */
    @Override
    public List<News> getRelatedNews(Long id) {
        // 先查询当前资讯的分类ID
        News current = this.getById(id);
        if (current == null || current.getCategoryId() == null) {
            return List.of();
        }

        // 查询同分类下的其他资讯
        List<News> list = this.list(
                new LambdaQueryWrapper<News>()
                        .eq(News::getStatus, 1)
                        .eq(News::getCategoryId, current.getCategoryId())
                        .ne(News::getId, id)  // 排除当前资讯
                        .orderByDesc(News::getIsTop)
                        .orderByDesc(News::getCreateTime)
                        .last("LIMIT 6")
        );
        fillCategoryName(list);
        return list;
    }

    /**
     * 批量填充分类名称
     * 先查询所有分类，建立 id -> name 映射，然后批量设置
     */
    private void fillCategoryName(List<News> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return;
        }

        // 收集所有需要的分类ID
        List<Long> categoryIds = newsList.stream()
                .map(News::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (categoryIds.isEmpty()) {
            return;
        }

        // 批量查询分类
        List<NewsCategory> categories = newsCategoryService.listByIds(categoryIds);
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(NewsCategory::getId, NewsCategory::getName));

        // 填充分类名称
        for (News news : newsList) {
            if (news.getCategoryId() != null) {
                news.setCategoryName(categoryNameMap.get(news.getCategoryId()));
            }
        }
    }
}

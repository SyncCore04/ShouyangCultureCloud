package com.shouyang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shouyang.entity.News;
import com.shouyang.entity.NewsCategory;

import java.util.List;

/**
 * 资讯 Service 接口
 *
 * @author shouyang
 */
public interface NewsService extends IService<News> {

    /**
     * 分页查询资讯列表
     *
     * @param page       当前页
     * @param size       每页条数
     * @param categoryId 分类ID（可选）
     * @param keyword    关键词（可选，标题模糊搜索）
     * @return 分页结果，包含分类名称
     */
    IPage<News> getNewsList(int page, int size, Long categoryId, String keyword);

    /**
     * 获取资讯详情（浏览量 +1）
     *
     * @param id 资讯ID
     * @return 资讯详情（含分类名称）
     */
    News getNewsDetail(Long id);

    /**
     * 获取所有启用的资讯分类
     *
     * @return 分类列表
     */
    List<NewsCategory> getCategoryList();

    /**
     * 获取热门资讯（浏览量前 10）
     *
     * @return 热门资讯列表
     */
    List<News> getHotNews();

    /**
     * 获取相关资讯（同分类下的其他资讯，6 条）
     *
     * @param id 当前资讯ID
     * @return 相关资讯列表
     */
    List<News> getRelatedNews(Long id);
}

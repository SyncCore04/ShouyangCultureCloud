package com.shouyang.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.vo.SearchResultVO;

/**
 * 全站搜索 Service 接口
 *
 * @author shouyang
 */
public interface SearchService {

    /**
     * 全站搜索
     *
     * @param keyword 关键词
     * @param type    搜索类型：all/news/scenic/food/hotel/activity/product/heritage/pavilion/guide
     * @param page    页码
     * @param size    每页条数
     * @return 分页搜索结果
     */
    Page<SearchResultVO> search(String keyword, String type, int page, int size);
}

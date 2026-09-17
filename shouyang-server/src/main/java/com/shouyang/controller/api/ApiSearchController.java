package com.shouyang.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.service.SearchService;
import com.shouyang.vo.SearchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全站搜索 Controller
 * 提供跨模块的统一搜索接口
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/search")
public class ApiSearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 全站搜索
     *
     * @param keyword 关键词
     * @param type    搜索类型：all（全部）、news、scenic、food、hotel、activity、product、heritage、pavilion、guide
     * @param page    页码
     * @param size    每页条数
     * @return 分页搜索结果
     */
    @GetMapping
    public Result<Page<SearchResultVO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 关键词为空时返回空结果
        if (keyword == null || keyword.trim().isEmpty()) {
            Page<SearchResultVO> emptyPage = new Page<>(page, size);
            emptyPage.setTotal(0);
            return Result.success(emptyPage);
        }

        Page<SearchResultVO> result = searchService.search(keyword.trim(), type, page, size);
        return Result.success(result);
    }
}

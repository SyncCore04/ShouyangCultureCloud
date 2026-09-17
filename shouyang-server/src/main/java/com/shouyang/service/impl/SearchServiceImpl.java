package com.shouyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.entity.Activity;
import com.shouyang.entity.Food;
import com.shouyang.entity.Heritage;
import com.shouyang.entity.Hotel;
import com.shouyang.entity.News;
import com.shouyang.entity.Pavilion;
import com.shouyang.entity.Product;
import com.shouyang.entity.ScenicSpot;
import com.shouyang.entity.TravelGuide;
import com.shouyang.service.ActivityService;
import com.shouyang.service.FoodService;
import com.shouyang.service.HeritageService;
import com.shouyang.service.HotelService;
import com.shouyang.service.NewsService;
import com.shouyang.service.PavilionService;
import com.shouyang.service.ProductService;
import com.shouyang.service.ScenicSpotService;
import com.shouyang.service.SearchService;
import com.shouyang.service.TravelGuideService;
import com.shouyang.vo.SearchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全站搜索 Service 实现类
 * 在 Service 层分别查询各表，然后组装成统一格式的列表，内存中分页
 *
 * @author shouyang
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private NewsService newsService;
    @Autowired
    private ScenicSpotService scenicSpotService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ProductService productService;
    @Autowired
    private HeritageService heritageService;
    @Autowired
    private PavilionService pavilionService;
    @Autowired
    private TravelGuideService travelGuideService;

    @Override
    public Page<SearchResultVO> search(String keyword, String type, int page, int size) {
        List<SearchResultVO> allResults = new ArrayList<>();

        if (type == null || type.isEmpty() || "all".equals(type)) {
            // 搜索全部类型
            allResults.addAll(searchNews(keyword));
            allResults.addAll(searchScenic(keyword));
            allResults.addAll(searchFood(keyword));
            allResults.addAll(searchHotel(keyword));
            allResults.addAll(searchActivity(keyword));
            allResults.addAll(searchProduct(keyword));
            allResults.addAll(searchHeritage(keyword));
            allResults.addAll(searchPavilion(keyword));
            allResults.addAll(searchGuide(keyword));
        } else {
            // 搜索指定类型
            switch (type) {
                case "news":
                    allResults.addAll(searchNews(keyword));
                    break;
                case "scenic":
                    allResults.addAll(searchScenic(keyword));
                    break;
                case "food":
                    allResults.addAll(searchFood(keyword));
                    break;
                case "hotel":
                    allResults.addAll(searchHotel(keyword));
                    break;
                case "activity":
                    allResults.addAll(searchActivity(keyword));
                    break;
                case "product":
                    allResults.addAll(searchProduct(keyword));
                    break;
                case "heritage":
                    allResults.addAll(searchHeritage(keyword));
                    break;
                case "pavilion":
                    allResults.addAll(searchPavilion(keyword));
                    break;
                case "guide":
                    allResults.addAll(searchGuide(keyword));
                    break;
                default:
                    break;
            }
        }

        // 按创建时间倒序排序
        allResults.sort(Comparator.comparing(SearchResultVO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));

        // 内存分页
        Page<SearchResultVO> pageResult = new Page<>(page, size);
        pageResult.setTotal(allResults.size());

        int fromIndex = (page - 1) * size;
        if (fromIndex >= allResults.size()) {
            pageResult.setRecords(new ArrayList<>());
        } else {
            int toIndex = Math.min(fromIndex + size, allResults.size());
            pageResult.setRecords(allResults.subList(fromIndex, toIndex));
        }

        return pageResult;
    }

    // ========== 各类型搜索方法 ==========

    /**
     * 搜索资讯
     */
    private List<SearchResultVO> searchNews(String keyword) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(News::getTitle, keyword);
        }
        wrapper.orderByDesc(News::getCreateTime);
        List<News> list = newsService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("news");
            vo.setTypeName("资讯");
            vo.setTitle(item.getTitle());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getSummary());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索景点
     */
    private List<SearchResultVO> searchScenic(String keyword) {
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicSpot::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(ScenicSpot::getName, keyword);
        }
        wrapper.orderByDesc(ScenicSpot::getCreateTime);
        List<ScenicSpot> list = scenicSpotService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("scenic");
            vo.setTypeName("景点");
            vo.setTitle(item.getName());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索美食
     */
    private List<SearchResultVO> searchFood(String keyword) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Food::getName, keyword);
        }
        wrapper.orderByDesc(Food::getCreateTime);
        List<Food> list = foodService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("food");
            vo.setTypeName("美食");
            vo.setTitle(item.getName());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索酒店
     */
    private List<SearchResultVO> searchHotel(String keyword) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Hotel::getName, keyword);
        }
        wrapper.orderByDesc(Hotel::getCreateTime);
        List<Hotel> list = hotelService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("hotel");
            vo.setTypeName("酒店");
            vo.setTitle(item.getName());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索活动
     */
    private List<SearchResultVO> searchActivity(String keyword) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Activity::getTitle, keyword);
        }
        wrapper.orderByDesc(Activity::getCreateTime);
        List<Activity> list = activityService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("activity");
            vo.setTypeName("活动");
            vo.setTitle(item.getTitle());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索文创商品
     */
    private List<SearchResultVO> searchProduct(String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        List<Product> list = productService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("product");
            vo.setTypeName("文创");
            vo.setTitle(item.getName());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索非遗文化
     */
    private List<SearchResultVO> searchHeritage(String keyword) {
        LambdaQueryWrapper<Heritage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Heritage::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Heritage::getName, keyword);
        }
        wrapper.orderByDesc(Heritage::getCreateTime);
        List<Heritage> list = heritageService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("heritage");
            vo.setTypeName("非遗");
            vo.setTitle(item.getName());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索数字展馆
     */
    private List<SearchResultVO> searchPavilion(String keyword) {
        LambdaQueryWrapper<Pavilion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pavilion::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Pavilion::getName, keyword);
        }
        wrapper.orderByDesc(Pavilion::getCreateTime);
        List<Pavilion> list = pavilionService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("pavilion");
            vo.setTypeName("展馆");
            vo.setTitle(item.getName());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索旅游攻略
     */
    private List<SearchResultVO> searchGuide(String keyword) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(TravelGuide::getTitle, keyword);
        }
        wrapper.orderByDesc(TravelGuide::getCreateTime);
        List<TravelGuide> list = travelGuideService.list(wrapper);

        return list.stream().map(item -> {
            SearchResultVO vo = new SearchResultVO();
            vo.setId(item.getId());
            vo.setType("guide");
            vo.setTypeName("攻略");
            vo.setTitle(item.getTitle());
            vo.setCoverImage(item.getCoverImage());
            vo.setSummary(item.getDescription());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }
}

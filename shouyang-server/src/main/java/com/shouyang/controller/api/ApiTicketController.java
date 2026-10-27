package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.dto.TicketOrderDTO;
import com.shouyang.entity.Ticket;
import com.shouyang.service.TicketOrderService;
import com.shouyang.service.TicketService;
import com.shouyang.utils.UserContext;
import com.shouyang.vo.TicketOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台票务 Controller（列表/详情展示 + 在线预订 + 数据统计）
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/ticket")
public class ApiTicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketOrderService ticketOrderService;

    /**
     * 票务列表（分页）
     *
     * @param page    当前页
     * @param size    每页条数
     * @param keyword 关键词（名称模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<Ticket>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ticket::getStatus, 1); // 只查上架的
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Ticket::getName, keyword.trim());
        }
        wrapper.orderByDesc(Ticket::getCreateTime);

        IPage<Ticket> result = ticketService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 票务详情
     *
     * @param id 票务ID
     * @return 票务详情
     */
    @GetMapping("/{id}")
    public Result<Ticket> detail(@PathVariable Long id) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null || ticket.getStatus() != 1) {
            return Result.error(404, "票务不存在或已下架");
        }
        return Result.success(ticket);
    }

    /**
     * 票务统计数据（供前台可视化看板使用，无需登录）
     *
     * @return 在售票种数、总余票、平均票价、免费票种数、票价区间分布、余票率排行
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        List<Ticket> tickets = ticketService.list(new LambdaQueryWrapper<Ticket>()
                .eq(Ticket::getStatus, 1));

        int totalTypes = tickets.size();
        int totalRemain = 0;
        int freeCount = 0;
        int free = 0, low = 0, mid = 0, high = 0;
        BigDecimal priceSum = BigDecimal.ZERO;
        int priceCount = 0;
        List<Map<String, Object>> remainRank = new ArrayList<>();

        for (Ticket t : tickets) {
            int remain = t.getRemainCount() != null ? t.getRemainCount() : 0;
            int total = t.getTotalCount() != null ? t.getTotalCount() : 0;
            totalRemain += remain;

            BigDecimal price = t.getPrice() != null ? t.getPrice() : BigDecimal.ZERO;
            if (price.compareTo(BigDecimal.ZERO) == 0) {
                freeCount++;
                free++;
            } else {
                priceSum = priceSum.add(price);
                priceCount++;
                if (price.compareTo(new BigDecimal("50")) <= 0) {
                    low++;
                } else if (price.compareTo(new BigDecimal("100")) <= 0) {
                    mid++;
                } else {
                    high++;
                }
            }

            // 余票率排行数据（余票率 = 剩余 / 总数 * 100）
            if (total > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", t.getName());
                item.put("remainCount", remain);
                item.put("remainRate", Math.round(remain * 1000.0 / total) / 10.0);
                remainRank.add(item);
            }
        }

        // 票价区间分布
        List<Map<String, Object>> priceRanges = new ArrayList<>();
        priceRanges.add(buildRange("免费", free));
        priceRanges.add(buildRange("1-50元", low));
        priceRanges.add(buildRange("51-100元", mid));
        priceRanges.add(buildRange("100元以上", high));

        // 余票率降序，取前5
        remainRank.sort((a, b) -> Double.compare((double) b.get("remainRate"), (double) a.get("remainRate")));
        if (remainRank.size() > 5) {
            remainRank = remainRank.subList(0, 5);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalTypes", totalTypes);
        result.put("totalRemain", totalRemain);
        result.put("freeCount", freeCount);
        result.put("avgPrice", priceCount > 0
                ? priceSum.divide(new BigDecimal(priceCount), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        result.put("priceRanges", priceRanges);
        result.put("remainRank", remainRank);
        return Result.success(result);
    }

    /**
     * 门票预订（需登录）
     *
     * @param dto 预订参数
     * @return 订单ID
     */
    @PostMapping("/order")
    public Result<Long> order(@Valid @RequestBody TicketOrderDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Long orderId = ticketOrderService.order(
                dto.getTicketId(),
                userId,
                dto.getName(),
                dto.getPhone(),
                dto.getQuantity()
        );
        return Result.success(orderId);
    }

    /**
     * 我的门票列表（需登录，仅有效订单）
     *
     * @param page 当前页
     * @param size 每页条数
     * @return 订单列表（含票务信息）
     */
    @GetMapping("/my/list")
    public Result<IPage<TicketOrderVO>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(ticketOrderService.getMyOrderList(userId, page, size));
    }

    /**
     * 取消预订（需登录，回补库存）
     *
     * @param orderId 订单ID
     * @return 无
     */
    @PostMapping("/order/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        ticketOrderService.cancelOrder(userId, orderId);
        return Result.success(null);
    }

    /**
     * 构建票价区间项
     */
    private Map<String, Object> buildRange(String label, int count) {
        Map<String, Object> item = new HashMap<>();
        item.put("range", label);
        item.put("count", count);
        return item;
    }
}

package com.shouyang.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.result.Result;
import com.shouyang.entity.Ticket;
import com.shouyang.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 前台票务 Controller（仅展示，不做购买）
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/ticket")
public class ApiTicketController {

    @Autowired
    private TicketService ticketService;

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
}

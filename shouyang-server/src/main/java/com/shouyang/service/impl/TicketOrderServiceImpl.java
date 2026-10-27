package com.shouyang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shouyang.common.exception.BusinessException;
import com.shouyang.entity.Ticket;
import com.shouyang.entity.TicketOrder;
import com.shouyang.mapper.TicketOrderMapper;
import com.shouyang.service.TicketOrderService;
import com.shouyang.service.TicketService;
import com.shouyang.vo.TicketOrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 门票预订订单 Service 实现类
 *
 * @author shouyang
 */
@Service
public class TicketOrderServiceImpl extends ServiceImpl<TicketOrderMapper, TicketOrder> implements TicketOrderService {

    @Autowired
    private TicketService ticketService;

    /**
     * 门票预订（含完整校验，条件更新扣减库存防超卖）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long order(Long ticketId, Long userId, String name, String phone, Integer quantity) {
        // 1. 校验票务是否存在且上架
        Ticket ticket = ticketService.getById(ticketId);
        if (ticket == null || ticket.getStatus() == null || ticket.getStatus() != 1) {
            throw new BusinessException("票务不存在或已下架");
        }

        // 2. 校验是否已结束预订
        if (ticket.getEndTime() != null && LocalDateTime.now().isAfter(ticket.getEndTime())) {
            throw new BusinessException("该票务已结束预订");
        }

        // 3. 校验预订数量
        if (quantity == null || quantity < 1) {
            throw new BusinessException("预订数量不正确");
        }

        // 4. 校验余票是否充足
        int remain = ticket.getRemainCount() != null ? ticket.getRemainCount() : 0;
        if (remain <= 0) {
            throw new BusinessException("该票务已售罄");
        }
        if (quantity > remain) {
            throw new BusinessException("余票不足，仅剩 " + remain + " 张");
        }

        // 5. 创建订单
        TicketOrder order = new TicketOrder();
        order.setTicketId(ticketId);
        order.setUserId(userId);
        order.setName(name);
        order.setPhone(phone);
        order.setQuantity(quantity);
        order.setStatus((byte) 1); // 1=有效
        this.save(order);

        // 6. 扣减库存（带余票条件的原子更新，防止并发超卖）
        boolean success = ticketService.lambdaUpdate()
                .eq(Ticket::getId, ticketId)
                .apply("remain_count >= {0}", quantity)
                .setSql("remain_count = remain_count - " + quantity)
                .update();
        if (!success) {
            throw new BusinessException("余票不足，预订失败");
        }

        return order.getId();
    }

    /**
     * 我的门票列表（含票务信息，仅有效订单）
     */
    @Override
    public IPage<TicketOrderVO> getMyOrderList(Long userId, int page, int size) {
        // 1. 分页查询订单记录
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketOrder::getUserId, userId)
                .eq(TicketOrder::getStatus, 1) // 只查有效订单
                .orderByDesc(TicketOrder::getCreateTime);

        IPage<TicketOrder> orderPage = this.page(new Page<>(page, size), wrapper);
        List<TicketOrder> records = orderPage.getRecords();

        // 2. 组装票务信息
        List<TicketOrderVO> voList = new ArrayList<>();
        for (TicketOrder order : records) {
            TicketOrderVO vo = new TicketOrderVO();
            BeanUtils.copyProperties(order, vo);
            // 填充票务信息
            Ticket ticket = ticketService.getById(order.getTicketId());
            if (ticket != null) {
                vo.setTicketName(ticket.getName());
                vo.setCoverImage(ticket.getCoverImage());
                vo.setVenue(ticket.getVenue());
                vo.setPrice(ticket.getPrice());
                vo.setStartTime(ticket.getStartTime());
                vo.setEndTime(ticket.getEndTime());
            }
            voList.add(vo);
        }

        IPage<TicketOrderVO> result = new Page<>(page, size, orderPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 取消预订（校验归属与状态，回补库存，仅未开始的票务可取消）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        // 1. 校验订单是否存在且属于当前用户
        TicketOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人的订单");
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new BusinessException("该订单已取消或已失效");
        }

        // 2. 校验票务状态（已结束的票务无法取消）
        Ticket ticket = ticketService.getById(order.getTicketId());
        if (ticket == null) {
            throw new BusinessException("票务不存在");
        }
        if (ticket.getEndTime() != null && LocalDateTime.now().isAfter(ticket.getEndTime())) {
            throw new BusinessException("票务已结束，无法取消预订");
        }

        // 3. 取消订单（状态改为0）
        order.setStatus((byte) 0);
        this.updateById(order);

        // 4. 回补库存
        ticketService.lambdaUpdate()
                .eq(Ticket::getId, order.getTicketId())
                .setSql("remain_count = remain_count + " + order.getQuantity())
                .update();
    }
}

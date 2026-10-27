package com.shouyang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shouyang.entity.TicketOrder;
import com.shouyang.vo.TicketOrderVO;

/**
 * 门票预订订单 Service 接口
 *
 * @author shouyang
 */
public interface TicketOrderService extends IService<TicketOrder> {

    /**
     * 门票预订（校验余票并扣减库存）
     *
     * @param ticketId 票务ID
     * @param userId   用户ID
     * @param name     取票人姓名
     * @param phone    联系电话
     * @param quantity 预订数量
     * @return 订单ID
     */
    Long order(Long ticketId, Long userId, String name, String phone, Integer quantity);

    /**
     * 我的门票列表（含票务信息）
     *
     * @param userId 用户ID
     * @param page   当前页
     * @param size   每页条数
     * @return 分页结果
     */
    IPage<TicketOrderVO> getMyOrderList(Long userId, int page, int size);

    /**
     * 取消预订（回补库存，仅未开始的票务可取消）
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     */
    void cancelOrder(Long userId, Long orderId);
}

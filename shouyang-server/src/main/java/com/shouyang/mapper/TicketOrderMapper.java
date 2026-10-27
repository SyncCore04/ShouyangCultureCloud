package com.shouyang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shouyang.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门票预订订单 Mapper 接口
 *
 * @author shouyang
 */
@Mapper
public interface TicketOrderMapper extends BaseMapper<TicketOrder> {
}

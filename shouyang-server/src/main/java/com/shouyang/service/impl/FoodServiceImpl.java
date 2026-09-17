package com.shouyang.service.impl;

import com.shouyang.entity.Food;
import com.shouyang.mapper.FoodMapper;
import com.shouyang.service.FoodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 美食表 服务实现类
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

}

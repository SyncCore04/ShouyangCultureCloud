package com.shouyang.service.impl;

import com.shouyang.entity.Activity;
import com.shouyang.mapper.ActivityMapper;
import com.shouyang.service.ActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

}

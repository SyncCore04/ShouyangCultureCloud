package com.shouyang.service.impl;

import com.shouyang.entity.Favorite;
import com.shouyang.mapper.FavoriteMapper;
import com.shouyang.service.FavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

}

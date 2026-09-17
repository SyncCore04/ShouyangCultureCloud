package com.shouyang.service.impl;

import com.shouyang.entity.Product;
import com.shouyang.mapper.ProductMapper;
import com.shouyang.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文创商品表 服务实现类
 * </p>
 *
 * @author shouyang
 * @since 2026-09-17
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}

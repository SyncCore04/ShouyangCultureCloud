package com.shouyang.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shouyang.common.exception.BusinessException;
import com.shouyang.common.result.ResultCode;
import com.shouyang.dto.UserLoginDTO;
import com.shouyang.entity.SysAdmin;
import com.shouyang.mapper.SysAdminMapper;
import com.shouyang.service.SysAdminService;
import com.shouyang.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员 Service 实现类
 *
 * @author shouyang
 */
@Service
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements SysAdminService {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 管理员登录
     */
    @Override
    public Map<String, Object> login(UserLoginDTO dto) {
        // 1. 根据用户名查询管理员
        SysAdmin admin = this.getOne(new LambdaQueryWrapper<SysAdmin>()
                .eq(SysAdmin::getUsername, dto.getUsername()));
        if (admin == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }

        // 2. 验证密码（BCrypt 校验）
        if (!BCrypt.checkpw(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 4. 生成 JWT token（存储 userId、username、userType=admin）
        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), "admin");

        // 5. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        // 管理员信息（不含密码）
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("id", admin.getId());
        adminInfo.put("username", admin.getUsername());
        adminInfo.put("nickname", admin.getNickname());
        adminInfo.put("role", admin.getRole());
        result.put("adminInfo", adminInfo);

        return result;
    }

    /**
     * 获取管理员信息
     */
    @Override
    public SysAdmin getAdminInfo(Long adminId) {
        SysAdmin admin = this.getById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }
        return admin;
    }
}

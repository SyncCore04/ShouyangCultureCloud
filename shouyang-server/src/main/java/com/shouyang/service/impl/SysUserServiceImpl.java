package com.shouyang.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shouyang.common.exception.BusinessException;
import com.shouyang.common.result.ResultCode;
import com.shouyang.dto.PasswordUpdateDTO;
import com.shouyang.dto.UserLoginDTO;
import com.shouyang.dto.UserRegisterDTO;
import com.shouyang.dto.UserUpdateDTO;
import com.shouyang.entity.SysUser;
import com.shouyang.mapper.SysUserMapper;
import com.shouyang.service.SysUserService;
import com.shouyang.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户 Service 实现类
 *
 * @author shouyang
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    @Override
    public void register(UserRegisterDTO dto) {
        // 1. 用户名唯一校验
        Long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXIST);
        }

        // 2. 创建用户实体
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        // BCrypt 加密密码
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setStatus((byte) 1); // 正常状态

        // 3. 保存到数据库
        this.save(user);
    }

    /**
     * 用户登录
     */
    @Override
    public Map<String, Object> login(UserLoginDTO dto) {
        // 1. 根据用户名查询用户
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }

        // 2. 验证密码（BCrypt 校验）
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 4. 生成 JWT token（存储 userId、username、userType）
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), "user");

        // 5. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        // 用户信息（不含密码）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("gender", user.getGender());
        result.put("userInfo", userInfo);

        return result;
    }

    /**
     * 获取用户信息
     */
    @Override
    public SysUser getUserInfo(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }
        return user;
    }

    /**
     * 更新用户信息
     */
    @Override
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }

        // 更新非空字段
        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        this.updateById(user);
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(Long userId, PasswordUpdateDTO dto) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }

        // 1. 验证旧密码
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 2. 更新新密码（BCrypt 加密）
        user.setPassword(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
        this.updateById(user);
    }
}

package com.shouyang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shouyang.dto.PasswordUpdateDTO;
import com.shouyang.dto.UserLoginDTO;
import com.shouyang.dto.UserRegisterDTO;
import com.shouyang.dto.UserUpdateDTO;
import com.shouyang.entity.SysUser;

import java.util.Map;

/**
 * 用户 Service 接口
 *
 * @author shouyang
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     *
     * @param dto 注册信息
     */
    void register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return 包含 token 和 userInfo 的 Map
     */
    Map<String, Object> login(UserLoginDTO dto);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    SysUser getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新信息
     */
    void updateUserInfo(Long userId, UserUpdateDTO dto);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto    旧密码和新密码
     */
    void updatePassword(Long userId, PasswordUpdateDTO dto);
}

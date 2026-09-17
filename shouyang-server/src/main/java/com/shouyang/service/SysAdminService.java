package com.shouyang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shouyang.dto.UserLoginDTO;
import com.shouyang.entity.SysAdmin;

import java.util.Map;

/**
 * 管理员 Service 接口
 *
 * @author shouyang
 */
public interface SysAdminService extends IService<SysAdmin> {

    /**
     * 管理员登录
     *
     * @param dto 登录信息
     * @return 包含 token 和 adminInfo 的 Map
     */
    Map<String, Object> login(UserLoginDTO dto);

    /**
     * 获取管理员信息
     *
     * @param adminId 管理员ID
     * @return 管理员实体
     */
    SysAdmin getAdminInfo(Long adminId);
}

package com.shouyang.controller.admin;

import cn.hutool.crypto.digest.BCrypt;
import com.shouyang.common.exception.BusinessException;
import com.shouyang.common.result.Result;
import com.shouyang.common.result.ResultCode;
import com.shouyang.dto.PasswordUpdateDTO;
import com.shouyang.dto.UserLoginDTO;
import com.shouyang.entity.SysAdmin;
import com.shouyang.service.SysAdminService;
import com.shouyang.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 后台管理员认证 Controller
 * 提供管理员登录、获取管理员信息等接口
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminAuthController {

    @Autowired
    private SysAdminService sysAdminService;

    /**
     * 管理员登录
     *
     * @param dto 登录信息
     * @return token 和管理员信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        Map<String, Object> result = sysAdminService.login(dto);
        return Result.success(result);
    }

    /**
     * 获取当前登录管理员信息
     *
     * @return 管理员信息（不含密码）
     */
    @GetMapping("/info")
    public Result<SysAdmin> info() {
        Long adminId = UserContext.getUserId();
        SysAdmin admin = sysAdminService.getAdminInfo(adminId);
        // 密码字段不返回
        admin.setPassword(null);
        return Result.success(admin);
    }

    /**
     * 修改管理员密码
     *
     * @param dto 密码修改信息
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        Long adminId = UserContext.getUserId();
        SysAdmin admin = sysAdminService.getById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }

        // 验证旧密码
        if (!BCrypt.checkpw(dto.getOldPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 更新新密码
        String newHash = BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt());
        admin.setPassword(newHash);
        sysAdminService.updateById(admin);

        return Result.success();
    }
}

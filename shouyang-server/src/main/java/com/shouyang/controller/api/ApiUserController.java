package com.shouyang.controller.api;

import com.shouyang.common.result.Result;
import com.shouyang.dto.PasswordUpdateDTO;
import com.shouyang.dto.UserLoginDTO;
import com.shouyang.dto.UserRegisterDTO;
import com.shouyang.dto.UserUpdateDTO;
import com.shouyang.entity.SysUser;
import com.shouyang.service.SysUserService;
import com.shouyang.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 前台用户认证 Controller
 * 提供用户注册、登录、获取/更新用户信息、修改密码等接口
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/user")
@Validated
public class ApiUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户注册
     *
     * @param dto 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        sysUserService.register(dto);
        return Result.success("注册成功", null);
    }

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return token 和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        Map<String, Object> result = sysUserService.login(dto);
        return Result.success(result);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息（不含密码）
     */
    @GetMapping("/info")
    public Result<SysUser> info() {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserService.getUserInfo(userId);
        // 密码字段不返回
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新当前登录用户信息
     *
     * @param dto 更新信息
     * @return 更新结果
     */
    @PutMapping("/info")
    public Result<Void> updateInfo(@Valid @RequestBody UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        sysUserService.updateUserInfo(userId, dto);
        return Result.success("更新成功", null);
    }

    /**
     * 修改当前登录用户密码
     *
     * @param dto 旧密码和新密码
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        sysUserService.updatePassword(userId, dto);
        return Result.success("密码修改成功", null);
    }
}

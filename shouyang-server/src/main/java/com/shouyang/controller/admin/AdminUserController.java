package com.shouyang.controller.admin;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shouyang.common.exception.BusinessException;
import com.shouyang.common.result.Result;
import com.shouyang.common.result.ResultCode;
import com.shouyang.entity.SysUser;
import com.shouyang.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台用户管理 Controller
 * 提供用户列表、详情、启用/禁用、删除、重置密码等接口
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户列表（分页）
     * 支持关键词搜索用户名/昵称，状态筛选
     */
    @GetMapping("/list")
    public Result<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Byte status) {

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 关键词搜索（用户名或昵称）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword));
        }
        // 状态筛选
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        // 按注册时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> pageResult = sysUserService.page(new Page<>(page, size), wrapper);

        // 密码字段不返回
        pageResult.getRecords().forEach(user -> user.setPassword(null));

        return Result.success(pageResult);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    public Result<SysUser> detail(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Byte status = Byte.valueOf(params.get("status").toString());

        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }

        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }
        sysUserService.removeById(id);
        return Result.success();
    }

    /**
     * 重置用户密码为 123456
     */
    @PutMapping("/password/reset")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());

        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_NOT_FOUND);
        }

        // 用 Hutool BCrypt 加密
        String newPassword = BCrypt.hashpw("123456", BCrypt.gensalt());
        user.setPassword(newPassword);
        sysUserService.updateById(user);
        return Result.success();
    }
}

package com.shouyang.controller.admin;

import cn.hutool.crypto.digest.BCrypt;
import com.shouyang.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 临时工具 Controller - 生成 BCrypt 密码哈希
 * 使用后请删除
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/util")
public class AdminUtilController {

    /**
     * 生成密码哈希
     */
    @GetMapping("/hash")
    public Result<String> hash(@RequestParam String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        return Result.success(hash);
    }

    /**
     * 校验密码
     */
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam String password, @RequestParam String hash) {
        boolean result = BCrypt.checkpw(password, hash);
        return Result.success(result);
    }
}

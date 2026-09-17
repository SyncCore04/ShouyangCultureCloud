package com.shouyang.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shouyang.entity.SysAdmin;
import com.shouyang.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 启动时检查管理员密码是否可用 Hutool BCrypt 校验，如不可用则自动重置
 * （解决不同 BCrypt 实现不兼容的问题）
 *
 * @author shouyang
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SysAdminService sysAdminService;

    @Override
    public void run(String... args) {
        try {
            // 检查 admin 账号是否存在
            SysAdmin admin = sysAdminService.getOne(new LambdaQueryWrapper<SysAdmin>()
                    .eq(SysAdmin::getUsername, "admin"));
            if (admin == null) {
                System.out.println("[DataInitializer] 未找到 admin 账号，跳过密码检查");
                return;
            }

            // 尝试用 Hutool BCrypt 校验密码 123456
            boolean passwordValid = false;
            try {
                passwordValid = BCrypt.checkpw("123456", admin.getPassword());
            } catch (Exception e) {
                System.out.println("[DataInitializer] 密码校验异常: " + e.getMessage());
            }

            if (!passwordValid) {
                // 密码校验失败，用 Hutool 重新生成哈希并更新
                String newHash = BCrypt.hashpw("123456", BCrypt.gensalt());
                admin.setPassword(newHash);
                sysAdminService.updateById(admin);
                System.out.println("[DataInitializer] 管理员密码已重置为 Hutool BCrypt 哈希，新密码: 123456");
            } else {
                System.out.println("[DataInitializer] 管理员密码校验正常，无需重置");
            }
        } catch (Exception e) {
            System.out.println("[DataInitializer] 初始化异常: " + e.getMessage());
        }
    }
}

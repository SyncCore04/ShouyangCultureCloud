package com.shouyang.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录 DTO
 *
 * @author shouyang
 */
@Data
public class UserLoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

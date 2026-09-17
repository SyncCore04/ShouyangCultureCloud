package com.shouyang.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户信息更新 DTO
 *
 * @author shouyang
 */
@Data
public class UserUpdateDTO {

    /**
     * 昵称
     */
    @Size(max = 20, message = "昵称最长20个字符")
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 性别 0未知 1男 2女
     */
    private Byte gender;
}

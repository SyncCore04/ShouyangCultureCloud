package com.shouyang.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 活动报名 DTO
 *
 * @author shouyang
 */
@Data
public class ActivityRegisterDTO {

    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    /**
     * 报名人姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 联系电话
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 备注
     */
    private String remark;
}

package com.shouyang.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 门票预订 DTO
 *
 * @author shouyang
 */
@Data
public class TicketOrderDTO {

    /**
     * 票务ID
     */
    @NotNull(message = "票务ID不能为空")
    private Long ticketId;

    /**
     * 取票人姓名
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
     * 预订数量（不超过剩余票数）
     */
    @NotNull(message = "预订数量不能为空")
    @Min(value = 1, message = "预订数量至少为1张")
    private Integer quantity;
}

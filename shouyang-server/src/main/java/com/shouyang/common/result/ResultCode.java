package com.shouyang.common.result;

import lombok.Getter;

/**
 * 统一响应结果码枚举
 *
 * @author shouyang
 */
@Getter
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 客户端错误 */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "请求资源不存在"),

    /** 服务器错误 */
    INTERNAL_ERROR(500, "服务器内部错误"),

    /** 业务错误（1000+） */
    USERNAME_EXIST(1001, "用户名已存在"),
    USERNAME_NOT_FOUND(1002, "用户名不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),
    TOKEN_INVALID(1005, "Token 无效"),
    TOKEN_EXPIRED(1006, "Token 已过期"),
    FILE_UPLOAD_FAIL(1007, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(1008, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(1009, "文件大小超出限制"),
    DATA_NOT_FOUND(1010, "数据不存在"),
    ALREADY_EXISTS(1011, "数据已存在"),
    ACTIVITY_FULL(1012, "活动报名人数已满"),
    ACTIVITY_ENDED(1013, "活动已结束"),
    ALREADY_REGISTERED(1014, "您已报名该活动");

    /** 状态码 */
    private final Integer code;

    /** 提示信息 */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

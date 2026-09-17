package com.shouyang.common.exception;

import com.shouyang.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * 用于在业务逻辑中抛出可预期的异常，由全局异常处理器统一捕获返回
 *
 * @author shouyang
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final Integer code;

    /**
     * 构造方法：使用结果码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 构造方法：自定义错误码和消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造方法：自定义消息（默认 500 错误码）
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_ERROR.getCode();
    }
}

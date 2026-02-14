package com.hotel.exception;

import com.hotel.common.ErrorCode;

/**
 * 错误码枚举
 */
public enum ErrorCodeEnum {

    /**
     * 用户名已存在
     */
    USER_EXISTS(ErrorCode.USER_EXISTS, "用户名已存在"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(ErrorCode.LOGIN_ERROR, "用户名或密码错误"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(ErrorCode.USER_DISABLED, "用户已被禁用"),

    /**
     * 客房不存在
     */
    ROOM_NOT_FOUND(ErrorCode.ROOM_NOT_FOUND, "客房不存在"),

    /**
     * 客房已被预订
     */
    ROOM_BOOKED(ErrorCode.ROOM_BOOKED, "客房已被预订"),

    /**
     * 客房状态异常
     */
    ROOM_STATUS_ERROR(ErrorCode.ROOM_STATUS_ERROR, "客房状态异常"),

    /**
     * 订单不存在
     */
    ORDER_NOT_FOUND(ErrorCode.ORDER_NOT_FOUND, "订单不存在"),

    /**
     * 订单状态异常
     */
    ORDER_STATUS_ERROR(ErrorCode.ORDER_STATUS_ERROR, "订单状态异常"),

    /**
     * 订单已支付
     */
    ORDER_PAID(ErrorCode.ORDER_PAID, "订单已支付"),

    /**
     * 订单已取消
     */
    ORDER_CANCELLED(ErrorCode.ORDER_CANCELLED, "订单已取消");

    private final Integer code;
    private final String message;

    ErrorCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

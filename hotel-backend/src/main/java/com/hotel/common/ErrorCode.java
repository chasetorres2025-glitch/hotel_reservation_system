package com.hotel.common;

/**
 * 错误码定义
 */
public class ErrorCode {

    /**
     * 操作成功
     */
    public static final int SUCCESS = 200;

    /**
     * 请求参数错误
     */
    public static final int BAD_REQUEST = 400;

    /**
     * 未登录或登录已过期
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 无权限访问
     */
    public static final int FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * 服务器内部错误
     */
    public static final int INTERNAL_ERROR = 500;

    // ========== 用户相关错误码 1000-1999 ==========
    
    /**
     * 用户名已存在
     */
    public static final int USER_EXISTS = 1001;

    /**
     * 用户名或密码错误
     */
    public static final int LOGIN_ERROR = 1002;

    /**
     * 用户已被禁用
     */
    public static final int USER_DISABLED = 1003;

    // ========== 客房相关错误码 2000-2999 ==========
    
    /**
     * 客房不存在
     */
    public static final int ROOM_NOT_FOUND = 2001;

    /**
     * 客房已被预订
     */
    public static final int ROOM_BOOKED = 2002;

    /**
     * 客房状态异常
     */
    public static final int ROOM_STATUS_ERROR = 2003;

    // ========== 订单相关错误码 3000-3999 ==========
    
    /**
     * 订单不存在
     */
    public static final int ORDER_NOT_FOUND = 3001;

    /**
     * 订单状态异常
     */
    public static final int ORDER_STATUS_ERROR = 3002;

    /**
     * 订单已支付
     */
    public static final int ORDER_PAID = 3003;

    /**
     * 订单已取消
     */
    public static final int ORDER_CANCELLED = 3004;

    private ErrorCode() {
    }
}

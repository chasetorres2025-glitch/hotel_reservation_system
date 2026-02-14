package com.hotel.common;

/**
 * 常量定义
 */
public class Constants {

    /**
     * 用户角色 - 管理员
     */
    public static final String ROLE_ADMIN = "admin";

    /**
     * 用户角色 - 普通用户
     */
    public static final String ROLE_USER = "user";

    /**
     * 用户状态 - 启用
     */
    public static final int USER_STATUS_ENABLED = 1;

    /**
     * 用户状态 - 禁用
     */
    public static final int USER_STATUS_DISABLED = 0;

    /**
     * 客房状态 - 空闲
     */
    public static final int ROOM_STATUS_AVAILABLE = 0;

    /**
     * 客房状态 - 预订
     */
    public static final int ROOM_STATUS_BOOKED = 1;

    /**
     * 客房状态 - 入住
     */
    public static final int ROOM_STATUS_OCCUPIED = 2;

    /**
     * 客房状态 - 维护
     */
    public static final int ROOM_STATUS_MAINTENANCE = 3;

    /**
     * 订单状态 - 待支付
     */
    public static final int ORDER_STATUS_PENDING_PAYMENT = 0;

    /**
     * 订单状态 - 待确认
     */
    public static final int ORDER_STATUS_PENDING_CONFIRM = 1;

    /**
     * 订单状态 - 已确认
     */
    public static final int ORDER_STATUS_CONFIRMED = 2;

    /**
     * 订单状态 - 已入住
     */
    public static final int ORDER_STATUS_CHECKED_IN = 3;

    /**
     * 订单状态 - 已完成
     */
    public static final int ORDER_STATUS_COMPLETED = 4;

    /**
     * 订单状态 - 已取消
     */
    public static final int ORDER_STATUS_CANCELLED = 5;

    /**
     * 订单状态 - 已拒绝
     */
    public static final int ORDER_STATUS_REJECTED = 6;

    /**
     * 支付状态 - 待支付
     */
    public static final int PAYMENT_STATUS_PENDING = 0;

    /**
     * 支付状态 - 已支付
     */
    public static final int PAYMENT_STATUS_PAID = 1;

    /**
     * 支付状态 - 已退款
     */
    public static final int PAYMENT_STATUS_REFUNDED = 2;

    /**
     * Session中存储用户信息的key
     */
    public static final String SESSION_USER_KEY = "currentUser";

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页数量
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    private Constants() {
    }
}

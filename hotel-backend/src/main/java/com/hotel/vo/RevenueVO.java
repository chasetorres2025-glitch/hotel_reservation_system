package com.hotel.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 营收统计VO
 */
@Data
public class RevenueVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总营收
     */
    private BigDecimal totalRevenue;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 待支付金额
     */
    private BigDecimal unpaidAmount;

    /**
     * 各类型营收详情
     */
    private List<RevenueDetailVO> details;

    /**
     * 营收详情
     */
    @Data
    public static class RevenueDetailVO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 客房类型名称
         */
        private String typeName;

        /**
         * 订单数量
         */
        private Integer orderCount;

        /**
         * 营收金额
         */
        private BigDecimal revenue;

        /**
         * 占比
         */
        private BigDecimal percentage;
    }
}

package com.hotel.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 营收趋势VO
 */
@Data
public class RevenueTrendVO {
    /**
     * 日期列表
     */
    private List<LocalDate> dates;

    /**
     * 营收列表
     */
    private List<BigDecimal> revenues;

    /**
     * 订单数列表
     */
    private List<Integer> orderCounts;
}

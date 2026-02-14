package com.hotel.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 预订率统计VO
 */
@Data
public class BookingRateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总客房数
     */
    private Integer totalRooms;

    /**
     * 已预订客房数
     */
    private Integer bookedRooms;

    /**
     * 预订率
     */
    private BigDecimal bookingRate;

    /**
     * 各类型预订详情
     */
    private List<BookingDetailVO> details;

    /**
     * 预订详情
     */
    @Data
    public static class BookingDetailVO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 客房类型名称
         */
        private String typeName;

        /**
         * 总数
         */
        private Integer total;

        /**
         * 已预订数
         */
        private Integer booked;

        /**
         * 预订率
         */
        private BigDecimal rate;
    }
}

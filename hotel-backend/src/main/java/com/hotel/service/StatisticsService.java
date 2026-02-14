package com.hotel.service;

import com.hotel.vo.BookingRateVO;
import com.hotel.vo.OrderStatsVO;
import com.hotel.vo.RevenueTrendVO;
import com.hotel.vo.RevenueVO;

import java.time.LocalDate;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 预订率统计
     */
    BookingRateVO getBookingRate(LocalDate startDate, LocalDate endDate);

    /**
     * 营收统计
     */
    RevenueVO getRevenue(LocalDate startDate, LocalDate endDate);

    /**
     * 营收趋势
     */
    RevenueTrendVO getRevenueTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 订单统计
     */
    OrderStatsVO getOrderStats();
}

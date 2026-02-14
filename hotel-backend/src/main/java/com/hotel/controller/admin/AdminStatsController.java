package com.hotel.controller.admin;

import com.hotel.common.Result;
import com.hotel.service.StatisticsService;
import com.hotel.vo.BookingRateVO;
import com.hotel.vo.OrderStatsVO;
import com.hotel.vo.RevenueTrendVO;
import com.hotel.vo.RevenueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 管理端 - 数据统计控制器
 */
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 预订率统计
     */
    @GetMapping("/booking-rate")
    public Result<BookingRateVO> getBookingRate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        BookingRateVO result = statisticsService.getBookingRate(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 营收统计
     */
    @GetMapping("/revenue")
    public Result<RevenueVO> getRevenue(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        RevenueVO result = statisticsService.getRevenue(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 营收趋势
     */
    @GetMapping("/revenue-trend")
    public Result<RevenueTrendVO> getRevenueTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        RevenueTrendVO result = statisticsService.getRevenueTrend(startDate, endDate);
        return Result.success(result);
    }

    /**
     * 订单统计
     */
    @GetMapping("/order")
    public Result<OrderStatsVO> getOrderStats() {
        OrderStatsVO result = statisticsService.getOrderStats();
        return Result.success(result);
    }
}

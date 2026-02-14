package com.hotel.service.impl;

import com.hotel.common.Constants;
import com.hotel.mapper.OrderMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.StatisticsService;
import com.hotel.vo.BookingRateVO;
import com.hotel.vo.OrderStatsVO;
import com.hotel.vo.RevenueTrendVO;
import com.hotel.vo.RevenueVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public BookingRateVO getBookingRate(LocalDate startDate, LocalDate endDate) {
        BookingRateVO vo = new BookingRateVO();

        // 获取总客房数
        int totalRooms = roomMapper.count(null, null, null, null).intValue();
        vo.setTotalRooms(totalRooms);

        // 获取已预订客房数（状态为预订或入住）
        int bookedRooms = roomMapper.countByStatus(Constants.ROOM_STATUS_BOOKED) +
                roomMapper.countByStatus(Constants.ROOM_STATUS_OCCUPIED);
        vo.setBookedRooms(bookedRooms);

        // 计算预订率
        BigDecimal bookingRate = BigDecimal.ZERO;
        if (totalRooms > 0) {
            bookingRate = BigDecimal.valueOf(bookedRooms)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalRooms), 2, RoundingMode.HALF_UP);
        }
        vo.setBookingRate(bookingRate);

        // 获取各类型预订详情
        List<BookingRateVO.BookingDetailVO> details = new ArrayList<>();
        roomTypeMapper.selectAllEnabled().forEach(roomType -> {
            BookingRateVO.BookingDetailVO detail = new BookingRateVO.BookingDetailVO();
            detail.setTypeName(roomType.getTypeName());

            int typeTotal = roomMapper.countByTypeId(roomType.getId());
            int typeBooked = roomMapper.countByStatus(Constants.ROOM_STATUS_BOOKED) +
                    roomMapper.countByStatus(Constants.ROOM_STATUS_OCCUPIED);

            detail.setTotal(typeTotal);
            detail.setBooked(typeBooked);

            BigDecimal rate = BigDecimal.ZERO;
            if (typeTotal > 0) {
                rate = BigDecimal.valueOf(typeBooked)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(typeTotal), 2, RoundingMode.HALF_UP);
            }
            detail.setRate(rate);

            details.add(detail);
        });
        vo.setDetails(details);

        return vo;
    }

    @Override
    public RevenueVO getRevenue(LocalDate startDate, LocalDate endDate) {
        RevenueVO vo = new RevenueVO();

        // 获取总营收
        BigDecimal totalRevenue = orderMapper.sumTotalAmount(null);
        vo.setTotalRevenue(totalRevenue);

        // 获取已支付金额
        BigDecimal paidAmount = orderMapper.sumPaidAmount();
        vo.setPaidAmount(paidAmount);

        // 计算待支付金额
        BigDecimal unpaidAmount = totalRevenue.subtract(paidAmount);
        vo.setUnpaidAmount(unpaidAmount);

        // 获取各类型营收详情
        List<RevenueVO.RevenueDetailVO> details = new ArrayList<>();
        roomTypeMapper.selectAllEnabled().forEach(roomType -> {
            RevenueVO.RevenueDetailVO detail = new RevenueVO.RevenueDetailVO();
            detail.setTypeName(roomType.getTypeName());

            // 这里简化处理，实际需要根据订单关联查询
            detail.setOrderCount(0);
            detail.setRevenue(BigDecimal.ZERO);
            detail.setPercentage(BigDecimal.ZERO);

            details.add(detail);
        });
        vo.setDetails(details);

        return vo;
    }

    @Override
    public RevenueTrendVO getRevenueTrend(LocalDate startDate, LocalDate endDate) {
        RevenueTrendVO vo = new RevenueTrendVO();
        
        // 如果没有指定日期范围，默认最近30天
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<LocalDate> dates = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();
        List<Integer> orderCounts = new ArrayList<>();

        // 按天统计
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current);
            
            // 获取当天的营收
            BigDecimal dayRevenue = orderMapper.sumTotalAmountByDate(current);
            revenues.add(dayRevenue != null ? dayRevenue : BigDecimal.ZERO);
            
            // 获取当天的订单数
            Integer dayOrderCount = orderMapper.countByDate(current);
            orderCounts.add(dayOrderCount != null ? dayOrderCount : 0);
            
            current = current.plusDays(1);
        }

        vo.setDates(dates);
        vo.setRevenues(revenues);
        vo.setOrderCounts(orderCounts);

        return vo;
    }

    @Override
    public OrderStatsVO getOrderStats() {
        OrderStatsVO vo = new OrderStatsVO();

        vo.setTotalOrders(orderMapper.count(null, null, null, null, null, null, null, null).intValue());
        vo.setPendingPayment(orderMapper.countByStatus(Constants.ORDER_STATUS_PENDING_PAYMENT));
        vo.setPendingConfirm(orderMapper.countByStatus(Constants.ORDER_STATUS_PENDING_CONFIRM));
        vo.setConfirmed(orderMapper.countByStatus(Constants.ORDER_STATUS_CONFIRMED));
        vo.setCheckedIn(orderMapper.countByStatus(Constants.ORDER_STATUS_CHECKED_IN));
        vo.setCompleted(orderMapper.countByStatus(Constants.ORDER_STATUS_COMPLETED));
        vo.setCancelled(orderMapper.countByStatus(Constants.ORDER_STATUS_CANCELLED));
        vo.setRejected(orderMapper.countByStatus(Constants.ORDER_STATUS_REJECTED));

        vo.setTotalAmount(orderMapper.sumTotalAmount(null));
        vo.setPaidAmount(orderMapper.sumPaidAmount());

        return vo;
    }
}

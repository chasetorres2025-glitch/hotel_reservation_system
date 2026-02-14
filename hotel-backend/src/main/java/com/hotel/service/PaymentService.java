package com.hotel.service;

import com.hotel.dto.PaymentDTO;
import com.hotel.entity.Payment;
import com.hotel.vo.OrderStatsVO;
import com.hotel.vo.RevenueVO;
import com.hotel.vo.BookingRateVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付记录
     */
    Payment createPayment(PaymentDTO paymentDTO);

    /**
     * 根据订单ID查询支付记录
     */
    List<Payment> listByOrderId(Long orderId);

    /**
     * 更新支付状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据订单ID更新支付状态
     */
    void updateStatusByOrderId(Long orderId, Integer status);
}

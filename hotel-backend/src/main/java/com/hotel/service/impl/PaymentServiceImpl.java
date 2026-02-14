package com.hotel.service.impl;

import com.hotel.common.Constants;
import com.hotel.dto.PaymentDTO;
import com.hotel.entity.Order;
import com.hotel.entity.Payment;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.OrderMapper;
import com.hotel.mapper.PaymentMapper;
import com.hotel.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public Payment createPayment(PaymentDTO paymentDTO) {
        // 检查订单
        Order order = orderMapper.selectById(paymentDTO.getOrderId());
        if (order == null) {
            throw new BusinessException(3001, "订单不存在");
        }

        Payment payment = new Payment();
        payment.setOrderId(paymentDTO.getOrderId());
        payment.setPaymentNo(generatePaymentNo());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setAmount(paymentDTO.getAmount());
        payment.setStatus(Constants.PAYMENT_STATUS_PENDING);

        paymentMapper.insert(payment);

        return payment;
    }

    @Override
    public List<Payment> listByOrderId(Long orderId) {
        return paymentMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Payment payment = paymentMapper.selectById(id);
        if (payment == null) {
            throw new BusinessException(404, "支付记录不存在");
        }

        paymentMapper.updateStatus(id, status);

        // 如果支付成功，更新支付时间
        if (status == Constants.PAYMENT_STATUS_PAID) {
            payment.setPaymentTime(LocalDateTime.now());
            paymentMapper.updatePaymentTime(id, LocalDateTime.now());
        }
    }

    /**
     * 根据订单ID更新支付状态
     */
    @Override
    @Transactional
    public void updateStatusByOrderId(Long orderId, Integer status) {
        paymentMapper.updateStatusByOrderId(orderId, status);
    }

    /**
     * 生成支付流水号
     */
    private String generatePaymentNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PAY" + dateStr + uuid;
    }
}

package com.hotel.mapper;

import com.hotel.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentMapper {

    /**
     * 根据ID查询支付记录
     */
    Payment selectById(@Param("id") Long id);

    /**
     * 根据支付流水号查询
     */
    Payment selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单ID查询支付记录列表
     */
    List<Payment> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 新增支付记录
     */
    int insert(Payment payment);

    /**
     * 更新支付状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据订单ID更新支付状态
     */
    int updateStatusByOrderId(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 更新支付时间
     */
    int updatePaymentTime(@Param("id") Long id, @Param("paymentTime") java.time.LocalDateTime paymentTime);
}

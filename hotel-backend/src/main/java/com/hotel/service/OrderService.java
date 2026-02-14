package com.hotel.service;

import com.hotel.common.PageResult;
import com.hotel.dto.OrderDTO;
import com.hotel.dto.OrderQueryDTO;
import com.hotel.dto.PaymentDTO;
import com.hotel.entity.Order;
import com.hotel.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 分页查询订单列表
     */
    PageResult<OrderVO> listOrders(OrderQueryDTO queryDTO);

    /**
     * 查询用户订单列表
     */
    PageResult<OrderVO> listUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询订单
     */
    OrderVO getOrderById(Long id);

    /**
     * 提交订单
     */
    OrderVO createOrder(OrderDTO orderDTO);

    /**
     * 支付订单
     */
    void payOrder(PaymentDTO paymentDTO);

    /**
     * 确认订单
     */
    void confirmOrder(Long id);

    /**
     * 拒绝订单
     */
    void rejectOrder(Long id, String remark);

    /**
     * 办理入住
     */
    void checkIn(Long id);

    /**
     * 办理退房
     */
    void checkOut(Long id);

    /**
     * 取消订单
     */
    void cancelOrder(Long id);

    /**
     * 根据ID获取实体
     */
    Order getEntityById(Long id);
}

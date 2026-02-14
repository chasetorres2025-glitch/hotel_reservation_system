package com.hotel.mapper;

import com.hotel.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {

    /**
     * 根据ID查询订单
     */
    Order selectById(@Param("id") Long id);

    /**
     * 根据订单编号查询
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询订单列表（带关联信息）
     */
    List<Order> selectList(@Param("orderNo") String orderNo,
                           @Param("userId") Long userId,
                           @Param("roomId") Long roomId,
                           @Param("status") Integer status,
                           @Param("contactName") String contactName,
                           @Param("contactPhone") String contactPhone,
                           @Param("checkInDateStart") LocalDate checkInDateStart,
                           @Param("checkInDateEnd") LocalDate checkInDateEnd);

    /**
     * 统计订单数量
     */
    Long count(@Param("orderNo") String orderNo,
               @Param("userId") Long userId,
               @Param("roomId") Long roomId,
               @Param("status") Integer status,
               @Param("contactName") String contactName,
               @Param("contactPhone") String contactPhone,
               @Param("checkInDateStart") LocalDate checkInDateStart,
               @Param("checkInDateEnd") LocalDate checkInDateEnd);

    /**
     * 查询用户订单列表
     */
    List<Order> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 新增订单
     */
    int insert(Order order);

    /**
     * 更新订单
     */
    int update(Order order);

    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新已支付金额
     */
    int updatePaidAmount(@Param("id") Long id, @Param("paidAmount") BigDecimal paidAmount);

    /**
     * 统计各状态订单数量
     */
    int countByStatus(@Param("status") Integer status);

    /**
     * 统计订单总金额
     */
    BigDecimal sumTotalAmount(@Param("status") Integer status);

    /**
     * 统计已支付金额
     */
    BigDecimal sumPaidAmount();

    /**
     * 统计某一天的营收
     */
    BigDecimal sumTotalAmountByDate(@Param("date") LocalDate date);

    /**
     * 统计某一天的订单数
     */
    Integer countByDate(@Param("date") LocalDate date);

    /**
     * 检查客房在指定日期是否可用
     */
    int checkRoomAvailable(@Param("roomId") Long roomId,
                           @Param("checkInDate") LocalDate checkInDate,
                           @Param("checkOutDate") LocalDate checkOutDate,
                           @Param("excludeOrderId") Long excludeOrderId);
}

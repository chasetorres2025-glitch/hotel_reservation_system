package com.hotel.service.impl;

import com.hotel.common.Constants;
import com.hotel.common.ErrorCode;
import com.hotel.common.PageResult;
import com.hotel.dto.OrderDTO;
import com.hotel.dto.OrderQueryDTO;
import com.hotel.dto.PaymentDTO;
import com.hotel.entity.Order;
import com.hotel.entity.Payment;
import com.hotel.entity.Room;
import com.hotel.entity.User;
import com.hotel.exception.BusinessException;
import com.hotel.exception.ErrorCodeEnum;
import com.hotel.mapper.OrderMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.UserMapper;
import com.hotel.service.OrderService;
import com.hotel.service.PaymentService;
import com.hotel.service.UserService;
import com.hotel.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Override
    public PageResult<OrderVO> listOrders(OrderQueryDTO queryDTO) {
        List<Order> orders = orderMapper.selectList(
                queryDTO.getOrderNo(),
                queryDTO.getUserId(),
                queryDTO.getRoomId(),
                queryDTO.getStatus(),
                queryDTO.getContactName(),
                queryDTO.getContactPhone(),
                queryDTO.getCheckInDateStart(),
                queryDTO.getCheckInDateEnd()
        );

        Long total = orderMapper.count(
                queryDTO.getOrderNo(),
                queryDTO.getUserId(),
                queryDTO.getRoomId(),
                queryDTO.getStatus(),
                queryDTO.getContactName(),
                queryDTO.getContactPhone(),
                queryDTO.getCheckInDateStart(),
                queryDTO.getCheckInDateEnd()
        );

        List<OrderVO> voList = orders.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<OrderVO> listUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        List<Order> orders = orderMapper.selectByUserId(userId, status);
        Long total = (long) orders.size();

        List<OrderVO> voList = orders.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, total, pageNum, pageSize);
    }

    @Override
    public OrderVO getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO createOrder(OrderDTO orderDTO) {
        User currentUser = userService.getCurrentUser();

        // 检查客房是否存在
        Room room = roomMapper.selectById(orderDTO.getRoomId());
        if (room == null) {
            throw new BusinessException(ErrorCodeEnum.ROOM_NOT_FOUND);
        }

        // 检查客房状态
        if (room.getStatus() != Constants.ROOM_STATUS_AVAILABLE) {
            throw new BusinessException(ErrorCodeEnum.ROOM_BOOKED);
        }

        // 检查日期
        if (orderDTO.getCheckInDate().isAfter(orderDTO.getCheckOutDate()) ||
                orderDTO.getCheckInDate().isEqual(orderDTO.getCheckOutDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "入住日期必须早于退房日期");
        }

        if (orderDTO.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "入住日期不能早于当前日期");
        }

        // 检查客房在指定日期是否可用
        if (orderMapper.checkRoomAvailable(orderDTO.getRoomId(), orderDTO.getCheckInDate(),
                orderDTO.getCheckOutDate(), null) > 0) {
            throw new BusinessException(ErrorCodeEnum.ROOM_BOOKED);
        }

        // 计算入住天数和总金额
        int days = Period.between(orderDTO.getCheckInDate(), orderDTO.getCheckOutDate()).getDays();
        BigDecimal totalAmount = room.getPrice().multiply(BigDecimal.valueOf(days));

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(currentUser.getId());
        order.setRoomId(orderDTO.getRoomId());
        order.setCheckInDate(orderDTO.getCheckInDate());
        order.setCheckOutDate(orderDTO.getCheckOutDate());
        order.setGuestCount(orderDTO.getGuestCount());
        order.setContactName(orderDTO.getContactName());
        order.setContactPhone(orderDTO.getContactPhone());
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setStatus(Constants.ORDER_STATUS_PENDING_PAYMENT);
        order.setSpecialRequest(orderDTO.getSpecialRequest());

        orderMapper.insert(order);

        // 更新客房状态为预订
        roomMapper.updateStatus(room.getId(), Constants.ROOM_STATUS_BOOKED);

        return convertToVO(order);
    }

    @Override
    @Transactional
    public void payOrder(PaymentDTO paymentDTO) {
        User currentUser = userService.getCurrentUser();

        // 检查订单
        Order order = orderMapper.selectById(paymentDTO.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        // 检查是否是自己的订单
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此订单");
        }

        // 检查订单状态
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING_PAYMENT) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        // 检查支付金额
        BigDecimal unpaidAmount = order.getTotalAmount().subtract(order.getPaidAmount());
        if (paymentDTO.getAmount().compareTo(unpaidAmount) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "支付金额不能超过待支付金额");
        }

        // 创建支付记录
        Payment payment = paymentService.createPayment(paymentDTO);

        // 更新支付状态
        paymentService.updateStatus(payment.getId(), Constants.PAYMENT_STATUS_PAID);

        // 更新订单已支付金额
        BigDecimal newPaidAmount = order.getPaidAmount().add(paymentDTO.getAmount());
        orderMapper.updatePaidAmount(order.getId(), newPaidAmount);

        // 如果全额支付，更新订单状态为待确认
        if (newPaidAmount.compareTo(order.getTotalAmount()) >= 0) {
            orderMapper.updateStatus(order.getId(), Constants.ORDER_STATUS_PENDING_CONFIRM);
        }
    }

    @Override
    @Transactional
    public void confirmOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING_CONFIRM) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        orderMapper.updateStatus(id, Constants.ORDER_STATUS_CONFIRMED);
    }

    @Override
    @Transactional
    public void rejectOrder(Long id, String remark) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING_CONFIRM) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        // 更新订单状态
        order.setStatus(Constants.ORDER_STATUS_REJECTED);
        order.setRemark(remark);
        orderMapper.update(order);

        // 更新客房状态为空闲
        roomMapper.updateStatus(order.getRoomId(), Constants.ROOM_STATUS_AVAILABLE);

        // 更新支付记录状态为退款
        paymentService.updateStatusByOrderId(id, Constants.PAYMENT_STATUS_REFUNDED);
    }

    @Override
    @Transactional
    public void checkIn(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (order.getStatus() != Constants.ORDER_STATUS_CONFIRMED) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        orderMapper.updateStatus(id, Constants.ORDER_STATUS_CHECKED_IN);

        // 更新客房状态为入住
        roomMapper.updateStatus(order.getRoomId(), Constants.ROOM_STATUS_OCCUPIED);
    }

    @Override
    @Transactional
    public void checkOut(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (order.getStatus() != Constants.ORDER_STATUS_CHECKED_IN) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        orderMapper.updateStatus(id, Constants.ORDER_STATUS_COMPLETED);

        // 更新客房状态为空闲
        roomMapper.updateStatus(order.getRoomId(), Constants.ROOM_STATUS_AVAILABLE);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        User currentUser = userService.getCurrentUser();

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        // 检查是否是自己的订单
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此订单");
        }

        // 检查订单状态（只有待支付和待确认状态可以取消）
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING_PAYMENT &&
                order.getStatus() != Constants.ORDER_STATUS_PENDING_CONFIRM) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "当前订单状态不可取消");
        }

        orderMapper.updateStatus(id, Constants.ORDER_STATUS_CANCELLED);

        // 更新客房状态为空闲
        roomMapper.updateStatus(order.getRoomId(), Constants.ROOM_STATUS_AVAILABLE);

        // 如果已支付，退款
        if (order.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            paymentService.updateStatusByOrderId(id, Constants.PAYMENT_STATUS_REFUNDED);
        }
    }

    @Override
    public Order getEntityById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }
        return order;
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD" + dateStr + uuid;
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(Order order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 计算入住天数
        if (order.getCheckInDate() != null && order.getCheckOutDate() != null) {
            vo.setDays(Period.between(order.getCheckInDate(), order.getCheckOutDate()).getDays());
        }

        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(order.getStatus()));

        // 获取用户信息
        if (order.getUserId() != null) {
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName());
            }
        }

        // 获取客房信息
        if (order.getRoomId() != null) {
            Room room = roomMapper.selectById(order.getRoomId());
            if (room != null) {
                vo.setRoomNumber(room.getRoomNumber());
            }
        }

        return vo;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case Constants.ORDER_STATUS_PENDING_PAYMENT:
                return "待支付";
            case Constants.ORDER_STATUS_PENDING_CONFIRM:
                return "待确认";
            case Constants.ORDER_STATUS_CONFIRMED:
                return "已确认";
            case Constants.ORDER_STATUS_CHECKED_IN:
                return "已入住";
            case Constants.ORDER_STATUS_COMPLETED:
                return "已完成";
            case Constants.ORDER_STATUS_CANCELLED:
                return "已取消";
            case Constants.ORDER_STATUS_REJECTED:
                return "已拒绝";
            default:
                return "未知";
        }
    }

}

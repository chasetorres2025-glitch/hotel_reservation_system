package com.hotel.controller.user;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.OrderDTO;
import com.hotel.dto.PaymentDTO;
import com.hotel.entity.User;
import com.hotel.service.OrderService;
import com.hotel.service.UserService;
import com.hotel.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户端 - 订单控制器
 */
@RestController
@RequestMapping("/api/user/order")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * 我的订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        User currentUser = userService.getCurrentUser();
        PageResult<OrderVO> result = orderService.listUserOrders(currentUser.getId(), status, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO order = orderService.getOrderById(id);
        return Result.success(order);
    }

    /**
     * 提交预订
     */
    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody OrderDTO orderDTO) {
        OrderVO order = orderService.createOrder(orderDTO);
        return Result.success("预订成功", order);
    }

    /**
     * 订单支付
     */
    @PostMapping("/pay/{id}")
    public Result<Void> pay(@PathVariable Long id, @RequestBody PaymentDTO paymentDTO) {
        paymentDTO.setOrderId(id);
        orderService.payOrder(paymentDTO);
        return Result.success("支付成功", null);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success("订单已取消", null);
    }
}

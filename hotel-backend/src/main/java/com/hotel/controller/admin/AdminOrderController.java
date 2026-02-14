package com.hotel.controller.admin;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.OrderQueryDTO;
import com.hotel.service.OrderService;
import com.hotel.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 订单管理控制器
 */
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单列表查询
     */
    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(OrderQueryDTO queryDTO) {
        PageResult<OrderVO> result = orderService.listOrders(queryDTO);
        return Result.success(result);
    }

    /**
     * 订单详情查询
     */
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO order = orderService.getOrderById(id);
        return Result.success(order);
    }

    /**
     * 确认订单
     */
    @PutMapping("/confirm/{id}")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.success("订单已确认", null);
    }

    /**
     * 拒绝订单
     */
    @PutMapping("/reject/{id}")
    public Result<Void> reject(@PathVariable Long id, @RequestParam(required = false) String remark) {
        orderService.rejectOrder(id, remark);
        return Result.success("订单已拒绝", null);
    }

    /**
     * 办理入住
     */
    @PutMapping("/check-in/{id}")
    public Result<Void> checkIn(@PathVariable Long id) {
        orderService.checkIn(id);
        return Result.success("入住成功", null);
    }

    /**
     * 办理退房
     */
    @PutMapping("/check-out/{id}")
    public Result<Void> checkOut(@PathVariable Long id) {
        orderService.checkOut(id);
        return Result.success("退房成功", null);
    }
}

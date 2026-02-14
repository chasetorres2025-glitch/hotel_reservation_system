package com.hotel.controller.user;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.RoomQueryDTO;
import com.hotel.service.RoomService;
import com.hotel.service.RoomTypeService;
import com.hotel.vo.RoomTypeVO;
import com.hotel.vo.RoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户端 - 客房控制器
 */
@RestController
@RequestMapping("/api/user/room")
public class UserRoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeService roomTypeService;

    /**
     * 客房列表查询（可预订）
     */
    @GetMapping("/list")
    public Result<PageResult<RoomVO>> list(
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        RoomQueryDTO queryDTO = new RoomQueryDTO();
        queryDTO.setTypeId(typeId);
        queryDTO.setMinPrice(minPrice);
        queryDTO.setMaxPrice(maxPrice);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setStatus(0); // 只查询空闲客房
        
        PageResult<RoomVO> result = roomService.listRooms(queryDTO);
        return Result.success(result);
    }

    /**
     * 客房详情查询
     */
    @GetMapping("/{id}")
    public Result<RoomVO> getById(@PathVariable Long id) {
        RoomVO room = roomService.getRoomById(id);
        return Result.success(room);
    }

    /**
     * 客房类型列表
     */
    @GetMapping("/type/list")
    public Result<List<RoomTypeVO>> listTypes() {
        List<RoomTypeVO> list = roomTypeService.listAllEnabled();
        return Result.success(list);
    }
}

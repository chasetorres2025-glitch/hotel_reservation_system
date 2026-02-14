package com.hotel.controller.admin;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.RoomDTO;
import com.hotel.dto.RoomQueryDTO;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.service.RoomService;
import com.hotel.service.RoomTypeService;
import com.hotel.vo.RoomTypeVO;
import com.hotel.vo.RoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端 - 客房管理控制器
 */
@RestController
@RequestMapping("/api/admin/room")
public class AdminRoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeService roomTypeService;

    /**
     * 客房列表查询
     */
    @GetMapping("/list")
    public Result<PageResult<RoomVO>> list(RoomQueryDTO queryDTO) {
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
     * 新增客房
     */
    @PostMapping
    public Result<RoomVO> add(@Valid @RequestBody RoomDTO roomDTO) {
        RoomVO room = roomService.addRoom(roomDTO);
        return Result.success("新增成功", room);
    }

    /**
     * 修改客房
     */
    @PutMapping
    public Result<RoomVO> update(@Valid @RequestBody RoomDTO roomDTO) {
        RoomVO room = roomService.updateRoom(roomDTO);
        return Result.success("修改成功", room);
    }

    /**
     * 删除客房
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return Result.success("删除成功", null);
    }

    /**
     * 修改客房状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        roomService.updateStatus(id, status);
        return Result.success("状态修改成功", null);
    }

    /**
     * 客房类型列表
     */
    @GetMapping("/type/list")
    public Result<List<RoomTypeVO>> listTypes() {
        List<RoomTypeVO> list = roomTypeService.listAll();
        return Result.success(list);
    }

    /**
     * 新增客房类型
     */
    @PostMapping("/type")
    public Result<RoomTypeVO> addType(@Valid @RequestBody RoomTypeDTO roomTypeDTO) {
        RoomTypeVO roomType = roomTypeService.addRoomType(roomTypeDTO);
        return Result.success("新增成功", roomType);
    }

    /**
     * 修改客房类型
     */
    @PutMapping("/type")
    public Result<RoomTypeVO> updateType(@Valid @RequestBody RoomTypeDTO roomTypeDTO) {
        RoomTypeVO roomType = roomTypeService.updateRoomType(roomTypeDTO);
        return Result.success("修改成功", roomType);
    }

    /**
     * 删除客房类型
     */
    @DeleteMapping("/type/{id}")
    public Result<Void> deleteType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return Result.success("删除成功", null);
    }
}

package com.hotel.service;

import com.hotel.common.PageResult;
import com.hotel.dto.RoomDTO;
import com.hotel.dto.RoomQueryDTO;
import com.hotel.entity.Room;
import com.hotel.vo.RoomVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客房服务接口
 */
public interface RoomService {

    /**
     * 分页查询客房列表
     */
    PageResult<RoomVO> listRooms(RoomQueryDTO queryDTO);

    /**
     * 查询可预订客房列表
     */
    List<RoomVO> listAvailableRooms(Long typeId, BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 根据ID查询客房
     */
    RoomVO getRoomById(Long id);

    /**
     * 新增客房
     */
    RoomVO addRoom(RoomDTO roomDTO);

    /**
     * 修改客房
     */
    RoomVO updateRoom(RoomDTO roomDTO);

    /**
     * 删除客房
     */
    void deleteRoom(Long id);

    /**
     * 修改客房状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据ID获取实体
     */
    Room getEntityById(Long id);
}

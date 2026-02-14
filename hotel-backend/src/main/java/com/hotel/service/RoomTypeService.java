package com.hotel.service;

import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.RoomType;
import com.hotel.vo.RoomTypeVO;

import java.util.List;

/**
 * 客房类型服务接口
 */
public interface RoomTypeService {

    /**
     * 查询所有启用的客房类型
     */
    List<RoomTypeVO> listAllEnabled();

    /**
     * 查询所有客房类型
     */
    List<RoomTypeVO> listAll();

    /**
     * 根据ID查询客房类型
     */
    RoomTypeVO getRoomTypeById(Long id);

    /**
     * 新增客房类型
     */
    RoomTypeVO addRoomType(RoomTypeDTO roomTypeDTO);

    /**
     * 修改客房类型
     */
    RoomTypeVO updateRoomType(RoomTypeDTO roomTypeDTO);

    /**
     * 删除客房类型
     */
    void deleteRoomType(Long id);

    /**
     * 修改状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据ID获取实体
     */
    RoomType getEntityById(Long id);
}

package com.hotel.service.impl;

import com.hotel.common.Constants;
import com.hotel.common.ErrorCode;
import com.hotel.common.PageResult;
import com.hotel.dto.RoomDTO;
import com.hotel.dto.RoomQueryDTO;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.RoomService;
import com.hotel.vo.RoomVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客房服务实现类
 */
@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Override
    public PageResult<RoomVO> listRooms(RoomQueryDTO queryDTO) {
        List<Room> rooms = roomMapper.selectList(
                queryDTO.getRoomNumber(),
                queryDTO.getTypeId(),
                queryDTO.getFloor(),
                queryDTO.getStatus()
        );

        Long total = roomMapper.count(
                queryDTO.getRoomNumber(),
                queryDTO.getTypeId(),
                queryDTO.getFloor(),
                queryDTO.getStatus()
        );

        List<RoomVO> voList = rooms.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<RoomVO> listAvailableRooms(Long typeId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Room> rooms = roomMapper.selectAvailableRooms(typeId, minPrice, maxPrice);
        return rooms.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomVO getRoomById(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客房不存在");
        }
        return convertToVO(room);
    }

    @Override
    @Transactional
    public RoomVO addRoom(RoomDTO roomDTO) {
        // 检查房间号是否存在
        if (roomMapper.checkRoomNumberExists(roomDTO.getRoomNumber(), null) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "房间号已存在");
        }

        // 检查客房类型是否存在
        RoomType roomType = roomTypeMapper.selectById(roomDTO.getTypeId());
        if (roomType == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "客房类型不存在");
        }

        Room room = new Room();
        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setTypeId(roomDTO.getTypeId());
        room.setFloor(roomDTO.getFloor());
        room.setPrice(roomDTO.getPrice() != null ? roomDTO.getPrice() : roomType.getBasePrice());
        room.setStatus(roomDTO.getStatus() != null ? roomDTO.getStatus() : Constants.ROOM_STATUS_AVAILABLE);
        room.setDescription(roomDTO.getDescription());
        room.setImageUrl(roomDTO.getImageUrl());

        roomMapper.insert(room);

        return convertToVO(room);
    }

    @Override
    @Transactional
    public RoomVO updateRoom(RoomDTO roomDTO) {
        Room room = roomMapper.selectById(roomDTO.getId());
        if (room == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客房不存在");
        }

        // 检查房间号是否重复
        if (StringUtils.isNotBlank(roomDTO.getRoomNumber()) &&
                roomMapper.checkRoomNumberExists(roomDTO.getRoomNumber(), roomDTO.getId()) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "房间号已存在");
        }

        if (StringUtils.isNotBlank(roomDTO.getRoomNumber())) {
            room.setRoomNumber(roomDTO.getRoomNumber());
        }
        if (roomDTO.getTypeId() != null) {
            room.setTypeId(roomDTO.getTypeId());
        }
        if (roomDTO.getFloor() != null) {
            room.setFloor(roomDTO.getFloor());
        }
        if (roomDTO.getPrice() != null) {
            room.setPrice(roomDTO.getPrice());
        }
        if (roomDTO.getStatus() != null) {
            room.setStatus(roomDTO.getStatus());
        }
        if (roomDTO.getDescription() != null) {
            room.setDescription(roomDTO.getDescription());
        }
        if (roomDTO.getImageUrl() != null) {
            room.setImageUrl(roomDTO.getImageUrl());
        }

        roomMapper.update(room);

        return convertToVO(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        roomMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        roomMapper.updateStatus(id, status);
    }

    @Override
    public Room getEntityById(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客房不存在");
        }
        return room;
    }

    /**
     * 转换为VO
     */
    private RoomVO convertToVO(Room room) {
        if (room == null) {
            return null;
        }
        RoomVO vo = new RoomVO();
        BeanUtils.copyProperties(room, vo);

        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(room.getStatus()));

        // 获取客房类型信息
        if (room.getTypeId() != null) {
            RoomType roomType = roomTypeMapper.selectById(room.getTypeId());
            if (roomType != null) {
                vo.setTypeName(roomType.getTypeName());
                vo.setTypeCode(roomType.getTypeCode());
                vo.setBedType(roomType.getBedType());
                vo.setArea(roomType.getArea());
                vo.setFacilities(roomType.getFacilities());
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
            case Constants.ROOM_STATUS_AVAILABLE:
                return "空闲";
            case Constants.ROOM_STATUS_BOOKED:
                return "预订";
            case Constants.ROOM_STATUS_OCCUPIED:
                return "入住";
            case Constants.ROOM_STATUS_MAINTENANCE:
                return "维护";
            default:
                return "未知";
        }
    }
}

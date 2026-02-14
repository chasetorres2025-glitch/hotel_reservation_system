package com.hotel.service.impl;

import com.hotel.common.Constants;
import com.hotel.common.ErrorCode;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.RoomType;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.RoomTypeService;
import com.hotel.vo.RoomTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客房类型服务实现类
 */
@Slf4j
@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<RoomTypeVO> listAllEnabled() {
        List<RoomType> list = roomTypeMapper.selectAllEnabled();
        return list.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomTypeVO> listAll() {
        List<RoomType> list = roomTypeMapper.selectList();
        return list.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomTypeVO getRoomTypeById(Long id) {
        RoomType roomType = roomTypeMapper.selectById(id);
        if (roomType == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客房类型不存在");
        }
        return convertToVO(roomType);
    }

    @Override
    @Transactional
    public RoomTypeVO addRoomType(RoomTypeDTO roomTypeDTO) {
        // 检查类型编码是否存在
        if (roomTypeMapper.checkTypeCodeExists(roomTypeDTO.getTypeCode(), null) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "类型编码已存在");
        }

        RoomType roomType = new RoomType();
        roomType.setTypeCode(roomTypeDTO.getTypeCode());
        roomType.setTypeName(roomTypeDTO.getTypeName());
        roomType.setBasePrice(roomTypeDTO.getBasePrice());
        roomType.setBedType(roomTypeDTO.getBedType());
        roomType.setArea(roomTypeDTO.getArea());
        roomType.setFacilities(roomTypeDTO.getFacilities());
        roomType.setDescription(roomTypeDTO.getDescription());
        roomType.setStatus(roomTypeDTO.getStatus() != null ? roomTypeDTO.getStatus() : 1);

        roomTypeMapper.insert(roomType);

        return convertToVO(roomType);
    }

    @Override
    @Transactional
    public RoomTypeVO updateRoomType(RoomTypeDTO roomTypeDTO) {
        RoomType roomType = roomTypeMapper.selectById(roomTypeDTO.getId());
        if (roomType == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客房类型不存在");
        }

        // 检查类型编码是否重复
        if (roomTypeDTO.getTypeCode() != null &&
                roomTypeMapper.checkTypeCodeExists(roomTypeDTO.getTypeCode(), roomTypeDTO.getId()) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "类型编码已存在");
        }

        if (roomTypeDTO.getTypeCode() != null) {
            roomType.setTypeCode(roomTypeDTO.getTypeCode());
        }
        if (roomTypeDTO.getTypeName() != null) {
            roomType.setTypeName(roomTypeDTO.getTypeName());
        }
        if (roomTypeDTO.getBasePrice() != null) {
            roomType.setBasePrice(roomTypeDTO.getBasePrice());
        }
        if (roomTypeDTO.getBedType() != null) {
            roomType.setBedType(roomTypeDTO.getBedType());
        }
        if (roomTypeDTO.getArea() != null) {
            roomType.setArea(roomTypeDTO.getArea());
        }
        if (roomTypeDTO.getFacilities() != null) {
            roomType.setFacilities(roomTypeDTO.getFacilities());
        }
        if (roomTypeDTO.getDescription() != null) {
            roomType.setDescription(roomTypeDTO.getDescription());
        }
        if (roomTypeDTO.getStatus() != null) {
            roomType.setStatus(roomTypeDTO.getStatus());
        }

        roomTypeMapper.update(roomType);

        return convertToVO(roomType);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        // 检查是否有客房使用该类型
        int count = roomMapper.countByTypeId(id);
        if (count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该类型下存在客房，无法删除");
        }

        roomTypeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        roomTypeMapper.updateStatus(id, status);
    }

    @Override
    public RoomType getEntityById(Long id) {
        RoomType roomType = roomTypeMapper.selectById(id);
        if (roomType == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客房类型不存在");
        }
        return roomType;
    }

    /**
     * 转换为VO
     */
    private RoomTypeVO convertToVO(RoomType roomType) {
        if (roomType == null) {
            return null;
        }
        RoomTypeVO vo = new RoomTypeVO();
        BeanUtils.copyProperties(roomType, vo);
        return vo;
    }
}

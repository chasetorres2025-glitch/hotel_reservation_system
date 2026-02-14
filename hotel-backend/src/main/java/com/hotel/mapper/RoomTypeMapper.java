package com.hotel.mapper;

import com.hotel.entity.RoomType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客房类型Mapper接口
 */
@Mapper
public interface RoomTypeMapper {

    /**
     * 根据ID查询客房类型
     */
    RoomType selectById(@Param("id") Long id);

    /**
     * 根据类型编码查询
     */
    RoomType selectByTypeCode(@Param("typeCode") String typeCode);

    /**
     * 查询所有启用的客房类型
     */
    List<RoomType> selectAllEnabled();

    /**
     * 查询客房类型列表
     */
    List<RoomType> selectList();

    /**
     * 新增客房类型
     */
    int insert(RoomType roomType);

    /**
     * 更新客房类型
     */
    int update(RoomType roomType);

    /**
     * 删除客房类型
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 检查类型编码是否存在
     */
    int checkTypeCodeExists(@Param("typeCode") String typeCode, @Param("excludeId") Long excludeId);
}

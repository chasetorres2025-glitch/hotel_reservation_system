package com.hotel.mapper;

import com.hotel.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客房Mapper接口
 */
@Mapper
public interface RoomMapper {

    /**
     * 根据ID查询客房
     */
    Room selectById(@Param("id") Long id);

    /**
     * 根据房间号查询
     */
    Room selectByRoomNumber(@Param("roomNumber") String roomNumber);

    /**
     * 查询客房列表（带类型信息）
     */
    List<Room> selectList(@Param("roomNumber") String roomNumber,
                          @Param("typeId") Long typeId,
                          @Param("floor") Integer floor,
                          @Param("status") Integer status);

    /**
     * 统计客房数量
     */
    Long count(@Param("roomNumber") String roomNumber,
               @Param("typeId") Long typeId,
               @Param("floor") Integer floor,
               @Param("status") Integer status);

    /**
     * 查询可预订客房列表（空闲状态）
     */
    List<Room> selectAvailableRooms(@Param("typeId") Long typeId,
                                    @Param("minPrice") java.math.BigDecimal minPrice,
                                    @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * 新增客房
     */
    int insert(Room room);

    /**
     * 更新客房
     */
    int update(Room room);

    /**
     * 删除客房
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新客房状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 检查房间号是否存在
     */
    int checkRoomNumberExists(@Param("roomNumber") String roomNumber, @Param("excludeId") Long excludeId);

    /**
     * 统计各状态客房数量
     */
    int countByStatus(@Param("status") Integer status);

    /**
     * 统计某类型客房数量
     */
    int countByTypeId(@Param("typeId") Long typeId);
}

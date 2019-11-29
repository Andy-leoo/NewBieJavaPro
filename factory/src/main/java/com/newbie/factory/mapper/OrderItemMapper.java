package com.newbie.factory.mapper;

import com.newbie.factory.bean.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    List<OrderItem> selectByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Long userId);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}
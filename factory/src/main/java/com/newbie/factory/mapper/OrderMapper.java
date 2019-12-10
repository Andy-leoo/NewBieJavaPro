package com.newbie.factory.mapper;

import com.newbie.factory.bean.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<Order> selectOrderAll();

    Order selectByUserIdAndOrderNo(@Param("userId") Long userId, @Param("orderNo") Long orderNo);

    Order selectByOrderNo(Long orderNo);

    List<Order> selectByUserId(Long userId);
}
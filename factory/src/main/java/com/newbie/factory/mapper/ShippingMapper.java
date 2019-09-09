package com.newbie.factory.mapper;

import com.newbie.factory.bean.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdShipId(@Param("userId")Integer userId, @Param("id")Integer id);

    int updateByShipping(Shipping shipping);

    Shipping selectByShipping(Integer userId, Integer id);

    List<Shipping> selectByUserId(Integer userId);
}
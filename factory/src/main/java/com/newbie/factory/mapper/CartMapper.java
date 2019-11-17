package com.newbie.factory.mapper;

import com.newbie.factory.bean.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Long userId);

    int selectCartProductCheckedStatusByUserId(Long userId);
}
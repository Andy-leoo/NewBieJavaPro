package com.newbie.factory.mapper;

import com.newbie.factory.bean.Cart;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectCartByCheckAndUseId(Long userId);

    Cart selectCartByUserIdAndProductId(Long userId, Integer productId);

    void checkedOrUncheckedProduct(Long userId, Integer productId, int checked);

    int selectCartProductCount(Long userId);

    List<Cart> selectCartByUserId(Long userId);

    void deleteByProducts(Long userId, String productIds);

    int selectCartProductCheckedStatusByUserId(Long userId);
}
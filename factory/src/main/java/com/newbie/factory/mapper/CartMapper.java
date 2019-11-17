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

    void deleteByProducts(@Param("userId") Long userId,@Param("productIds") String productIds);

    void checkedOrUncheckedProduct(@Param("userId") Long userId, @Param("productId") Integer productId,@Param("checked") Integer checked);

    // 使用函数计数  sum 如果返回空 ，使用 int 基本类型接不到值，要么使用integer接，要么再sql中处理
    int selectCartProductCount(@Param("userId") Long userId);
}
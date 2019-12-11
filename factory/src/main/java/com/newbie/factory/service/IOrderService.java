package com.newbie.factory.service;

import com.newbie.factory.common.ServerResponse;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;

public interface IOrderService {
    ServerResponse queryOrderPayStatus(Long orderNo, Long id);


    ServerResponse checkAliCallBack(HashMap<String, String> params);

    ServerResponse createOrder(@Param("userId") Long userId,@Param("shippingId") Integer shippingId);

    ServerResponse getOrderCartProduct(Long userId);

    ServerResponse getOrderList(Long userId, Integer pageNum, Integer pageSize);

    ServerResponse getOrderDetail(@Param("userId") Long userId,@Param("orderNo") Long orderNo);
}

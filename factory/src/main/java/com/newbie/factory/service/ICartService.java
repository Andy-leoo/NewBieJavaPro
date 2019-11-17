package com.newbie.factory.service;

import com.newbie.factory.bean.vo.CartVo;
import com.newbie.factory.common.ServerResponse;

public interface ICartService {
    ServerResponse list(Long id);

    ServerResponse<CartVo> add(Long id, Integer productId, Integer count);

    ServerResponse<CartVo> update(Long id, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProduct(Long id, String productIds);

    ServerResponse<CartVo> selectOrUnSelect(Long id,Integer productId, int checked);

    ServerResponse<Integer> getCartProductCount(Long id);
}

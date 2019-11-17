package com.newbie.factory.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.newbie.factory.bean.Cart;
import com.newbie.factory.bean.Product;
import com.newbie.factory.bean.vo.CartProductVo;
import com.newbie.factory.bean.vo.CartVo;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.mapper.CartMapper;
import com.newbie.factory.mapper.ProductMapper;
import com.newbie.factory.service.ICartService;
import com.newbie.factory.utils.BigDecimalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/09/30 15:15 <br>
 * 购物车业务处理
 * @see com.newbie.factory.service.impl <br>
 */
@Service
public class CartService implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Value("ftp.server.http.perfix")
    private String imageHost;

    @Override
    public ServerResponse list(Long userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> add(Long userId, Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                                                ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //1. 从数据库中查询 是否有数据  根据用户id  产品id
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if (cart == null){
            // 购物车没有此产品  直接新增
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setUserId(Integer.valueOf(userId.toString()));
            cartMapper.insert(cartItem);
        }else {
            // 购物车有此产品， 修改产品数量
            cart.setUserId(Integer.valueOf(userId.toString()));
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //库存数量校验联动。
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Long userId, Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                                                ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //1. 从数据库中查询 是否有数据  根据用户id  产品id
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if (cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Long userId, String productIds) {
        // productIds  根据，分割而成的 产品id 串
        //使用谷歌的 guava 的方法
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                                                ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByProducts(userId , productIds);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Long userId, Integer productId,int checked) {
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Long userId) {
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    private CartVo getCartVoLimit(Long userId){
        //根据 userid 查 购物车
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        CartVo cartVo = new CartVo();
        //将  cartProductVo  放入 cartVo中
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        //初始化 购物车总价
        BigDecimal cartTotalPrice = new BigDecimal("0");

        //对购物车 判断 是否为空
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem: cartList) {
                //对 cartProductVo 封装
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                //查对应的产品信息
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()){
                        //产品库存 大于 购物车的存量
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        buyLimitCount = cartItem.getQuantity();
                    }else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //更新购物车中的库存
                        Cart cart = new Cart();
                        cart.setId(cartItem.getId());
                        cart.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cart);
                    }

                    cartProductVo.setQuantity(buyLimitCount);

                    //计算总价   当前的 产品与数量
                    cartProductVo.setProductPrice(BigDecimalUtil.mul(buyLimitCount,product.getPrice().doubleValue()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                //如果  产品是勾选的 就将勾选的 加到总价中去
                if (cartItem.getChecked() == Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue() , cartProductVo.getProductPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(imageHost);
        return  cartVo;
    }


    private boolean getAllCheckedStatus(Long userId){
        if (userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }













}

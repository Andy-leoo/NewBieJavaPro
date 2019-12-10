package com.newbie.factory.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.newbie.factory.bean.*;
import com.newbie.factory.bean.vo.OrderItemVo;
import com.newbie.factory.bean.vo.OrderProductVo;
import com.newbie.factory.bean.vo.OrderVo;
import com.newbie.factory.bean.vo.ShippingVo;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.mapper.*;
import com.newbie.factory.service.IOrderService;
import com.newbie.factory.utils.BigDecimalUtil;
import com.newbie.factory.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/11/20 16:55 <br>
 * @TODO 订单 +
 * @see com.newbie.factory.service.impl <br>
 */
@Slf4j
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Value("ftp.server.http.prefix")
    private String ftpHost;

    @Override
    public ServerResponse queryOrderPayStatus(Long orderNo, Long userId) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order ==null){
            return ServerResponse.createByErrorMsg("该用户没有此订单！");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse checkAliCallBack(HashMap<String, String> params) {
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        String totalAmount = params.get("total_amount");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMsg("此订单不是newbie商城项目");
        }
        if (StringUtils.isNotEmpty(totalAmount)){
            if (!totalAmount.equals(order.getPayment())){
                return ServerResponse.createByErrorMsg("回调总金额与订单金额不符，失败！");
            }
        }else {
            return ServerResponse.createByErrorMsg("回调无总金额，失败！");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse createOrder(Long userId, Integer shippingId) {
        //1. 从购物车中获取选中的数据
        List<Cart> cartList = cartMapper.selectCartByCheckAndUseId(userId);
        //2.计算订单总价
        //---2.1 获取购物车中所有的 购物产品
        ServerResponse serverResponse = this.getCartItemList(userId,cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        //---2.2 计算出所有的产品总额
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMsg("购物车为空！");
        }
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);
        //3. 生成订单
        Order order = this.assembleOrder(userId,shippingId,payment);
        if (order == null){
            return ServerResponse.createByErrorMsg("生成订单错误！");
        }
        //4. 入订单产品表
        for (OrderItem orderItem :orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //---4.1 批量插入
        orderItemMapper.batchInsert(orderItemList);
        //5.减少库存
        this.reduceProductStock(orderItemList);
        //6. 清购物车
        this.cleanCart(cartList);
        //7.封装参数并返回
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/10 15:38 <br>
     * @desc 获取订单的商品信息
     */
    @Override
    public ServerResponse getOrderCartProduct(Long userId) {
        OrderProductVo orderProductVo = new OrderProductVo();

        //拿出 购物车中的 商品
        List<Cart> cartList = cartMapper.selectCartByCheckAndUseId(userId);
        ServerResponse serverResponse = this.getCartItemList(userId,cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        //放进 vo
        List<OrderItemVo> orderItemVos = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem :orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVos.add(assembleOrderItemVo(orderItem));
        }

        orderProductVo.setOrderItemVoList(orderItemVos);
        orderProductVo.setImageHost(ftpHost);
        orderProductVo.setProductTotalPrice(payment);

        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse getOrderList(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        //根据
        List<OrderVo> orderVos = assembleOrderVoList(orderList,userId);

        return null;
    }


    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Long userId) {

        return null;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/4 17:11 <br>
     * @desc 封装 订单 参数
     */
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        //包装order vo类
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());

        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());

        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping!=null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setImageHost(ftpHost);

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem: orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }

        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/10 15:26 <br>
     * @desc 封装订单中的产品vo
     */
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        BeanUtils.copyProperties(orderItem,orderItemVo);
        return orderItemVo;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/10 15:14 <br>
     * @desc 封装 shipping vo 参数  收货地址
     */
    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        //bean 值复制
        BeanUtils.copyProperties(shipping,shippingVo);
        return shippingVo;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/4 8:55 <br>
     * @desc 清空购物车
     */
    private void cleanCart(List<Cart> cartList) {
        for (Cart cart :cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/4 8:52 <br>
     * @desc 减少库存
     */
    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem item :orderItemList){
            Product product = productMapper.selectByPrimaryKey(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/3 16:12 <br>
     * @desc 生成订单
     */
    private Order assembleOrder(Long userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        //生成不重复
        Long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId.intValue());
        order.setPayment(payment);
        order.setShippingId(shippingId);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPostage(0);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setCreateTime(DateTimeUtil.getDate());
        int rowCount = orderMapper.insertSelective(order);
        if (rowCount >0 ){
            return order;
        }
        return null;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/3 16:18 <br>
     * @desc 生成订单号
     */
    private Long generateOrderNo() {
        long createTime = System.currentTimeMillis();
        //当前时间戳 + 1-99 随机数
        return createTime+new Random().nextInt(100);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/3 15:55 <br>
     * @desc 计算订单总额
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem item: orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(),item.getTotalPrice().doubleValue());
        }
        return payment;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/3 11:19 <br>
     * @desc 获取购物车中所有的 购物产品
     */
    private ServerResponse getCartItemList(Long userId,List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMsg("购物车为空");
        }

        for (Cart cartItem: cartList){
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            //校验 产品状态
            if (Const.ProductStatusEnum.NO_SALE.getCode() != product.getStatus()){
                return ServerResponse.createByErrorMsg("产品 ：" + product.getName() +"不再售卖！");
            }
            //校验产品库存
            if(cartItem.getQuantity() > product.getStock()){
                return ServerResponse.createByErrorMsg("产品："+ product.getName() +"库存不足");
            }

            OrderItem item = new OrderItem();
            item.setUserId(userId.intValue());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setQuantity(cartItem.getQuantity());
            item.setCurrentUnitPrice(product.getPrice());
            item.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));

            orderItemList.add(item);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }
}

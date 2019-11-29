package com.newbie.factory.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbie.factory.bean.Order;
import com.newbie.factory.bean.OrderItem;
import com.newbie.factory.bean.PayInfo;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.mapper.OrderItemMapper;
import com.newbie.factory.mapper.OrderMapper;
import com.newbie.factory.mapper.PayInfoMapper;
import com.newbie.factory.service.IOrderService;
import com.newbie.factory.utils.BigDecimalUtil;
import com.newbie.factory.utils.DateTimeUtil;
import com.newbie.factory.utils.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class OrderService implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
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
}

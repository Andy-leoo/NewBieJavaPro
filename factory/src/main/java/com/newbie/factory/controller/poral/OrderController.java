package com.newbie.factory.controller.poral;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.newbie.factory.bean.User;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.service.IOrderService;
import com.newbie.factory.service.IPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/11/20 16:53 <br>
 * @TODO 订单
 * @see com.newbie.factory.controller.poral <br>
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IPayService iPayService;
    @Value("alipay_public_key")
    private String alipayPublicKey;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/29 17:27 <br>
     * @desc 创建订单
     */
    @RequestMapping("/create")
    public ServerResponse create(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId() , shippingId);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/29 17:27 <br>
     * @desc 获取订单的商品信息
     */
    @RequestMapping("/get_order_cart_product")
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/29 17:27 <br>
     * @desc 获取订单的商品信息
     */
    @RequestMapping("/list")
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize,
                                                    @RequestParam(value = "pageNum" ,defaultValue = "1") Integer pageNum){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/11 9:33 <br>
     * @desc 订单详情
     */
    @RequestMapping("/detail")
    public ServerResponse detail(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }






    /*************************************************************************************
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/21 14:11 <br>
     * @desc 支付接口
     */
    @RequestMapping("/pay")
    public ServerResponse pay(HttpSession session, Long orderNo , HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path =request.getSession().getServletContext().getRealPath("upload");
        return iPayService.pay(orderNo , user.getId(), path);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/28 15:18 <br>
     * @desc 查询订单支付状态
     */
    @RequestMapping("/query_order_pay_status")
    public ServerResponse queryOrderPayStatus(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.queryOrderPayStatus(orderNo , user.getId());
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/28 16:19 <br>
     * @desc 支付宝回调
     */
    @RequestMapping("alipay_callback")
    public Object alipayCallback(HttpServletRequest request){
        HashMap<String, String> params = Maps.newHashMap();
        //获取 回调数据
        Map requestParamMap = request.getParameterMap();
        //使用迭代器 循环取
        for (Iterator iter = requestParamMap.keySet().iterator();iter.hasNext();){
            String name = (String) iter.next();
            String[] values = (String[]) requestParamMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] +",";
            }
            //放入新建的数据map
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //在通知返回参数列表中，除去sign、sign_type两个参数外  才是待验签
        params.remove("sign_type");
        //sign 字段 会在验签时候 自己去掉
        try {
            boolean flag = AlipaySignature.rsaCheckV2(params, alipayPublicKey, "utf-8", Configs.getSignType());

            if (!flag){
                return ServerResponse.createByErrorMsg("非法请求，验证不通过，请停止回调！");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调验签失败，" , e);
        }
        //验签通过  进行参数验证
        ServerResponse serverResponse = iOrderService.checkAliCallBack(params);
        if (!serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }
}

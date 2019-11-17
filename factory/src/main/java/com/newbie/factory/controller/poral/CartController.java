package com.newbie.factory.controller.poral;

import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.User;
import com.newbie.factory.bean.vo.CartVo;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/09/30 15:05 <br>
 * 购物车
 * @see com.newbie.factory.controller.poral <br>
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/14 17:04 <br>
     * @desc 增加购物车
     */
    @RequestMapping("/add")
    public ServerResponse<CartVo> add(HttpSession session , Integer count , Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.add(user.getId(),productId,count);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/17 9:46 <br>
     * @desc 修改购物车
     */
    @RequestMapping("/update")
    public ServerResponse<CartVo> update(HttpSession session , Integer count , Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.update(user.getId(),productId,count);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/17 20:52 <br>
     * @desc 删除购物车 产品
     */
    @RequestMapping("/delete_product")
    public ServerResponse<CartVo> deleteProduct(HttpSession session , String productIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.deleteProduct(user.getId(),productIds);
    }



    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/30 15:21 <br>
     * @desc 购物车列表
     */
    @RequestMapping("/list")
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.list(user.getId());
    }

    /***
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/17 22:24 <br>
     * @desc 全选
     */
    @RequestMapping("/select_all")
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/17 22:25 <br>
     * @desc T全不选
     */
    @RequestMapping("/un_select_all")
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelect(user.getId(),null,Const.Cart.NOCHECKED);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/17 22:48 <br>
     * @desc 单选
     */
    @RequestMapping("/select")
    public ServerResponse<CartVo> select(HttpSession session ,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/17 22:49 <br>
     * @desc 单反选
     */
    @RequestMapping("/un_select")
    public ServerResponse<CartVo> unSelect(HttpSession session ,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelect(user.getId(),productId,Const.Cart.NOCHECKED);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/11/17 22:50 <br>
     * @desc 查询在购物车里的产品数量
     */
    @RequestMapping("get_cart_product_count")
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createBySuccess(0);
        }
        return cartService.getCartProductCount(user.getId());
    }
}

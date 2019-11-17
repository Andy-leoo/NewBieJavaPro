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
}

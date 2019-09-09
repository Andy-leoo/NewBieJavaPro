package com.newbie.factory.controller.poral;

import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.Shipping;
import com.newbie.factory.bean.User;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/09/09 14:18 <br>
 * @ 收货地址
 * @see com.newbie.factory.controller.poral <br>
 */
@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService shippingService;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/9 15:02 <br>
     * @desc 新增 地址
     */
    @RequestMapping("/add")
    public ServerResponse insertShipping(HttpSession session , Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        return shippingService.insert(Integer.valueOf(user.getId().toString()),shipping);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/9 17:07 <br>
     * @desc 删除
     */
    @RequestMapping("/del")
    public ServerResponse<String> del(HttpSession session , Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        return shippingService.del(Integer.valueOf(user.getId().toString()),shippingId);
    }

    @RequestMapping("/update")
    public ServerResponse update(HttpSession session , Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        return shippingService.update(Integer.valueOf(user.getId().toString()),shipping);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/9 17:07 <br>
     * @desc 查询
     */
    @RequestMapping("/select")
    public ServerResponse<String> select(HttpSession session , Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        return shippingService.select(Integer.valueOf(user.getId().toString()),shippingId);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/9 17:07 <br>
     * @desc 分页查询列表
     */
    @RequestMapping("/list")
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                         HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        return shippingService.pageList(Integer.valueOf(user.getId().toString()),pageNum,pageSize);
    }

}

package com.newbie.factory.controller.backend;

import com.newbie.factory.bean.User;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.service.IOrderService;
import com.newbie.factory.service.IUserService;
import lombok.extern.slf4j.Slf4j;
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
 * @createDate 2019/11/29 14:50 <br>
 * @ 后台 订单
 * @see com.newbie.factory.controller.backend <br>
 */
@Slf4j
@RestController
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderService orderService;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/12/11 10:01 <br>
     * @desc 管理员查询 订单列表
     */
    public ServerResponse list(@RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "pageNum" ,defaultValue = "1") Integer pageNum,
                               HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登入，请登入！");
        }
        //校验是否为管理员
        if (userService.checkAdminRole(user).isSuccess()) {
            return orderService.getManageOrderList(pageNum,pageSize);
        }
        return ServerResponse.createByErrorMsg("无权限操作！");
    }
}

package com.newbie.factory.controller.backend;

import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.User;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/28 16:03 <br>
 * @ 后台 管理员user
 * @see com.newbie.factory.controller.backend <br>
 */
@RestController
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 16:06 <br>
     * @desc 后台管理员登入
     */
    @RequestMapping(value = "/login" , method = RequestMethod.POST)
    public ServerResponse<User> login(String username , String password , HttpSession session){
        ServerResponse<User> response = iUserService.loginUser(username, password);
        if (response.isSuccess()){
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN){
                //为管理员
                session.setAttribute(Const.CURRENT_USER , user);
            }else {
                return ServerResponse.createByErrorMsg("不是管理员不能登入");
            }
        }
        return response;
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public ServerResponse<PageInfo<User>> userPageList(int page , int pageSize ,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        return iUserService.userPageList(page,pageSize);
    }
}

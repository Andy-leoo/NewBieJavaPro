package com.newbie.factory.controller.poral;

import com.newbie.factory.bean.User;
import com.newbie.factory.bean.vo.UserVo;
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
 * @createDate 2019/08/26 18:04 <br>
 * 门户 user 控制层
 * @see com.newbie.factory.controller <br>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/27 15:36 <br>
     * @desc 门户登入
     */
    @RequestMapping(value = "/login" , method = RequestMethod.POST)
    public ServerResponse<User> loginUser(String userName, String password , HttpSession session){
        ServerResponse<User> login = userService.loginUser(userName, password);
        if (login.isSuccess()){
            session.setAttribute(Const.CURRENT_USER , login.getData());
        }
        return login;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 11:12 <br>
     * @desc 门户登出
     */
    @RequestMapping(value = "/logout" , method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 11:48 <br>
     * @desc 注册用户
     */
    @RequestMapping(value = "/register" , method = RequestMethod.POST)
    public ServerResponse<String> register(UserVo user){
        return userService.register(user);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 12:55 <br>
     * @desc 校验参数
     */
    @RequestMapping(value = "/check_vaild" , method = RequestMethod.POST)
    public ServerResponse<String> checkVaild(String str,String type){
        return userService.checkValid(str ,type);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 12:57 <br>
     * @desc 获取登入用户信息
     */
    @RequestMapping(value = "/get_user_info" , method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMsg("用户未登入，无法获取当前用户信息");
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 14:38 <br>
     * @desc 获取找回密码问题
     */
    @RequestMapping(value = "forget_get_question" , method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username){
        return userService.selectUseruQuestion(username);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 15:10 <br>
     * @desc 提交问题答案
     */
    @RequestMapping(value = "forget_check_answer" , method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username,String question , String answer){
        return userService.checkAnswer(username,question,answer);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 15:12 <br>
     * @desc 重设密码
     */
    @RequestMapping(value = "forget_reset_password" , method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username , String passwordNew ,String forgetToken){
        return userService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 15:34 <br>
     * @desc 登录状态 修改密码
     */
    @RequestMapping(value = "reset_password" , method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(String passwordOld , String passwordNew , HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMsg("用户未登录");
        }
        return userService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 15:42 <br>
     * @desc 修改基本信息
     */
    @RequestMapping(value = "update_information" , method = RequestMethod.POST)
    public ServerResponse<String> updateInformation(HttpSession session , UserVo userVO){
        User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (sessionUser == null){
            return ServerResponse.createByErrorMsg("用户未登入");
        }
        userVO.setId(sessionUser.getId());
        ServerResponse response = userService.updateInformation(userVO);
        if (response.isSuccess()){
            return ServerResponse.createBySuccessMsg("更新信息成功");
        }
        return response;
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/28 16:02 <br>
     * @desc 获取用户基本信息
     */
    @RequestMapping(value = "get_information.do" , method = RequestMethod.POST)
    public ServerResponse<UserVo> getInformation(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "未登入！");
        }
        return userService.getInformation(user.getId());
    }
}

package com.newbie.factory.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.User;
import com.newbie.factory.bean.UserBasicInf;
import com.newbie.factory.bean.vo.UserVo;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.common.TokenCache;
import com.newbie.factory.mapper.UserBasicInfMapper;
import com.newbie.factory.mapper.UserMapper;
import com.newbie.factory.service.IUserService;
import com.newbie.factory.utils.MD5Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/27 15:18 <br>
 *  门户 user 业务逻辑层
 * @see com.newbie.factory.service.impl <br>
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserBasicInfMapper userBasicInfMapper;

    @Override
    public ServerResponse<User> loginUser(String userName, String password) {
        /**
         * 检查用户名
         */
        int resultCount = userMapper.checkUserName(userName);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }
        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLoginUser(userName, md5password);
        if (user == null) {
            return ServerResponse.createByErrorMsg("密码错误");
        }
        //将 user 用户的密码滞空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登入成功", user);
    }

    @Override
    public ServerResponse<String> register(UserVo userVO) {
        //检测 用户名
        ServerResponse<String> validResponse = checkValid(userVO.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        //检测 Email
        validResponse = checkValid(userVO.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        //copy数据
        User user = new User();
        BeanUtils.copyProperties(userVO,user);
        //角色
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //密码 MD5 加密
        user.setPassword(MD5Util.MD5EncodeUtf8(userVO.getPassword()));

        UserBasicInf userBasicInf = new UserBasicInf();
        BeanUtils.copyProperties(userVO,userBasicInf);
        //新增user
        int resultCount = userMapper.insert(user);
        int count = userBasicInfMapper.insert(userBasicInf);
        if (resultCount == 0 || count == 0){
            return ServerResponse.createByErrorMsg("注册失败");
        }
        return ServerResponse.createBySuccessMsg("注册成功");
    }

    public ServerResponse<String> checkValid(String str , String type){
        if (StringUtils.isNotBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMsg("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userBasicInfMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMsg("email已存在");
                }
            }
            if (Const.MOBILE.equals(type)) {
                int resultCount = userBasicInfMapper.checkMobile(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMsg("mobile已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccessMsg("校验成功");
    }

    @Override
    public ServerResponse<String> selectUseruQuestion(String username) {
        //先对username 进行检测
        ServerResponse<String> valid = checkValid(username, Const.USERNAME);
        if (valid.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        //todo 关联查询  修改
        String userId = userMapper.selectIdByUserName(username);
        String question = userBasicInfMapper.selectQuestion(userId);
        if (question != null){
            return ServerResponse.createBySuccessMsg(question);
        }
        return ServerResponse.createByErrorMsg("用户未设置找回密码问题！");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int count = userMapper.checkAnswer(username, question, answer);
        if (count > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username , forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMsg("问题回答错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //1.判断token
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("参数错误，重新传递Token");
        }
        //2.判断用户名
        ServerResponse<String> valid = checkValid(username, Const.USERNAME);
        if (valid.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        //3.获取token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("token无效或过期");
        }
        //4.判断token是否一致
        if (StringUtils.equals(forgetToken,token)){
            String md5PasswordNew = MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userMapper.updatePasswordByUsername(username, md5PasswordNew);
            if (count > 0){
                return ServerResponse.createBySuccessMsg("重置密码成功！");
            }
        }else {
            return ServerResponse.createByErrorMsg("token错误，请重新发起");
        }
        return ServerResponse.createByErrorMsg("修改密码失败！");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        if (StringUtils.equals(passwordOld,passwordNew)){
            return ServerResponse.createByErrorMsg("新密码不能与旧密码相同！");
        }
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count如果不指定id
        int count = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (count == 0){
            return ServerResponse.createByErrorMsg("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updatePasswordCount = userMapper.updateByPrimaryKeySelective(user);
        if (updatePasswordCount > 0){
            return ServerResponse.createBySuccessMsg("更新密码成功");
        }
        return ServerResponse.createByErrorMsg("更新密码失败");
    }

    @Override
    public ServerResponse updateInformation(UserVo userVO) {
        //username 不能够被更新
        //email 也要进行一个校验，校验其他有没有使用到 email
        int count = userBasicInfMapper.checkEmailByUserId(userVO.getEmail() , userVO.getId());
        if (count > 0){
            return ServerResponse.createByErrorMsg("email重复，请更换！");
        }
        count = userBasicInfMapper.checkMobileByUserId(userVO.getEmail() , userVO.getId());
        if (count > 0){
            return ServerResponse.createByErrorMsg("mobile重复，请更换！");
        }
        UserBasicInf updateUser = new UserBasicInf();
        updateUser.setId(userVO.getId());
        updateUser.setEmail(userVO.getEmail());
        updateUser.setMobile(userVO.getMobile());
        updateUser.setQuestion(userVO.getQuestion());
        updateUser.setAnswer(userVO.getAnswer());
        int resultCount = userBasicInfMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount > 0){
            return ServerResponse.createBySuccessMsg("更新个人信息成功");
        }
        return  ServerResponse.createByErrorMsg("更新失败");
    }

    @Override
    public ServerResponse<UserVo> getInformation(Long id) {
        UserVo userVO = new UserVo();
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null){
            return ServerResponse.createByErrorMsg("找不到当前用户");
        }
        UserBasicInf userBasicInf = userBasicInfMapper.selectByPrimaryKey(id);
        //copy
        BeanUtils.copyProperties(user,userVO);
        BeanUtils.copyProperties(userBasicInf,userVO);
        //滞空 password
        userVO.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(userVO);
    }

    @Override
    public ServerResponse<PageInfo<User>> userPageList(int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        List<User> list = userMapper.queryAll();
        PageInfo<User> userPageInfo = new PageInfo<>(list);
        return ServerResponse.createBySuccess(userPageInfo);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}

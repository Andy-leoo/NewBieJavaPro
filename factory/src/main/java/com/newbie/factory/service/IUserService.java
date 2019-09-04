package com.newbie.factory.service;


import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.User;
import com.newbie.factory.bean.vo.UserVO;
import com.newbie.factory.common.ServerResponse;

public interface IUserService {

    ServerResponse<User> loginUser(String userName , String password);

    ServerResponse<String> register(UserVO user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectUseruQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse updateInformation(UserVO userVO);

    ServerResponse<UserVO> getInformation(Long id);

    ServerResponse<PageInfo<User>> userPageList(int page, int pageSize);

    ServerResponse checkAdminRole(User user);
}

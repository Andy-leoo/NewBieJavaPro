package com.newbie.factory.mapper;

import com.newbie.factory.bean.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    User selectLoginUser(String userName, String md5password);

    String selectIdByUserName(String username);

    int checkAnswer(String username, String question, String answer);

    int updatePasswordByUsername(String username, String md5PasswordNew);

    int checkPassword(String md5EncodeUtf8, Long id);

    List<User> queryAll();

}
package com.newbie.factory.mapper;

import com.newbie.factory.bean.UserBasicInf;

public interface UserBasicInfMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserBasicInf record);

    int insertSelective(UserBasicInf record);

    UserBasicInf selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBasicInf record);

    int updateByPrimaryKey(UserBasicInf record);

    int checkEmail(String str);

    int checkMobile(String str);

    String selectQuestion(String userId);

    int checkEmailByUserId(String email, Long id);

    int checkMobileByUserId(String email, Long id);
}
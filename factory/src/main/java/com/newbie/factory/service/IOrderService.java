package com.newbie.factory.service;

import com.newbie.factory.common.ServerResponse;

import java.util.HashMap;

public interface IOrderService {
    ServerResponse queryOrderPayStatus(Long orderNo, Long id);


    ServerResponse checkAliCallBack(HashMap<String, String> params);

}

package com.newbie.factory.service;

import com.newbie.factory.common.ServerResponse;

public interface IPayService {
    ServerResponse pay(Long orderNo, Long id, String path);


}

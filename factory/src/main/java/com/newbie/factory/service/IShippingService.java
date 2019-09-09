package com.newbie.factory.service;

import com.newbie.factory.bean.Shipping;
import com.newbie.factory.common.ServerResponse;

public interface IShippingService {

    ServerResponse insert(Integer userId ,Shipping shipping);

    ServerResponse del(Integer userId ,Integer id);

    ServerResponse update(Integer userId ,Shipping shipping);

    ServerResponse select(Integer userId , Integer id);

    ServerResponse pageList(Integer userId , Integer pageNum , Integer pageSize);
}

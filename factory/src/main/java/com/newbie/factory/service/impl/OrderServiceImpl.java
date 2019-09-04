package com.newbie.factory.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.Order;
import com.newbie.factory.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/09 9:57 <br>
 * @ 订单 业务处理
 * @see com.newbie.factory.service <br>
 */
@Service
public class OrderServiceImpl {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/9 10:00 <br>
     * @desc page当前页  pagesize表示当前展示多少个
     */
    public PageInfo<Order> findByPageOrder(int page,int pageSize){
        // pageHelper 帮我们生成分页语句
        PageHelper.startPage(page,pageSize);
        List<Order> orders = orderMapper.selectOrderAll();
        PageInfo<Order> orderPageInfo = new PageInfo<>(orders);
        return orderPageInfo;
    }
}

package com.newbie.factory.controller.poral;

import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.Order;
import com.newbie.factory.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/09 9:52 <br>
 * @ 订单 控制类
 * @see com.newbie.factory.controller <br>
 */
@RestController
public class OrderController {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/27 15:04 <br>
     * @desc 测试 订单分页列表
     */
    @RequestMapping("/orderListPage")
    public PageInfo<Order> findByPage(int page , int pageSize){
        return orderServiceImpl.findByPageOrder(page,pageSize);
    }
}

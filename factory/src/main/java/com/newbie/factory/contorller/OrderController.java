package com.newbie.factory.contorller;

import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.Order;
import com.newbie.factory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/09 9:52 <br>
 * @ 订单 控制类
 * @see com.newbie.factory.contorller <br>
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/orderListPage")
    public PageInfo<Order> findByPage(int page , int pageSize){
        return orderService.findByPageOrder(page,pageSize);
    }
}

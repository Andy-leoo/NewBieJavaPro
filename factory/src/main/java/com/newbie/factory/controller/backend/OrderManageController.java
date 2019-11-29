package com.newbie.factory.controller.backend;

import com.newbie.factory.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/11/29 14:50 <br>
 * @TODO 后台 订单
 * @see com.newbie.factory.controller.backend <br>
 */
@Slf4j
@RestController
@RequestMapping("/manage/order")
public class OrderManageController {

    public ServerResponse list(){
        return null;
    }
}

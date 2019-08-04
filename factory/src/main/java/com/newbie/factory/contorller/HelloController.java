package com.newbie.factory.contorller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Andy-J<br>
 * @version 1.0<br>
 * @createDate 2019/8/3 22:04 <br>
 * @desc 测试
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(){
        return "hello world";
    }

}

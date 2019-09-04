package com.newbie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
 * @author Andy-J<br>
 * @version 1.0<br>
 * @createDate 2019/8/3 22:07 <br>
 * @desc 主程序类
 */
@EnableTransactionManagement//开启注解的事务管理
@MapperScan("com.newbie.factory.mapper")
@SpringBootApplication
public class NewBieAppliation {
    public static void main(String[] args) {
        SpringApplication.run(NewBieAppliation.class, args);
    }
}

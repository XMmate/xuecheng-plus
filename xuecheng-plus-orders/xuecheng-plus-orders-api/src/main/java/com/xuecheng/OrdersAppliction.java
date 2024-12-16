package com.xuecheng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan({"com.xuecheng.orders.mapper"})
//@ComponentScan(basePackages = "com.xuecheng.messagesdk.service")
public class OrdersAppliction {
    public static void main(String[] args) {
        SpringApplication.run(OrdersAppliction.class,args);
    }
}

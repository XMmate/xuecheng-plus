package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages={"com.xuecheng.auth.ucenter.feignclient"})
public class AuthAppliction {
    public static void main(String[] args) {
        SpringApplication.run(AuthAppliction.class,args);
    }
}

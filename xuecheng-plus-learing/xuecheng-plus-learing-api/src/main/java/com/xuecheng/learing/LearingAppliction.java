package com.xuecheng.learing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication(scanBasePackages = "com.xuecheng.learing")
@EnableDiscoveryClient
@EnableFeignClients(basePackages={"com.xuecheng.learing.feignclient"})
public class LearingAppliction {
    public static void main(String[] args) {
        SpringApplication.run(LearingAppliction.class,args);
    }
}

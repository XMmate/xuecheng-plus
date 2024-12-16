package com.xuecheng.searh;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SearchAppliction {
    public static void main(String[] args) {
        SpringApplication.run(SearchAppliction.class,args);
    }
}

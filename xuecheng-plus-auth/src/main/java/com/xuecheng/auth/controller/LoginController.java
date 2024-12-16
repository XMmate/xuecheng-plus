package com.xuecheng.auth.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Slf4j
public class LoginController {

    @RequestMapping("/login-success")
    public String loginSuccess(){

        return "登录成功";
    }
}

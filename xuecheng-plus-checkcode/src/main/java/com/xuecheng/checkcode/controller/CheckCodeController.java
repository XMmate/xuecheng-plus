package com.xuecheng.checkcode.controller;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class CheckCodeController {
    @Resource(name = "PicCheckCodeService")
    private CheckCodeService picCheckCodeService;


    /**
     * 请求验证码
     * @param checkCodeParamsDto
     * @return
     */
    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto){
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    /**
     * 校验验证吗
     * @param key
     * @param code
     * @return
     */
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code){
        Boolean isSuccess = picCheckCodeService.verify(key,code);
        return isSuccess;
    }
}


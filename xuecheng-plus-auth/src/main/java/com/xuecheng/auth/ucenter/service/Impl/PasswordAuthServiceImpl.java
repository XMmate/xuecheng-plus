package com.xuecheng.auth.ucenter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xuecheng.auth.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.auth.ucenter.mapper.XcUserMapper;
import com.xuecheng.auth.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.auth.ucenter.model.dto.XcUserExt;
import com.xuecheng.auth.ucenter.model.po.XcUser;
import com.xuecheng.auth.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 密码认证,实现统一认证入口
 */

@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {

        //校验验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();

        if(StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)){
            throw new RuntimeException("验证码为空");

        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if(!verify){
            throw new RuntimeException("验证码输入错误");
        }


        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser==null){
            throw  new RuntimeException("用户不存在");
        }
        String password = authParamsDto.getPassword();
        String passwordDb = xcUser.getPassword();
        if (!password.equals(passwordDb) ){
            throw  new RuntimeException("用户名或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        return xcUserExt;
    }
}

package com.xuecheng.auth.ucenter.service.Impl;
import com.alibaba.fastjson.JSON;
import com.xuecheng.auth.ucenter.mapper.XcMenuMapper;
import com.xuecheng.auth.ucenter.mapper.XcPermissionMapper;
import com.xuecheng.auth.ucenter.mapper.XcUserMapper;
import com.xuecheng.auth.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.auth.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.auth.ucenter.model.dto.XcUserExt;
import com.xuecheng.auth.ucenter.model.po.XcMenu;
import com.xuecheng.auth.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcUserRoleMapper xcUserRoleMapper;
    @Autowired
    XcPermissionMapper xcPermissionMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    XcMenuMapper xcMenuMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


       
        AuthParamsDto authParamsDto = null;
        try {
            //将认证参数转为AuthParamsDto类型
            authParamsDto = JSON.parseObject(username, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}",username);
            throw new RuntimeException("认证请求数据格式不对");
        }
        String authType = authParamsDto.getAuthType();
        AuthService authService =  applicationContext.getBean(authType + "_authservice", AuthService.class);
        XcUserExt execute = authService.execute(authParamsDto);
        return getUserPrincipal(execute);

    }


    public UserDetails getUserPrincipal(XcUserExt user){
        List<String> permissions = new ArrayList<>();
        permissions.add("p1");
      //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());

        for (XcMenu xcMenu : xcMenus) {
            permissions.add( xcMenu.getCode());
        }
        String[] authorities = permissions.toArray(new String[0]);

        String password = user.getPassword();
        user.setPermissions(permissions);
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password ).authorities(authorities).build();
        return userDetails;
    }

    }


package com.xuecheng.auth.ucenter.service;

import com.xuecheng.auth.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.auth.ucenter.model.dto.XcUserExt;

/**
 * 统一认证入口
 */
public interface AuthService {
    XcUserExt execute(AuthParamsDto authParamsDto);

}

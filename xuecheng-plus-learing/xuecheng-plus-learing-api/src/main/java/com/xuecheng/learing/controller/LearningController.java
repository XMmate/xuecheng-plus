package com.xuecheng.learing.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.learing.service.LearningService;
import com.xuecheng.learing.util.SecurityUtil;
import com.xuecheng.learing.util.SecurityUtil.XcUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LearningController {

    @Autowired
    LearningService learningService;

    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getvideo(@PathVariable("courseId") Long courseId, @PathVariable("teachplanId") Long teachplanId, @PathVariable("mediaId") String mediaId) {

        //登录用户
        XcUser user = SecurityUtil.getUser();
        String userId = null;
        if (user != null) {
            userId = user.getId();
        }
        //获取视频
        return learningService.getVideo(userId, courseId, teachplanId, mediaId);
    }
}

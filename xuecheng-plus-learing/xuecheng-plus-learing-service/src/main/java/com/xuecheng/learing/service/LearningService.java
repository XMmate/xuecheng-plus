package com.xuecheng.learing.service;

import com.xuecheng.base.model.RestResponse;

public interface LearningService {
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}

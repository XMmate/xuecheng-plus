package com.xuecheng.learing.feignclient;

import com.xuecheng.base.model.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


 @FeignClient(value = "media",fallbackFactory = MediaServiceClientFallbackFactory.class)
 @RequestMapping("/media")
 public interface MediaServiceClient {
  /**
   * 远程调用媒资服务获取
   * @param mediaId
   * @return
   */
  @GetMapping("/open/preview/{mediaId}")
  public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId);

 }

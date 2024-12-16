package com.xuecheng.media.service;

import com.xuecheng.media.po.MediaProcess;

import java.util.List;

public interface MediaProcessService {
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

}

package com.xuecheng.media.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.po.MediaProcess;
import com.xuecheng.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class MediaProcessServiceImpl implements MediaProcessService {
    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        LambdaQueryWrapper<MediaProcess> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaProcess::getId,shardIndex);
        queryWrapper.eq(MediaProcess::getStatus,1).or().eq(MediaProcess::getStatus,3);

        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectList(queryWrapper);
        return mediaProcesses;
    }
}

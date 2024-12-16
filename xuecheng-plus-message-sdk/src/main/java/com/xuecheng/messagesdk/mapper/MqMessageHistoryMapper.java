package com.xuecheng.messagesdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.messagesdk.model.po.MqMessageHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MqMessageHistoryMapper extends BaseMapper<MqMessageHistory> {
}

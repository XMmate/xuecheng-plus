package com.xuecheng.messagesdk.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.messagesdk.mapper.MqMessageHistoryMapper;
import com.xuecheng.messagesdk.model.po.MqMessageHistory;
import com.xuecheng.messagesdk.service.MqMessageHistoryService;
import org.springframework.stereotype.Service;


@Service
public class MqMessageHistoryServiceImpl  extends ServiceImpl<MqMessageHistoryMapper, MqMessageHistory> implements MqMessageHistoryService {

}

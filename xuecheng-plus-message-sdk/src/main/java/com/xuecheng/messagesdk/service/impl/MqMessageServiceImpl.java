package com.xuecheng.messagesdk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.messagesdk.mapper.MqMessageHistoryMapper;
import com.xuecheng.messagesdk.mapper.MqMessageMapper;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.model.po.MqMessageHistory;
import com.xuecheng.messagesdk.service.MqMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {
    @Autowired
    MqMessageMapper mqMessageMapper;

    @Autowired
    MqMessageHistoryMapper mqMessageHistoryMapper;

    /**
     * 查询任务
     * @param shardIndex
     * @param shardTotal
     * @param messageType
     * @param count
     * @return
     */
    @Override
    public List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count) {
        return mqMessageMapper.selectListByShardIndex(shardTotal,shardIndex,messageType,count);
    }

    /**
     * 完成任务
     * @param id
     * @return
     */
    @Transactional
    @Override
    public int completed(long id) {
        MqMessage mqMessage = new MqMessage();
        //完成任务
        mqMessage.setState("1");
        int update = mqMessageMapper.update(mqMessage, new LambdaQueryWrapper<MqMessage>().eq(MqMessage::getId, id));
        if(update>0){
            mqMessage = mqMessageMapper.selectById(id);
            //添加到历史表
            MqMessageHistory mqMessageHistory = new MqMessageHistory();
            BeanUtils.copyProperties(mqMessage,mqMessageHistory);
            mqMessageHistoryMapper.insert(mqMessageHistory);
            //删除消息表
            mqMessageMapper.deleteById(id);
            return 1;
        }
        return 0;
    }

    /**
     * 完成阶段1任务
     * @param id
     * @return
     */
    @Override
    public int completedStageOne(long id) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setStageState1("1");
        return mqMessageMapper.update(mqMessage,new LambdaQueryWrapper<MqMessage>().eq(MqMessage::getId,id));
    }

    /**
     * 完成阶段任务2
     * @param id
     * @return
     */
    @Override
    public int completedStageTwo(long id) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setStageState2("1");
        return mqMessageMapper.update(mqMessage,new LambdaQueryWrapper<MqMessage>().eq(MqMessage::getId,id));
    }

    @Override
    public int completedStageThree(long id) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setStageState3("1");
        return mqMessageMapper.update(mqMessage,new LambdaQueryWrapper<MqMessage>().eq(MqMessage::getId,id));
    }

    @Override
    public int completedStageFour(long id) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setStageState4("1");
        return mqMessageMapper.update(mqMessage, new LambdaQueryWrapper<MqMessage>().eq(MqMessage::getId, id));
    }

    /**
     * 获取任务状态
     * @param id
     * @return
     */
    @Override
    public int getStageOne(long id) {
        String stageState1 = mqMessageMapper.selectById(id).getStageState1();
        return Integer.parseInt(stageState1);
    }

    @Override
    public int getStageTwo(long id) {
        String stageState1 = mqMessageMapper.selectById(id).getStageState2();
        return Integer.parseInt(stageState1);
    }

    @Override
    public int getStageThree(long id) {
        String stageState1 = mqMessageMapper.selectById(id).getStageState3();
        return Integer.parseInt(stageState1);
    }

    @Override
    public int getStageFour(long id) {
        String stageState1 = mqMessageMapper.selectById(id).getStageState4();
        return Integer.parseInt(stageState1);
    }

    /**
     * 添加消息
     * @param messageType
     * @param businessKey1
     * @param businessKey2
     * @param businessKey3
     * @return
     */
    @Override
    public MqMessage addMessage(String messageType,String businessKey1,String businessKey2,String businessKey3) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setMessageType(messageType);
        mqMessage.setBusinessKey1(businessKey1);
        mqMessage.setBusinessKey2(businessKey2);
        mqMessage.setBusinessKey3(businessKey3);
        int insert = mqMessageMapper.insert(mqMessage);
        if(insert>0){
            return mqMessage;
        }else{
            return null;
        }

    }


}

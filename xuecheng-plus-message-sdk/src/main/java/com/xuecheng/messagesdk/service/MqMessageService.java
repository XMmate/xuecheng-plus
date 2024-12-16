package com.xuecheng.messagesdk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.messagesdk.model.po.MqMessage;

import java.util.List;

public interface MqMessageService extends IService<MqMessage> {
    /**
     * 扫描记录表
     * @param shardIndex
     * @param shardTotal
     * @param messageType
     * @param count
     * @return
     */
    public List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count);

    /**
     * 完成任务
     * @param id
     * @return
     */
    public int completed(long id);

    /**
     * 完成阶段性任务
     * @param id
     * @return
     */

    public int completedStageOne(long id);
    public int completedStageTwo(long id);
    public int completedStageThree(long id);
    public int completedStageFour(long id);

    /**
     * 查询阶段性任务
     * @param id
     * @return
     */
    public int getStageOne(long id);
    public int getStageTwo(long id);
    public int getStageThree(long id);
    public int getStageFour(long id);

    public MqMessage addMessage(String messageType,String businessKey1,String businessKey2,String businessKey3);


}

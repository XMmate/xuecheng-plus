package com.xuecheng.messagesdk.service;

import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 任务的抽象类
 */
@Slf4j
public abstract  class MessageProcessAbstract  {
    @Autowired
    MqMessageService mqMessageService;
    protected MqMessageService getMqMessageService() {
        return mqMessageService;
    }
    /**
     * 执行任务内容
     * @param mqMessage
     * @return
     */
    public abstract boolean execute(MqMessage mqMessage);

    /**
     *  扫描消息表多线程执行任务
     * @param shardIndex
     * @param shardTotal
     * @param messageType
     * @param count
     * @param timeout
     */
    public void process(int shardIndex, int shardTotal,  String messageType,int count,long timeout) {

        try {
            //扫描消息表获取任务清单
            List<MqMessage> messageList = mqMessageService.getMessageList(shardIndex, shardTotal,messageType, count);
            //任务个数
            int size = messageList.size();
            log.debug("取出待处理消息"+size+"条");
            if(size<=0){
                return ;
            }

            //创建线程池
            ExecutorService threadPool = Executors.newFixedThreadPool(size);
            //计数器
            CountDownLatch countDownLatch = new CountDownLatch(size);
            messageList.forEach(message -> {
                threadPool.execute(() -> {
                    log.debug("开始任务:{}",message);
                    //处理任务
                    try {
                        boolean result = execute(message);
                        if(result){
                            log.debug("任务执行成功:{})",message);
                            //更新任务状态,删除消息表记录,添加到历史表
                            int completed = mqMessageService.completed(message.getId());
                            if (completed>0){
                                log.debug("任务执行成功:{}",message);
                            }else{
                                log.debug("任务执行失败:{}",message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.debug("任务出现异常:{},任务:{}",e.getMessage(),message);
                    }
                    //计数
                    countDownLatch.countDown();
                    log.debug("结束任务:{}",message);

                });
            });

            //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
            countDownLatch.await(timeout, TimeUnit.SECONDS);
            System.out.println("结束....");
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }



}

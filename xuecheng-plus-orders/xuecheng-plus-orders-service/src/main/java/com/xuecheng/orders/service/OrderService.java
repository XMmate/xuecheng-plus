package com.xuecheng.orders.service;

import com.alipay.api.AlipayApiException;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.po.XcPayRecord;

public interface OrderService {
    /**
     * 创建商品订单
     * @param userId
     * @param addOrderDto
     * @return
     */
    public PayRecordDto createOrder(String userId,AddOrderDto addOrderDto);


    /**
     * 生成支付二维码
     * @param addOrderDto
     * @return
     * @throws AlipayApiException
     */
    PayRecordDto generatePayCode(AddOrderDto addOrderDto) throws AlipayApiException;


    /**
     * 查询支付记录
     * @param payNo
     * @return
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * 查询支付结果
     * @param payNo
     * @return
     */
    public PayRecordDto queryPayResult(String payNo);

    /**
     * 向Rabbirmq广播发送支付结果
     * @param message
     */
    public void notifyPayResult(MqMessage message);
}

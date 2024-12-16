package com.xuecheng.orders.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.PayNotifyConfig;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.Transient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    XcOrdersMapper ordersMapper;

    @Autowired
    XcOrdersGoodsMapper  ordersGoodsMapper;

    @Autowired
    XcPayRecordMapper  payRecordMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Resource
    private SqlSessionFactory sqlSessionFactory;
    String qrcodeurl="http://100.75.176.55:63030/orders/requestpay?payNo=%s";

    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        XcOrders orders = saveXcOrders(userId, addOrderDto);
        if(orders==null){
            XueChengPlusException.cast("订单创建失败");
        }

        //添加支付记录
        XcPayRecord payRecord = createPayRecord(orders);

        //生成二维码
        String qrCode = null;
        try {
            //url要可以被模拟器访问到，url为下单接口(稍后定义)
            String url = String.format(qrcodeurl, payRecord.getPayNo());
            qrCode = new QRCodeUtil().createQRCode(url, 200, 200);
        } catch (IOException e) {
            XueChengPlusException.cast("生成二维码出错");
        }
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    /**
     * 生成二维码
     * @param addOrderDto
     * @return
     * @throws AlipayApiException
     */
    @Override
    public PayRecordDto generatePayCode(AddOrderDto addOrderDto) throws AlipayApiException {
        return null;
    }
    /**
     * 保存商品订单
     * @param userId
     * @param addOrderDto
     * @return
     */
    public XcOrders saveXcOrders(String userId,AddOrderDto addOrderDto){
        //幂等性处理 确保唯一性
        XcOrders order = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if(order!=null){
            return order;
        }
        order = new XcOrders();
        //生成订单号
        long orderId = IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        //todo 这里有严重的安全漏洞
        order.setTotalPrice(addOrderDto.getTotalPrice());
        order.setCreateDate(LocalDateTime.now());
        order.setStatus("600001");//未支付
        order.setUserId(userId);
        order.setOrderType(addOrderDto.getOrderType());
        order.setOrderName(addOrderDto.getOrderName());
        order.setOrderDetail(addOrderDto.getOrderDetail());
        order.setOrderDescrip(addOrderDto.getOrderDescrip());
        order.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        //插入订单表
        ordersMapper.insert(order);
        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        //批量插入数据优化版本
        // 关闭session的自动提交
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            xcOrdersGoodsList.stream().forEach(goods -> {
                XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
            BeanUtils.copyProperties(goods,xcOrdersGoods);
            xcOrdersGoods.setOrderId(orderId);//订单号
            ordersGoodsMapper.insert(xcOrdersGoods);
            });
            // 提交数据
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        //原始版本
//        xcOrdersGoodsList.forEach(goods->{
//            XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
//            BeanUtils.copyProperties(goods,xcOrdersGoods);
//            xcOrdersGoods.setOrderId(orderId);//订单号
//            ordersGoodsMapper.insert(xcOrdersGoods);
//        });
        return order;
    }



    /**
     * 根据业务id查询订单
     * @param businessId
     * @return
     */
    public XcOrders getOrderByBusinessId(String businessId){
        XcOrders orders = ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
        return orders;
    }

    /**
     * 创建支付记录表
     * @param orders
     * @return
     */
    public XcPayRecord createPayRecord(XcOrders orders){
        if(orders==null){
            XueChengPlusException.cast("订单不存在");
        }
        if(orders.getStatus().equals("600002")){
            XueChengPlusException.cast("订单已支付");
        }
        XcPayRecord payRecord = new XcPayRecord();
        //雪花算法
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        payRecord.setOrderId(orders.getId());//商品订单号
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001");//未支付
        payRecord.setUserId(orders.getUserId());
        payRecordMapper.insert(payRecord);
        return payRecord;
    }

    /**
     * 查询支付记录
     * @param payNo
     * @return
     */
    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        XcPayRecord xcPayRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }

    /**
     * 查询支付结果
     * @param payNo
     * @return
     */
    @Override
    public PayRecordDto queryPayResult(String payNo) {
        XcPayRecord payRecord = getPayRecordByPayno(payNo);
        if (payRecord == null) {
            XueChengPlusException.cast("请重新点击支付获取二维码");
        }
        //支付状态
        String status = payRecord.getStatus();
        //如果支付成功直接返回
        if ("601002".equals(status)) {
            PayRecordDto payRecordDto = new PayRecordDto();
            BeanUtils.copyProperties(payRecord, payRecordDto);
            return payRecordDto;
        }
        //从支付宝查询支付结果
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);
        //保存支付结果
        saveAliPayStatus( payStatusDto);
        //重新查询支付记录
        payRecord = getPayRecordByPayno(payNo);
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        return payRecordDto;
    }



    /**
     * 调用支付宝的接口查询支付结果
     * @param payNo
     * @return
     */
    public PayStatusDto queryPayResultFromAlipay(String payNo){
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient((AlipayConfig) getAlipayConfig());
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        // 设置订单支付时传入的商户订单号
        model.setOutTradeNo(payNo);

        // 设置银行间联模式下有用
        // model.setOrgPid("2088101117952222");

        // 设置查询选项
        List<String> queryOptions = new ArrayList<String>();
        queryOptions.add("trade_settle_info");
        model.setQueryOptions(queryOptions);
        request.setBizModel(model);
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        Map resultMap = JSON.parseObject(response.getBody(), Map.class);
        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        //支付结果
        //成功返回TRADE_SUCCESS
        String trade_status = (String) alipay_trade_query_response.get("trade_status");
        //total_amount 支付金额
        String total_amount = (String) alipay_trade_query_response.get("total_amount");
        //trade_no 支付宝订单号
        String trade_no = (String) alipay_trade_query_response.get("trade_no");

        PayStatusDto payStatusDto = new PayStatusDto();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_status(trade_status);
        payStatusDto.setApp_id("APP_ID");
        payStatusDto.setTrade_no(trade_no);
        payStatusDto.setTotal_amount(total_amount);
        return payStatusDto;
    }

    /**
     * 保存支付结果
     * @param payStatusDto
     */
    @Transactional
    public void saveAliPayStatus(PayStatusDto payStatusDto){
        //支付流水号
        String payNo = payStatusDto.getOut_trade_no();
        XcPayRecord payRecord = getPayRecordByPayno(payNo);
        if (payRecord == null) {
            XueChengPlusException.cast("支付记录找不到");
        }
        //支付结果
        String trade_status = payStatusDto.getTrade_status();
        if (trade_status.equals("TRADE_SUCCESS")) {
            //支付金额变为分
            Float totalPrice = payRecord.getTotalPrice() * 100;
            Float total_amount = Float.parseFloat(payStatusDto.getTotal_amount()) * 100;
            //校验是否一致
            if (totalPrice.intValue() != total_amount.intValue()) {
                //校验失败
                log.info("校验支付结果失败,支付记录:{},APP_ID:{},totalPrice:{}" ,payRecord.toString(),payStatusDto.getApp_id(),total_amount.intValue());
                XueChengPlusException.cast("校验支付结果失败");
            }
            log.debug("更新支付结果,支付交易流水号:{},支付结果:{}", payNo, trade_status);
            XcPayRecord payRecord_u = new XcPayRecord();
            payRecord_u.setStatus("601002");//支付成功
            payRecord_u.setOutPayChannel("Alipay");
            payRecord_u.setOutPayNo(payStatusDto.getTrade_no());//支付宝交易号
            payRecord_u.setPaySuccessTime(LocalDateTime.now());//通知时间
            int update1 = payRecordMapper.update(payRecord_u, new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
            if (update1 > 0) {
                log.info("更新支付记录状态成功:{}", payRecord_u.toString());
            } else {
                log.info("更新支付记录状态失败:{}", payRecord_u.toString());
                XueChengPlusException.cast("更新支付记录状态失败");
            }
            //关联的订单号
            Long orderId = payRecord.getOrderId();
            XcOrders orders = ordersMapper.selectById(orderId);
            if (orders == null) {
                log.info("根据支付记录[{}}]找不到订单", payRecord_u.toString());
                XueChengPlusException.cast("根据支付记录找不到订单");
            }
            XcOrders order_u = new XcOrders();
            order_u.setStatus("600002");//支付成功
            int update = ordersMapper.update(order_u, new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getId, orderId));
            if (update > 0) {
                log.info("更新订单表状态成功,订单号:{}", orderId);
            } else {
                log.info("更新订单表状态失败,订单号:{}", orderId);
                XueChengPlusException.cast("更新订单表状态失败");
            }
           //保存消息记录,参数1：支付结果通知类型，2: 业务id，3:业务类型
            MqMessage mqMessage = mqMessageService.addMessage("payresult_notify", orders.getOutBusinessId(), orders.getOrderType(), null);
            //通知消息
            notifyPayResult(mqMessage);

        }

    }
    @Override
    public void notifyPayResult(MqMessage message) {
        //1、消息体，转json
        String msg = JSON.toJSONString(message);
        //设置消息持久化
        Message msgObj = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        // 2.全局唯一的消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(message.getId().toString());
        // 3.添加callback
        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 3.1.ack，消息成功
                        log.debug("通知支付结果消息发送成功, ID:{}", correlationData.getId());
                        //删除消息表中的记录
                        mqMessageService.completed(message.getId());
                    }else{
                        // 3.2.nack，消息失败
                        log.error("通知支付结果消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage())
        );
        // 发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", msgObj,correlationData);
    }




    private static Object getAlipayConfig() {
        String privateKey  = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCIX6eqN3eXrp/4ZN0dJBmh01e9lfhpsvyxeDG5iNyND7UlT2ovu+5Mcbcc4T54QXTFwgT81f0olRGXleEkTh48USsglyCEsHpCjldwijMVza5vITzhmcZXAHwejgsESmwto/i6wbq494g6CuaMF0n2D9bLmgTJEeQOaOrT7eEJsAlZ3G53iEu/+cY8BNqDhWit+ai3d0a61xh9MrNbcRfjtwMUISSSgXio6B0TMKIzOk+SMTm6z59pdw5P3ZfS6PDvm09hUFV8jmMxIacjNB9ZR2kyuUb7yy+T7sdA2bv7ho2RE89XTiJsMqEjy4qAeq3VX2wOzTNU758+PwTwedxlAgMBAAECggEADpjmkoLDa1oGx4jMXj0p/TdpEeMGpxHAmKJ7+BcdPIAs7KR2gkuEc2xMpGu7yxV+kVh2PjYNlhV2v5oMcl1mQSCTFDb5BRr6yzr/Qw2X8EKG5GBTavaR02g64O5lYX0x3STLD/qCqR/d1hCqot4vTYZegIao8njWx3AVir/Qsb+dC1m5V6mLdj8cBivBtry28ir8WttWAgD2q1ZyXU5+ZJ6SfBCVrNyjRY2YJQU5Hy+6ZFCs/gTauFc+S7/NkukUAiv+qaUgrPxZfJ4fIY5E9TahyYIi8uUztKfXN7PB0CqVdNP6YqN3bOX/SjsUbPZB6c510fSWuUnBainD1Ig4QQKBgQDA5nxYyN3DYFfmM7klj+Q5caKUNecWDwJezLkoh14dEXxYGifDofTrgdZ7oLTMtFB/8z6HyJzHk/B4NoDaxj7+QH72dBbvL2urL5qFGOCjvy8jsV5AXyxZAHnuMB1qkBdCkKJLrp+ALDd21CIspkpcGnKL1+xsFLrxCeBL78TEdQKBgQC0+5zuHApcAWLOEUfzxmYdEP0a70XubHF5x1oPPB8bQnJ2VHmNaJmvHu+OmCGeFYa/To0M3KL/DqOTQLwetz7z6mmABiggaaLK8nhvdEmvE4XS/AMY0h0xOvoW3Xarj4xtp7+/fD0jNCRSoLxtua9R+7lAZTWjBHxBKm/ynV76MQKBgBvVJbvpv5WhIQ2mCODSkit3qe2YmW8lW0IyZ8ThQ0MOctKDc5lWlPAmG7xaw7uFiNftUOhmmbhyMBAoTQ9D63Lj+22z8lswjGh2xeTOYhs+Sp7ryJ2hHdT5rJ4hcx0dDT4dgE1LNNhZiOcsVIH8vF3tsvlaflJZ6187/ZD3V3zBAoGASQPZ5BZb95J3kjtWk/rO55sbAerWg8IsZb0PkGXpBs5f721VPL+egRAyBY1KwsRcgIHoVfANsmjthVLwklPLigxgU84oEiuMCeSYwh5gO8nci6owdasPK/VnQCJI5h4bwYGT0mUFJVa78Ln4SyxGlwS/UEOEyCffGUtNrsverYECgYBLL6QJ5CdV7Ox94LLUZlm39uqe5g2e7lWeQQPsVNktQNDJHXSPVlZsRydX+93hFHiZeTQNkOGZfJo0+PnjaJxaTyW5IirwHq8IGYHGJtyBW4Qf47MZw3MnaYiKeaaFkss96t1hsvNdgSUsOMgpaFsiT4QmZM05zN1ZJ70vGW2oxw==";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAviAHIyVLA2aDq3mL4raV7kIkj5SZ2yv+sjo90bRJDfuHD+uG6/WAmxpbbdqDWm1JPNLTILfrvDLyxn0TFkZGf3I+A/10o7AW4lZbT7dCf6etXI+CdH/vx/PnJsIXypkOEeQsyUb5uUOgN3Aglw3BkBQxQPZSOWOMjD/kYDI3c73NXueD+HO+F5BCEYkgPVgOLpx3wQrFgt4Ot9nGKb+mpoOtJ/JZjRjKKVJJnnwTQW7aX+6Kj1rO6khXP0MkjFGGTjTESSgcgvU6mX9uCikW+fEzB05U7dOVkzR7JHknNvj0Ibi7AJ5dBVx8FuXOGlp59U3zoWmiqme39Uwf83DD/wIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        alipayConfig.setAppId("9021000133652587");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;

    }
}


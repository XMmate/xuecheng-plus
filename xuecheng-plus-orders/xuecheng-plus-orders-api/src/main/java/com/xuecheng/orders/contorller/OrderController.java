package com.xuecheng.orders.contorller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import com.xuecheng.orders.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 生成二维码
     * @param addOrderDto
     * @return
     */
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) throws AlipayApiException {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XueChengPlusException.cast("请登录后继续选课");
        }
        return orderService.createOrder(user.getId(), addOrderDto);
    }


    /**
     * 扫码下单接口
     * @param payNo
     * @param httpResponse
     * @throws IOException
     */
    @GetMapping("/requestpay")
    public void requestpay(String payNo, HttpServletResponse httpResponse) throws IOException, AlipayApiException {
        XcPayRecord payRecord = orderService.getPayRecordByPayno(payNo);
        if(payRecord == null){
            XueChengPlusException.cast("请重新点击支付获取二维码");
        }
        //支付状态
        String status = payRecord.getStatus();
        if("601002".equals(status)){
            XueChengPlusException.cast("订单已支付，请勿重复支付。");
        }
        // 初始化SDK
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient((AlipayConfig) getAlipayConfig());
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        // 构造请求参数以调用接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        // 设置商户门店编号
        model.setStoreId("NJ_001");
        // 设置订单绝对超时时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.setTimeExpire(LocalDateTime.now().plusHours(3).format(formatter));
        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        extendParams.setHbFqSellerPercent("100");
        extendParams.setHbFqNum("3");
        extendParams.setIndustryRefluxInfo("{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}");
        extendParams.setSpecifiedSellerName("学成在线");
        extendParams.setRoyaltyFreeze("true");
        extendParams.setCardType("S0JP0000");
        model.setExtendParams(extendParams);
        // 设置订单标题
        model.setSubject("购买课程");

        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        // 设置PC扫码支付的方式
        model.setQrPayMode("1");

        // 设置商户自定义二维码宽度
        model.setQrcodeWidth(100L);

        // 设置请求后页面的集成方式
        model.setIntegrationType("PCWEB");

        // 设置商户订单号
        model.setOutTradeNo( String.valueOf(payRecord.getPayNo()));
        // 设置订单总金额
        model.setTotalAmount(String.valueOf(payRecord.getTotalPrice()));
        request.setBizModel(model);
        request.setNotifyUrl("https://c89752n820.goho.co/orders/paynotify");
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        String pageRedirectionData = response.getBody();
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");

            String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            System.out.println(diagnosisUrl);
        }
        String form =  response.getBody();
        httpResponse.setContentType("text/html;charset=" + "UTF-8");
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
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

    /**
     * 查寻支付结果
     * @param payNo
     * @return
     */
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDto payresult(String payNo)   {
        PayRecordDto payRecordDto = orderService.queryPayResult(payNo);
        return payRecordDto;
    }

    /**
     * 接收支付宝发送过来的通知
     * @param request
     * @param out
     */
    @PostMapping("/receivenotify")
    public void receivenotify(HttpServletRequest request, HttpServletResponse out){

    }



}

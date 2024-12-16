package com.xuecheng.orders.contorller.v1;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;


@Controller
public class PayTestController {

    @RequestMapping("/alipaytest")
    public static void main(HttpServletRequest httpRequest,
                            HttpServletResponse httpResponse) throws AlipayApiException, IOException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient((AlipayConfig) getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        // 设置商户门店编号
        model.setStoreId("NJ_001");

        // 设置订单绝对超时时间
        model.setTimeExpire("2024-7-18 12:05:01");

        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        extendParams.setHbFqSellerPercent("100");
        extendParams.setHbFqNum("3");
        extendParams.setIndustryRefluxInfo("{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}");
        extendParams.setSpecifiedSellerName("XXX的跨境小铺");
        extendParams.setRoyaltyFreeze("true");
        extendParams.setCardType("S0JP0000");
        model.setExtendParams(extendParams);

        // 设置订单标题
        model.setSubject("Iphone16 16G");

        // 设置请求来源地址
//        model.setRequestFromUrl("https://");

        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        // 设置PC扫码支付的方式
        model.setQrPayMode("2");

        // 设置商户自定义二维码宽度
        model.setQrcodeWidth(100L);

        // 设置请求后页面的集成方式
        model.setIntegrationType("PCWEB");

//         设置订单包含的商品列表信息
        List<GoodsDetail> goodsDetail = new ArrayList<GoodsDetail>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setAlipayGoodsId("20010001");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);

        // 设置商户的原始订单号
//        model.setMerchantOrderNo("20881008001");

        // 设置二级商户信息
//        SubMerchant subMerchant = new SubMerchant();
//        subMerchant.setMerchantId("2088721026495219");
//        subMerchant.setMerchantType("alipay");
//        model.setSubMerchant(subMerchant);

        // 设置开票信息
//        InvoiceInfo invoiceInfo = new InvoiceInfo();
//        InvoiceKeyInfo keyInfo = new InvoiceKeyInfo();
//        keyInfo.setTaxNum("1464888883494");
//        keyInfo.setIsSupportInvoice(true);
//        keyInfo.setInvoiceMerchantName("ABC|003");
//        invoiceInfo.setKeyInfo(keyInfo);
//        invoiceInfo.setDetails("[{\"code\":\"100294400\",\"name\":\"服饰\",\"num\":\"2\",\"sumPrice\":\"200.00\",\"taxRate\":\"6%\"}]");
//        model.setInvoiceInfo(invoiceInfo);

        // 设置商户订单号
        model.setOutTradeNo("2080320010101000109");

//        // 设置外部指定买家
//        ExtUserInfo extUserInfo = new ExtUserInfo();
//        extUserInfo.setCertType("IDENTITY_CARD");
//        extUserInfo.setCertNo("362334768769238881");
//        extUserInfo.setName("qmjibc6486");
//        extUserInfo.setMobile("16587658765");
//        extUserInfo.setMinAge("18");
//        extUserInfo.setNeedCheckInfo("F");
//        extUserInfo.setIdentityHash("27bfcd1dee4f22c8fe8a2374af9b660419d1361b1c207e9b41a754a113f38fcc");
//        model.setExtUserInfo(extUserInfo);

        // 设置订单总金额
        model.setTotalAmount("62.8");

        // 设置商户传入业务信息
//        model.setBusinessParams("{\"mc_create_trade_ip\":\"127.0.0.1\"}");

        // 设置优惠参数
//        model.setPromoParams("{\"storeIdType\":\"1\"}");

        request.setBizModel(model);
        request.setNotifyUrl("https://c89752n820.goho.co/orders/paynotify");
//        request.setNotifyUrl("c89752n820.goho.co:14545/orders/paynotify");
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        // 如果需要返回GET请求，请使用
        // AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        System.out.println(pageRedirectionData);

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
             String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
             System.out.println(diagnosisUrl);
        }
        String form =  response.getBody();//调用SDK生成表单
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


    //接收通知
    @PostMapping("/paynotify")
    public void paynotify(HttpServletRequest request,HttpServletResponse response) throws IOException, AlipayApiException {
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAviAHIyVLA2aDq3mL4raV7kIkj5SZ2yv+sjo90bRJDfuHD+uG6/WAmxpbbdqDWm1JPNLTILfrvDLyxn0TFkZGf3I+A/10o7AW4lZbT7dCf6etXI+CdH/vx/PnJsIXypkOEeQsyUb5uUOgN3Aglw3BkBQxQPZSOWOMjD/kYDI3c73NXueD+HO+F5BCEYkgPVgOLpx3wQrFgt4Ot9nGKb+mpoOtJ/JZjRjKKVJJnnwTQW7aX+6Kj1rO6khXP0MkjFGGTjTESSgcgvU6mX9uCikW+fEzB05U7dOVkzR7JHknNvj0Ibi7AJ5dBVx8FuXOGlp59U3zoWmiqme39Uwf83DD/wIDAQAB";
        Map<String,String> params = new HashMap<String,String>();

        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }


        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, "UTF-8", "RSA2");

        if(verify_result) {//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号

            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");


            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if (trade_status.equals("TRADE_FINISHED")) {//交易结束
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if (trade_status.equals("TRADE_SUCCESS")) {//交易成功
                System.out.println(trade_status);
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            }
            response.getWriter().write("success");
        }else{
            response.getWriter().write("fail");
        }


    }


}

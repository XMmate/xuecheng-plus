
package com.xuecheng.orders.contorller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.domain.AlipayTradeQueryModel;

import com.alipay.api.FileItem;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

public class AlipayTradeQuery {

    public static void main(String[] args) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient((AlipayConfig) getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        
        // 设置订单支付时传入的商户订单号
        model.setOutTradeNo("2080320010101000109");
        
        // 设置银行间联模式下有用
//        model.setOrgPid("2088101117952222");
        
        // 设置查询选项
        List<String> queryOptions = new ArrayList<String>();
        queryOptions.add("trade_settle_info");
        model.setQueryOptions(queryOptions);
        
        // 设置支付宝交易号
//        model.setTradeNo("2014112611001004680 073956707");
        
        request.setBizModel(model);
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");

        }
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
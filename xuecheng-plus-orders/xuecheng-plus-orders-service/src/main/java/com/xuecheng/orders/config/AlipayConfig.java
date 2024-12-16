package com.xuecheng.orders.config;
 /**
 * @description 支付宝配置参数
 * @author Mr.M
 * @date 2022/10/20 22:45
 * @version 1.0
 */
 public class AlipayConfig {
   //商户appid
	public static String APPID = "9021000133652587";
  // 私钥 pkcs8格式的
	public static String RSA_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCIX6eqN3eXrp/4ZN0dJBmh01e9lfhpsvyxeDG5iNyND7UlT2ovu+5Mcbcc4T54QXTFwgT81f0olRGXleEkTh48USsglyCEsHpCjldwijMVza5vITzhmcZXAHwejgsESmwto/i6wbq494g6CuaMF0n2D9bLmgTJEeQOaOrT7eEJsAlZ3G53iEu/+cY8BNqDhWit+ai3d0a61xh9MrNbcRfjtwMUISSSgXio6B0TMKIzOk+SMTm6z59pdw5P3ZfS6PDvm09hUFV8jmMxIacjNB9ZR2kyuUb7yy+T7sdA2bv7ho2RE89XTiJsMqEjy4qAeq3VX2wOzTNU758+PwTwedxlAgMBAAECggEADpjmkoLDa1oGx4jMXj0p/TdpEeMGpxHAmKJ7+BcdPIAs7KR2gkuEc2xMpGu7yxV+kVh2PjYNlhV2v5oMcl1mQSCTFDb5BRr6yzr/Qw2X8EKG5GBTavaR02g64O5lYX0x3STLD/qCqR/d1hCqot4vTYZegIao8njWx3AVir/Qsb+dC1m5V6mLdj8cBivBtry28ir8WttWAgD2q1ZyXU5+ZJ6SfBCVrNyjRY2YJQU5Hy+6ZFCs/gTauFc+S7/NkukUAiv+qaUgrPxZfJ4fIY5E9TahyYIi8uUztKfXN7PB0CqVdNP6YqN3bOX/SjsUbPZB6c510fSWuUnBainD1Ig4QQKBgQDA5nxYyN3DYFfmM7klj+Q5caKUNecWDwJezLkoh14dEXxYGifDofTrgdZ7oLTMtFB/8z6HyJzHk/B4NoDaxj7+QH72dBbvL2urL5qFGOCjvy8jsV5AXyxZAHnuMB1qkBdCkKJLrp+ALDd21CIspkpcGnKL1+xsFLrxCeBL78TEdQKBgQC0+5zuHApcAWLOEUfzxmYdEP0a70XubHF5x1oPPB8bQnJ2VHmNaJmvHu+OmCGeFYa/To0M3KL/DqOTQLwetz7z6mmABiggaaLK8nhvdEmvE4XS/AMY0h0xOvoW3Xarj4xtp7+/fD0jNCRSoLxtua9R+7lAZTWjBHxBKm/ynV76MQKBgBvVJbvpv5WhIQ2mCODSkit3qe2YmW8lW0IyZ8ThQ0MOctKDc5lWlPAmG7xaw7uFiNftUOhmmbhyMBAoTQ9D63Lj+22z8lswjGh2xeTOYhs+Sp7ryJ2hHdT5rJ4hcx0dDT4dgE1LNNhZiOcsVIH8vF3tsvlaflJZ6187/ZD3V3zBAoGASQPZ5BZb95J3kjtWk/rO55sbAerWg8IsZb0PkGXpBs5f721VPL+egRAyBY1KwsRcgIHoVfANsmjthVLwklPLigxgU84oEiuMCeSYwh5gO8nci6owdasPK/VnQCJI5h4bwYGT0mUFJVa78Ln4SyxGlwS/UEOEyCffGUtNrsverYECgYBLL6QJ5CdV7Ox94LLUZlm39uqe5g2e7lWeQQPsVNktQNDJHXSPVlZsRydX+93hFHiZeTQNkOGZfJo0+PnjaJxaTyW5IirwHq8IGYHGJtyBW4Qf47MZw3MnaYiKeaaFkss96t1hsvNdgSUsOMgpaFsiT4QmZM05zN1ZJ70vGW2oxw==";
  // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
  public static String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
  // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
  public static String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
  // 请求网关地址
  public static String URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
  // 编码
  public static String CHARSET = "UTF-8";
  // 返回格式
  public static String FORMAT = "json";
  // 支付宝公钥
	public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAviAHIyVLA2aDq3mL4raV7kIkj5SZ2yv+sjo90bRJDfuHD+uG6/WAmxpbbdqDWm1JPNLTILfrvDLyxn0TFkZGf3I+A/10o7AW4lZbT7dCf6etXI+CdH/vx/PnJsIXypkOEeQsyUb5uUOgN3Aglw3BkBQxQPZSOWOMjD/kYDI3c73NXueD+HO+F5BCEYkgPVgOLpx3wQrFgt4Ot9nGKb+mpoOtJ/JZjRjKKVJJnnwTQW7aX+6Kj1rO6khXP0MkjFGGTjTESSgcgvU6mX9uCikW+fEzB05U7dOVkzR7JHknNvj0Ibi7AJ5dBVx8FuXOGlp59U3zoWmiqme39Uwf83DD/wIDAQAB";
  // 日志记录目录
  public static String log_path = "/log";
  // RSA2
  public static String SIGNTYPE = "RSA2";
 }

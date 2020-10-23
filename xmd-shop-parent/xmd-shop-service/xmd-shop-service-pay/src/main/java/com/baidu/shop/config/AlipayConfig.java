package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/22
 * @Version V1.0
 **/

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
public class AlipayConfig {

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766851";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDcx4UyVJIglTi+tjkxcYDfCTxNZPtDJqNLoX9WcOowClG0KbehiohKYf+Krqg4NZokl8wlXQ7iW6r3McfbmToLQShPqYL5wYrclfZtJKjzC+cmVGN0pH7i1Lbk1Tq6d5wkmaPPLYbXJtn2vBUqsxDCT5UAaWfmd3SBUTWNqeAZF/9qOcLDMC6mEhlxc0hk1a8//TmnKZfQNr3ufr23TaFRiiy9ICBa89rO3Fm2SAECvFfSCoKRrHo2wD5Ux2WAT7rXe3QDzAYD//ak9IHZIW2fE4TOOntPMS/pkxu/7y7YPr0I9lUvwtrIFxOS/49FSOOpVgkbYx3rs5TpFgj1rxlbAgMBAAECggEAatLnMckVw0owYKaO2cuPjVtMoC4CHSWu0UNc95g+1+vGSKP+w9zhwQvVt2Z24mudH6OLFXEHaKLuABlIGIr1OMjYFmNwiiQJal3tFDYuwRq7RyKB1Mha1KoTrHk3p02EcXkdd3sRyTx1LhsnKrflnf8fm0llCnA2IPQIM3lEgx7hUT7zy7PsfRfzwdiG3fjm4ydAnXrg16fhgTkm24HWmfAP+1LRkeORRUAvO7PrQyyGqfKtG6I+03538GM9MeHRNFbQdD3wung93w9eMwsZ5cTXGm0dz6s+JStYuTyoL5l0BDlSvx4P1ggqtzKHej5sz4NI4pzOZsnNzN5wVAbRMQKBgQDxjGs805sejnyyMi+V/j0hwX4OFFAn6ZAXE61/QqkRtLsbbzPWKUI2IynXg2srSSBMtvphJmtgE3iUpBX8+U8TI3AFC2KHe3Pbci5FtBFjIrVqulbpwc7ju0vsp6vWh7ph//5+LmSXFeDHeQhKCQlEtLrlWRsXK7L3unLqeGaNlwKBgQDp/P/PPCJfu9Ys7aBWt8gQfSOrvLytixhxFB2I4odywseXrdeEuAWs7lZsFEGMQBwytXWiS+MgdABI1m8AeKwvVMf73u4Uf0V+4BAYHnbmz8HlMDERqsMD/Lqlr1Eje07E4HZaycD1kDU5v4lTHW0MVMn1+ACvB8FIiabvpSfS3QKBgCq7VrVaEhUjy/eSXUzBv3rbSjufaFbHQRB0ELt0xYg2WuuNjrQSdH4zVlGkFC+CLk6kCENrko9aonhZVvJ8AHs4R7b8vp+NPX/TAVCffHv3RfTtpjaQbNNo7ROezg2T0WtI6HLWLkF0Q2HGjarbUmJvPqkYTGya9HxS5cWJK0tdAoGBAJ0BRYkBubLxMCJPhIILqXPe9Dt9VkFAjzL5I2RBVE+eAR126aFFdZF3D4hQAzefoQwplHhFYW7aR4KYWD1Bu4+63MHV8XzRdYlPwZzdfD0d9OXbc2F+aoLXZ0pbnB8p003pq6D5tb9Qd1Z7fiLGIPDgR/AC7DqGApywuNIeyN0FAoGAJSrTO1OhrbQYzbLqS9ibjW0ZfChHnScVpsafFx2N3B738OhPmlHChQSjgX1qgh+e2rgP96OVUlbEhBUSvlyizzfzPJ/17zkFWdqk4aVQCv+eFTPhUzD1jcClVHO7PjCFaZyhODDaPIWxJAUM98EE6eDJKe1w/WiZtqELPWYUTQw=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhgRdNMoINoGsureAD09HgrhgSt/zVoOUI913Wy0RfvlhKDfvVbcqARR6HXgARM5DWKuvrUO2s50w9tAQ7eZ/InPOyI9boN6q6DVVYov04Ky6qTjR5YYB/SSboO0yxzw47vnfdEv1cvheZJFZEkFhVa1+bUFsX5gx+B24fRPhLQlYuqv1Gwph1IaUsbnHGee8qwI1nXGCRAczrqkr6PkSDQFyhh/evZ9VnNJWvyKx4KLbEqeMPoiZgqF6tsymDijvjEVy2Y/OHVEypbSCWxbfx4NXteaQYRU5Z8C8RKwT79JrECS4r1NER9V9I5hO/Z5RhSN7O+/tZ7l6gM7kEcK8UQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/returnNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须内网也可以正常访问
    public static String return_url = "http://localhost:8900/pay/returnURL";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

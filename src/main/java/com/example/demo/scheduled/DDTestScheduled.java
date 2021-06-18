package com.example.demo.scheduled;

import com.example.demo.util.HttpUtil;
import com.example.demo.util.PropertiesUtil;
import com.google.gson.Gson;
import org.elasticsearch.common.Strings;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TestScheduled
 * @Author MoHa
 * @Description TODO
 * @Date 2019-10-25 09:56
 **/
@Component
@EnableScheduling
public class DDTestScheduled {
    public static String WEBHOOK_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=bada4714-d432-49ac-86b3-090d6e016e43";

    public static String SJZ_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=6258d09a-6244-4858-95e0-54d738b2b2d7";

    private static String soupUrl = "https://api.qinor.cn/soup/";

    private static String soupUrl1 = "http://api.tianapi.com/txapi/dujitang/index?key=aad834783cbc454c77b995a38476d428";

    public static String[] NAMES = {"沈健力","毛泽威","黄秀娟","程月娇","何胜东","赵万梓","刘婷","潘维健"};
    private static String[] MOBILES = {"15757116539","15728007839","18858090722","15067480436","18815287551","15258316988","17636218668","15867461151"};
//    private int dayIndex = 3;
    private int weekIndex = 0;



    //周一至周五上班时间
    @Scheduled(cron = "00 15 9 ? * MON-FRI")
    public void everyDay() throws Exception {
        String content = ack1();
        System.out.println(HttpUtil.doPost(SJZ_TOKEN,content));

        //钉钉的消息提醒
        String jt = getJtStr();
        String text = String.format("早上好！鸡汤时间：%s",jt);
        String jsondd = "{\n" +
                "    \"at\": {\n" +
                "        \"isAtAll\": true\n" +
                "    },\n" +
                "    \"text\": {\n" +
                "        \"content\":\"" + text + "\"\n" +
                "    },\n" +
                "    \"msgtype\":\"text\"\n" +
                "}";
        HttpUtil.doPost(getUrl(), jsondd);
    }

    public String getUrl() throws Exception {
        Long timestamp = System.currentTimeMillis();
        String secret = "SEC326cbb24dc59443bb8252fbfccd659d83658e90dc5a2aa82fbdbeb11185bfe1d";

        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(org.apache.commons.codec.binary.Base64.encodeBase64(signData)), "UTF-8");
        System.out.println(sign);

        String url = "https://oapi.dingtalk.com/robot/send?access_token=200f178f3e2cb43f6fb36eb47637e7f9e601588881c72c55abe0c62494cbbf6e&timestamp=%s&sign=%s";
        url = String.format(url, timestamp, sign);
        return url;
    }


    public static String ack1(){
        String jt = getJtStr();
        String text = String.format("早上好！鸡汤时间：%s",jt);
        String content = " {\n" +
                "    \"at\": {\n" +
                "        \"isAtAll\": true\n" +
                "    },\n" +
                "    \"text\": {\n" +
                "        \"content\":\""+text+"\"\n" +
                "    },\n" +
                "    \"msgtype\":\"text\"\n" +
                "}";

        System.out.println(content);
        return content;
    }


    public static String ack2(){
        String content = ack1();
        HttpUtil.doPost("https://oapi.dingtalk.com/robot/send?access_token=d3319cd2208f6aa0e144e33c09abf272c8c0d811bebfa19d7fe88e68fb38b418",content);
        System.out.println(content);
        return content;
    }

    public static void main(String[] args) {
        ack2();
    }

    private static String getJtStr(){
        String soup =  HttpUtil.doGet(soupUrl1,null,null,null,false);
        if(Strings.isNullOrEmpty(soup)){
            soup = HttpUtil.doGet(soupUrl,null,null,null,false);
            return soup;
        }
        Map map =  new Gson().fromJson(soup,Map.class);
        List<Map> mapList = (List<Map>) map.get("newslist");
        Map content = mapList.get(mapList.size()-1);
        String jt = (String) content.get("content");
        return jt;
    }

}

package com.example.demo.scheduled;

import com.example.demo.util.HttpUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: moha
 * @create: 2020-02-11 15:14
 */
//@Component
//@EnableScheduling
public class DailyNotify {
    public static String WEBHOOK_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=89a51df2-07eb-4a81-91da-2d6a1912bed2";

    @Scheduled(cron = "0 0 9,18 ? * MON-FRI")
    public void task(){
        String text = "记得打卡!";
        String content = "{\n" +
                "    \"msgtype\": \"text\",\n" +
                "    \"text\": {\n" +
                "        \"content\": \"" + text + "\",\n" +
                "        \"mentioned_mobile_list\":[\"@all\"]\n" +
                "    }\n" +
                "}";
        HttpUtil.doPost(WEBHOOK_TOKEN,content);

    }
}

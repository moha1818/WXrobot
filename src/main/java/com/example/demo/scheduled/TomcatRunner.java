package com.example.demo.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName TomcatRunner
 * @Author MoHa
 * @Description TODO
 * @Date 2019-10-26 15:40
 **/
@Component
public class TomcatRunner implements ApplicationRunner {
    public static String WEBHOOK_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=89a51df2-07eb-4a81-91da-2d6a1912bed2";

    @Autowired
    private TestScheduled testScheduled;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        testScheduled.ack();
        //beginRobot();
    }
    public static void beginRobot(){
        String text = "机器人已上线";
        String content = "{\n" +
                "    \"msgtype\": \"text\",\n" +
                "    \"text\": {\n" +
                "        \"content\": \"" + text + "\",\n" +
                "    }\n" +
                "}";
        System.out.println(content);
        //HttpUtil.doPost(WEBHOOK_TOKEN,content);
    }
}

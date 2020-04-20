package com.example.demo.scheduled;

import com.example.demo.util.HttpUtil;
import com.example.demo.util.PropertiesUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName TestScheduled
 * @Author MoHa
 * @Description TODO
 * @Date 2019-10-25 09:56
 **/
@Component
@EnableScheduling
public class TestScheduled {
    public static String WEBHOOK_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=89a51df2-07eb-4a81-91da-2d6a1912bed2";

    public static String[] NAMES = {"沈健力","毛泽威","黄秀娟","程月娇","金陈天","竺祯炜","赵万梓"};
    private static String[] MOBILES = {"15757116539","15728007839","18858090722","15067480436","13575546447","18868945424","15258316988"};
//    private int dayIndex = 3;
    private int weekIndex = 0;
    //每周一
    @Scheduled(cron = "0 00 9 ? * MON")
    //@Scheduled(cron = "10 * * * * ?")
    public void testTasks() {
        System.out.println("******定时器启动*****");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
//        //工作日
//        if(dayWeek>1 && dayWeek<7){
//            String content = ack();
//            System.out.println(HttpUtil.doPost(WEBHOOK_TOKEN,content));
//            if(dayWeek == 6){
//                weekIndex = weekIndex == NAMES.length-1?0:weekIndex+1;
//            }
//            dayIndex = dayIndex == NAMES.length-1?0:dayIndex+1;
//        }else {
//            System.out.println("今天周末！！！");
//        }

        PropertiesUtil util = setweekIndex();
        System.out.println(weekIndex);
        String content = ack();
        System.out.println(HttpUtil.doPost(WEBHOOK_TOKEN,content));
        weekIndex = weekIndex == NAMES.length-1?0:weekIndex+1;
        util.update(String.valueOf(weekIndex));
    }

    public PropertiesUtil setweekIndex(){
        PropertiesUtil util = new PropertiesUtil();
        util.read();
        Map m = util.read();
        weekIndex = Integer.parseInt(String.valueOf(m.get("dayIndex")));
        return util;
    }

    public String ack(){
        String text = String.format("这周的值日组长:%s",NAMES[weekIndex]);
        String list = String.format("\"%s\"",MOBILES[weekIndex]);
        String content = "{\n" +
                "    \"msgtype\": \"text\",\n" +
                "    \"text\": {\n" +
                "        \"content\": \"" + text + "\",\n" +
                "        \"mentioned_mobile_list\":[" + list + "]\n" +
                "    }\n" +
                "}";

        System.out.println(content);
        return content;
    }
}

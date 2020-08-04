package com.example.demo.scheduled;

import com.example.demo.util.HttpUtil;
import com.example.demo.util.PropertiesUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.elasticsearch.common.Strings;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
public class TestScheduled {
    public static String WEBHOOK_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=bada4714-d432-49ac-86b3-090d6e016e43";

    public static String SJZ_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=6258d09a-6244-4858-95e0-54d738b2b2d7";

    private static String soupUrl = "https://api.qinor.cn/soup/";

    private static String soupUrl1 = "http://api.tianapi.com/txapi/dujitang/index?key=aad834783cbc454c77b995a38476d428";

    public static String[] NAMES = {"沈健力","毛泽威","黄秀娟","程月娇","何胜东","赵万梓","刘婷","潘维健"};
    private static String[] MOBILES = {"15757116539","15728007839","18858090722","15067480436","18815287551","15258316988","17636218668","15867461151"};
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


    //周一至周五上班时间
    @Scheduled(cron = "0 15 9 ? * MON-FRI")
    public void everyDay() {
        String content = ack1();
        System.out.println(HttpUtil.doPost(SJZ_TOKEN,content));
    }

    public String ack1(){
        String jt = getJtStr();
        String text = String.format("早上好！鸡汤时间：%s",jt);
        String content = "{\n" +
                "    \"msgtype\": \"text\",\n" +
                "    \"text\": {\n" +
                "        \"content\": \"" + text +"\""+
                "    }\n" +
                "}";

        System.out.println(content);
        return content;
    }

    private String getJtStr(){
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

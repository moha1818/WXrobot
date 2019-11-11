package com.example.demo.util;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    public Map read(){
        String fileName = this.getClass().getClassLoader().getResource("application.properties").getPath();//获取文件路径
        File file = new File(fileName);
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(file);
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<?> enumeration = prop.propertyNames();
        Map<String,String> param = new HashMap<>();
        while(enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            param.put(key,prop.getProperty(key.trim()));
        }
        return param;
    }

    public void update(String value){
        String fileName = this.getClass().getClassLoader().getResource("application.properties").getPath();//获取文件路径

        try {
            Properties props=new Properties();
            props.load(new FileInputStream(fileName));
            OutputStream fos = new FileOutputStream(fileName);
            props.setProperty("dayIndex", value);
            props.store(fos, "Update value");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("属性文件更新错误");
        }
    }

}

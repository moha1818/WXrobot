package com.example.demo.rest;

import com.example.demo.scheduled.TestScheduled;
import com.example.demo.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName RestController
 * @Author MoHa
 * @Description TODO
 * @Date 2019-10-29 15:11
 **/
@RestController
public class RobotController {
    @Autowired
    private TestScheduled testScheduled;

    @RequestMapping("/next")
    public String next(){
        //testScheduled.next();
        return "1";
    }

    @RequestMapping("/test")
    public String test(){
        return String.valueOf(testScheduled.NAMES);
    }

    @RequestMapping("/modify")
    public String modifyFile(String weekInt) throws IOException {
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        propertiesUtil.update(weekInt);
        testScheduled.setweekIndex();
        return testScheduled.ack();
    }
}

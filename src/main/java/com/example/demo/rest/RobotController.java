package com.example.demo.rest;

import com.example.demo.config.DrillColumn;
import com.example.demo.scheduled.TestScheduled;
import com.example.demo.service.ElasticSearchService;
import com.example.demo.service.impl.ElasticSearchServiceImpl;
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

//    @Autowired
//    private ElasticSearchService elasticSearchService;

    @RequestMapping("/next")
    public void next(){
        //testScheduled.next();
//        DrillColumn drill = new DrillColumn();
//        drill.setElementName("a");
//        drill.setElementQueryName("b");
//        drill.setElementValue("c");
        //return elasticSearchService.insert("dataextra",drill);
    }

//    @RequestMapping("/get")
//    public Object get(){
//        //testScheduled.next();
//        DrillColumn drill = elasticSearchService.findById("dataextra","FTme_m4BV8izRHdWxmRy",DrillColumn.class);
//
//        return drill;
//    }

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

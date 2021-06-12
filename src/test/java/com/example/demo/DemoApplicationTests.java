package com.example.demo;

import com.example.demo.service.ESClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    ESClientService esClientService;
    @Test
    void contextLoads() {
//        Map<String,Object> map = esClientService.searchAggregationDocument(null,"userparam","queryField",1);
//        System.out.println(map);

    }

}

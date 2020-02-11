package com.example.demo.util;

public enum ElasticsearchIndex {
    // 测试数据
    SUPPLIER("supplier"),
    WEBSITE("website")
    ;

    ElasticsearchIndex(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private String value;
}
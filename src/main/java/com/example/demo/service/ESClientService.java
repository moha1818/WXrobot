package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface ESClientService {
    List<Map<String, Object>> searchDocument(String indexName, Map<String, Object> mpParams,
                                             int from, int size, String[] fieldArray, Integer preciseQuery);

    Map<String, Object> searchAggregationDocument(Map<String, Object> mpParams, String indexName,
                                                  String fieldName, Integer preciseQuery);

    void createDocument(String indexName, String id) throws Exception;
}

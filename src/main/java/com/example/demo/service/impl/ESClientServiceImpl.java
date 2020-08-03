package com.example.demo.service.impl;

import com.example.demo.service.ESClientService;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: moha
 * @create: 2020-06-03 13:54
 */
//@Service
public class ESClientServiceImpl implements ESClientService {

    @Resource(name = "rhlClient")
    private RestHighLevelClient rhlClient;

    /**
     * 新增单条文档数据
     *
     * @param indexName 索引名称
     * @param id
     * @throws Exception
     */
    @Override
    public void createDocument(String indexName, String id) throws Exception {

        // 指定单条文档数据，最终会转化成Json格式
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("name", "张三");
            builder.field("age", 12);
            builder.field("email", "email123");
        }
        builder.endObject();

        // 创建新增文档数据的请求
        IndexRequest indexRequest = new IndexRequest(indexName).id(id).source(builder);
        // 手动指定路由的key，文档查询时可提高性能
        indexRequest.routing("userInfo");
        // 等待主分片保存的超时时长
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        // 刷新策略，WAIT_UNTIL设置则表示刷新使此请求的内容对搜索可见为止
        indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        // 操作类型为新增
        indexRequest.opType(DocWriteRequest.OpType.CREATE);

        // 异步执行新增文档数据请求
        rhlClient.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println(indexResponse.toString());
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }



    /**
     * 检索、分页
     *
     * @param indexName    索引名称
     * @param mpParams     查询参数
     * @param from         起始页
     * @param size         每页数量
     * @param fieldArray   返回列数组
     * @param preciseQuery 1:精确查询 2:模糊查询
     * @return
     */
    @Override
    public List<Map<String, Object>> searchDocument(String indexName, Map<String, Object> mpParams,
                                                    int from, int size, String[] fieldArray, Integer preciseQuery) {
        RestHighLevelClient restHighLevelClient = this.rhlClient;
        SearchRequest searchRequest = new SearchRequest(indexName);
        // 大多数搜索参数添加到searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            // 组合字段查询
            BoolQueryBuilder boolQueryBuilder = this.getBoolQueryBuilder(mpParams, preciseQuery);
            searchSourceBuilder.query(boolQueryBuilder);
            // 自定义返回列
            if (fieldArray != null && fieldArray.length > 0) {
                searchSourceBuilder.fetchSource(fieldArray, null);
            }
            // 按照Id倒序
            searchSourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.DESC));
            // 分页
            searchSourceBuilder.from(from);
            searchSourceBuilder.size(size);
            // 允许搜索的超时时长
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 返回结果
            SearchHits searchHitArray = searchResponse.getHits();
            for (SearchHit searchHit : searchHitArray.getHits()) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                mapList.add(sourceAsMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mapList;
    }

    /**
     * 组合字段查询条件
     *
     * @param mpParams
     * @param preciseQuery 1:精确查询 2:模糊查询
     * @return
     */
    public BoolQueryBuilder getBoolQueryBuilder(Map<String, Object> mpParams, Integer preciseQuery) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        try {
            if (mpParams != null) {
                for (Map.Entry<String, Object> entry : mpParams.entrySet()) {
                    if (preciseQuery != null && preciseQuery == 1) {
                        // 精确匹配
                        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(entry.getKey() + ".keyword", entry.getValue());
                        boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder);
                    } else {
                        // 模糊匹配
                        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                        boolQueryBuilder = boolQueryBuilder.must(matchQueryBuilder);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boolQueryBuilder;
    }


    /**
     * 查询结果聚合
     *
     * @param mpParams     查询参数
     * @param indexName    索引名称
     * @param fieldName    字段名称
     * @param preciseQuery 1:精确查询 2:模糊查询
     * @return
     */
    @Override
    public Map<String, Object> searchAggregationDocument(Map<String, Object> mpParams, String indexName,
                                                         String fieldName, Integer preciseQuery) {
        Map<String, Object> mpResult = new HashMap<>();
        RestHighLevelClient restHighLevelClient = null;
        SearchResponse searchResponse = null;
        try {
            restHighLevelClient = this.rhlClient;
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // 指定要聚合的字段，类似数据库的group by
            TermsAggregationBuilder aggregation = AggregationBuilders.terms(fieldName).field(fieldName);
            searchSourceBuilder.aggregation(aggregation);
            // 组合字段查询
            BoolQueryBuilder boolQueryBuilder = this.getBoolQueryBuilder(mpParams, preciseQuery);
            searchSourceBuilder.query(boolQueryBuilder);
            // 执行查询请求
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(searchSourceBuilder);
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 汇总聚合结果
            Aggregations aggregations = searchResponse.getAggregations();
            Terms byCompanyAggregation = aggregations.get(fieldName);
            if (byCompanyAggregation != null) {
                List<Terms.Bucket> bucketList = (List<Terms.Bucket>) byCompanyAggregation.getBuckets();
                for (Terms.Bucket bucket : bucketList) {
                    String key = bucket.getKeyAsString();
                    Long value = bucket.getDocCount();
                    mpResult.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mpResult;
    }
}

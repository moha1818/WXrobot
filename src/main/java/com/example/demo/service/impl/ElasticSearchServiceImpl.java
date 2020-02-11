package com.example.demo.service.impl;

import com.example.demo.service.ElasticSearchService;
import com.example.demo.util.ElasticsearchIndex;
import com.google.gson.*;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
public class ElasticSearchServiceImpl implements ElasticSearchService, InitializingBean {
    /**
     * The constant gson.
     */
    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    private String host;
    private int port;
    private String schema;
    private String type = "data";

    private String queryConfigFileName = "elasticsearch.xml";
    private Map<String, String> queryConfig;
    //private VelocityEngine velocityEngine;
    private static final String PAGE_FROM_VAR = "_page_from";
    private static final String PAGE_SIZE_VAR = "_page_size";
    private static final int DEFAULT_PAGE_SIZE = 10;

//    private boolean idCacheEnable = false;
//    private IdCacheService idCacheService;
//    private String redisHost;
//    private Integer redisPort;
    /**
     * The Client.
     */
    private RestHighLevelClient client;

    private String preTag = "<span>";
    private String postTag = "</span>";

    public ElasticSearchServiceImpl() {
    }

    public ElasticSearchServiceImpl(String host, int port, String schema) throws Exception {
        this.host = host;
        this.port = port;
        this.schema = schema;
        this.afterPropertiesSet();
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    @Override
    public void setTags(String preTag, String postTag) {
        this.preTag = preTag;
        this.postTag = postTag;
    }

    @Override
    public <T> String insert(String index, T entity) {
        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(gson.toJson(entity), XContentType.JSON);
        IndexResponse response = null;
        try {
            response = client.index(indexRequest,RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK && response.status() != RestStatus.CREATED) {
                //throw new BizException("Invalid response " + response.status());
            }
        } catch (IOException e) {
            logger.error("Failed to insert data", e);
            //throw new BizException(e);
        }
        return response.getId();
    }

    @Override
    public <T> T findById(String index, String id, Class<T> entityClass) {
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse response = null;
        try {
            response = client.get(getRequest);
        } catch (IOException e) {
            logger.error("Failed to get doc by id {}", id);
            //throw new BizException(e);
        }
        if (!response.isExists()) {
            return null;
        }
        return gson.fromJson(response.getSourceAsString(), entityClass);
    }

//    @Override
//    public <T> List<String> insert(ElasticsearchIndex index, List<T> entities) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (Object entity : entities) {
//            IndexRequest indexRequest = new IndexRequest(index.getValue(), type);
//            indexRequest.source(gson.toJson(entity), XContentType.JSON);
//            bulkRequest.add(indexRequest);
//        }
//
//        BulkResponse bulkResponse = fetchBulkResponse(bulkRequest);
//        return Arrays.stream(bulkResponse.getItems())
//                .map(q -> q.isFailed() ? null : q.getId())
//                .collect(Collectors.toList());
//    }
//
//    private BulkResponse fetchBulkResponse(BulkRequest bulkRequest) {
//        BulkResponse bulkResponse;
//        try {
//            bulkResponse = client.bulk(bulkRequest);
//        } catch (IOException e) {
//            logger.error("Failed to execute bulk", e);
//           // throw new BizException(e);
//        }
//        return bulkResponse;
//    }
//
//    private Field getBizIdField(Object entity) {
//        Field businessIdField;
//        List<Field> fields = ReflectionUtil.getAllFieldsList(
//                entity.getClass(),
//                false,
//                q -> q.isAnnotationPresent(Id.class) || q.isAnnotationPresent(org.springframework.data.annotation.Id.class),
//                true);
//        if (fields.size() > 0) {
//            businessIdField = fields.get(0);
//        } else {
//            businessIdField = ReflectionUtil.getFieldByName(entity.getClass(), "id");
//        }
//        return businessIdField;
//    }
//
//    private String getDocId(ElasticsearchIndex index, Object object) {
//        Field bizIdField = getBizIdField(object);
//        if (bizIdField == null) {
//           // throw new BizException("Id field not found");
//        }
//        Object bizId = ReflectionUtil.getValueFromField(bizIdField, object);
//        if (bizId == null) {
//           // throw new BizException("Id cannot be null");
//        }
//
//        return getDocIdByBizId(index, bizIdField, bizId.toString());
//    }
//
//    private String getDocIdByBizId(ElasticsearchIndex index, Field bizIdField, String bizId) {
//        String docId;
//        if (idCacheEnable) {
//            docId = idCacheService.fetchDocId(index, bizId);
//            if (!Strings.isNullOrEmpty(docId)) {
//                return docId;
//            }
//        }
//
//        String query = String.format("{\"query\":{\"term\":{\"%s\":\"%s\"}},\"_source\":false}", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, bizIdField.getName()), bizId);
//        ElasticResponse elasticResponse = rawQuery(index, query);
//        List<ElasticResponse.HitsBeanX.HitsBean> hits = elasticResponse.getHits().getHits();
//        if (hits.size() == 0) {
//            docId = null;
//        } else {
//            docId = hits.get(0).getId();
//        }
//
//        if (idCacheEnable) {
//            idCacheService.cache(index, bizId, docId);
//        }
//        return docId;
//    }
//
//    @Override
//    public <T> Boolean update(ElasticsearchIndex index, T entity) {
//        String id = getDocId(index, entity);
//        if (Strings.isNullOrEmpty(id)) {
//            logger.warn("Doc id of entity {} not found, skip update", id);
//            return false;
//        }
//        return updateByDocId(index, id, entity);
//    }
//
//    @Override
//    public <T> void upInsert(ElasticsearchIndex index, T entity) {
//        Boolean result = update(index,entity);
//        if(!result){
//            insert(index,entity);
//        }
//    }
//
//    @Override
//    public <T> Boolean updateByDocId(ElasticsearchIndex index, String docId, T entity) {
//        UpdateRequest updateRequest = new UpdateRequest(index.getValue(), type, docId);
//        updateRequest.doc(gson.toJson(entity), XContentType.JSON);
//        try {
//            UpdateResponse response = client.update(updateRequest);
//            if (response.status() != RestStatus.OK) {
//               // throw new BizException("Invalid response " + response.status());
//            }else{
//                return true;
//            }
//        } catch (IOException e) {
//            logger.error("Failed to update doc by id {}", docId);
//           // throw new BizException(e);
//        }
//    }
//
//    @Override
//    public <T> List<Boolean> update(ElasticsearchIndex index, List<T> entities) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (T entity : entities) {
//            String id = getDocId(index, entity);
//            if (Strings.isNullOrEmpty(id)) {
//                logger.warn("Doc id of entity {} not found, skip update", id);
//                continue;
//            }
//            UpdateRequest updateRequest = new UpdateRequest(index.getValue(), type, id);
//            updateRequest.doc(gson.toJson(entity), XContentType.JSON);
//            bulkRequest.add(updateRequest);
//        }
//
//        if (bulkRequest.numberOfActions() == 0) {
//            return ImmutableList.of();
//        }
//        BulkResponse bulkResponse = fetchBulkResponse(bulkRequest);
//        return Arrays.stream(bulkResponse.getItems()).map(q -> !q.isFailed()).collect(Collectors.toList());
//    }
//
//    @Override
//    public <T> List<Boolean> updateByDocIds(ElasticsearchIndex index, List<UpdateRequestEntity<T>> updateRequestEntities) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (UpdateRequestEntity entity : updateRequestEntities) {
//            String id = entity.getId();
//            UpdateRequest updateRequest = new UpdateRequest(index.getValue(), type, id);
//            updateRequest.doc(gson.toJson(entity.getEntity()), XContentType.JSON);
//            bulkRequest.add(updateRequest);
//        }
//
//        if (bulkRequest.numberOfActions() == 0) {
//            return ImmutableList.of();
//        }
//        BulkResponse bulkResponse = fetchBulkResponse(bulkRequest);
//        return Arrays.stream(bulkResponse.getItems()).map(q -> !q.isFailed()).collect(Collectors.toList());
//
//    }
//
//    @Override
//    public <T> void delete(ElasticsearchIndex index, T entity) {
//        String id = getDocId(index, entity);
//        if (Strings.isNullOrEmpty(id)) {
//            logger.warn("Doc id of entity {} not found, skip delete", id);
//            return;
//        }
//        deleteByDocId(index, id);
//    }
//
//    @Override
//    public void deleteByDocId(ElasticsearchIndex index, String id) {
//        DeleteRequest deleteRequest = new DeleteRequest(index.getValue(), type, id);
//        try {
//            DeleteResponse response = client.delete(deleteRequest);
//            if (response.status() != RestStatus.OK) {
//               // throw new BizException("Invalid response " + response.status());
//            }
//        } catch (IOException e) {
//            logger.error("Failed to delete doc by id {}", id);
//           // throw new BizException(e);
//        }
//        if (idCacheEnable) {
//            idCacheService.deleteByDocId(index, id);
//        }
//    }
//
//    @Override
//    public <T> List<Boolean> delete(ElasticsearchIndex index, List<T> entities) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (T entity : entities) {
//            String id = getDocId(index, entity);
//            if (Strings.isNullOrEmpty(id)) {
//                logger.warn("Doc id of entity {} not found, skip delete", id);
//                continue;
//            }
//            DeleteRequest deleteRequest = new DeleteRequest(index.getValue(), type, id);
//            bulkRequest.add(deleteRequest);
//            if (idCacheEnable) {
//                idCacheService.deleteByDocId(index, id);
//            }
//        }
//
//        if (bulkRequest.numberOfActions() == 0) {
//            return ImmutableList.of();
//        }
//        BulkResponse bulkResponse = fetchBulkResponse(bulkRequest);
//        return Arrays.stream(bulkResponse.getItems()).map(q -> !q.isFailed()).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Boolean> deleteByDocIds(ElasticsearchIndex index, List<String> ids) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (String id : ids) {
//            DeleteRequest deleteRequest = new DeleteRequest(index.getValue(), type, id);
//            bulkRequest.add(deleteRequest);
//        }
//        if (bulkRequest.numberOfActions() == 0) {
//            return ImmutableList.of();
//        }
//        BulkResponse bulkResponse = fetchBulkResponse(bulkRequest);
//        return Arrays.stream(bulkResponse.getItems()).map(q -> !q.isFailed()).collect(Collectors.toList());
//    }
//
//    @Override
//    public <T> T findById(ElasticsearchIndex index, String id, Class<T> entityClass) {
//        GetRequest getRequest = new GetRequest(index.getValue(), type, id);
//        GetResponse response;
//        try {
//            response = client.get(getRequest);
//        } catch (IOException e) {
//            logger.error("Failed to get doc by id {}", id);
//           // throw new BizException(e);
//        }
//        if (!response.isExists()) {
//            return null;
//        }
//        return gson.fromJson(response.getSourceAsString(), entityClass);
//    }
//
//    @Override
//    public <T> List<String> findIdByEntity(ElasticsearchIndex index, T entity) {
//        SearchRequest searchRequest = new SearchRequest(index.getValue());
//        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//        for (Field field : ReflectionUtil.getFieldsList(entity.getClass())) {
//            field.setAccessible(true);
//            Object value = ReflectionUtil.getValueFromField(field, entity);
//            if (value == null) {
//                continue;
//            }
//            String searchFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
//            boolQueryBuilder.must(new TermQueryBuilder(searchFieldName, value));
//        }
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQueryBuilder);
//        searchSourceBuilder.fetchSource(false);
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse;
//        try {
//            searchResponse = client.search(searchRequest);
//        } catch (IOException e) {
//            logger.error("Failed to search {}", searchSourceBuilder.toString(), e);
//           // throw new BizException(e);
//        }
//        return Arrays.stream(searchResponse.getHits().getHits()).map(SearchHit::getId).collect(Collectors.toList());
//    }
//
//    @Override
//    public <T> List<HighlightedResult<T>> searchHighlight(ElasticsearchIndex index, Class<T> entityClass, String keyword, String... fields) {
//        if (fields == null || fields.length == 0) {
//           // throw new BizException("fields not exists");
//        }
//
//        return searchHighlight(index, entityClass, keyword, toSearchFields(fields));
//    }
//
//    @Override
//    public <T> List<HighlightedResult<T>> searchHighlight(ElasticsearchIndex index, Class<T> entityClass, String keyword, SearchField... searchFields) {
//        if (searchFields == null || searchFields.length == 0) {
//           // throw new BizException("searchFields not exists");
//        }
//
//        SearchResponse response = doSearch(index.getValue(), true, keyword, searchFields);
//        if (response.getHits() == null || response.getHits().getTotalHits() == 0) {
//            return new ArrayList<>();
//        }
//
//        List<HighlightedResult<T>> list = new ArrayList<>();
//        List<T> parsedResults = parseSearchResponse(response, entityClass);
//        int i = 0;
//        for (SearchHit hit : response.getHits()) {
//            HighlightedResult<T> highlightedResult = new HighlightedResult<>();
//            highlightedResult.setData(parsedResults.get(i));
//            i++;
//
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//            Map<String, List<String>> highlight = new HashMap<>();
//            highlightFields.forEach((fieldName, field) -> highlight.put(
//                    fieldName,
//                    Arrays.stream(field.getFragments()).map(Text::string).collect(Collectors.toList())
//            ));
//            highlightedResult.setHighlight(highlight);
//            list.add(highlightedResult);
//        }
//        return list;
//    }
//
//    @Override
//    public <T> List<T> search(ElasticsearchIndex index, Class<T> entityClass, String keyword, String... fields) {
//        if (fields == null || fields.length == 0) {
//           // throw new BizException("fields not exists");
//        }
//
//        return search(index, entityClass, keyword, toSearchFields(fields));
//    }
//
//    @Override
//    public <T> List<T> search(ElasticsearchIndex index, Class<T> entityClass, String keyword, SearchField... searchFields) {
//        if (searchFields == null || searchFields.length == 0) {
//           // throw new BizException("searchFields not exists");
//        }
//
//        SearchResponse response = doSearch(index.getValue(), false, keyword, searchFields);
//        return parseSearchResponse(response, entityClass);
//    }
//
//
//    private <T> PageInfo<T> search(ElasticsearchIndex index, String templateId, Map<String, Object> params, boolean isEmptySearch, PageInfo<T> pageInfo, Class<T> entityClass) {
//        if (params == null) {
//            params = new HashMap<>();
//        }
//
//        String queryTemplate = queryConfig.get(templateId);
//        if (Strings.isNullOrEmpty(queryTemplate)) {
//           // throw new BizException("elasticsearch query template with name " + templateId + " not found");
//        }
//        if (pageInfo == null) {
//            pageInfo = new PageInfo<>();
//        }
//        if (pageInfo.getPageSize() == 0) {
//            pageInfo.setPageSize(DEFAULT_PAGE_SIZE);
//        }
//        if (pageInfo.getPageNum() == 0) {
//            pageInfo.setPageNum(1);
//        }
//        params.put(PAGE_FROM_VAR, (pageInfo.getPageNum() - 1) * pageInfo.getPageSize());
//        params.put(PAGE_SIZE_VAR, pageInfo.getPageSize());
//
//        String query = VelocityUtil.getContentByVelocity(velocityEngine, queryTemplate, params);
//
//        if (isEmptySearch) {
//            JsonObject jsonObject = (JsonObject) new JsonParser().parse(query);
//            jsonObject.remove("query");
//            query = jsonObject.toString();
//        }
//
//
//        ElasticResponse elasticResponse = rawQuery(index, query);
//        List<T> list = elasticResponse.getHits().getHits().stream().map(q -> {
//            q.getSource().addProperty("es_id",q.getId());
//            return
//            gson.fromJson(q.getSource().toString(), entityClass);
//        }).collect(Collectors.toList());
//
//        int total = elasticResponse.getHits().getTotal();
//        int pageSize = pageInfo.getPageSize();
//        pageInfo.setTotal(total);
//        pageInfo.setList(list);
//        //贼个还是要得
//        pageInfo.setPages(total%pageSize == 0 ? total/pageSize : total/pageSize + 1);
//        return pageInfo;
//    }
//
//    private String fillPageInfo(String query) {
//        if (!query.contains("\"from\"")) {
//            query = query.replaceFirst("\\{", "{\"from\": \\$" + PAGE_FROM_VAR + ",");
//        }
//        if (!query.contains("\"size\"")) {
//            query = query.replaceFirst("\\{", "{\"size\": \\$" + PAGE_SIZE_VAR + ",");
//        }
//        return query;
//    }
//
//    @Override
//    public <T> PageInfo<T> search(ElasticsearchIndex index, String templateId, Map<String, Object> params, PageInfo<T> pageInfo, Class<T> entityClass) {
//        return search(index, templateId, params, false, pageInfo, entityClass);
//    }
//
//
//    @Override
//    public <T> PageInfo<T> searchEmpty(ElasticsearchIndex index, String templateId, PageInfo<T> pageInfo, Class<T> entityClass) {
//        return search(index, templateId, new HashMap<>(), true, pageInfo, entityClass);
//    }
//
//    private ElasticResponse rawQuery(ElasticsearchIndex index, String query) {
//        RestClient lowLevelClient = client.getLowLevelClient();
//        Response response;
//        String responseBody;
//        try {
//            logger.debug("Searching elasticsearch with query: {}", query);
//            response = lowLevelClient.performRequest("POST",
//                    String.format("/%s/data/_search", index.getValue()),
//                    ImmutableMap.of(),
//                    new NStringEntity(query, ContentType.APPLICATION_JSON)
//            );
//            responseBody = EntityUtils.toString(response.getEntity());
//            logger.debug("Elasticsearch responses with body: {}", responseBody);
//        } catch (IOException e) {
//            logger.error("Failed to get response", e);
//           // throw new BizException(e);
//        }
//
//        int code = response.getStatusLine().getStatusCode();
//        if (code != HttpStatus.SC_OK) {
//            logger.error("Status code {}, body {}", code, responseBody);
//           // throw new BizException("Error status code " + code);
//        }
//        return gson.fromJson(responseBody, ElasticResponse.class);
//    }
//
//    /**
//     * <p class="detail">
//     * 功能:
//     * </p>
//     *
//     * @param index        :
//     * @param highlight    :
//     * @param keyword      :
//     * @param searchFields :
//     * @return search response
//     * @author hbprotoss
//     * @date 2018.01.29 17:08:28
//     */
//    private SearchResponse doSearch(String index, boolean highlight, String keyword, SearchField... searchFields) {
//        if (searchFields == null || searchFields.length == 0) {
//           // throw new BizException("searchFields not exists");
//        }
//
//        SearchResponse response;
//        if (searchFields.length == 1) {
//            response = match(index, highlight, keyword, searchFields[0]);
//        } else {
//            response = multiMatch(index, highlight, keyword, searchFields);
//        }
//        return response;
//    }
//
//    /**
//     * <p class="detail">
//     * 功能:
//     * </p>
//     *
//     * @param searchFields :
//     * @return search field [ ]
//     * @author hbprotoss
//     * @date 2018.01.29 17:08:28
//     */
//    private SearchField[] toSearchFields(String... searchFields) {
//        return Arrays.stream(searchFields).map(q -> {
//            SearchField field = new SearchField();
//            field.setField(q);
//            field.setWeight(1);
//            return field;
//        }).collect(Collectors.toList()).toArray(new SearchField[]{});
//    }
//
//    /**
//     * <p class="detail">
//     * 功能: match查询
//     * </p>
//     *
//     * @param index       :
//     * @param keyword     :
//     * @param searchField :
//     * @return list response
//     * @author hbprotoss
//     * @date 2018.01.29 15:33:08
//     */
//    private SearchResponse match(String index, boolean highlight, String keyword, SearchField searchField) {
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(searchField.getField(), keyword);
//        if (highlight) {
//            highlight(searchSourceBuilder, searchField);
//        }
//        searchSourceBuilder.query(matchQueryBuilder);
//        searchRequest.source(searchSourceBuilder);
//        return fetchSearchResponse(searchRequest);
//    }
//
//    /**
//     * <p class="detail">
//     * 功能: multi_match查询
//     * </p>
//     *
//     * @param index        :
//     * @param highlight    :
//     * @param keyword      :
//     * @param searchFields :
//     * @return list response
//     * @author hbprotoss
//     * @date 2018.01.29 15:33:08
//     */
//    private SearchResponse multiMatch(String index, boolean highlight, String keyword, SearchField... searchFields) {
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        String[] fields = Arrays.stream(searchFields).map(SearchField::getField).collect(Collectors.toList()).toArray(new String[]{});
//        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(keyword, fields);
//        for (SearchField searchField : searchFields) {
//            multiMatchQueryBuilder.field(searchField.getField(), searchField.getWeight());
//        }
//        if (highlight) {
//            highlight(searchSourceBuilder, searchFields);
//        }
//        searchSourceBuilder.query(multiMatchQueryBuilder);
//        searchRequest.source(searchSourceBuilder);
//        return fetchSearchResponse(searchRequest);
//    }
//
//    private void highlight(SearchSourceBuilder searchSourceBuilder, SearchField... searchFields) {
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.preTags(preTag);
//        highlightBuilder.postTags(postTag);
//        for (SearchField searchField : searchFields) {
//            highlightBuilder.field(searchField.getField());
//        }
//        searchSourceBuilder.highlighter(highlightBuilder);
//    }
//
//    /**
//     * <p class="detail">
//     * 功能: 查询实现
//     * </p>
//     *
//     * @param searchRequest :
//     * @return list response
//     * @author hbprotoss
//     * @date 2018.01.29 15:33:08
//     */
//    private SearchResponse fetchSearchResponse(SearchRequest searchRequest) {
//        SearchResponse response;
//        try {
//            response = client.search(searchRequest);
//        } catch (IOException e) {
//            logger.error("Failed to search", e);
//           // throw new BizException(e);
//        }
//        if (response.status() != RestStatus.OK) {
//           // throw new BizException("Error response code " + response.status());
//        }
//        return response;
//    }
//
//    /**
//     * <p class="detail">
//     * 功能:
//     * </p>
//     *
//     * @param <T>         the type parameter
//     * @param response    :
//     * @param entityClass :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.01.29 17:08:28
//     */
//    private <T> List<T> parseSearchResponse(SearchResponse response, Class<T> entityClass) {
//        List<T> list = new ArrayList<>();
//        Method idSetter = null;
//        try {
//            idSetter = entityClass.getMethod("setEsId", String.class);
//        } catch (NoSuchMethodException e) {
//            logger.debug("setEsId(String) not found", e);
//        }
//
//        for (SearchHit hit : response.getHits()) {
//
//            String source = hit.getSourceAsString();
//            T t = gson.fromJson(source, entityClass);
//            if (idSetter != null) {
//                try {
//                    idSetter.invoke(t, hit.getId());
//                } catch (IllegalAccessException | InvocationTargetException e) {
//                    logger.error("invoke setEsId(String) failed", e);
//                   // throw new BizException(e);
//                }
//            }
//            list.add(t);
//        }
//        return list;
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, schema)));
//
//
//        InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(queryConfigFileName));
//        SAXReader reader = new SAXReader();
//        Document document = reader.read(inputStreamReader);
//        Element root = document.getRootElement();
//        Iterator iterator = root.elementIterator("query");
//        queryConfig = new HashMap<>();
//        while (iterator.hasNext()) {
//            Element element = (Element) iterator.next();
//            String query = element.getText().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
//            queryConfig.put(element.attributeValue("id"), fillPageInfo(query));
//        }
//
//
//        velocityEngine = new VelocityEngine();
//        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
//        velocityEngine.init();
//
//        if (idCacheEnable) {
//            if (redisHost == null || redisPort == null) {
//                throw new IllegalArgumentException("redisHost or redisPort cannot be null");
//            }
//            idCacheService = new RedisIdCacheServiceImpl(redisHost, redisPort);
//        }
//    }

    public void setPreTag(String preTag) {
        this.preTag = preTag;
    }

    public void setPostTag(String postTag) {
        this.postTag = postTag;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

//    public void setSchema(String schema) {
//        this.schema = schema;
//    }
//
//    public void setIdCacheEnable(boolean idCacheEnable) {
//        this.idCacheEnable = idCacheEnable;
//    }
//
//    public void setRedisHost(String redisHost) {
//        this.redisHost = redisHost;
//    }
//
//    public void setRedisPort(Integer redisPort) {
//        this.redisPort = redisPort;
//    }
@Override
public void afterPropertiesSet(){
    client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, schema)));
//    client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, schema)));
//
//
//    InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(queryConfigFileName));
//    SAXReader reader = new SAXReader();
//    Document document = reader.read(inputStreamReader);
//    Element root = document.getRootElement();
//    Iterator iterator = root.elementIterator("query");
//    queryConfig = new HashMap<>();
//    while (iterator.hasNext()) {
//        Element element = (Element) iterator.next();
//        String query = element.getText().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
//        queryConfig.put(element.attributeValue("id"), fillPageInfo(query));
//    }
//
//
//    velocityEngine = new VelocityEngine();
//    velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
//    velocityEngine.init();
//
//    if (idCacheEnable) {
//        if (redisHost == null || redisPort == null) {
//            throw new IllegalArgumentException("redisHost or redisPort cannot be null");
//        }
//        idCacheService = new RedisIdCacheServiceImpl(redisHost, redisPort);
//    }
}

}

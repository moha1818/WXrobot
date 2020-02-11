package com.example.demo.service;

import com.example.demo.util.ElasticsearchIndex;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p class="detail">
 * 功能: Elasticsearch方法封装
 * http://wiki.great-tao.com/xwiki/bin/view/Main/JAVA开发/公共资源说明/Elasticsearch/简介与集成/
 * </p>
 *
 * @author hbprotoss
 * @ClassName Elastic search client.
 * @Version V1.0.
 * @date 2018.01.29 15:33:07
 */
public interface ElasticSearchService {

    /**
     * <p class="detail">
     * 功能: 插入数据
     * </p>
     *
     * @param <T>    the type parameter
     * @param index  :
     * @param entity :
     * @return string 文档ID
     * @author hbprotoss
     * @date 2018.01.29 15:33:08
     */
    <T> String insert(String index, T entity);

    <T> T findById(String index, String id, Class<T> entityClass);

//    /**
//     * <p class="detail">
//     * 功能: 批量插入
//     * </p>
//     *
//     * @param <T>      the type parameter
//     * @param index    :
//     * @param entities :
//     * @return string 所有项的文档ID, 中途有一个或多个entity插入失败，则list中对应位置的返回值为null
//     * @author hbprotoss
//     * @date 2018.02.07 10:31:45
//     */
//    <T> List<String> insert(ElasticsearchIndex index, List<T> entities);
//
//    /**
//     * <p class="detail">
//     * 功能: 更新数据
//     * </p>
//     *
//     * @param <T>    the type parameter
//     * @param index  :
//     * @param entity :
//     * @author hbprotoss
//     * @date 2018.01.30 14:13:47
//     */
//    <T> Boolean update(ElasticsearchIndex index, T entity);
//
//    /**
//     * 要是有就更新，要是没有就插入
//     * @param index
//     * @param entity
//     * @param <T>
//     */
//    <T> void upInsert(ElasticsearchIndex index, T entity);
//
//    /**
//     * <p class="detail">
//     * 功能: 根据文档ID更新数据
//     * </p>
//     *
//     * @param <T>    the type parameter
//     * @param index  :
//     * @param docId  :
//     * @param entity :
//     * @author hbprotoss
//     * @date 2018.02.11 14:38:46
//     */
//    <T> Boolean updateByDocId(ElasticsearchIndex index, String docId, T entity);
//
//    /**
//     * <p class="detail">
//     * 功能: 批量更新
//     * </p>
//     *
//     * @param <T>      the type parameter
//     * @param index    :
//     * @param entities :
//     * @return list 对应位置的entity是否更新成功
//     * @author hbprotoss
//     * @date 2018.02.07 11:22:37
//     */
//    <T> List<Boolean> update(ElasticsearchIndex index, List<T> entities);
//
//    /**
//     * <p class="detail">
//     * 功能: 批量根据文档ID更新数据
//     * </p>
//     *
//     * @param <T>      the type parameter
//     * @param index    :
//     * @param entities :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.02.11 14:38:47
//     */
//    <T> List<Boolean> updateByDocIds(ElasticsearchIndex index, List<UpdateRequestEntity<T>> entities);
//
//    /**
//     * <p class="detail">
//     * 功能: 删除数据
//     * </p>
//     *
//     * @param <T>    the type parameter
//     * @param index  :
//     * @param entity :
//     * @author hbprotoss
//     * @date 2018.01.30 14:13:47
//     */
//    <T> void delete(ElasticsearchIndex index, T entity);
//
//    /**
//     * <p class="detail">
//     * 功能: 根据文档ID删除数据
//     * </p>
//     *
//     * @param index :
//     * @param id    :
//     * @author hbprotoss
//     * @date 2018.02.11 14:38:47
//     */
//    void deleteByDocId(ElasticsearchIndex index, String id);
//
//    /**
//     * <p class="detail">
//     * 功能: 批量删除
//     * </p>
//     *
//     * @param <T>      the type parameter
//     * @param index    :
//     * @param entities :
//     * @return list 对应位置是否删除成功
//     * @author hbprotoss
//     * @date 2018.02.07 11:30:07
//     */
//    <T> List<Boolean> delete(ElasticsearchIndex index, List<T> entities);
//
//    /**
//     * <p class="detail">
//     * 功能: 批量根据文档ID删除数据
//     * </p>
//     *
//     * @param index :
//     * @param ids   :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.02.11 14:38:47
//     */
//    List<Boolean> deleteByDocIds(ElasticsearchIndex index, List<String> ids);
//
//    /**
//     * <p class="detail">
//     * 功能: 查询数据
//     * </p>
//     *
//     * @param <T>         the type parameter
//     * @param index       :
//     * @param id          :
//     * @param entityClass :
//     * @return t
//     * @author hbprotoss
//     * @date 2018.01.30 14:13:47
//     */
//    <T> T findById(ElasticsearchIndex index, String id, Class<T> entityClass);
//
//    /**
//     * <p class="detail">
//     * 功能: 根据实体类做查询（类比mybatis）
//     * </p>
//     *
//     * @param <T>    the type parameter
//     * @param index  :
//     * @param entity :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.02.07 10:29:59
//     */
//    <T> List<String> findIdByEntity(ElasticsearchIndex index, T entity);
//
//    /**
//     * <p class="detail">
//     * 功能: 返回带高亮的结果
//     * </p>
//     *
//     * @param <T>         the type parameter
//     * @param index       :
//     * @param entityClass :
//     * @param keyword     :
//     * @param fields      :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.01.29 17:08:27
//     */
//    <T> List<HighlightedResult<T>> searchHighlight(ElasticsearchIndex index, Class<T> entityClass, String keyword, String... fields);
//
//    /**
//     * <p class="detail">
//     * 功能: 返回带高亮的结果
//     * </p>
//     *
//     * @param <T>          the type parameter
//     * @param index        :
//     * @param entityClass  :
//     * @param keyword      :
//     * @param searchFields :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.01.29 17:14:28
//     */
//    <T> List<HighlightedResult<T>> searchHighlight(ElasticsearchIndex index, Class<T> entityClass, String keyword, SearchField... searchFields);
//
//    /**
//     * <p class="detail">
//     * 功能: 在指定index中搜索keyword，所有字段权重均为1
//     * </p>
//     *
//     * @param <T>         the type parameter
//     * @param index       :
//     * @param entityClass :
//     * @param keyword     :
//     * @param fields      :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.01.29 15:33:08
//     */
//    <T> List<T> search(ElasticsearchIndex index, Class<T> entityClass, String keyword, String... fields);
//
//    /**
//     * <p class="detail">
//     * 功能: 在指定index中搜索keyword，可指定权重
//     * </p>
//     *
//     * @param <T>          the type parameter
//     * @param index        :
//     * @param entityClass  :
//     * @param keyword      :
//     * @param searchFields :
//     * @return list
//     * @author hbprotoss
//     * @date 2018.01.29 15:33:08
//     */
//    <T> List<T> search(ElasticsearchIndex index, Class<T> entityClass, String keyword, SearchField... searchFields);
//
//
//
//    /**
//     * <p class="detail">
//     * 功能: 使用模版中指定查询语句的search方法
//     * </p>
//     *
//     * @param <T>         the type parameter
//     * @param index       :
//     * @param templateId  : elasticsearch.xml中的查询语句模版名
//     * @param params      : 填充的变量
//     * @param pageInfo    : pageNum从1开始
//     * @param entityClass :
//     * @return list info
//     * @author hbprotoss
//     * @date 2018.02.08 14:37:38
//     */
//    <T> PageInfo<T> search(ElasticsearchIndex index, String templateId, Map<String, Object> params, PageInfo<T> pageInfo, Class<T> entityClass);
//
//    /**
//     * <p class="detail">
//     * 功能: 空关键字查询
//     * </p>
//     *
//     * @param <T>         the type parameter
//     * @param index       :
//     * @param templateId  :
//     * @param pageInfo    :
//     * @param entityClass :
//     * @return page info
//     * @author hbprotoss
//     * @date 2018.02.26 19:37:48
//     */
//    <T> PageInfo<T> searchEmpty(ElasticsearchIndex index, String templateId, PageInfo<T> pageInfo, Class<T> entityClass);
//
    /**
     * <p class="detail">
     * 功能:
     * </p>
     *
     * @throws IOException the io exception
     * @author hbprotoss
     * @date 2018.01.30 14:13:48
     */
    void close() throws IOException;

    /**
     * Sets tags.
     *
     * @param preTag  the pre tag
     * @param postTag the post tag
     */
    void setTags(String preTag, String postTag);
}

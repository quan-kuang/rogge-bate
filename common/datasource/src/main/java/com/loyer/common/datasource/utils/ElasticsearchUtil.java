package com.loyer.common.datasource.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.common.OracleUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.core.utils.request.HttpUtil;
import com.loyer.common.datasource.entity.SearchSourceQuery;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.Base64Util;
import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * Elasticsearch工具类
 *
 * @author kuangq
 * @date 2020-12-04 16:56
 */
public class ElasticsearchUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchUtil.class);

    private static final RestHighLevelClient REST_HIGH_LEVEL_CLIENT = ContextUtil.getBean(RestHighLevelClient.class);

    private static final Environment ENVIRONMENT = ContextUtil.getBean(Environment.class);

    /**
     * 创建索引
     *
     * @author kuangq
     * @date 2020-12-07 16:02
     */
    @SneakyThrows
    public static boolean createIndex(String index) {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder()
                //设置分片数
                .put("index.number_of_shards", 5)
                //设置副本数
                .put("index.number_of_replicas", 0)
        );
        CreateIndexResponse response = REST_HIGH_LEVEL_CLIENT.indices().create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 删除索引
     *
     * @author kuangq
     * @date 2020-12-07 16:02
     */
    @SneakyThrows
    public static boolean deleteIndex(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse response = REST_HIGH_LEVEL_CLIENT.indices().delete(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     *
     * @author kuangq
     * @date 2020-12-07 16:02
     */
    @SneakyThrows
    public static boolean existsIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        return REST_HIGH_LEVEL_CLIENT.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据索引和ID插入数据
     *
     * @author kuangq
     * @date 2020-12-07 16:03
     */
    @SneakyThrows
    public static void insertData(String index, String id, Object object) {
        checkIndexAndId(index, id);
        //判断索引是否存在
        if (!existsIndex(index)) {
            createIndex(index);
        }
        //创建请求
        IndexRequest request = new IndexRequest(index);
        //设置ID
        request.id(id);
        //设置超时时间
        request.timeout(TimeValue.timeValueSeconds(1));
        //将数据转成jsonStr保存
        request.source(JSON.toJSONString(object), XContentType.JSON);
        //客户端发送请求
        IndexResponse response = REST_HIGH_LEVEL_CLIENT.index(request, RequestOptions.DEFAULT);
        logger.info("【ES新增数据】{}：{}", index, response.status());
    }

    /**
     * 根据索引和ID删除数据
     *
     * @author kuangq
     * @date 2020-12-07 16:03
     */
    @SneakyThrows
    public static void deleteData(String index, String id) {
        checkIndexAndId(index, id);
        DeleteRequest request = new DeleteRequest(index, id);
        DeleteResponse response = REST_HIGH_LEVEL_CLIENT.delete(request, RequestOptions.DEFAULT);
        logger.info("【ES删除数据】{}：{}", index, response.status());
    }

    /**
     * 根据主键批量删除数据
     *
     * @author kuangq
     * @date 2020-12-10 15:03
     */
    @SneakyThrows
    public static void deleteData(String index, String... uuids) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        BoolQueryBuilder boolQueryBuilder = boolQuery();
        boolQueryBuilder.must(termsQuery("uuid", uuids));
        request.setQuery(boolQueryBuilder);
        //实时刷新
        request.setRefresh(true);
        BulkByScrollResponse response = REST_HIGH_LEVEL_CLIENT.deleteByQuery(request, RequestOptions.DEFAULT);
        logger.info("【ES批量删除数据】{}：{}", index, response.getStatus().getDeleted());
    }

    /**
     * 根据索引和ID修改数据
     *
     * @author kuangq
     * @date 2020-12-07 16:04
     */
    @SneakyThrows
    public static void updateData(String index, String id, Object object) {
        checkIndexAndId(index, id);
        UpdateRequest request = new UpdateRequest(index, id);
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        request.doc(JSON.toJSONString(object), XContentType.JSON);
        UpdateResponse response = REST_HIGH_LEVEL_CLIENT.update(request, RequestOptions.DEFAULT);
        logger.info("【ES修改数据】{}：{}", index, response.status());
    }

    /**
     * 根据索引和ID判断数据是否存在
     *
     * @author kuangq
     * @date 2020-12-07 16:13
     */
    @SneakyThrows
    public static boolean existsData(String index, String id) {
        checkIndexAndId(index, id);
        GetRequest request = new GetRequest(index, id);
        //不获取返回的_source的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        return REST_HIGH_LEVEL_CLIENT.exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据索引和ID查询数据
     *
     * @author kuangq
     * @date 2020-12-07 16:14
     */
    @SneakyThrows
    public static Map<String, Object> selectData(String index, String id, String fields) {
        checkIndexAndId(index, id);
        GetRequest request = new GetRequest(index, id);
        //根据fields判断是否只查询特定字段，查询所有则不设置该属性
        if (StringUtils.isNotBlank(fields)) {
            request.fetchSourceContext(new FetchSourceContext(true, fields.split(","), Strings.EMPTY_ARRAY));
        }
        GetResponse response = REST_HIGH_LEVEL_CLIENT.get(request, RequestOptions.DEFAULT);
        return response.getSource();
    }

    /**
     * ES条件分页查询
     *
     * @author kuangq
     * @date 2020-12-07 18:08
     */
    public static Map<String, Object> selectData(SearchSourceQuery searchSourceQuery) {
        //校验索引是否存在
        if (!existsIndex(searchSourceQuery.getIndex())) {
            throw new BusinessException(HintEnum.HINT_1090, searchSourceQuery.getIndex());
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询条件
        if (searchSourceQuery.getQueryBuilder() != null) {
            searchSourceBuilder.query(searchSourceQuery.getQueryBuilder());
        }
        //根据fields判断是否只查询特定字段，查询所有则不设置该属性
        if (StringUtils.isNotBlank(searchSourceQuery.getFields())) {
            searchSourceBuilder.fetchSource(new FetchSourceContext(true, searchSourceQuery.getFields().split(","), Strings.EMPTY_ARRAY));
        }
        //设置是否查询全部，默认查询前1W条数据，FALSE不会返回总条数
        if (searchSourceQuery.getHitAll() != null && searchSourceQuery.getHitAll()) {
            searchSourceBuilder.trackTotalHits(searchSourceQuery.getHitAll());
        }
        //设置是否返回数据源，设置FALSE，则不返回sourceAsMap、sourceAsString字段，默认true
        searchSourceBuilder.fetchSource((Boolean) OracleUtil.nvl(searchSourceQuery.getFetchSource(), true));
        //设置分页查询起始页，默认为0
        searchSourceBuilder.from((Integer) OracleUtil.nvl2(searchSourceQuery.getPageNum(), (searchSourceQuery.getPageNum() - 1) * searchSourceQuery.getPageSize(), 0));
        //设置分页查询大小，默认10
        searchSourceBuilder.size((Integer) OracleUtil.nvl(searchSourceQuery.getPageSize(), 10));
        //设置排序字段，默认根据createTime倒序排序
        searchSourceBuilder.sort(OracleUtil.nvl(searchSourceQuery.getSortField(), "createTime").toString(), (SortOrder) OracleUtil.nvl(searchSourceQuery.getSortOrder(), SortOrder.DESC));
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest(searchSourceQuery.getIndex());
        searchRequest.source(searchSourceBuilder);
        //分页查询
        if ("DSL".equalsIgnoreCase(searchSourceQuery.getMode())) {
            return pagingQuery(searchRequest);
        }
        return pagingSearch(searchRequest);
    }

    /**
     * 使用ES提供的API进行分页查询
     *
     * @author kuangq
     * @date 2020-12-07 23:17
     */
    @SneakyThrows
    public static Map<String, Object> pagingSearch(SearchRequest searchRequest) {
        //发起查询请求
        SearchResponse searchResponse = REST_HIGH_LEVEL_CLIENT.search(searchRequest, RequestOptions.DEFAULT);
        //判断响应状态==200
        if (searchResponse != null && searchResponse.status().getStatus() == HttpServletResponse.SC_OK) {
            //解析结果集
            List<Map<String, Object>> list = new ArrayList<>();
            SearchHits searchHits = searchResponse.getHits();
            long total = searchHits.getTotalHits().value;
            //遍历source
            for (SearchHit searchHit : searchHits.getHits()) {
                Map<String, Object> map = searchHit.getSourceAsMap();
                if (map == null) {
                    map = new HashMap(1);
                }
                map.put("uuid", searchHit.getId());
                list.add(map);
            }
            //返回结果集
            logger.info("【ES分页查询总记录】{}、【分页条数】{}", total, searchHits.getHits().length);
            return new HashMap<String, Object>(2) {{
                put("total", total);
                put("list", list);
            }};
        }
        throw new BusinessException(HintEnum.HINT_1095, searchRequest);
    }

    /**
     * 使用RestTemplate直接发起es分页查询请求
     *
     * @author kuangq
     * @date 2021-03-15 10:16
     */
    public static Map<String, Object> pagingQuery(SearchRequest searchRequest) {
        //获取es配置
        String servers = Objects.requireNonNull(ENVIRONMENT.getProperty("elasticsearch.servers")).split(SpecialCharsConst.COMMA)[0];
        String scheme = ENVIRONMENT.getProperty("elasticsearch.scheme");
        String username = ENVIRONMENT.getProperty("elasticsearch.username");
        String password = ENVIRONMENT.getProperty("elasticsearch.password");
        //设置header，进行basic auth
        Map<String, String> headers = new HashMap<>(1);
        String auth = Base64Util.encode(StringUtil.decode(username + ":" + password));
        headers.put("Authorization", "Basic " + auth);
        //设置入参
        Object uriVariables = JSONObject.parse(searchRequest.source().toString());
        //设置请求url
        String index = searchRequest.indices()[0];
        String url = String.format("%s://%s/%s/_search", scheme, servers, index);
        //发起请求
        Object result = HttpUtil.doPostJson(url, headers, uriVariables, Object.class);
        //解析结果集
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(result));
        JSONObject hits = jsonObject.getJSONObject("hits");
        List<Map<String, Object>> list = new ArrayList<>();
        //遍历_source
        for (Object object : hits.getJSONArray("hits")) {
            JSONObject item = JSONObject.parseObject(JSON.toJSONString(object));
            Map<String, Object> map = item.getObject("_source", Map.class);
            map.put("uuid", item.getString("_id"));
            list.add(map);
        }
        //返回分页结果
        return new HashMap<String, Object>(2) {{
            put("total", hits.getJSONObject("total").getInteger("value"));
            put("list", list);
        }};
    }

    /**
     * 校验index和id必传
     *
     * @author kuangq
     * @date 2020-12-18 9:20
     */
    private static void checkIndexAndId(String index, String id) {
        if (StringUtils.isBlank(index) || StringUtils.isBlank(id)) {
            throw new BusinessException(HintEnum.HINT_1014);
        }
    }

    /**
     * 获取实例对应ES的index名称
     *
     * @author kuangq
     * @date 2020-12-17 17:02
     */
    public static String getIndex(Object object) {
        //获取className
        String className = object.getClass().getName().replaceAll("[A-Z]", "_$0").toLowerCase();
        //截取类名并将驼峰式转换为下划线
        return className.substring(className.lastIndexOf(".") + 2);
    }
}
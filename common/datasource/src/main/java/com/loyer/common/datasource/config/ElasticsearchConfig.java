package com.loyer.common.datasource.config;

import com.loyer.common.core.constant.SpecialCharsConst;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch配置
 *
 * @author kuangq
 * @date 2020-12-02 17:39
 */
@ConditionalOnProperty(prefix = "elasticsearch", name = "enable", havingValue = "true")
@Configuration
public class ElasticsearchConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${elasticsearch.scheme}")
    private String scheme;

    @Value("${elasticsearch.servers}")
    private String servers;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.maxConnTotal}")
    private int maxConnTotal;

    @Value("${elasticsearch.maxConnPerRoute}")
    private int maxConnPerRoute;

    @Value("${elasticsearch.connectTimeout}")
    private int connectTimeout;

    @Value("${elasticsearch.socketTimeout}")
    private int socketTimeout;

    @Value("${elasticsearch.connectionRequestTimeout}")
    private int connectionRequestTimeout;

    /**
     * RestClientBuilder
     *
     * @author kuangq
     * @date 2020-12-07 15:51
     */
    public RestClientBuilder restClientBuilder() {
        //多个节点用,号分割
        String[] serverAry = servers.split(SpecialCharsConst.COMMA);
        //创建连接节点
        HttpHost[] httpHosts = new HttpHost[serverAry.length];
        //多个从节点持续在内部new多个HttpHost
        for (int i = 0; i < serverAry.length; i++) {
            //节点名称为ip:port
            String[] server = serverAry[i].split(SpecialCharsConst.COLON);
            httpHosts[i] = new HttpHost(server[0], Integer.parseInt(server[1]), scheme);
        }
        //创建REST创建器
        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);
        //创建用户认证对象
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        //基本配置
        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
            //禁用抢占式身份认证
            httpAsyncClientBuilder.disableAuthCaching();
            //设置同时间允许使用的最大连接数
            httpAsyncClientBuilder.setMaxConnTotal(maxConnTotal);
            //设置针对一个域名同时间允许使用的最大连接数
            httpAsyncClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
            //设置连接保持存活时长，此处保持和超时时间一致
            httpAsyncClientBuilder.setKeepAliveStrategy((response, context) -> socketTimeout);
            //设置身份认证需要的账号密码
            httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            return httpAsyncClientBuilder;
        });
        //设置请求超时配置
        restClientBuilder.setRequestConfigCallback(builder -> builder
                //连接超时（默认为1秒）
                .setConnectTimeout(connectTimeout)
                //数据读取超时（默认为30秒）
                .setSocketTimeout(socketTimeout)
                //获取连接的超时，不宜过长
                .setConnectionRequestTimeout(connectionRequestTimeout)
        );
        //设置监听器，每次节点失败都可以监听到，可以作额外处理
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                super.onFailure(node);
                logger.error("【ES节点连接异常】{}://{}:{}", node.getHost().getSchemeName(), node.getHost().getHostName(), node.getHost().getPort());
            }
        });
        return restClientBuilder;
    }

    @Bean
    public RestClient restClient() {
        return restClientBuilder().build();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(restClientBuilder());
    }
}
package org.example.blogsakuraDDD.domain.blog.es.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.example.blogsakuraDDD.domain.blog.es.service.ESDomainService;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.interfaces.vo.blog.article.ArticleVO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * ES搜索处理层
 */
@Service
@Slf4j
public class ESDomainServiceImpl implements ESDomainService {


    @Resource
    private RestHighLevelClient client;

    /**
     * 添加id的文章到ES中
     *
     * @param id
     */
    @Override
    public void addArticleVOToESById(Long id, ArticleVO articleVO) {

        // 1. 准备文档数据
        IndexRequest request = new IndexRequest("article").id(String.valueOf(id));
        // 2. 序列化
        request.source(JSON.toJSONString(articleVO), XContentType.JSON);
        // 3. 发送请求
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("添加文章到ES失败，失败原因：{}", e.getMessage());
        }
    }

    /**
     * 在ES中删除id的文章
     *
     * @param id
     */
    @Override
    public void deleteArticleVOFromESById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 1. 准备删除请求
        DeleteRequest request = new DeleteRequest("article").id(String.valueOf(id));
        try {
            // 2. 发送请求
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除ES文章失败，原因：{}", e.getMessage());
        }
    }


    /**
     * 初始化数据库数据到ES
     *
     */

//    @PostConstruct
    @Override
    public void initDataToES(List<ArticleVO> articleVOList) {
        BulkRequest request = new BulkRequest();
        for (ArticleVO articleVO : articleVOList) {
            request.add(new IndexRequest("article")
                    .id(String.valueOf(articleVO.getId()))
                    .source(JSON.toJSONString(articleVO), XContentType.JSON));
        }
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("初始化数据库到ES失败，原因：{}", e.getMessage());
        }
    }
}

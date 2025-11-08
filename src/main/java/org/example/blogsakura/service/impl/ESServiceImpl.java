package org.example.blogsakura.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.example.blogsakura.service.ArticleService;
import org.example.blogsakura.service.ESService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ES搜索处理层
 */
@Service
@Slf4j
public class ESServiceImpl implements ESService {

    @Resource
    private ArticleService articleService;

    @Resource
    private RestHighLevelClient client;

    /**
     * 添加id的文章到ES中
     *
     * @param id
     */
    @Override
    public void addArticleVOToESById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Article article = articleService.getById(id);
        if (article == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章为null");
            return;
        }
        ArticleVO articleVO = articleService.getArticleVO(article);
        // 1. 准备文档数据
        IndexRequest request = new IndexRequest("article").id(String.valueOf(id));
        // 2. 序列化
        request.source(JSON.toJSONString(articleVO), XContentType.JSON);
        // 3. 发送请求
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化数据库数据到ES
     *
     */
    @Override
//    @PostConstruct
    public void initDataToES() {
        log.info("开始初始化数据到Elastic Search");
        List<Article> articleList = articleService.list();
        List<ArticleVO> articleVOList = articleService.getArticleVOList(articleList);
        BulkRequest request = new BulkRequest();
        for (ArticleVO articleVO : articleVOList) {
            request.add(new IndexRequest("article")
                    .id(String.valueOf(articleVO.getId()))
                    .source(JSON.toJSONString(articleVO), XContentType.JSON));
        }
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照title查询
     *
     * @param title
     * @return
     */
    @Override
    public List<ArticleVO> searchArticleVOByTitle(String title) {
        SearchRequest request = new SearchRequest("article");
        request.source().query(QueryBuilders.matchQuery("title", title));
        request.source().highlighter(new HighlightBuilder()
                .field("title")
                .requireFieldMatch(false)
                .preTags("<em>").postTags("</em>"));
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照title或Content查询
     *
     * @param keyword
     * @return
     */
    @Override
    public List<ArticleVO> searchArticleOrContentVOByTitle(String keyword) {
        SearchRequest request = new SearchRequest("article");
        request.source().query(QueryBuilders.multiMatchQuery(keyword, "title", "content"));
        request.source().highlighter(new HighlightBuilder()
                .field("content")
                .requireFieldMatch(false)
                .preTags("<em>").postTags("</em>"));
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 对响应数据进行处理
     *
     * @param response
     * @return
     */
    private List<ArticleVO> handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        if (searchHits.getTotalHits() != null) {
            long total = searchHits.getTotalHits().value;
        }
        SearchHit[] hits = searchHits.getHits();
        List<ArticleVO> articleVOList = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            String id = hit.getId();
            Article article = articleService.getById(id);
            ArticleVO articleVO = articleService.getArticleVO(article);
            if (highlightFields != null && !highlightFields.isEmpty()) {
                HighlightField highlightField = highlightFields.get("content");
                if (highlightField != null) {
                    articleVO.setContent(highlightField.getFragments()[0].toString());
                }
            }
            articleVOList.add(articleVO);
        }
        return articleVOList;
    }
}

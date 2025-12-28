package org.example.blogsakuraDDD.application.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.example.blogsakuraDDD.application.service.ArticleApplicationService;
import org.example.blogsakuraDDD.application.service.ESApplicationService;
import org.example.blogsakuraDDD.domain.blog.article.entity.Article;
import org.example.blogsakuraDDD.domain.blog.es.service.ESDomainService;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.interfaces.vo.blog.article.ArticleVO;
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
public class ESApplicationServiceImpl implements ESApplicationService {


    @Resource
    private ESDomainService esDomainService;

    @Resource
    private ArticleApplicationService articleApplicationService;

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
        Article article = articleApplicationService.getById(id);
        if (article == null) {
            return;
        }
        ArticleVO articleVO = articleApplicationService.getArticleVO(article);
        esDomainService.addArticleVOToESById(id, articleVO);
    }

    /**
     * 在ES中删除id的文章
     *
     * @param id
     */
    @Override
    public void deleteArticleVOFromESById(Long id) {
        esDomainService.deleteArticleVOFromESById(id);
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
     * 初始化数据库数据到ES
     *
     */
    @Override
    public void initDataToES() {
        log.info("开始初始化数据到Elastic Search");
        List<Article> articleList = articleApplicationService.list();
        List<ArticleVO> articleVOList = articleApplicationService.getArticleVOList(articleList);
        esDomainService.initDataToES(articleVOList);
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
            Article article = articleApplicationService.getById(id);
            ArticleVO articleVO = articleApplicationService.getArticleVO(article);
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

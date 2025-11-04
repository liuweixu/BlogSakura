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
import org.example.blogsakura.mapper.ArticleMapper;
import org.example.blogsakura.pojo.Article;
import org.example.blogsakura.pojo.ArticleDoc;
import org.example.blogsakura.service.ESService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
class ESServiceImpl implements ESService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private RestHighLevelClient client;

    @Override
    public void insertArticleDocById(String id) {
        try {
            // 先查询相应Article
            Article article = articleMapper.getArticleById(id);
            if (article == null) {
                log.info("插入ES-文章不存在");
                return;
            }
            // 转为ArticleDoc类型
            ArticleDoc articleDoc = new ArticleDoc(article);

            // 1.准备文档数据
            IndexRequest request = new IndexRequest("article").id(id);
            // 2.序列化
            request.source(JSON.toJSONString(articleDoc), XContentType.JSON);
            // 3.发送请求
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteArticleDocById(String id) {
        try {
            // 1.准备删除请求
            DeleteRequest request = new DeleteRequest("article").id(id);
            // 2.发送请求
            client.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 新增初始化操作
     */
    @Override
//    @PostConstruct
    public void initDataToES() {
        log.info("开始初始化数据到Elastic Search");
        try {
            List<Article> articles = articleMapper.getArticleList();
            BulkRequest request = new BulkRequest();
            for (Article article : articles) {
                log.info("" + article);
                ArticleDoc articleDoc = new ArticleDoc(article);
                request.add(new IndexRequest("article").id(articleDoc.getId())
                        .source(JSON.toJSONString(articleDoc), XContentType.JSON));
            }
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Article> findArticleByTitle(String title) {
        try {
            SearchRequest request = new SearchRequest("article");
            request.source().query(QueryBuilders.matchQuery("title", title));
            request.source().highlighter(new HighlightBuilder()
                    .field("title")
                    .requireFieldMatch(false)
                    .preTags("<em>").postTags("</em>")
            );
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Article> findArticleByTitleOrContent(String keyword) {
        try {
            SearchRequest request = new SearchRequest("article");
            request.source().query(QueryBuilders.multiMatchQuery(keyword, "title", "content"));
            request.source().highlighter(new HighlightBuilder()
                    .field("content")
                    .requireFieldMatch(false)
                    .preTags("<em>").postTags("</em>")
            );
            request.source().from(0).size(5);
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
    private List<Article> handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        if (searchHits.getTotalHits() != null) {
            long total = searchHits.getTotalHits().value;
        }
        SearchHit[] hits = searchHits.getHits();
        List<Article> articles = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            String id = hit.getId();
            Article article = articleMapper.getArticleById(id);
            if (highlightFields != null && !highlightFields.isEmpty()) {
                HighlightField highlightField = highlightFields.get("content");
                if (highlightField != null) {
                    article.setContent(highlightField.getFragments()[0].toString());
                }
            }
            articles.add(article);
        }
        return articles;
    }
}

package org.example.blogsakura2;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.annotation.Resource;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.example.blogsakura2.mapper.ArticleMapper;
import org.example.blogsakura2.pojo.Article;
import org.example.blogsakura2.pojo.ArticleDoc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ESTests {

    @Resource
    private RestHighLevelClient client;

    @Resource
    private ArticleMapper articleMapper;

    @Test
    void testCreateHotelIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("hotel");
//        request.source(HotelConstants.MAPPING_TEMPLATE, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    void testDeleteHotelIndex() throws IOException {
        DeleteIndexRequest hotel = new DeleteIndexRequest("hotel");
        client.indices().delete(hotel, RequestOptions.DEFAULT);
    }

    @Test
    void testExistsHotelIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("article");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void testAddDocument() throws IOException {
        // 批量查询数据
        List<Article> articles = articleMapper.getArticleList();

        BulkRequest request = new BulkRequest();
        for (Article article : articles) {
            // 先进行转换
            ArticleDoc articleDoc = new ArticleDoc(article);
            request.add(new IndexRequest("article")
                    .id(articleDoc.getId())
                    .source(JSON.toJSONString(articleDoc), XContentType.JSON));
        }
        client.bulk(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocumentById() throws IOException {
        GetRequest request = new GetRequest("article", "761125090078232576");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        Article article = JSON.parseObject(json, Article.class);
        System.out.println(article.getContent());
    }

    @Test
    void testMatchAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("article");
        searchRequest.source().query(QueryBuilders.matchAllQuery());
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println(total);
    }

    @Test
    void testHighlight() throws IOException {
        SearchRequest request = new SearchRequest("article");
        request.source().query(QueryBuilders.matchQuery("all", "范"));
        request.source().highlighter(new HighlightBuilder().field("content").requireFieldMatch(false));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }


    private void handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        long totol = searchHits.getTotalHits().value;
        System.out.println(totol);

        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Article article = JSON.parseObject(json, Article.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("content");
                if (highlightField != null) {
                    article.setContent(highlightField.getFragments()[0].toString());
                }
            }
            System.out.println(article.getContent());
        }
    }

}

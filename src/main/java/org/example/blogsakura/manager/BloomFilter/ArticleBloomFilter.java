package org.example.blogsakura.manager.BloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ArticleBloomFilter {

    @Resource
    private ArticleService articleService;
    private BloomFilter<Long> bloomFilter;

    @PostConstruct
    public void init() {
        // 假设数据库总文章数大约 100_000
        bloomFilter = BloomFilter.create(Funnels.longFunnel(), 100_000, 0.01);

        // 预热布隆过滤器：把所有文章id加入布隆过滤器
        log.info("预测布隆过滤器");
        List<Long> allArticleIds = fetchAllArticleIdsFromDB();
        for (Long articleId : allArticleIds) {
            log.info(articleId.toString());
        }
        allArticleIds.forEach(bloomFilter::put);
    }

    public boolean mightExist(Long id) {
        return bloomFilter.mightContain(id);
    }

    private List<Long> fetchAllArticleIdsFromDB() {
        return articleService.list().stream().map(Article::getId).collect(Collectors.toList());
    }

    public void put(Long articleId) {
        log.info("更新文章id：{}到布隆过滤器", articleId);
        bloomFilter.put(articleId);
    }
}

package org.example.blogsakura2.configure;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import org.example.blogsakura2.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ArticleBloomFilter {

    private BloomFilter<String> bloomFilter;

    @Autowired
    private ArticleMapper articleMapper;

    @PostConstruct
    public void init() {
        // 假设数据库总文章数大约 100_000
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), 100_000, 0.01);

        // 预热布隆过滤器：把所有文章id加入布隆过滤器
        List<String> allArticleIds = fetchAllArticleIdsFromDB();
        allArticleIds.forEach(bloomFilter::put);
    }

    public boolean mightExist(String id) {
        return bloomFilter.mightContain(id);
    }

    private List<String> fetchAllArticleIdsFromDB() {
        return articleMapper.getId();
    }
}

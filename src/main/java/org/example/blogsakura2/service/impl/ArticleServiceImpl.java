package org.example.blogsakura2.service.impl;

import com.qcloud.cos.COSClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.configuration.CosClientConfig;
//import org.example.blogsakura.
import org.example.blogsakura2.mapper.ArticleMapper;
import org.example.blogsakura2.mapper.ChannelMapper;
import org.example.blogsakura2.pojo.Article;
import org.example.blogsakura2.pojo.ArticleInsert;
import org.example.blogsakura2.pojo.ArticleQuery;
import org.example.blogsakura2.pojo.ArticleUpdate;
import org.example.blogsakura2.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ChannelMapper channelMapper;
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;
//    @Autowired
//    private CosManager cosManager;

    /**
     * 获取文章列表
     *
     * @param articleQuery
     * @return
     */
    @Override
    public List<Article> getArticleList(ArticleQuery articleQuery) {
        if (articleQuery == null || articleQuery.getChannelName().isEmpty()) {
            return articleMapper.getArticleList();
        } else {
            return articleMapper.getArticleListByChannel(articleQuery.getChannelName());
        }
    }


    /**
     * 首页：获取文章列表
     *
     * @return
     */
    @Override
    public List<Article> getHomeArticleList() {
        return articleMapper.getArticleList();
    }

    /**
     * 按照id删除文章
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticleById(String id) {
        articleMapper.deleteArticleById(id);
    }

    /**
     * 按照id获取文章
     *
     * @param id
     * @return
     */
    @Override
    public Article getArticleById(String id) {
        return articleMapper.getArticleById(id);
    }

    /**
     * 插入文章
     *
     * @param articleInsert
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertArticle(ArticleInsert articleInsert, String id) {
        Article article = new Article();
        article.setId(id);
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        article.setPublishDate(nowTime);
        article.setEditDate(nowTime);
        article.setTitle(articleInsert.getTitle());
        article.setContent(articleInsert.getContent());
        article.setImageUrl(articleInsert.getImageUrl());
        article.setImageType(articleInsert.getImageType());
        log.info("channelname:{}", articleInsert.getChannel());
        Long channelId = channelMapper.getChannelIdByName(articleInsert.getChannel());
        article.setChannelId(channelId);
        articleMapper.insertArticle(article);
    }

    /**
     * 更新文章
     *
     * @param articleUpdate
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleUpdate articleUpdate, String id) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(articleUpdate.getTitle());
        article.setContent(articleUpdate.getContent());
        article.setChannelId(channelMapper.getChannelIdByName(articleUpdate.getChannel()));
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        article.setEditDate(nowTime);
        article.setImageType(articleUpdate.getImageType());
        article.setImageUrl(articleUpdate.getImageUrl());

        articleMapper.updateArticle(article);
    }

    /**
     * 将上传的图片处理，作为一个File类上传到COS中
     *
     * @param file
     * @return
     */
    public String uploadImageToCOS(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        // 生成COS对象键（可以根据需要自定义路径格式）
        String cosKey = "/images/" + uuid + ".png";
//        try {
//            return cosManager.uploadFileWithoutLocal(file, cosKey);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return cosKey;
    }
}

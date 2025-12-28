package org.example.blogsakura.domain.blog.article.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.infrastruct.constants.RabbitMQConstants;
import org.example.blogsakura.infrastruct.manager.BloomFilter.ArticleBloomFilter;
import org.example.blogsakura.infrastruct.manager.cos.CosManager;
import org.example.blogsakura.infrastruct.manager.cos.CosManagerLocalProcess;
import org.example.blogsakura.domain.blog.channel.entity.Channel;
import org.example.blogsakura.domain.blog.article.entity.Article;
import org.example.blogsakura.domain.blog.article.service.ArticleDomainService;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.mapper.ArticleMapper;
import org.example.blogsakura.infrastruct.mapper.ChannelMapper;
import org.example.blogsakura.interfaces.assembler.ArticleAssembler;
import org.example.blogsakura.interfaces.dto.blog.article.ArticleQueryRequest;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 文章表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class ArticleDomainServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleDomainService {


    @Resource
    private ChannelMapper channelMapper;

    @Resource
    private CosManagerLocalProcess cosManagerLocalProcess;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ArticleBloomFilter articleBloomFilter;

    @Resource
    private CosManager cosManager;

    /**
     * 分页查询条件
     *
     * @param articleQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ArticleQueryRequest articleQueryRequest) {
        ThrowUtils.throwIf(articleQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        Long id = articleQueryRequest.getId();
        String channelName = articleQueryRequest.getChannel();
        Channel channel = channelMapper.getChannelByChannelName(channelName);
        String title = articleQueryRequest.getTitle();
        String content = articleQueryRequest.getContent();
        Integer imageType = articleQueryRequest.getImageType();
        String imageUrl = articleQueryRequest.getImageUrl();
        LocalDateTime publishDate = articleQueryRequest.getPublishDate();
        LocalDateTime editDate = articleQueryRequest.getEditDate();
        Long view = articleQueryRequest.getView();
        Long like = articleQueryRequest.getLike();
        String sortField = articleQueryRequest.getSortField();
        String sortOrder = articleQueryRequest.getSortOrder();
        return Optional.ofNullable(channel)
                .map(ch -> QueryWrapper.create()
                        .eq("id", id)
                        .eq("channelId", ch.getId())
                        .eq("imageType", imageType)
                        .like("content", content)
                        .like("title", title)
                        .orderBy(sortField, "ascend".equals(sortOrder)))
                .orElseGet(() -> QueryWrapper.create()
                        .eq("id", id)
                        .eq("title", title)
                        .eq("imageType", imageType)
                        .like("content", content)
                        .orderBy(sortField, "ascend".equals(sortOrder)));
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
        String cosKey = "/blogs/images/" + uuid + ".png";
        try {
            return cosManagerLocalProcess.uploadFileWithoutLocal(file, cosKey);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 添加文章
     *
     * @param articleVO
     * @return
     */
    @Override
    public Boolean addArticle(ArticleVO articleVO) {
        String channel = articleVO.getChannel();
        Long channelId = channelMapper.getChannelByChannelName(channel).getId();
        Article article = ArticleAssembler.toArticleEntity(articleVO);
        article.setChannelId(channelId);
        boolean result = this.save(article);
        if (result) {
            articleBloomFilter.put(article.getId());
            rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE,
                    RabbitMQConstants.ARTICLE_INSERT_KEY, article.getId());
        }
        return result;
    }

    /**
     * 删除文章
     *
     * @return
     */
    @Override
    public Boolean deleteArticle(Long id) {
        boolean result = this.removeById(id);
        if (result) {
            rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE,
                    RabbitMQConstants.ARTICLE_DELETE_KEY, id);
        }
        return result;
    }

    /**
     * 更新文章
     *
     * @param articleVO
     * @return
     */
    @Override
    public Boolean updateArticle(ArticleVO articleVO) {
        String channel = articleVO.getChannel();
        Long channelId = channelMapper.getChannelByChannelName(channel).getId();
        Article article = ArticleAssembler.toArticleEntity(articleVO);
        article.setChannelId(channelId);
        article.setId(articleVO.getId());
        // 更新前把COS中的图像删除掉
        String imgUrl = this.getById(articleVO.getId()).getImageUrl();
        boolean resultDelete = cosManager.deleteCOSPicture(imgUrl);
        boolean result = this.updateById(article);
        if (result && resultDelete) {
            rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE,
                    RabbitMQConstants.ARTICLE_INSERT_KEY, article.getId());
        }
        return result;
    }


}

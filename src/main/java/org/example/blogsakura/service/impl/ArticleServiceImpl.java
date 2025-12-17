package org.example.blogsakura.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.manager.cos.CosManagerLocalProcess;
import org.example.blogsakura.mapper.ChannelMapper;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.mapper.ArticleMapper;
import org.example.blogsakura.model.dto.article.ArticleQueryRequest;
import org.example.blogsakura.model.dto.channel.Channel;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.example.blogsakura.service.ArticleService;
import org.example.blogsakura.service.ChannelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 文章表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ChannelService channelService;

    @Resource
    private ChannelMapper channelMapper;

    @Resource
    private CosManagerLocalProcess cosManagerLocalProcess;

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
     * article 文章信息
     *
     * @param article
     * @return
     */
    @Override
    public ArticleVO getArticleVO(Article article) {
        ThrowUtils.throwIf(article == null, ErrorCode.PARAMS_ERROR, "参数为空");
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        Long channelId = article.getChannelId();
        articleVO.setChannel(channelService.getById(channelId).getChannel());
        articleVO.setId(article.getId());
        return articleVO;
    }

    /**
     * 返回后端上的文章列表信息
     *
     * @param articleList
     * @return
     */
    @Override
    public List<ArticleVO> getArticleVOList(List<Article> articleList) {
        ThrowUtils.throwIf(articleList == null, ErrorCode.PARAMS_ERROR);
        return articleList.stream().map(this::getArticleVO).toList();
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
}

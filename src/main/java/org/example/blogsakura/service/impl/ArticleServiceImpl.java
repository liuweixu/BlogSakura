package org.example.blogsakura.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.mapper.ChannelMapper;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.mapper.ArticleMapper;
import org.example.blogsakura.model.dto.article.ArticleQueryRequest;
import org.example.blogsakura.model.dto.channel.Channel;
import org.example.blogsakura.model.dto.user.UserQueryRequest;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.example.blogsakura.service.ArticleService;
import org.example.blogsakura.service.ChannelService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    /**
     * 分页查询条件
     *
     * @param articleQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ArticleQueryRequest articleQueryRequest) {
        if (articleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
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
        if (channel != null) {
            return QueryWrapper.create()
                    .eq("id", id)
                    .eq("channelId", channel.getId())
                    .eq("imageType", imageType)
                    .like("content", content)
                    .like("title", title)
                    .orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            return QueryWrapper.create()
                    .eq("id", id)
                    .eq("title", title)
                    .eq("imageType", imageType)
                    .like("content", content)
                    .orderBy(sortField, "ascend".equals(sortOrder));
        }
    }

    /**
     * article 文章信息
     *
     * @param article
     * @return
     */
    @Override
    public ArticleVO getArticleVO(Article article) {
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        Long channelId = article.getChannelId();
        articleVO.setChannel(channelService.getById(channelId).getChannel());
        articleVO.setId(String.valueOf(article.getId()));
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
        if (articleList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        return articleList.stream().map(this::getArticleVO).collect(Collectors.toList());
    }


}

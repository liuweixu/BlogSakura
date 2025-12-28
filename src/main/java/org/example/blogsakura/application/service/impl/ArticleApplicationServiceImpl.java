package org.example.blogsakura.application.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.application.service.ChannelApplicationService;
import org.example.blogsakura.domain.blog.article.service.ArticleDomainService;
import org.example.blogsakura.domain.blog.article.entity.Article;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.mapper.ArticleMapper;
import org.example.blogsakura.interfaces.dto.blog.article.ArticleQueryRequest;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.example.blogsakura.application.service.ArticleApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文章表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class ArticleApplicationServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleApplicationService {

    @Resource
    private ArticleDomainService articleDomainService;

    @Resource
    private ChannelApplicationService channelApplicationService;


    /**
     * 分页查询条件
     *
     * @param articleQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ArticleQueryRequest articleQueryRequest) {
        return articleDomainService.getQueryWrapper(articleQueryRequest);
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
        articleVO.setChannel(channelApplicationService.getById(channelId).getChannel());
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
     * 添加文章
     *
     * @param articleVO
     * @return
     */
    @Override
    public Boolean addArticle(ArticleVO articleVO) {
        return articleDomainService.addArticle(articleVO);
    }

    /**
     * 删除文章
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @Override
    public Boolean deleteArticle(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return articleDomainService.deleteArticle(id);
    }

    /**
     * 更新文章
     *
     * @param articleVO
     * @return
     */
    @Override
    public Boolean updateArticle(ArticleVO articleVO) {
        ThrowUtils.throwIf(articleVO == null, ErrorCode.PARAMS_ERROR);
        return articleDomainService.updateArticle(articleVO);
    }

    /**
     * 查询所有文章表
     *
     * @return
     */
    @Override
    public List<ArticleVO> getArticleVOList() {
        List<Article> articleList = articleDomainService.list();
        return this.getArticleVOList(articleList);
    }

    /**
     * 根据主键获取文章
     *
     * @param id
     * @return
     */
    @Override
    public ArticleVO getArticleVOById(Long id) {
        Article article = articleDomainService.getById(id);
        return this.getArticleVO(article);
    }

    /**
     * 分页查询文章表
     *
     * @param articleQueryRequest
     * @return
     */
    @Override
    public Page<ArticleVO> getArticleVOListByPage(ArticleQueryRequest articleQueryRequest) {
        ThrowUtils.throwIf(articleQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = articleQueryRequest.getCurrentPage();
        long pageSize = articleQueryRequest.getPageSize();
        Page<Article> articlePage = articleDomainService.page(Page.of(currentPage, pageSize),
                articleDomainService.getQueryWrapper(articleQueryRequest));
        // 数据脱敏
        Page<ArticleVO> articleVOPage = new Page<>(currentPage, pageSize, articlePage.getTotalRow());
        List<ArticleVO> articleVOList = this.getArticleVOList(articlePage.getRecords());
        articleVOPage.setRecords(articleVOList);
        return articleVOPage;
    }

    /**
     * 获取文件名
     *
     * @param file
     * @return
     */
    @Override
    public String getUploadFile(MultipartFile file) {
        return articleDomainService.uploadImageToCOS(file);
    }

    /**
     * 前端筛选文章信息
     *
     * @param id
     * @return
     */
    @Override
    public ArticleVO getFrontendArticleVOById(Long id) {
        Article article = articleDomainService.getById(id);
        return this.getArticleVO(article);
    }

    /**
     * 前端获取文章列表信息
     *
     * @return
     */
    @Override
    public List<ArticleVO> getFrontendArticleVOList() {
        return this.getArticleVOList(articleDomainService.list());
    }

    /**
     * 前端文章表分页
     *
     * @param articleQueryRequest
     * @return
     */
    @Override
    public Page<ArticleVO> getFrontendArticleVOListByPage(ArticleQueryRequest articleQueryRequest) {
        ThrowUtils.throwIf(articleQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = articleQueryRequest.getCurrentPage();
        long pageSize = articleQueryRequest.getPageSize();
        Page<Article> articlePage = articleDomainService.page(Page.of(currentPage, pageSize),
                articleDomainService.getQueryWrapper(articleQueryRequest));
        // 数据脱敏
        Page<ArticleVO> articleVOPage = new Page<>(currentPage, pageSize, articlePage.getTotalRow());
        List<ArticleVO> articleVOList = this.getArticleVOList(articlePage.getRecords());
        articleVOPage.setRecords(articleVOList);
        return articleVOPage;
    }

    /**
     * 获取features（3个随机文章）
     *
     * @return
     */
    @Override
    public List<ArticleVO> getFrontendArticleVOListFeatures() {
        List<ArticleVO> articleVOList = this.getArticleVOList(articleDomainService.list());
        int length = articleVOList.size();
        Set<Integer> set = new HashSet<>();
        while (set.size() < Math.min(3, length)) {
            int num = RandomUtil.randomInt(Math.min(3, length)) + 1;
            set.add(num);
        }
        List<ArticleVO> results = new ArrayList<>();
        for (int i = 0; i < set.size(); i++) {
            results.add(articleVOList.get(i));
        }
        return results;
    }
}

package org.example.blogsakura.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.domain.blog.article.entity.Article;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.interfaces.dto.blog.article.ArticleQueryRequest;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文章表 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface ArticleApplicationService extends IService<Article> {

    /**
     * 分页查询条件
     *
     * @param articleQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ArticleQueryRequest articleQueryRequest);

    /**
     * 返回后端上的文章信息
     *
     * @param article
     * @return
     */
    ArticleVO getArticleVO(Article article);

    /**
     * 返回后端上的文章列表信息
     *
     * @param articleList
     * @return
     */
    List<ArticleVO> getArticleVOList(List<Article> articleList);


    /**
     * 添加文章
     *
     * @param articleVO
     * @return
     */
    Boolean addArticle(ArticleVO articleVO);

    /**
     * 删除文章
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    Boolean deleteArticle(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新文章
     *
     * @param articleVO
     * @return
     */
    Boolean updateArticle(ArticleVO articleVO);

    /**
     * 查询所有文章表
     *
     * @return
     */
    List<ArticleVO> getArticleVOList();

    /**
     * 根据主键获取文章
     *
     * @param id
     * @return
     */
    ArticleVO getArticleVOById(Long id);

    /**
     * 分页查询文章表
     *
     * @param articleQueryRequest
     * @return
     */
    Page<ArticleVO> getArticleVOListByPage(ArticleQueryRequest articleQueryRequest);

    /**
     * 获取文件名
     *
     * @param file
     * @return
     */
    String getUploadFile(MultipartFile file);

    /**
     * 前端筛选文章信息
     *
     * @param id
     * @return
     */
    ArticleVO getFrontendArticleVOById(Long id);

    /**
     * 前端获取文章列表信息
     *
     * @return
     */
    List<ArticleVO> getFrontendArticleVOList();

    /**
     * 前端文章表分页
     *
     * @param articleQueryRequest
     * @return
     */
    Page<ArticleVO> getFrontendArticleVOListByPage(ArticleQueryRequest articleQueryRequest);

    /**
     * 获取features（3个随机文章）
     *
     * @return
     */
    List<ArticleVO> getFrontendArticleVOListFeatures();

}

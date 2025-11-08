package org.example.blogsakura.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.model.dto.article.ArticleQueryRequest;
import org.example.blogsakura.model.dto.user.UserQueryRequest;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文章表 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface ArticleService extends IService<Article> {

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
     * 将上传的图片处理，作为一个File类上传到COS中
     *
     * @param file
     * @return
     */
    public String uploadImageToCOS(MultipartFile file);
}

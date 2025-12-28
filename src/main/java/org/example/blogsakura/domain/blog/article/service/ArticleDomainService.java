package org.example.blogsakura.domain.blog.article.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.blogsakura.domain.blog.article.entity.Article;
import org.example.blogsakura.interfaces.dto.blog.article.ArticleQueryRequest;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文章表 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface ArticleDomainService extends IService<Article> {

    /**
     * 分页查询条件
     *
     * @param articleQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ArticleQueryRequest articleQueryRequest);


    /**
     * 将上传的图片处理，作为一个File类上传到COS中
     *
     * @param file
     * @return
     */
    String uploadImageToCOS(MultipartFile file);

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
     * @return
     */
    Boolean deleteArticle(Long id);

    /**
     * 更新文章
     *
     * @param articleVO
     * @return
     */
    Boolean updateArticle(ArticleVO articleVO);
}

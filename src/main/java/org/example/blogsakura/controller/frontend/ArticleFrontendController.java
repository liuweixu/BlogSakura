package org.example.blogsakura.controller.frontend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.model.dto.article.ArticleQueryRequest;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.example.blogsakura.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleFrontendController {

    private final ArticleService articleService;

    /**
     * 前端筛选id的文章信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public BaseResponse<ArticleVO> getFrontendArticleVOById(@PathVariable Long id) {
        Article article = articleService.getById(id);
        ArticleVO articleVO = articleService.getArticleVO(article);
        return ResultUtils.success(articleVO);
    }

    /**
     * 前端获取文章列表信息
     *
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<ArticleVO>> getFrontendArticleVOList() {
        return ResultUtils.success(articleService.getArticleVOList(articleService.list()));
    }

    /**
     * 分页查询文章表。
     *
     * @param articleQueryRequest 分页查询请求
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    public BaseResponse<Page<ArticleVO>> getArticleVOListByPage(@RequestBody ArticleQueryRequest articleQueryRequest) {
        ThrowUtils.throwIf(articleQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = articleQueryRequest.getCurrentPage();
        long pageSize = articleQueryRequest.getPageSize();
        Page<Article> articlePage = articleService.page(Page.of(currentPage, pageSize),
                articleService.getQueryWrapper(articleQueryRequest));
        // 数据脱敏
        Page<ArticleVO> articleVOPage = new Page<>(currentPage, pageSize, articlePage.getTotalRow());
        List<ArticleVO> articleVOList = articleService.getArticleVOList(articlePage.getRecords());
        articleVOPage.setRecords(articleVOList);
        return ResultUtils.success(articleVOPage);
    }
}

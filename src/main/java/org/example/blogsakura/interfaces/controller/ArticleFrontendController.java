package org.example.blogsakura.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.interfaces.dto.blog.article.ArticleQueryRequest;
import org.example.blogsakura.interfaces.vo.blog.article.ArticleVO;
import org.example.blogsakura.application.service.ArticleApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleFrontendController {

    private final ArticleApplicationService articleApplicationService;

    /**
     * 前端筛选id的文章信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public BaseResponse<ArticleVO> getFrontendArticleVOById(@PathVariable Long id) {
        return ResultUtils.success(articleApplicationService.getFrontendArticleVOById(id));
    }

    /**
     * 前端获取文章列表信息
     *
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<ArticleVO>> getFrontendArticleVOList() {
        return ResultUtils.success(articleApplicationService.getFrontendArticleVOList());
    }

    /**
     * 分页查询文章表。
     *
     * @param articleQueryRequest 分页查询请求
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    public BaseResponse<Page<ArticleVO>> getFrontendArticleVOListByPage(@RequestBody ArticleQueryRequest articleQueryRequest) {
        return ResultUtils.success(articleApplicationService.getFrontendArticleVOListByPage(articleQueryRequest));
    }

    @GetMapping("/list/features")
    public BaseResponse<List<ArticleVO>> getFrontendArticleVOListFeatures() {
        return ResultUtils.success(articleApplicationService.getFrontendArticleVOListFeatures());
    }
}

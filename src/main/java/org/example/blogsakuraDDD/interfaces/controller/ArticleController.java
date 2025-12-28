package org.example.blogsakuraDDD.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakuraDDD.infrastruct.aop.Log;
import org.example.blogsakuraDDD.infrastruct.common.BaseResponse;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.infrastruct.common.ResultUtils;
import org.example.blogsakuraDDD.interfaces.dto.blog.article.ArticleQueryRequest;
import org.example.blogsakuraDDD.interfaces.vo.blog.article.ArticleVO;
import org.springframework.web.bind.annotation.*;
import org.example.blogsakuraDDD.application.service.ArticleApplicationService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文章表 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/article")
@Slf4j
public class ArticleController {

    @Resource
    private ArticleApplicationService articleApplicationService;

    /**
     * 新增文章表。
     *
     * @param articleVO 前端返回的文章表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    @Log("新增文章")
    public BaseResponse<Boolean> addArticle(@RequestBody ArticleVO articleVO) {
        return ResultUtils.success(articleApplicationService.addArticle(articleVO));
    }

    /**
     * 根据主键删除文章表。
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    @Log("删除文章")
    public BaseResponse<Boolean> deleteArticle(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(articleApplicationService.deleteArticle(deleteRequest, request));
    }

    /**
     * 根据主键更新文章表。
     *
     * @param articleVO 前端返回的文章表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    @Log("更新文章")
    public BaseResponse<Boolean> updateArticle(@RequestBody ArticleVO articleVO) {
        return ResultUtils.success(articleApplicationService.updateArticle(articleVO));
    }

    /**
     * 查询所有文章表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public BaseResponse<List<ArticleVO>> getArticleVOList() {
        return ResultUtils.success(articleApplicationService.getArticleVOList());
    }

    /**
     * 根据主键获取文章。
     *
     * @param id 文章表主键
     * @return 文章表详情
     */
    @GetMapping("/{id}")
    public BaseResponse<ArticleVO> getArticleVOById(@PathVariable Long id) {
        return ResultUtils.success(articleApplicationService.getArticleVOById(id));
    }

    /**
     * 分页查询文章表。
     *
     * @param articleQueryRequest 分页查询请求
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    public BaseResponse<Page<ArticleVO>> getArticleVOListByPage(@RequestBody ArticleQueryRequest articleQueryRequest) {
        return ResultUtils.success(articleApplicationService.getArticleVOListByPage(articleQueryRequest));
    }

    @PostMapping("/upload/image")
    public BaseResponse<String> getUploadFile(@RequestPart("image") MultipartFile file) {
        // 获取文件名
        return ResultUtils.success(articleApplicationService.getUploadFile(file));
    }

}

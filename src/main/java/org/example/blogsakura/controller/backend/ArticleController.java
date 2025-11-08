package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.aop.Log;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.constants.RabbitMQConstants;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.manager.ArticleBloomFilter;
import org.example.blogsakura.mapper.ChannelMapper;
import org.example.blogsakura.model.dto.article.ArticleQueryRequest;
import org.example.blogsakura.model.dto.channel.Channel;
import org.example.blogsakura.model.vo.article.ArticleVO;
import org.example.blogsakura.service.ChannelService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.blogsakura.model.dto.article.Article;
import org.example.blogsakura.service.ArticleService;
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
    private ArticleService articleService;

    @Resource
    private ChannelMapper channelMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ArticleBloomFilter articleBloomFilter;

    /**
     * 新增文章表。
     *
     * @param articleVO 前端返回的文章表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    @Log("新增文章")
    public BaseResponse<Boolean> addArticle(@RequestBody ArticleVO articleVO) {
        log.info("articleVO: {}", articleVO.toString());
        String channel = articleVO.getChannel();
        Long channelId = channelMapper.getChannelByChannelName(channel).getId();
        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        article.setChannelId(channelId);
        boolean result = articleService.save(article);
        log.info("result: {}", result);
        if (result) {
            articleBloomFilter.put(article.getId());
            rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE,
                    RabbitMQConstants.ARTICLE_INSERT_KEY, article.getId());
        }
        return ResultUtils.success(result);
    }

    /**
     * 根据主键删除文章表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/{id}")
    @Log("删除文章")
    public BaseResponse<Boolean> removeArticleById(@PathVariable Long id) {
        boolean result = articleService.removeById(id);
        if (result) {
            rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE,
                    RabbitMQConstants.ARTICLE_DELETE_KEY, id);
        }
        return ResultUtils.success(result);
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
        String channel = articleVO.getChannel();
        Long channelId = channelMapper.getChannelByChannelName(channel).getId();
        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        article.setChannelId(channelId);
        article.setId(Long.valueOf(articleVO.getId()));
        boolean result = articleService.updateById(article);
        if (result) {
            rabbitTemplate.convertAndSend(RabbitMQConstants.ARTICLE_EXCHANGE,
                    RabbitMQConstants.ARTICLE_INSERT_KEY, article.getId());
        }
        return ResultUtils.success(result);
    }

    /**
     * 查询所有文章表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public BaseResponse<List<ArticleVO>> getArticleVOList() {
        List<Article> articleList = articleService.list();
        return ResultUtils.success(articleService.getArticleVOList(articleList));
    }

    /**
     * 根据主键获取文章表。
     *
     * @param id 文章表主键
     * @return 文章表详情
     */
    @GetMapping("/{id}")
    public BaseResponse<ArticleVO> getArticleVOById(@PathVariable Long id) {
        Article article = articleService.getById(id);
        return ResultUtils.success(articleService.getArticleVO(article));
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

    @PostMapping("/upload/image")
    public BaseResponse<String> getUploadFile(@RequestPart("image") MultipartFile file) {
        // 获取文件名
        return ResultUtils.success(articleService.uploadImageToCOS(file));
    }

}

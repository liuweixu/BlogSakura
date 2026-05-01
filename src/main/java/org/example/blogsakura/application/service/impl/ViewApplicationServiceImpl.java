package org.example.blogsakura.application.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.example.blogsakura.application.service.RankingApplicationService;
import org.example.blogsakura.domain.blog.view.service.ViewDomainService;
import org.example.blogsakura.application.service.ViewApplicationService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViewApplicationServiceImpl implements ViewApplicationService {

    @Resource
    private ViewDomainService viewDomainService;

    private final RankingApplicationService rankingApplicationService;

    /**
     * 更新点赞数:
     * 修改方案：先查询Redis，Redis不存在才查询数据库
     *
     * @param id
     */
    @Override
    public Long updateViews(Long id) {
        Long latestViewCount = viewDomainService.updateViews(id);
        if (latestViewCount != null) {
            rankingApplicationService.increaseRankingScore(id);
        }
        return latestViewCount;
    }
}

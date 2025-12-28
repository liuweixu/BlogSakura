package org.example.blogsakura.application.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.domain.blog.view.service.ViewDomainService;
import org.example.blogsakura.application.service.ViewApplicationService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ViewApplicationServiceImpl implements ViewApplicationService {

    @Resource
    private ViewDomainService viewDomainService;

    /**
     * 更新点赞数:
     * 修改方案：先查询Redis，Redis不存在才查询数据库
     *
     * @param id
     */
    @Override
    public Long updateViews(Long id) {
        return viewDomainService.updateViews(id);
    }
}

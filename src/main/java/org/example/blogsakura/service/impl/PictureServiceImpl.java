package org.example.blogsakura.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.mapper.PictureMapper;
import org.example.blogsakura.service.PictureService;
import org.springframework.stereotype.Service;

/**
 * 图片管理 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

}

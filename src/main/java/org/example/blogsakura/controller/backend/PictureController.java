package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.service.PictureService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图片管理 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private PictureService pictureService;

    /**
     * 保存图片管理。
     *
     * @param picture 图片管理
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Picture picture) {
        return pictureService.save(picture);
    }

    /**
     * 根据主键删除图片管理。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return pictureService.removeById(id);
    }

    /**
     * 根据主键更新图片管理。
     *
     * @param picture 图片管理
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Picture picture) {
        return pictureService.updateById(picture);
    }

    /**
     * 查询所有图片管理。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Picture> list() {
        return pictureService.list();
    }

    /**
     * 根据主键获取图片管理。
     *
     * @param id 图片管理主键
     * @return 图片管理详情
     */
    @GetMapping("getInfo/{id}")
    public Picture getInfo(@PathVariable Long id) {
        return pictureService.getById(id);
    }

    /**
     * 分页查询图片管理。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Picture> page(Page<Picture> page) {
        return pictureService.page(page);
    }

}

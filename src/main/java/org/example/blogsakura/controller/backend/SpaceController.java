package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.service.SpaceService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 空间 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/space")
public class SpaceController {

    @Resource
    private SpaceService spaceService;

    /**
     * 保存空间。
     *
     * @param space 空间
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Space space) {
        return spaceService.save(space);
    }

    /**
     * 根据主键删除空间。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return spaceService.removeById(id);
    }

    /**
     * 根据主键更新空间。
     *
     * @param space 空间
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Space space) {
        return spaceService.updateById(space);
    }

    /**
     * 查询所有空间。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Space> list() {
        return spaceService.list();
    }

    /**
     * 根据主键获取空间。
     *
     * @param id 空间主键
     * @return 空间详情
     */
    @GetMapping("getInfo/{id}")
    public Space getInfo(@PathVariable Long id) {
        return spaceService.getById(id);
    }

    /**
     * 分页查询空间。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Space> page(Page<Space> page) {
        return spaceService.page(page);
    }

}

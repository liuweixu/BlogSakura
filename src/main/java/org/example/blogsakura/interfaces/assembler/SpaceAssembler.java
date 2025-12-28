package org.example.blogsakura.interfaces.assembler;

import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.interfaces.dto.space.SpaceAddRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

public class SpaceAssembler {

    public static Space toSpaceEntity(SpaceAddRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceUpdateRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }
}

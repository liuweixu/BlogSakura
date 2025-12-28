package org.example.blogsakura.interfaces.assembler;

import org.example.blogsakura.domain.space.entity.SpaceUser;
import org.example.blogsakura.interfaces.dto.space.SpaceUserAddRequest;
import org.example.blogsakura.interfaces.dto.space.SpaceUserEditRequest;
import org.springframework.beans.BeanUtils;

public class SpaceUserAssembler {

    public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }

    public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }
}

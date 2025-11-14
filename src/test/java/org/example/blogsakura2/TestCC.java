package org.example.blogsakura2;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.BlogsakuraApplication;
import org.example.blogsakura.manager.CosManager;
import org.example.blogsakura.manager.PictureMessage;
import org.example.blogsakura.manager.auth.SpaceUserAuthManager;
import org.example.blogsakura.manager.auth.model.SpaceUserRole;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.model.dto.picture.UploadPictureResult;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
@ContextConfiguration(classes = {BlogsakuraApplication.class})
public class TestCC {

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @Test
    void test() throws IOException {

        log.info("spaceUserAuthManager:{}", spaceUserAuthManager.getPermissionsByRole("admin"));
    }
}

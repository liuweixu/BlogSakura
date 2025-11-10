package org.example.blogsakura2;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.BlogsakuraApplication;
import org.example.blogsakura.manager.CosManager;
import org.example.blogsakura.manager.PictureMessage;
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
    private PictureMessage pictureMessage;

    @Resource
    private CosManager cosManager;

    @Test
    void test() throws IOException {
        String url = "https://www.codefather.cn/logo.png";
        UploadPictureResult uploadPictureResult = cosManager.uploadPictureByUrl(url, "/test");
        log.info("uploadPictureResult:{}", uploadPictureResult.toString());
    }
}

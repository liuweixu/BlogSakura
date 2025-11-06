package org.example.blogsakura2;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.manager.PictureMessage;
import org.example.blogsakura.model.dto.picture.Picture;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
@Slf4j
public class TestCC {


    @Resource
    private PictureMessage pictureMessage;

    @Test
    void test() throws IOException {
        String imageurl = "src/main/resources/3151645a-7b66-494a-b0a1-b3aa41739a8e.png";
        File file = new File(imageurl);
        Picture picture = pictureMessage.getPicture(file);
        log.info(picture.toString());
    }
}

package org.example.blogsakura2;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.BlogsakuraApplication;
import org.example.blogsakura.manager.auth.SpaceUserAuthManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

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

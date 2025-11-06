package org.example.blogsakura2;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.example.blogsakura2.controller.ArticleController;
import org.example.blogsakura2.controller.UserController;
import org.example.blogsakura2.mapper.ArticleMapper;
import org.example.blogsakura2.pojo.Article;
import org.example.blogsakura2.pojo.User;
import org.example.blogsakura2.service.ViewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.*;

@SpringBootTest
public class BlogsakuraApplicationTests {

    @Autowired
    private UserController userController;

    @Autowired
    private ApplicationContext applicationContext; // IOC对象


    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ArticleMapper articleMapper;


    @Test
    public void contextLoads() {
        User user = new User();
        user.setMobile("18612345677");
        user.setCode("246810");

        System.out.println(user);
    }

//    @TestCC
//    public void jdbcTest() throws Exception {
//        //1. 注册驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");
//
//        //2. 获取数据库连接
//        String url = "jdbc:mysql://127.0.0.1:3306/blog";
//        String username = "root";
//        String password = "12345678";
//        Connection connection = DriverManager.getConnection(url, username, password);
//
//        //3. 执行SQL
//        Statement statement = connection.createStatement();
//        String sql = "select * from article";
//        ResultSet resultSet = statement.executeQuery(sql);
//
//        List<ArticleVO> objects = new ArrayList<>();
//        //4. 处理SQL执行结果
//        while (resultSet.next()) {
//            String id = resultSet.getString("id");
//            String title = resultSet.getString("title");
//            String content = resultSet.getString("content");
//            Long channelId = resultSet.getLong("channel_id");
//            Integer imageType = resultSet.getInt("image_type");
//            String imageUrl = resultSet.getString("image_url");
//            String publishDate = resultSet.getString("publish_date");
//            String editDate = resultSet.getString("edit_date");
//            ArticleVO article = new ArticleVO(id, title, content, channelId, imageType, imageUrl, publishDate, editDate);
//            objects.add(article);
//        }
//
//        //5. 释放资源
//        statement.close();
//        connection.close();
//        resultSet.close();
//
//        //遍历集合
//        objects.forEach(System.out::println);
//    }

    @Test
    public void test() throws Exception {
        Class<?> classz = Class.forName("org.example.blogsakura2.BlogsakuraApplicationTests");
        Method contextLoads = classz.getMethod("contextLoads");
        contextLoads.invoke(classz.getDeclaredConstructor().newInstance());
    }


    @Test
    void testGetBean() {
        ArticleController controller = applicationContext.getBean(ArticleController.class);
        System.out.println(controller);

        ArticleController articleController = (ArticleController) applicationContext.getBean("articleController");
        System.out.println(articleController);
    }

    @Test
    void testTransisent() {
        User user = new User();
        user.setMobile("18612345677");
        user.setCode("246810");
        Gson gson = new Gson();
        String json = gson.toJson(user);
        System.out.println(json);
    }

    @Test
    void testRedis() {
        System.out.println(redisTemplate.opsForValue().get("2"));
    }

    @Resource
    private ViewService viewService;

    @Test
    public void testConcurrentUpdateViews() throws InterruptedException {
        String articleId = "1654321123456789037";
        redisTemplate.opsForValue().set(articleId, "198");

        int threadCount = 1720; // 模拟20个用户同时刷新阅读数
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount * 2,
                threadCount * 2 + 1,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000));
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    Long current = viewService.getViews(articleId);
                    viewService.updateViews(articleId, current);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        countDownLatch.await();
        executor.shutdown();


        Thread.sleep(3000);

        Article article = articleMapper.getArticleById(articleId);
        long dbResult = article.getView();
        String redisResult = redisTemplate.opsForValue().get(articleId);
        System.out.println("Redis 最终值：" + redisResult);
        System.out.println("数据库 最终值：" + dbResult);
    }

    @Test
    public void testConcurrentReadWrite() throws InterruptedException {
        String articleId = "761067900055326720";
        redisTemplate.opsForValue().set(articleId, "100");

        int readers = 100;
        int writers = 5;
        CountDownLatch latch = new CountDownLatch(readers + writers);
        ExecutorService executor = Executors.newFixedThreadPool(100);

        // 模拟多个读线程
        for (int i = 0; i < readers; i++) {
            executor.submit(() -> {
                try {
                    Long v = viewService.getViews(articleId);
                    System.out.println(Thread.currentThread().getName() + " 读：" + v);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 模拟多个写线程
        for (int i = 0; i < writers; i++) {
            executor.submit(() -> {
                try {
                    Long current = Long.valueOf(redisTemplate.opsForValue().get(articleId));
                    Long newView = current + 10;
                    viewService.updateViews(articleId, newView);
                    System.out.println(Thread.currentThread().getName() + " 写：" + newView);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("最终 Redis：" + redisTemplate.opsForValue().get(articleId));
        System.out.println("最终 DB：" + viewService.getViews(articleId));
    }

    @Test
    void testLua() {
        String luascipts = "local key = KEYS[1]\n" +
                "local current = redis.call(\"GET\", key)\n" +
                "if current == false then\n" +
                "    redis.call(\"SET\", key, 1)\n" +
                "else\n" +
                "    redis.call(\"INCR\", key)\n" +
                "    return tonumber(current) + 1\n" +
                "end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luascipts, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList("number"));
        System.out.println(result);
    }

}

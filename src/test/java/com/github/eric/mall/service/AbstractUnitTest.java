package com.github.eric.mall.service;

import com.github.eric.mall.MallApplication;
import com.github.eric.mall.vo.CartProductVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MallApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public abstract class AbstractUnitTest {
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @BeforeEach
    public void initDatabase() {
        // 在每个测试开始前，执行一次flyway:clean flyway:migrate
//        ClassicConfiguration conf = new ClassicConfiguration();
//        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
//        Flyway flyway = new Flyway(conf);
//        flyway.clean();
//        flyway.migrate();
        // 注册
    }


    public <T,P> void verifyData(List<P> cartProductVoList, List<P> cartProductVoListInDb, Function<? super P, T> function) {
        List<T> fields = cartProductVoList.stream().map(function).collect(Collectors.toList());
        List<T> inDbFields = cartProductVoListInDb.stream().map(function).collect(Collectors.toList());

        Assertions.assertEquals(fields.size(),inDbFields.size());
        fields.removeAll(inDbFields);
        Assertions.assertEquals(0,fields.size());
    }
}

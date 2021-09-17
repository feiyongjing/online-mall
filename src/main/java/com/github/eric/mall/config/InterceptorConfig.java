package com.github.eric.mall.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/error",
                        "/user/login",
                        "/user/register");
    }

    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("reasonable", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
//        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }


    @Bean(name = "dirtctQueue")
    public Queue dirtctQueue() {
        return new Queue("payNotify", true, false, false);
    }

    @Bean(name = "test-1")
    public Queue queue() {
        return new Queue("test1", true, false, false);
    }


    @Bean(name = "dirtctExchange")
    public DirectExchange dirtctExchange() {
        return new DirectExchange("dirtctExchange");
    }

    @Bean
    public Binding confirmTestFanoutExchangeAndQueue(
            @Qualifier("dirtctExchange") DirectExchange confirmTestExchange,
            @Qualifier("dirtctQueue") Queue confirmTestQueue) {

        return BindingBuilder.bind(confirmTestQueue).to(confirmTestExchange).with("payNotify");
    }
//    /**
//     * description 配置事务管理器
//     **/
//    @Bean
//    public PlatformTransactionManager transactionManager(DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
}

package com.github.eric.mall.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.github.eric.onlinemallpay.config.RabbitmqConfig.*;

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
    @Bean
    DirectExchange exchange() {
        return new DirectExchange(TEST_EXCHANGE_HAHA);
    }

    @Bean(PAY_NOTIFY_QUEUE)
    public Queue queue1() {
        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange",TEST_EXCHANGE_DEAD);
        map.put("x-dead-letter-routing-key",TEST_ROUTING_KEY_DEAD);
        return QueueBuilder.durable(PAY_NOTIFY_QUEUE).withArguments(map).build();
    }

    @Bean(PAY_NOTIFY_DEAD_QUEUE)
    public Queue queue2() {
        return QueueBuilder.durable(PAY_NOTIFY_DEAD_QUEUE).build();
    }

    @Bean
    Binding bindingExchangeMessages(@Qualifier(PAY_NOTIFY_QUEUE) Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(TEST_ROUTING_KEY_HAHA);
    }

    @Bean
    Binding bindingExchangeMessage(@Qualifier(PAY_NOTIFY_DEAD_QUEUE) Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(TEST_ROUTING_KEY_DEAD);
    }

//    /**
//     * description 配置事务管理器
//     **/
//    @Bean
//    public PlatformTransactionManager transactionManager(DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
}

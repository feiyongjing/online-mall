package com.github.eric.mall.config;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.exception.UserLoginException;
import com.github.eric.mall.generate.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class UserLoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("当前参数" + request.getQueryString());

        User user = (User) request.getSession().getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            throw new UserLoginException();
        }
        return true;
    }
}
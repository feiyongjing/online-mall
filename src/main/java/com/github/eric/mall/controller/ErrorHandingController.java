package com.github.eric.mall.controller;


import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.UserLoginException;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ErrorHandingController {
    @ExceptionHandler(UserLoginException.class)
    public @ResponseBody
    ResponseVo<User> onError(HttpServletResponse response) {
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }
}
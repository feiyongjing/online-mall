package com.github.eric.mall.controller;


import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.UserLoginException;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import static com.github.eric.mall.enums.ResponseEnum.*;
import static com.github.eric.mall.enums.ResponseEnum.PARAM_ERROR;

@ControllerAdvice
public class ErrorHandingController {
    @ExceptionHandler(Exception.class)
    public @ResponseBody
    ResponseVo<User> onError(HttpServletResponse response,Exception e) {
        return ResponseVo.error(ERROR,e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public @ResponseBody
    ResponseVo<User> onRuntimeException(HttpServletResponse response,RuntimeException e) {
        return ResponseVo.error(ERROR,e.getMessage());
    }

    @ExceptionHandler(UserLoginException.class)
    public @ResponseBody
    ResponseVo<User> UserLoginHandle(HttpServletResponse response) {
        return ResponseVo.error(NEED_LOGIN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody
    ResponseVo<User> UserLoginHandle(HttpServletResponse response, MethodArgumentNotValidException e) {
        return ResponseVo.error(PARAM_ERROR,e.getBindingResult());
    }
}
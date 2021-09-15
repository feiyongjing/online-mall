package com.github.eric.mall.service;

import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.ResultException;
import com.github.eric.mall.form.UserForm;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.vo.ResponseVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class UserServiceTest extends AbstractUnitTest{

    public static final String USERNAME="aaa";
    public static final String PASSWORD="111";
    public static final String EMAIL="@qq.com";
    public static final Integer ROLE=1;

    @Autowired
    UserService userService;

    @BeforeEach
    public void registerTest(){
        ResponseVo<String> responseVo = userService.register(new UserForm(USERNAME, PASSWORD, EMAIL, ROLE));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
        Assertions.assertEquals(ResponseEnum.USER_REGISTER_SUCCESS.getDesc(),responseVo.getData());
    }


    @Test
    public void checkDataNumber() {
        verifyException(ResultException.class,ResponseEnum.USERNAME_EXIST.getDesc()
                ,() -> userService.checkDataNumber(1,0,ResponseEnum.USERNAME_EXIST));
        verifyException(ResultException.class,ResponseEnum.EMAIL_EXIST.getDesc()
                ,() -> userService.checkDataNumber(1,0,ResponseEnum.EMAIL_EXIST));
        verifyException(ResultException.class,ResponseEnum.USER_REGISTER_FAIL.getDesc()
                ,() -> userService.checkDataNumber(0,1,ResponseEnum.USER_REGISTER_FAIL));
    }

    @Test
    public void loginTestSuccess(){
        ResponseVo<User> responseVo = userService.login("aaa", "111");
        User user = responseVo.getData();
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
        Assertions.assertEquals(USERNAME,user.getUsername());
        Assertions.assertEquals("",user.getPassword());
        Assertions.assertEquals(EMAIL,user.getEmail());
        Assertions.assertEquals(ROLE,user.getRole());
    }
    @Test
    public void loginTestFailure(){
        verifyException(ResultException.class,ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getDesc()
                ,() -> userService.login("bbb", "111"));
        verifyException(ResultException.class,ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getDesc()
                ,() -> userService.login("aaa", "222"));
    }



}
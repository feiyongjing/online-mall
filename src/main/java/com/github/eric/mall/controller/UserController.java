package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.UserLoginException;
import com.github.eric.mall.form.UserForm;
import com.github.eric.mall.form.UserLoginForm;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.service.UserService;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public ResponseVo<String> register(@Valid @RequestBody UserForm userForm){
        return userService.register(userForm);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpSession session){
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        session.setAttribute(OnlineMallConst.CURRENT_USER,userResponseVo.getData());
        return userResponseVo;
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseVo<User> userInfo(HttpSession session){
        User user= (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        return ResponseVo.success(user);
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseVo<User> logout(HttpSession session){
        session.removeAttribute(OnlineMallConst.CURRENT_USER);
        return ResponseVo.successByMsg("退出登录成功");
    }
}

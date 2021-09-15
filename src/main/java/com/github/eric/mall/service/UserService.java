package com.github.eric.mall.service;

import com.github.eric.mall.dao.MyUserMapper;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.enums.RoleEnum;
import com.github.eric.mall.exception.ResultException;
import com.github.eric.mall.form.UserForm;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.generate.entity.UserExample;
import com.github.eric.mall.generate.mapper.UserMapper;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MyUserMapper myUserMapper;

    public ResponseVo<String> register(UserForm userForm) {
        UserExample userExample=new UserExample();
        userExample.createCriteria().andUsernameEqualTo(userForm.getUsername());
        long sameNameCount = userMapper.countByExample(userExample);
        checkDataNumber(sameNameCount, 0, ResponseEnum.USERNAME_EXIST);

        userExample.clear();
        userExample.createCriteria().andEmailEqualTo(userForm.getEmail());
        long sameEmailCount = userMapper.countByExample(userExample);
        checkDataNumber(sameEmailCount, 0, ResponseEnum.EMAIL_EXIST);

        userForm.setPassword(DigestUtils.md5DigestAsHex(userForm.getPassword().getBytes(StandardCharsets.UTF_8)));
        userForm.setRole(RoleEnum.CUSTOMER.getCode());
        User user=new User();
        BeanUtils.copyProperties(userForm,user);

        int resultCount = userMapper.insertSelective(user);
        checkDataNumber(resultCount, 1, ResponseEnum.USER_REGISTER_FAIL);

        return ResponseVo.success(ResponseEnum.USER_REGISTER_SUCCESS.getDesc());
    }

    public void checkDataNumber(long expect, int actual, ResponseEnum usernameExist) {
        if (expect != actual) {
            throw new ResultException(usernameExist.getDesc());
        }
    }

    public ResponseVo<User> login(String username, String password) {
        User user = myUserMapper.getUserByUsername(username);
        if(user==null || !DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8))
                .equalsIgnoreCase(user.getPassword())){
            throw new ResultException(ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        }
        user.setPassword("");
        return ResponseVo.success(user);
    }
}

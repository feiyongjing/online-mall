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

    public ResponseVo<User> register(UserForm userForm) {
        UserExample userExample=new UserExample();
        userExample.createCriteria().andUsernameEqualTo(userForm.getUsername());
        long sameNameCount = userMapper.countByExample(userExample);
        if(sameNameCount!=0){
            throw new ResultException(ResponseEnum.USERNAME_EXIST.getDesc());
        }

        userExample.clear();
        userExample.createCriteria().andEmailEqualTo(userForm.getEmail());
        long sameEmailCount = userMapper.countByExample(userExample);
        if(sameEmailCount!=0){
            throw new ResultException(ResponseEnum.EMAIL_EXIST.getDesc());
        }

        userForm.setPassword(DigestUtils.md5DigestAsHex(userForm.getPassword().getBytes(StandardCharsets.UTF_8)));
        userForm.setRole(RoleEnum.CUSTOMER.getCode());
        User user=new User();
        BeanUtils.copyProperties(userForm,user);

        int resultCount = userMapper.insertSelective(user);
        if(resultCount!=1){
            throw new ResultException("注册失败");
        }

        return ResponseVo.success();
    }

    public ResponseVo<User> login(String username, String password) {
        User user = myUserMapper.getUserByUsername(username);
        if(user==null || !DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8))
                .equalsIgnoreCase(user.getPassword())){
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVo.success(user);
    }
}

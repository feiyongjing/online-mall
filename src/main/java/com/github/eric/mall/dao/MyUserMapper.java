package com.github.eric.mall.dao;

import com.github.eric.mall.generate.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyUserMapper {

    User getUserByUsername(String username);
}

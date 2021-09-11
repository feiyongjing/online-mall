package com.github.eric.mall.dao;

import com.github.eric.mall.generate.entity.Shipping;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyShippingMapper {
    Shipping selectByIdAndUserId(Integer id,Integer userId);
}

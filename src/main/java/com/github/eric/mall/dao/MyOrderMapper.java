package com.github.eric.mall.dao;

import com.github.eric.mall.generate.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyOrderMapper {
    Order getByOrderNo(Long orderNo);

    int updateOrderStatusByOrderNo(Long orderNo, Integer status);
}

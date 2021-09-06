package com.github.eric.mall.dao;

import com.github.eric.mall.generate.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyOrderItemMapper {

    int insertOrderItemList(@Param("orderItemList") List<OrderItem> orderItemList);
}

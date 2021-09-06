package com.github.eric.mall.dao;

import com.github.eric.mall.generate.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyProductMapper {
    int batchUpdateByPrimaryKeys(@Param("productList") List<Product> productList);
}

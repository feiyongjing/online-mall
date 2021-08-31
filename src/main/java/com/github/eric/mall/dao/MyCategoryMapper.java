package com.github.eric.mall.dao;

import com.github.eric.mall.generate.entity.Category;
import com.github.eric.mall.vo.CategoryVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyCategoryMapper {
    List<Category> selectAll();
}

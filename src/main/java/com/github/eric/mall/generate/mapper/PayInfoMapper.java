package com.github.eric.mall.generate.mapper;

import com.github.eric.mall.generate.entity.PayInfo;
import com.github.eric.mall.generate.entity.PayInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayInfoMapper {
    long countByExample(PayInfoExample example);

    int deleteByExample(PayInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    List<PayInfo> selectByExample(PayInfoExample example);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PayInfo record, @Param("example") PayInfoExample example);

    int updateByExample(@Param("record") PayInfo record, @Param("example") PayInfoExample example);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);
}
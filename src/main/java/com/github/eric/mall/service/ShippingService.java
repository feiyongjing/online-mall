package com.github.eric.mall.service;

import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.ShippingForm;
import com.github.eric.mall.generate.entity.Shipping;
import com.github.eric.mall.generate.entity.ShippingExample;
import com.github.eric.mall.generate.mapper.ShippingMapper;
import com.github.eric.mall.vo.ResponseVo;
import com.github.eric.mall.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ShippingService {

    @Autowired
    ShippingMapper shippingMapper;


    public ResponseVo<ShippingVo> addShipping(ShippingForm shippingForm, Integer userId) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setUserId(userId);

        int row = shippingMapper.insertSelective(shipping);
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.ERROR, "新建地址失败");
        }
        return ResponseVo.success(new ShippingVo(shipping.getId()));
    }

    public ResponseVo<String> deleteByShippingId(Integer shippingId, Integer userId) {
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null || !Objects.equals(shipping.getUserId(), userId)) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }
        int row = shippingMapper.deleteByPrimaryKey(shippingId);
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.success("删除地址成功");
    }

    public ResponseVo<String> updateByShippingId(ShippingForm shippingForm, Integer shippingId, Integer userId) {
        Shipping shippingInDb = shippingMapper.selectByPrimaryKey(shippingId);
        if (shippingInDb == null || !Objects.equals(shippingInDb.getUserId(), userId)) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setId(shippingInDb.getId());
        shipping.setUserId(shippingInDb.getUserId());

        int i = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (i == 0) {
            return ResponseVo.error(ResponseEnum.ERROR, "修改地址失败");
        }
        return ResponseVo.success("修改地址成功");
    }

    public ResponseVo<PageInfo<Shipping>> getShippingList(Integer pageNum, Integer pageSize, Integer userId) {
        ShippingExample shippingExample=new ShippingExample();
        shippingExample.createCriteria().andUserIdEqualTo(userId);

        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings=shippingMapper.selectByExample(shippingExample);

        PageInfo<Shipping> pageInfo = new PageInfo<>(shippings);
        return ResponseVo.success(pageInfo);
    }
}

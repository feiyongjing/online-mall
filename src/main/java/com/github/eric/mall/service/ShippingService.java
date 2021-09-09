package com.github.eric.mall.service;

import com.github.eric.mall.dao.MyShippingMapper;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.ResultException;
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

@Service
public class ShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    @Autowired
    MyShippingMapper myShippingMapper;


    public ResponseVo<ShippingVo> addShipping(ShippingForm shippingForm, Integer userId) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setUserId(userId);

        checkDatabaseUpdateOperations(shippingMapper.insertSelective(shipping),"新建地址失败");
        return ResponseVo.success(new ShippingVo(shipping.getId()));
    }

    public void checkDatabaseUpdateOperations(int row, String msg){
        if (row == 0) {
            throw new ResultException(msg);
        }
    }

    public ResponseVo<String> deleteByShippingId(Integer shippingId, Integer userId) {
        Shipping shipping = getShippingByIdAndUserId(shippingId, userId);
        checkDatabaseUpdateOperations(shippingMapper.deleteByPrimaryKey(shipping.getId()),ResponseEnum.DELETE_SHIPPING_FAIL.getDesc());
        return ResponseVo.success("删除地址成功");
    }

    public ResponseVo<String> updateByShippingId(ShippingForm shippingForm, Integer shippingId, Integer userId) {
        Shipping shippingInDb = getShippingByIdAndUserId(shippingId, userId);

        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setId(shippingInDb.getId());
        shipping.setUserId(shippingInDb.getUserId());

        checkDatabaseUpdateOperations(shippingMapper.updateByPrimaryKeySelective(shipping),"修改地址失败");
        return ResponseVo.success("修改地址成功");
    }

    public Shipping getShippingByIdAndUserId(Integer shippingId, Integer userId) {
        Shipping shippingInDb = myShippingMapper.selectByIdAnduserId(shippingId, userId);
        if (shippingInDb == null) {
            throw new ResultException(ResponseEnum.SHIPPING_NOT_EXIST.getDesc());
        }
        return shippingInDb;
    }

    public ResponseVo<PageInfo<Shipping>> getShippingList(Integer pageNum, Integer pageSize, Integer userId) {
        ShippingExample shippingExample=new ShippingExample();
        shippingExample.createCriteria().andUserIdEqualTo(userId);

        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings=shippingMapper.selectByExample(shippingExample);

        PageInfo<Shipping> pageInfo = new PageInfo<>(shippings);
        return ResponseVo.success(pageInfo);
    }


    public List<Shipping> getShippingListByIdsAndUser(List<Integer> ids,Integer userId) {
        ShippingExample shippingExample=new ShippingExample();
        shippingExample.createCriteria().andIdIn(ids).andUserIdEqualTo(userId);
        List<Shipping> shippingList=shippingMapper.selectByExample(shippingExample);
        if (shippingList.isEmpty()) {
            throw new ResultException(ResponseEnum.SHIPPING_NOT_EXIST.getDesc());
        }
        return shippingList;
    }
}

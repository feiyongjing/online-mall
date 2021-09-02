package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.ShippingForm;
import com.github.eric.mall.generate.entity.Shipping;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.service.ShippingService;
import com.github.eric.mall.vo.ResponseVo;
import com.github.eric.mall.vo.ShippingVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    ShippingService shippingService;


    @PostMapping("/add")
    @ResponseBody
    public ResponseVo<ShippingVo> addShipping(@RequestBody ShippingForm shippingForm, HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return shippingService.addShipping(shippingForm, user.getId());
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseVo<String> deleteByShippingId(@RequestParam("shippingId") Integer shippingId, HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return shippingService.deleteByShippingId(shippingId, user.getId());
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseVo<String> updateByShippingId(@RequestBody ShippingForm shippingForm,
                                                     @RequestParam("shippingId") Integer shippingId,
                                                     HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return shippingService.updateByShippingId(shippingForm,shippingId,user.getId());
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseVo<PageInfo<Shipping>> getShippingList(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                          HttpSession session) {
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return shippingService.getShippingList(pageNum,pageSize,user.getId());
    }
}

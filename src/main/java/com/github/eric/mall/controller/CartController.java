package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.CartAddForm;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.service.CartService;
import com.github.eric.mall.vo.CartVo;
import com.github.eric.mall.vo.ProductDetailVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/list")
    @ResponseBody
    public ResponseVo<CartVo> getCartProductList(HttpSession session) {
        User user= (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if(user==null){
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return cartService.getCartProductList(user.getId());
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseVo<CartVo> addCartProduct(@RequestBody CartAddForm cartAddForm, HttpSession session) {
        User user= (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if(user==null){
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return ResponseVo.success(cartService.addCartProduct(cartAddForm,user.getId()));
    }
}

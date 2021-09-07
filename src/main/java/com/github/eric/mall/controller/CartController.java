package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.CartAddForm;
import com.github.eric.mall.form.CartUpdateForm;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.service.CartService;
import com.github.eric.mall.vo.CartVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/list")
    @ResponseBody
    public ResponseVo<CartVo> getCartProductList(HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return ResponseVo.success(cartService.getCartProductList(user.getId()));
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseVo<CartVo> addCartProduct(@Valid @RequestBody CartAddForm cartAddForm, HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return ResponseVo.success(cartService.addCartProduct(cartAddForm, user.getId()));
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseVo<CartVo> updateCartProduct(@RequestParam("productId") Integer productId, @RequestBody CartUpdateForm cartUpdateForm, HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return ResponseVo.success(cartService.updateCartProduct(productId, cartUpdateForm, user.getId()));
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseVo<CartVo> deleteCartProduct(@RequestParam("productId") Integer productId, HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return ResponseVo.success(cartService.deleteCartProduct(productId, user.getId()));
    }

    @PostMapping("/selectAll")
    @ResponseBody
    public ResponseVo<CartVo> selectAllCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        boolean selectAll=true;
        return ResponseVo.success(cartService.isSelectAllCartProduct(user.getId(),selectAll));
    }
    @PostMapping("/unSelectAll")
    @ResponseBody
    public ResponseVo<CartVo> onSelectAllCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        boolean selectAll=false;
        return ResponseVo.success(cartService.isSelectAllCartProduct(user.getId(),selectAll));
    }
    @GetMapping("/sum")
    @ResponseBody
    public ResponseVo<Integer> getCartProductSum(HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return ResponseVo.success(cartService.getCartProductSum(user.getId()));
    }


}

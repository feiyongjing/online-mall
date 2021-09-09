package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.generate.entity.Product;
import com.github.eric.mall.service.ProductService;
import com.github.eric.mall.vo.ProductDetailVo;
import com.github.eric.mall.vo.ProductVo;
import com.github.eric.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/page")
    @ResponseBody
    public ResponseVo<PageInfo<ProductVo>> getProductByCategoryId(
            @RequestParam(value = "categoryId", defaultValue =OnlineMallConst.ROOT_PARENT_ID) Integer categoryId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return productService.getProductByCategoryId(categoryId,pageNum,pageSize);
    }

    @GetMapping("/id")
    @ResponseBody
    public ResponseVo<ProductDetailVo> getProductById(@RequestParam(value = "productId") Integer productId){
        return ResponseVo.success(productService.getProductById(productId));
    }

}

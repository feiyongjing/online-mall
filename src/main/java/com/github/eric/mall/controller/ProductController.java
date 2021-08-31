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
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        if(categoryId==null){
            categoryId= OnlineMallConst.ROOT_PARENT_ID;
        }
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        return productService.getProductByCategoryId(categoryId,pageNum,pageSize);
    }

    @GetMapping("/id")
    @ResponseBody
    public ResponseVo<ProductDetailVo> getProductById(@RequestParam(value = "productId") Integer productId){
        return productService.getProductById(productId);
    }

}

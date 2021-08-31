package com.github.eric.mall.controller;


import com.github.eric.mall.service.CategotyService;
import com.github.eric.mall.vo.CategoryVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategotyService categotyService;

    @GetMapping("/all")
    @ResponseBody
    public ResponseVo<List<CategoryVo>> getCategoryAll(){
        return categotyService.selectAll();
    }
}

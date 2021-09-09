package com.github.eric.mall.service;

import com.github.eric.mall.dao.MyProductMapper;
import com.github.eric.mall.enums.ProductStatusEnum;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.ResultException;
import com.github.eric.mall.generate.entity.Product;
import com.github.eric.mall.generate.entity.ProductExample;
import com.github.eric.mall.generate.mapper.ProductMapper;
import com.github.eric.mall.vo.ProductDetailVo;
import com.github.eric.mall.vo.ProductVo;
import com.github.eric.mall.vo.ResponseVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private CategotyService categotyService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    MyProductMapper myProductMapper;

    public Map<Integer, Product> findByIdIn(List<Integer> productIds) {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andIdIn(productIds);
        List<Product> products = productMapper.selectByExample(productExample);
        return products.stream().collect(Collectors.toMap(Product::getId, product -> product));
    }

    public ResponseVo<PageInfo<ProductVo>> getProductByCategoryId(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = categotyService.getCategoryIdSet(categoryId);
        return ResponseVo.success(getProductByCategoryId(categoryIdSet, pageNum, pageSize));

    }

    private PageInfo<ProductVo> getProductByCategoryId(Set<Integer> categoryIdSet, Integer pageNum, Integer pageSize) {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria()
                .andStatusEqualTo(ProductStatusEnum.ON_SALE.getCode())
                .andCategoryIdIn(new ArrayList<>(categoryIdSet));

        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectByExample(productExample);


        List<ProductVo> productVos = new ArrayList<>();
        products.forEach(product -> {
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(product, productVo);
            productVos.add(productVo);
        });

        PageInfo pageInfo = new PageInfo<>(products);
        pageInfo.setList(productVos);
//        PageInfo<ProductVo> pageInfo=new PageInfo<>(productVos);
        return pageInfo;

    }


    public ProductDetailVo getProductById(Integer productId) {
        Product productInDb = productMapper.selectByPrimaryKey(productId);
        if (productInDb == null
                || productInDb.getStatus().equals(ProductStatusEnum.OFF_SALE.getCode())
                || productInDb.getStatus().equals(ProductStatusEnum.DELETE.getCode())) {
            throw new ResultException(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc());
        }
        ProductDetailVo result = new ProductDetailVo();
        BeanUtils.copyProperties(productInDb, result);
        return result;
    }

    public int batchUpdateByPrimaryKeys(List<Product> productList) {
        return myProductMapper.batchUpdateByPrimaryKeys(productList);
    }
}

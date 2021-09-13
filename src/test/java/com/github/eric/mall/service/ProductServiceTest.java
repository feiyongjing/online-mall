package com.github.eric.mall.service;

import com.github.eric.mall.enums.ProductStatusEnum;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.generate.entity.Product;
import com.github.eric.mall.vo.ProductDetailVo;
import com.github.eric.mall.vo.ProductVo;
import com.github.eric.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Transactional
class ProductServiceTest extends AbstractUnitTest{
    public static final Map<Integer, Product> PRODUCT_ID_AND_PRODUCT;
    public static Integer CATEGORY_ID = 100002;
    public static Integer PAGE_NUM = 1;
    public static Integer PAGE_SIZE = 10;

    static {
        PRODUCT_ID_AND_PRODUCT = new HashMap<>();
        PRODUCT_ID_AND_PRODUCT.put(26, buildProduct(26, 100002, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "iPhone 7，现更以红色呈现。", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", "241997c4-9e62-4824-b7f0-7425c3c28917.jpeg,b6c56eb0-1748-49a9-98dc-bcc4b9788a54.jpeg,92f17532-1527-4563-aa1d-ed01baa0f7b2.jpeg,3adbe4f7-e374-4533-aa79-cc4a98c529bf.jpeg", "<p><img alt=\"10000.jpg\" src=\"http://img.springboot.cn/00bce8d4-e9af-4c8d-b205-e6c75c7e252b.jpg\" width=\"790\" height=\"553\"><br></p><p><img alt=\"20000.jpg\" src=\"http://img.springboot.cn/4a70b4b4-01ee-46af-9468-31e67d0995b8.jpg\" width=\"790\" height=\"525\"><br></p><p><img alt=\"30000.jpg\" src=\"http://img.springboot.cn/0570e033-12d7-49b2-88f3-7a5d84157223.jpg\" width=\"790\" height=\"365\"><br></p><p><img alt=\"40000.jpg\" src=\"http://img.springboot.cn/50515c02-3255-44b9-a829-9e141a28c08a.jpg\" width=\"790\" height=\"525\"><br></p><p><img alt=\"50000.jpg\" src=\"http://img.springboot.cn/c138fc56-5843-4287-a029-91cf3732d034.jpg\" width=\"790\" height=\"525\"><br></p><p><img alt=\"60000.jpg\" src=\"http://img.springboot.cn/c92d1f8a-9827-453f-9d37-b10a3287e894.jpg\" width=\"790\" height=\"525\"><br></p><p><br></p><p><img alt=\"TB24p51hgFkpuFjSspnXXb4qFXa-1776456424.jpg\" src=\"http://img.springboot.cn/bb1511fc-3483-471f-80e5-c7c81fa5e1dd.jpg\" width=\"790\" height=\"375\"><br></p><p><br></p><p><img alt=\"shouhou.jpg\" src=\"http://img.springboot.cn/698e6fbe-97ea-478b-8170-008ad24030f7.jpg\" width=\"750\" height=\"150\"><br></p><p><img alt=\"999.jpg\" src=\"http://img.springboot.cn/ee276fe6-5d79-45aa-8393-ba1d210f9c89.jpg\" width=\"790\" height=\"351\"><br></p>", BigDecimal.valueOf(6999.00), 96));
        PRODUCT_ID_AND_PRODUCT.put(27, buildProduct(27, 100006, "Midea/美的 BCD-535WKZM(E)冰箱双开门对开门风冷无霜智能电家用", "送品牌烤箱，五一大促", "http://img.springboot.cn/ac3e571d-13ce-4fad-89e8-c92c2eccf536.jpeg", "ac3e571d-13ce-4fad-89e8-c92c2eccf536.jpeg,4bb02f1c-62d5-48cc-b358-97b05af5740d.jpeg,36bdb49c-72ae-4185-9297-78829b54b566.jpeg", "<p><img alt=\"miaoshu.jpg\" src=\"http://img.springboot.cn/9c5c74e6-6615-4aa0-b1fc-c17a1eff6027.jpg\" width=\"790\" height=\"444\"><br></p><p><img alt=\"miaoshu2.jpg\" src=\"http://img.springboot.cn/31dc1a94-f354-48b8-a170-1a1a6de8751b.jpg\" width=\"790\" height=\"1441\"><img alt=\"miaoshu3.jpg\" src=\"http://img.springboot.cn/7862594b-3063-4b52-b7d4-cea980c604e0.jpg\" width=\"790\" height=\"1442\"><img alt=\"miaoshu4.jpg\" src=\"http://img.springboot.cn/9a650563-dc85-44d6-b174-d6960cfb1d6a.jpg\" width=\"790\" height=\"1441\"><br></p>", BigDecimal.valueOf(3299.00), 99));
        PRODUCT_ID_AND_PRODUCT.put(28, buildProduct(28, 100012, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "NOVA青春版1999元", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", "0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg,13da2172-4445-4eb5-a13f-c5d4ede8458c.jpeg,58d5d4b7-58d4-4948-81b6-2bae4f79bf02.jpeg", "<p><img alt=\"11TB2fKK3cl0kpuFjSsziXXa.oVXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/5c2d1c6d-9e09-48ce-bbdb-e833b42ff664.jpg\" width=\"790\" height=\"966\"><img alt=\"22TB2YP3AkEhnpuFjSZFpXXcpuXXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/9a10b877-818f-4a27-b6f7-62887f3fb39d.jpg\" width=\"790\" height=\"1344\"><img alt=\"33TB2Yyshk.hnpuFjSZFpXXcpuXXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/7d7fbd69-a3cb-4efe-8765-423bf8276e3e.jpg\" width=\"790\" height=\"700\"><img alt=\"TB2diyziB8kpuFjSspeXXc7IpXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/1d7160d2-9dba-422f-b2a0-e92847ba6ce9.jpg\" width=\"790\" height=\"393\"><br></p>", BigDecimal.valueOf(1999.00), 100));
        PRODUCT_ID_AND_PRODUCT.put(29, buildProduct(29, 100008, "Haier/海尔HJ100-1HU1 10公斤滚筒洗衣机全自动带烘干家用大容量 洗烘一体", "门店机型 德邦送货", "http://img.springboot.cn/173335a4-5dce-4afd-9f18-a10623724c4e.jpeg", "173335a4-5dce-4afd-9f18-a10623724c4e.jpeg,42b1b8bc-27c7-4ee1-80ab-753d216a1d49.jpeg,2f1b3de1-1eb1-4c18-8ca2-518934931bec.jpeg", "<p><img alt=\"1TB2WLZrcIaK.eBjSspjXXXL.XXa_!!2114960396.jpg\" src=\"http://img.springboot.cn/ffcce953-81bd-463c-acd1-d690b263d6df.jpg\" width=\"790\" height=\"920\"><img alt=\"2TB2zhOFbZCO.eBjSZFzXXaRiVXa_!!2114960396.jpg\" src=\"http://img.springboot.cn/58a7bd25-c3e7-4248-9dba-158ef2a90e70.jpg\" width=\"790\" height=\"1052\"><img alt=\"3TB27mCtb7WM.eBjSZFhXXbdWpXa_!!2114960396.jpg\" src=\"http://img.springboot.cn/2edbe9b3-28be-4a8b-a9c3-82e40703f22f.jpg\" width=\"790\" height=\"820\"><br></p>", BigDecimal.valueOf(4299.00), 100));
    }


    private ProductService productService;

    @Autowired
    public ProductServiceTest(ProductService productService) {
        this.productService = productService;
    }

    public static Product buildProduct(Integer id, Integer categoryId, String name, String subtitle, String mainImage, String subImages, String detail, BigDecimal price, Integer stock) {
        Product result = new Product();
        result.setId(id);
        result.setCategoryId(categoryId);
        result.setName(name);
        result.setSubtitle(subtitle);
        result.setMainImage(mainImage);
        result.setSubImages(subImages);
        result.setDetail(detail);
        result.setPrice(price);
        result.setStock(stock);
        result.setStatus(ProductStatusEnum.ON_SALE.getCode());
        return result;
    }

    @Test
    public void findByIdIn() {
        List<Integer> productIdList = new ArrayList<>(PRODUCT_ID_AND_PRODUCT.keySet());
        List<Product> productList = new ArrayList<>(PRODUCT_ID_AND_PRODUCT.values());
        Map<Integer, Product> productIdAndProduct = productService.findByIdIn(productIdList);
        Assertions.assertEquals(productIdList, new ArrayList<>(productIdAndProduct.keySet()));
        verifyData(productList, productIdAndProduct, Product::getId);
        verifyData(productList, productIdAndProduct, Product::getCategoryId);
        verifyData(productList, productIdAndProduct, Product::getName);
        verifyData(productList, productIdAndProduct, Product::getSubtitle);
        verifyData(productList, productIdAndProduct, Product::getMainImage);
        verifyData(productList, productIdAndProduct, Product::getSubImages);
        verifyData(productList, productIdAndProduct, Product::getDetail);
        verifyData(productList, productIdAndProduct, product -> product.getPrice().setScale(2,ROUND_HALF_UP));
        verifyData(productList, productIdAndProduct, Product::getStock);
        verifyData(productList, productIdAndProduct, Product::getStatus);
    }

    public void verifyData(List<Product> productIdList, Map<Integer, Product> productIdAndProduct, Function<? super Product, ?> function) {
        Assertions.assertEquals(productIdList.stream().map(function).collect(Collectors.toList()),
                productIdAndProduct.values().stream().map(function).collect(Collectors.toList()));
    }

    @Test
    public void getProductByCategoryId() {
        ResponseVo<PageInfo<ProductVo>> responseVo = productService.getProductByCategoryId(CATEGORY_ID, PAGE_NUM, PAGE_SIZE);
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        Assertions.assertEquals(PAGE_NUM, responseVo.getData().getPageNum());
        Assertions.assertEquals(PAGE_SIZE, responseVo.getData().getPageSize());
        Assertions.assertEquals(2, responseVo.getData().getTotal());
        Assertions.assertEquals(Arrays.asList(26, 28), responseVo.getData().getList().stream().map(ProductVo::getId).collect(Collectors.toList()));
    }

    @Test
    public void getProductById() {
        ProductDetailVo productDetailVo = productService.getProductById(29);
        Product product = PRODUCT_ID_AND_PRODUCT.get(29);
        Assertions.assertEquals(product.getId(),productDetailVo.getId());
        Assertions.assertEquals(product.getCategoryId(),productDetailVo.getCategoryId());
        Assertions.assertEquals(product.getName(),productDetailVo.getName());
        Assertions.assertEquals(product.getSubtitle(),productDetailVo.getSubtitle());
        Assertions.assertEquals(product.getMainImage(),productDetailVo.getMainImage());
        Assertions.assertEquals(product.getSubImages(),productDetailVo.getSubImages());
        Assertions.assertEquals(product.getDetail(),productDetailVo.getDetail());
        Assertions.assertEquals(0,product.getPrice().compareTo(productDetailVo.getPrice()));
        Assertions.assertEquals(product.getStock(),productDetailVo.getStock());
        Assertions.assertEquals(product.getStatus(),productDetailVo.getStatus());

    }


    @Test
    public void batchUpdateByPrimaryKeys() {
        Product product1 = PRODUCT_ID_AND_PRODUCT.get(26);
        Product product2 = PRODUCT_ID_AND_PRODUCT.get(27);
        product1.setStock(444);
        product2.setStock(555);
        int row = productService.batchUpdateByPrimaryKeys(Arrays.asList(product1,product2));
        Assertions.assertEquals(2, row);
    }


}
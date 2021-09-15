package com.github.eric.mall.service;

import com.github.eric.mall.enums.ProductStatusEnum;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.ResultException;
import com.github.eric.mall.form.CartAddForm;
import com.github.eric.mall.form.CartUpdateForm;
import com.github.eric.mall.generate.entity.Cart;
import com.github.eric.mall.generate.entity.Product;
import com.github.eric.mall.vo.CartProductVo;
import com.github.eric.mall.vo.CartVo;
import com.github.eric.mall.vo.ProductDetailVo;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class CartServiceTest extends AbstractUnitTest {

    @Autowired
    CartService cartService;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void getCartProductListSuccess() {
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        CartProductVo cartProductVo_1 = new CartProductVo(28, 1, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "NOVA青春版1999元", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", BigDecimal.valueOf(1999.00), 1, BigDecimal.valueOf(1999.00), 100, true);
        CartProductVo cartProductVo_2 = new CartProductVo(26, 1, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "iPhone 7，现更以红色呈现。", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", BigDecimal.valueOf(6999.00), 1, BigDecimal.valueOf(6999.00), 96, true);
        cartProductVoList.add(cartProductVo_1);
        cartProductVoList.add(cartProductVo_2);

        CartVo cartVo_1 = cartService.getCartProductList(2);
        List<CartProductVo> cartProductVoListInDb = cartVo_1.getCartProductVoList();
        assertTrue(cartVo_1.getSelectedAll());
        assertEquals(BigDecimal.valueOf(8998.00).setScale(2, ROUND_HALF_UP), cartVo_1.getCartTotalPrice());
        assertEquals(2, cartVo_1.getCartTotalQuantity());
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductId);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getQuantity);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductName);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSubtitle);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductMainImage);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStatus);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductTotalPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStock);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSelected);

        CartVo cartVo_2 = cartService.getCartProductList(1);
        assertTrue(cartVo_2.getSelectedAll());
        assertEquals(BigDecimal.valueOf(0).setScale(2, ROUND_HALF_UP), cartVo_2.getCartTotalPrice().setScale(2, ROUND_HALF_UP));
        assertEquals(0, cartVo_2.getCartTotalQuantity());
        assertEquals(0,cartVo_2.getCartProductVoList().size());
    }

    @Test
    void getProductAndCartMap() {
        Map<Integer, Cart> productIdAndCartMap = cartService.getProductAndCartMap(2);
        assertEquals(2, productIdAndCartMap.size());
        assertEquals(new Cart(26, 1, true)
                , productIdAndCartMap.get(26));
        assertEquals(new Cart(28, 1, true)
                , productIdAndCartMap.get(28));
    }

    @BeforeEach
    public void addCartProduct_1() {
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        CartProductVo cartProductVo_1 = new CartProductVo(28, 1, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "NOVA青春版1999元", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", BigDecimal.valueOf(1999.00), 1, BigDecimal.valueOf(1999.00), 100, true);
        CartProductVo cartProductVo_2 = new CartProductVo(26, 1, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "iPhone 7，现更以红色呈现。", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", BigDecimal.valueOf(6999.00), 1, BigDecimal.valueOf(6999.00), 96, true);
        cartProductVoList.add(cartProductVo_1);
        cartProductVoList.add(cartProductVo_2);

        cartService.addCartProduct(new CartAddForm(26, true), 2);
        CartVo cartVo = cartService.addCartProduct(new CartAddForm(28, true), 2);
        List<CartProductVo> cartProductVoListInDb = cartVo.getCartProductVoList();
        assertTrue(cartVo.getSelectedAll());
        assertEquals(BigDecimal.valueOf(8998.00).setScale(2, ROUND_HALF_UP), cartVo.getCartTotalPrice());
        assertEquals(2, cartVo.getCartTotalQuantity());
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductId);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getQuantity);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductName);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSubtitle);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductMainImage);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStatus);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductTotalPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStock);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSelected);
    }

    @Test
    public void addCartProduct_2() {
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        CartProductVo cartProductVo_1 = new CartProductVo(28, 1, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "NOVA青春版1999元", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", BigDecimal.valueOf(1999.00), 1, BigDecimal.valueOf(1999.00), 100, true);
        CartProductVo cartProductVo_2 = new CartProductVo(26, 1, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "iPhone 7，现更以红色呈现。", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", BigDecimal.valueOf(6999.00), 1, BigDecimal.valueOf(6999.00), 96, true);
        cartProductVoList.add(cartProductVo_1);
        cartProductVoList.add(cartProductVo_2);

        cartService.addCartProduct(new CartAddForm(26, true), 2);
        CartVo cartVo = cartService.addCartProduct(new CartAddForm(28, true), 2);

        cartProductVo_1.setQuantity(2);
        cartProductVo_1.setProductTotalPrice(cartProductVo_1.getProductTotalPrice().add(cartProductVo_1.getProductPrice()));
        cartProductVo_2.setQuantity(2);
        cartProductVo_2.setProductTotalPrice(cartProductVo_2.getProductTotalPrice().add(cartProductVo_2.getProductPrice()));

        List<CartProductVo> cartProductVoListInDb = cartVo.getCartProductVoList();
        assertTrue(cartVo.getSelectedAll());
        assertEquals(cartProductVo_1.getProductTotalPrice().add(cartProductVo_2.getProductTotalPrice()).setScale(2, ROUND_HALF_UP), cartVo.getCartTotalPrice());
        assertEquals(4, cartVo.getCartTotalQuantity());
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductId);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getQuantity);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductName);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSubtitle);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductMainImage);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStatus);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductTotalPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStock);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSelected);
    }

    @AfterEach
    void clearRedisData(){
        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        redisTemplate.delete(String.format(CartService.CART_REDIS_KEY_TEMPLATE, 2));
    }

    @Test
    void updateCartProductSuccess() {
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        CartProductVo cartProductVo_1 = new CartProductVo(28, 1, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "NOVA青春版1999元", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", BigDecimal.valueOf(1999.00), 1, BigDecimal.valueOf(1999.00), 100, true);
        CartProductVo cartProductVo_2 = new CartProductVo(26, 10, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "iPhone 7，现更以红色呈现。", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", BigDecimal.valueOf(6999.00), 1, BigDecimal.valueOf(69990.00), 96, false);
        cartProductVoList.add(cartProductVo_1);
        cartProductVoList.add(cartProductVo_2);

        CartVo cartVo = cartService.updateCartProduct(26,new CartUpdateForm(10,false),2);
        List<CartProductVo> cartProductVoListInDb = cartVo.getCartProductVoList();
        assertFalse(cartVo.getSelectedAll());
        assertEquals(BigDecimal.valueOf(1999.00).setScale(2, ROUND_HALF_UP), cartVo.getCartTotalPrice());
        assertEquals(1, cartVo.getCartTotalQuantity());
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductId);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getQuantity);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductName);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSubtitle);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductMainImage);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStatus);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductTotalPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStock);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSelected);
    }

    @Test
    void updateCartProductFailure() {
        verifyException(ResultException.class, ResponseEnum.CART_PRODUCT_NOT_EXIST.getDesc()
                ,() -> cartService.updateCartProduct(27, new CartUpdateForm(),2));
    }
    @Test
    void deleteCartProductSuccess() {
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        CartProductVo cartProductVo_1 = new CartProductVo(28, 1, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "NOVA青春版1999元", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", BigDecimal.valueOf(1999.00), 1, BigDecimal.valueOf(1999.00), 100, true);
        cartProductVoList.add(cartProductVo_1);
        CartVo cartVo = cartService.deleteCartProduct(26, 2);
        List<CartProductVo> cartProductVoListInDb = cartVo.getCartProductVoList();
        assertTrue(cartVo.getSelectedAll());
        assertEquals(BigDecimal.valueOf(1999.00).setScale(2, ROUND_HALF_UP), cartVo.getCartTotalPrice());
        assertEquals(1, cartVo.getCartTotalQuantity());
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductId);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getQuantity);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductName);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSubtitle);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductMainImage);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStatus);
        verifyData(cartProductVoList,cartProductVoListInDb, cartProductVo -> cartProductVo.getProductTotalPrice().setScale(2, ROUND_HALF_UP));
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductStock);
        verifyData(cartProductVoList,cartProductVoListInDb,CartProductVo::getProductSelected);
    }

    @Test
    void deleteCartProductFailure() {
        verifyException(ResultException.class, ResponseEnum.CART_PRODUCT_NOT_EXIST.getDesc()
                ,() -> cartService.deleteCartProduct(27, 2));
    }

    @Test
    public void checkProduct() {
        ProductDetailVo productDetailVo1=null;
        verifyException(ResultException.class, ResponseEnum.PRODUCT_NOT_EXIST.getDesc()
                ,() -> cartService.checkProduct(productDetailVo1));

        ProductDetailVo productDetailVo2=new ProductDetailVo();
        productDetailVo2.setStatus(ProductStatusEnum.OFF_SALE.getCode());
        verifyException(ResultException.class, ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc()
                ,() -> cartService.checkProduct(productDetailVo2));
        productDetailVo2.setStatus(ProductStatusEnum.DELETE.getCode());
        verifyException(ResultException.class, ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc()
                ,() -> cartService.checkProduct(productDetailVo2));

        ProductDetailVo productDetailVo3=new ProductDetailVo();
        productDetailVo3.setStatus(ProductStatusEnum.ON_SALE.getCode());
        productDetailVo3.setStock(0);
        verifyException(ResultException.class, ResponseEnum.PRODUCT_STOCK_ERROR.getDesc()
                ,() -> cartService.checkProduct(productDetailVo3));
    }

    @Test
    void isSelectAllCartProduct() {
        CartVo notSelectAllProductCartVo = cartService.isSelectAllCartProduct(2, false);
        CartVo selectAllProductCartVo = cartService.isSelectAllCartProduct(2, true);

        assertFalse(notSelectAllProductCartVo.getSelectedAll());
        assertFalse(notSelectAllProductCartVo.getCartProductVoList().stream().allMatch(CartProductVo::getProductSelected));

        assertTrue(selectAllProductCartVo.getSelectedAll());
        assertTrue(selectAllProductCartVo.getCartProductVoList().stream().allMatch(CartProductVo::getProductSelected));

    }

    @Test
    void getCartProductSum() {
        Integer sum = cartService.getCartProductSum(2);
        assertEquals(2, sum);
    }
}
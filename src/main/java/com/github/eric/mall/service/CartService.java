package com.github.eric.mall.service;

import com.github.eric.mall.enums.ProductStatusEnum;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.CartAddForm;
import com.github.eric.mall.form.CartUpdateForm;
import com.github.eric.mall.generate.entity.Cart;
import com.github.eric.mall.generate.entity.Product;
import com.github.eric.mall.generate.mapper.ProductMapper;
import com.github.eric.mall.vo.CartProductVo;
import com.github.eric.mall.vo.CartVo;
import com.github.eric.mall.vo.ProductDetailVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {
    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ProductService productService;

    @Autowired
    ProductMapper productMapper;

    public ResponseVo<CartVo> getCartProductList(Integer userId) {

        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        Map<Integer, Cart> entries = hashOperations.entries(String.format(CART_REDIS_KEY_TEMPLATE, userId));
        if(entries.isEmpty()){
            return ResponseVo.success(getCartVo(entries, new HashMap<>()));
        }
        Set<Integer> productIds = entries.keySet();

        Map<Integer, Product> productMap = productService.findByIdIn(new ArrayList<>(productIds));

        return ResponseVo.success(getCartVo(entries, productMap));
    }

    private CartVo getCartVo(Map<Integer, Cart> entries, Map<Integer, Product> productMap) {
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        boolean selectedAll = true;
        int cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.valueOf(0);
        for (Map.Entry<Integer, Cart> entrie : entries.entrySet()) {
            Product product = productMap.get(entrie.getKey());
            Cart cart = entrie.getValue();
            CartProductVo cartProductVo = new CartProductVo(product.getId()
                    , cart.getQuantity()
                    , product.getName()
                    , product.getSubtitle()
                    , product.getMainImage()
                    , product.getPrice()
                    , product.getStatus()
                    , product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()))
                    , product.getStock()
                    , cart.getProductSelected());

            if (cartProductVo.getProductSelected()) {
                cartTotalQuantity += cartProductVo.getQuantity();
                cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
            } else {
                selectedAll = false;
            }
            cartProductVoList.add(cartProductVo);
        }
        return new CartVo(cartProductVoList, selectedAll, cartTotalPrice, cartTotalQuantity);
    }

    public ResponseVo<CartVo> addCartProduct(CartAddForm cartAddForm, Integer userId) {

        ProductDetailVo productDetailVo = productService.getProductById(cartAddForm.getProductId());
        if (productDetailVo == null) {
            // 没有商品
            ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }
        if (productDetailVo.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
            // 商品状态不是在售
            ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        if (productDetailVo.getStock() <= 0) {
            // 商品库存不足
            ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
        Integer quantity = 1;
        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        Cart cart = hashOperations.get(String.format(CART_REDIS_KEY_TEMPLATE, userId),
                productDetailVo.getId());
        if (cart == null) {
            cart = new Cart(productDetailVo.getId(), quantity, cartAddForm.getSelected());
        } else {
            cart.setQuantity(cart.getQuantity() + quantity);
        }

        hashOperations.put(String.format(CART_REDIS_KEY_TEMPLATE, userId),
                productDetailVo.getId(), cart);
        return getCartProductList(userId);
    }

    public ResponseVo<CartVo> updateCartProduct(Integer productId, CartUpdateForm cartUpdateForm, Integer userId) {
        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        Cart cart = hashOperations.get(String.format(CART_REDIS_KEY_TEMPLATE, userId), productId);
        if (cart == null) {
            // 购物车中没有对应的商品
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        } else {
            if (cartUpdateForm.getQuantity() != null && cartUpdateForm.getQuantity() >= 0) {
                cart.setQuantity(cartUpdateForm.getQuantity());
            }
            if (cartUpdateForm.getSelected() != null) {
                cart.setProductSelected(cartUpdateForm.getSelected());
            }
        }

        hashOperations.put(String.format(CART_REDIS_KEY_TEMPLATE, userId),
                productId, cart);
        return getCartProductList(userId);
    }

    public ResponseVo<CartVo> deleteCartProduct(Integer productId, Integer userId) {
        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        Cart cart = hashOperations.get(String.format(CART_REDIS_KEY_TEMPLATE, userId), productId);
        if (cart == null) {
            // 购物车中没有对应的商品
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        hashOperations.delete(String.format(CART_REDIS_KEY_TEMPLATE, userId), productId);
        return getCartProductList(userId);
    }

    public ResponseVo<CartVo> isSelectAllCartProduct(Integer userId,Boolean selectAll) {
        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        Map<Integer, Cart> entries = hashOperations.entries(String.format(CART_REDIS_KEY_TEMPLATE, userId));

        for (Map.Entry<Integer, Cart> entrie : entries.entrySet()) {
            entrie.getValue().setProductSelected(selectAll);
        }
        hashOperations.putAll(String.format(CART_REDIS_KEY_TEMPLATE, userId),entries);
        return getCartProductList(userId);
    }

    public ResponseVo<Integer> getCartProductSum(Integer userId) {
        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        Map<Integer, Cart> entries = hashOperations.entries(String.format(CART_REDIS_KEY_TEMPLATE, userId));

        Integer result=0;
        for (Map.Entry<Integer, Cart> entrie : entries.entrySet()) {
            result+= entrie.getValue().getQuantity();
        }

        return ResponseVo.success(result);
    }
}

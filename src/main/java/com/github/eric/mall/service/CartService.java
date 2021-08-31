package com.github.eric.mall.service;

import com.github.eric.mall.form.CartAddForm;
import com.github.eric.mall.vo.CartProductVo;
import com.github.eric.mall.vo.CartVo;
import com.github.eric.mall.vo.ProductDetailVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ProductService productService;

    public ResponseVo<CartVo> getCartProductList(Integer userId) {


        return null;
    }

    public CartVo addCartProduct(CartAddForm cartAddForm, Integer userId) {
        if (redisTemplate.hasKey(userId)) {
            CartVo cartVo = (CartVo) redisTemplate.opsForValue().get(userId);
            List<CartProductVo> cartProductVoList = cartVo.getCartProductVoList();

            // 购物车中是否有对应的商品
            boolean anyMatch = cartProductVoList.stream().anyMatch(cartProductVo -> {
                return cartProductVo.getProductId().equals(cartAddForm.getProductId());
            });

            if (anyMatch) {
                cartProductVoList.forEach(cartProductVo -> {
                    if (cartProductVo.getProductId().equals(cartAddForm.getProductId())) {
                        cartProductVo.setQuantity(cartProductVo.getQuantity() + 1);
                        cartProductVo.setProductTotalPrice(cartProductVo.getProductTotalPrice().add(cartProductVo.getProductPrice()));
                        cartProductVo.setProductSelected(cartAddForm.getSelected());

                        BigDecimal productPrice = cartProductVo.getProductPrice();
                        BigDecimal cartTotalPrice = cartVo.getCartTotalPrice();

                        cartVo.setCartTotalPrice(cartTotalPrice.add(productPrice));
                    }
                });

                cartVo.setCartProductVoList(cartProductVoList);
                redisTemplate.opsForValue().set(userId, cartVo);
                return cartVo;
            } else {
                // 查询数据库的商品信息，组装好后添加到Redis
                ProductDetailVo productDetailVo = productService.getProductById(cartAddForm.getProductId());

                CartProductVo cartProductVo = getCartProductVo(productDetailVo);

                cartProductVoList.add(cartProductVo);

                setCartInfo(cartVo,cartProductVoList);
                redisTemplate.opsForValue().set(userId, cartVo);
                return cartVo;
            }
        } else {
            // 缓存中没有购物车信息，查询数据库的商品信息，组装好后添加到Redis
            CartVo cartVo = new CartVo();
            ProductDetailVo productDetailVo = productService.getProductById(cartAddForm.getProductId());

            CartProductVo cartProductVo = getCartProductVo(productDetailVo);

            List<CartProductVo> cartProductVoList = new ArrayList<>();
            cartProductVoList.add(cartProductVo);

            setCartInfo(cartVo,cartProductVoList);
            redisTemplate.opsForValue().set(userId, cartVo);
            return cartVo;

        }
    }

    private void setCartInfo(CartVo cartVo, List<CartProductVo> cartProductVoList) {
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setSelectedAll(cartProductVoList.stream().allMatch(CartProductVo::getProductSelected));
        cartVo.setCartTotalQuantity(cartProductVoList.stream().mapToInt(CartProductVo::getQuantity).sum());
        cartVo.setCartTotalPrice(cartProductVoList.stream()
                .map(CartProductVo::getProductTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));

    }

    private CartProductVo getCartProductVo(ProductDetailVo productDetailVo) {
        CartProductVo cartProductVo = new CartProductVo();

        cartProductVo.setProductId(productDetailVo.getId());
        cartProductVo.setQuantity(1);
        cartProductVo.setProductName(productDetailVo.getName());
        cartProductVo.setProductSubtitle(productDetailVo.getSubtitle());
        cartProductVo.setProductMainImage(productDetailVo.getMainImage());
        cartProductVo.setProductPrice(productDetailVo.getPrice());
        cartProductVo.setProductStatus(productDetailVo.getStatus());
        cartProductVo.setProductTotalPrice(productDetailVo.getPrice().multiply(BigDecimal.valueOf(1)));
        cartProductVo.setProductStock(productDetailVo.getStock());
        cartProductVo.setProductSelected(true);
        return cartProductVo;
    }
}

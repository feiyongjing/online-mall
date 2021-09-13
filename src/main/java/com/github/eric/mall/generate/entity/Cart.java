package com.github.eric.mall.generate.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class Cart implements Serializable {
    private static final long serialVersionUID =1L;
    private Integer productId;
    private Integer quantity;
    private Boolean productSelected;


    public Cart() {
    }

    public Cart(Integer productId, Integer quantity, Boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productSelected = productSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(productId, cart.productId) &&
                Objects.equals(quantity, cart.quantity) &&
                Objects.equals(productSelected, cart.productSelected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, productSelected);
    }
}

package com.niranjankagri.mutation.testdata;

import com.niranjankagri.mutation.cart.Cart;

import static com.niranjankagri.mutation.testdata.Amounts.cents;

/**
 * Test data builder for carts. Tests state only what matters to them:
 * {@code aCart().worth("100.00")} when only the subtotal counts, {@code .with(...)} when the lines do.
 */
public final class CartBuilder {

    private final Cart cart = new Cart();

    private CartBuilder() {
    }

    public static CartBuilder aCart() {
        return new CartBuilder();
    }

    /** One line whose subtotal is exactly this amount. */
    public CartBuilder worth(String amount) {
        cart.add("ANY-SKU", cents(amount), 1);
        return this;
    }

    public CartBuilder with(String sku, String unitPrice, int quantity) {
        cart.add(sku, cents(unitPrice), quantity);
        return this;
    }

    public Cart build() {
        return cart;
    }
}

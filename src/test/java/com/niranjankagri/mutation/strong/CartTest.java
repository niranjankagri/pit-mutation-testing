package com.niranjankagri.mutation.strong;

import com.niranjankagri.mutation.cart.Cart;
import com.niranjankagri.mutation.cart.CartLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/** Exact totals and the edges of each input rule (price 0 is allowed, quantity 1 is the smallest). */
@DisplayName("Cart")
class CartTest {

    @Test
    @DisplayName("subtotal is price x quantity summed over the lines")
    void subtotalIsPriceTimesQuantitySummed() {
        Cart cart = new Cart().add("WRENCH", cents("49.00"), 2).add("SOCKETS", cents("35.50"), 3);

        // 2 x 49.00 + 3 x 35.50; a "*" mutated to "/" or "+=" to "-=" gives a different number
        assertThat(cart.subtotalCents()).isEqualTo(cents("204.50"));
        assertThat(cart.itemCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("an empty cart is empty, with nothing to pay")
    void emptyCart() {
        Cart cart = new Cart();

        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.subtotalCents()).isZero();
        assertThat(cart.itemCount()).isZero();
    }

    @Test
    @DisplayName("lines keep what was added, in order")
    void linesKeepWhatWasAdded() {
        Cart cart = new Cart().add("WRENCH", cents("49.00"), 2).add("LEVEL", cents("12.25"), 1);

        assertThat(cart.isEmpty()).isFalse();
        assertThat(cart.lines()).containsExactly(
                new CartLine("WRENCH", cents("49.00"), 2),
                new CartLine("LEVEL", cents("12.25"), 1));
    }

    @Test
    @DisplayName("a free gift (price 0) and a single unit are allowed")
    void edgesThatAreAllowed() {
        Cart cart = new Cart().add("GIFT", 0, 1);

        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.subtotalCents()).isZero();
    }

    @Test
    @DisplayName("a negative price is refused")
    void negativePriceIsRefused() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Cart().add("WRENCH", -1, 1))
                .withMessage("Price must not be negative: -1");
    }

    @Test
    @DisplayName("a quantity of 0 is refused")
    void zeroQuantityIsRefused() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Cart().add("WRENCH", cents("49.00"), 0))
                .withMessage("Quantity must be at least 1: 0");
    }
}

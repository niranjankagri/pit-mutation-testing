package com.niranjankagri.mutation.weak;

import com.niranjankagri.mutation.cart.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * WEAK on purpose: runs every line of {@link Cart} (100% line coverage) but checks only that "something came back".
 * Compare with {@code strong.CartTest}; the README lists the mutants these tests let through.
 */
@DisplayName("Cart (weak suite)")
class WeakCartTest {

    @Test
    void cartWithItemsHasASubtotal() {
        Cart cart = new Cart().add("WRENCH", 4_900, 2).add("SOCKETS", 3_550, 1);

        assertThat(cart.subtotalCents()).isPositive();
        assertThat(cart.itemCount()).isPositive();
        assertThat(cart.lines()).isNotEmpty();
        assertThat(cart.isEmpty()).isFalse();
    }

    @Test
    void badLinesAreRejected() {
        assertThatThrownBy(() -> new Cart().add("WRENCH", -1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Cart().add("WRENCH", 4_900, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}

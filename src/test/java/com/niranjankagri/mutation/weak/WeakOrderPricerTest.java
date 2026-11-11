package com.niranjankagri.mutation.weak;

import com.niranjankagri.mutation.cart.Cart;
import com.niranjankagri.mutation.pricing.CustomerTier;
import com.niranjankagri.mutation.pricing.Receipt;
import com.niranjankagri.mutation.pricing.ShippingSpeed;
import com.niranjankagri.mutation.testdata.PricingFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.niranjankagri.mutation.testdata.CartBuilder.aCart;
import static com.niranjankagri.mutation.testdata.CouponBuilder.MID_MONTH;
import static com.niranjankagri.mutation.testdata.CouponBuilder.aCoupon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * WEAK on purpose: the kind of "end-to-end" pricing test that looks thorough. It walks every path of
 * {@code OrderPricer} and checks the receipt is plausible, never the exact amounts.
 */
@DisplayName("Order pricer (weak suite)")
class WeakOrderPricerTest {

    private final PricingFixture fixture = new PricingFixture();

    @Test
    void orderIsPriced() {
        Receipt receipt = fixture.pricer.price(aCart().worth("200.00").build(), CustomerTier.STANDARD, 1_500,
                ShippingSpeed.STANDARD, Optional.empty(), MID_MONTH);

        assertThat(receipt).isNotNull();
        assertThat(receipt.totalCents()).isPositive();
        assertThat(receipt.taxCents()).isPositive();
    }

    @Test
    void couponIsUsed() {
        Receipt receipt = fixture.pricer.price(aCart().worth("40.00").build(), CustomerTier.STANDARD, 800,
                ShippingSpeed.STANDARD, Optional.of(aCoupon().build()), MID_MONTH);

        assertThat(receipt.couponUsed()).isTrue();
        assertThat(receipt.discountCents()).isPositive();
    }

    @Test
    void emptyCartIsRejected() {
        assertThatThrownBy(() -> fixture.pricer.price(new Cart(), CustomerTier.STANDARD, 500,
                ShippingSpeed.STANDARD, Optional.empty(), MID_MONTH)).isInstanceOf(IllegalArgumentException.class);
    }
}

package com.niranjankagri.mutation.strong;

import com.niranjankagri.mutation.cart.Cart;
import com.niranjankagri.mutation.pricing.Coupon;
import com.niranjankagri.mutation.pricing.CouponCheck;
import com.niranjankagri.mutation.pricing.CustomerTier;
import com.niranjankagri.mutation.pricing.Receipt;
import com.niranjankagri.mutation.pricing.ShippingSpeed;
import com.niranjankagri.mutation.testdata.PricingFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static com.niranjankagri.mutation.testdata.CartBuilder.aCart;
import static com.niranjankagri.mutation.testdata.CouponBuilder.LAST_DAY;
import static com.niranjankagri.mutation.testdata.CouponBuilder.MID_MONTH;
import static com.niranjankagri.mutation.testdata.CouponBuilder.aCoupon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Whole receipts with every amount worked out by hand in the comments, so a reader can check the expectation
 * without running the code. Exact receipts are what kill the arithmetic mutants (+ to -, * to /).
 */
@DisplayName("Order pricer")
class OrderPricerTest {

    private final PricingFixture fixture = new PricingFixture();

    @Test
    @DisplayName("loyalty discount, tax on the discounted subtotal, shipping untaxed")
    void loyaltyDiscountTaxAndShipping() {
        Receipt receipt = price(aCart().worth("200.00").build(), CustomerTier.STANDARD, 1_500, Optional.empty());

        // 200.00 - 5% (10.00) = 190.00; tax 18% = 34.20; standard shipping is free from 50.00
        assertThat(receipt).isEqualTo(new Receipt(cents("200.00"), 5, cents("10.00"), cents("34.20"),
                cents("0.00"), cents("224.20"), false));
    }

    @Test
    @DisplayName("small order pays shipping on top of the taxed goods")
    void smallOrderPaysShipping() {
        Receipt receipt = price(aCart().worth("40.00").build(), CustomerTier.STANDARD, 1_500, Optional.empty());

        // no discount; tax 18% of 40.00 = 7.20; 1.5 kg = 4.99 + 1.50 = 6.49; total 40.00 + 7.20 + 6.49
        assertThat(receipt).isEqualTo(new Receipt(cents("40.00"), 0, cents("0.00"), cents("7.20"),
                cents("6.49"), cents("53.69"), false));
    }

    @Test
    @DisplayName("tax is rounded half up to the cent")
    void taxIsRoundedHalfUp() {
        // 18% of 33.33 = 5.9994 -> 6.00 (rounding down would give 5.99)
        assertThat(price(aCart().worth("33.33").build(), CustomerTier.STANDARD, 500, Optional.empty()).taxCents())
                .isEqualTo(cents("6.00"));
        // 18% of 0.02 = 0.0036 -> 0.00 (rounding up would give 0.01)
        assertThat(price(aCart().worth("0.02").build(), CustomerTier.STANDARD, 500, Optional.empty()).taxCents())
                .isZero();
    }

    @Test
    @DisplayName("a better coupon replaces the loyalty discount and is used up")
    void betterCouponWins() {
        Coupon coupon = aCoupon().percentOff(15).build();

        Receipt receipt = price(aCart().worth("200.00").build(), CustomerTier.STANDARD, 1_500, Optional.of(coupon));

        // coupon 15% beats loyalty 5%: 200.00 - 30.00 = 170.00; tax 30.60; free shipping
        assertThat(receipt).isEqualTo(new Receipt(cents("200.00"), 15, cents("30.00"), cents("30.60"),
                cents("0.00"), cents("200.60"), true));
        assertThat(fixture.coupons.check(coupon, cents("200.00"), MID_MONTH)).isEqualTo(CouponCheck.ALREADY_USED);
    }

    @Test
    @DisplayName("a coupon that only equals the loyalty discount is not used up")
    void equalCouponLoses() {
        Coupon coupon = aCoupon().percentOff(5).build();

        Receipt receipt = price(aCart().worth("200.00").build(), CustomerTier.STANDARD, 1_500, Optional.of(coupon));

        assertThat(receipt.discountPercent()).isEqualTo(5);
        assertThat(receipt.couponUsed()).isFalse();
        assertThat(fixture.coupons.check(coupon, cents("200.00"), MID_MONTH)).isEqualTo(CouponCheck.ACCEPTED);
    }

    @Test
    @DisplayName("a refused coupon gives no discount")
    void refusedCouponGivesNothing() {
        Coupon coupon = aCoupon().percentOff(15).build();

        Receipt receipt = price(aCart().worth("40.00").build(), CustomerTier.STANDARD, 500, Optional.of(coupon),
                LAST_DAY.plusDays(1));

        assertThat(receipt.discountPercent()).isZero();
        assertThat(receipt.discountCents()).isZero();
        assertThat(receipt.couponUsed()).isFalse();
    }

    @Test
    @DisplayName("an empty cart cannot be priced")
    void emptyCartIsRefused() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> price(new Cart(), CustomerTier.STANDARD, 500, Optional.empty()))
                .withMessage("Cannot price an empty cart");
    }

    private Receipt price(Cart cart, CustomerTier tier, int grams, Optional<Coupon> coupon) {
        return price(cart, tier, grams, coupon, MID_MONTH);
    }

    private Receipt price(Cart cart, CustomerTier tier, int grams, Optional<Coupon> coupon, java.time.LocalDate day) {
        return fixture.pricer.price(cart, tier, grams, ShippingSpeed.STANDARD, coupon, day);
    }
}

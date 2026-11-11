package com.niranjankagri.mutation.weak;

import com.niranjankagri.mutation.pricing.CustomerTier;
import com.niranjankagri.mutation.pricing.DiscountPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * WEAK on purpose: one value in the middle of each band, never at a threshold, and "a discount was given" instead
 * of how much. Every line runs; no boundary is tested.
 */
@DisplayName("Discount policy (weak suite)")
class WeakDiscountPolicyTest {

    private final DiscountPolicy policy = new DiscountPolicy();

    @Test
    void bigGoldOrderGetsADiscount() {
        assertThat(policy.percentFor(cents("600.00"), CustomerTier.GOLD)).isPositive();
    }

    @Test
    void mediumOrderGetsADiscount() {
        assertThat(policy.percentFor(cents("200.00"), CustomerTier.STANDARD)).isPositive();
    }

    @Test
    void smallOrderGetsNoNegativeDiscount() {
        assertThat(policy.percentFor(cents("20.00"), CustomerTier.STANDARD)).isNotNegative();
    }
}

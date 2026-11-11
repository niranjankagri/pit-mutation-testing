package com.niranjankagri.mutation.strong;

import com.niranjankagri.mutation.pricing.CustomerTier;
import com.niranjankagri.mutation.pricing.DiscountPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Both sides of each threshold, for both tiers, with the exact percentage. The rows one cent apart are what kill
 * the "{@code >=} became {@code >}" mutants that the weak suite let through.
 */
@DisplayName("Discount policy")
class DiscountPolicyTest {

    private final DiscountPolicy policy = new DiscountPolicy();

    @ParameterizedTest(name = "{0} ({1}) gets {2}%")
    @CsvSource({
            // subtotal, tier,     percent
            "0.00,     STANDARD, 0",
            "99.99,    STANDARD, 0",
            "100.00,   STANDARD, 5",
            "499.99,   STANDARD, 5",
            "500.00,   STANDARD, 10",
            "99.99,    GOLD,     5",
            "100.00,   GOLD,     10",
            "499.99,   GOLD,     10",
            // 10 + 5 = 15, capped at 12
            "500.00,   GOLD,     12",
    })
    void percentForSubtotalAndTier(String subtotal, CustomerTier tier, int expectedPercent) {
        assertThat(policy.percentFor(cents(subtotal), tier)).isEqualTo(expectedPercent);
    }
}

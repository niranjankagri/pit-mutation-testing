package com.niranjankagri.mutation.strong;

import com.niranjankagri.mutation.pricing.ShippingCalculator;
import com.niranjankagri.mutation.pricing.ShippingSpeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/** Exact fees at the free-shipping threshold and at each kilogram edge (1,000 g / 1,001 g). */
@DisplayName("Shipping calculator")
class ShippingCalculatorTest {

    private final ShippingCalculator shipping = new ShippingCalculator();

    @ParameterizedTest(name = "{0}, {1} g, {2}: {3}")
    @CsvSource({
            // subtotal, grams, speed,    fee
            "49.99,      1,     STANDARD, 4.99",
            "49.99,      1000,  STANDARD, 4.99",
            "49.99,      1001,  STANDARD, 6.49",
            "49.99,      2000,  STANDARD, 6.49",
            "49.99,      2001,  STANDARD, 7.99",
            "50.00,      2001,  STANDARD, 0.00",
            // express is never free and costs double
            "50.00,      1000,  EXPRESS,  9.98",
            "50.00,      1001,  EXPRESS,  12.98",
    })
    void fee(String subtotal, int grams, ShippingSpeed speed, String expectedFee) {
        assertThat(shipping.feeCents(cents(subtotal), grams, speed)).isEqualTo(cents(expectedFee));
    }

    @Test
    @DisplayName("a parcel of 0 g is refused")
    void zeroWeightIsRefused() {
        assertThatIllegalArgumentException().isThrownBy(() -> shipping.feeCents(cents("20.00"), 0, ShippingSpeed.STANDARD))
                .withMessage("Weight must be at least 1 g: 0");
    }
}

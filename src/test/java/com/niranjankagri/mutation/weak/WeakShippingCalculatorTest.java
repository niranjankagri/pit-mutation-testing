package com.niranjankagri.mutation.weak;

import com.niranjankagri.mutation.pricing.ShippingCalculator;
import com.niranjankagri.mutation.pricing.ShippingSpeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** WEAK on purpose: every branch runs, but fees are only checked for "positive" and amounts stay away from 50.00. */
@DisplayName("Shipping calculator (weak suite)")
class WeakShippingCalculatorTest {

    private final ShippingCalculator shipping = new ShippingCalculator();

    @Test
    void smallOrderPaysShipping() {
        assertThat(shipping.feeCents(cents("20.00"), 2_500, ShippingSpeed.STANDARD)).isPositive();
    }

    @Test
    void bigOrderShipsFree() {
        assertThat(shipping.feeCents(cents("80.00"), 500, ShippingSpeed.STANDARD)).isZero();
    }

    @Test
    void expressCostsSomething() {
        assertThat(shipping.feeCents(cents("80.00"), 500, ShippingSpeed.EXPRESS)).isPositive();
    }

    @Test
    void weightlessParcelIsRejected() {
        assertThatThrownBy(() -> shipping.feeCents(cents("20.00"), 0, ShippingSpeed.STANDARD))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

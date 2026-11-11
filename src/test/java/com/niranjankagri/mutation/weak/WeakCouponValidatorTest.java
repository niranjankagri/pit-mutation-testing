package com.niranjankagri.mutation.weak;

import com.niranjankagri.mutation.pricing.Coupon;
import com.niranjankagri.mutation.pricing.CouponCheck;
import com.niranjankagri.mutation.pricing.CouponValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static com.niranjankagri.mutation.testdata.CouponBuilder.MID_MONTH;
import static com.niranjankagri.mutation.testdata.CouponBuilder.aCoupon;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * WEAK on purpose: each refusal path runs, but the tests only check "not accepted", never which reason, and the
 * dates are a month away from the validity window instead of one day.
 */
@DisplayName("Coupon validator (weak suite)")
class WeakCouponValidatorTest {

    private final CouponValidator validator = new CouponValidator();

    @Test
    void validCouponIsAccepted() {
        assertThat(validator.check(aCoupon().build(), cents("50.00"), MID_MONTH).accepted()).isTrue();
    }

    @Test
    void refusedCouponsAreNotAccepted() {
        Coupon coupon = aCoupon().minimum("30.00").build();

        assertThat(validator.check(coupon, cents("50.00"), LocalDate.of(2026, 10, 1))).isNotEqualTo(CouponCheck.ACCEPTED);
        assertThat(validator.check(coupon, cents("50.00"), LocalDate.of(2026, 12, 31))).isNotEqualTo(CouponCheck.ACCEPTED);
        assertThat(validator.check(coupon, cents("10.00"), MID_MONTH)).isNotEqualTo(CouponCheck.ACCEPTED);
    }

    @Test
    void usedCouponIsNotAccepted() {
        Coupon coupon = aCoupon().build();
        validator.markUsed(coupon);

        assertThat(validator.check(coupon, cents("50.00"), MID_MONTH)).isNotEqualTo(CouponCheck.ACCEPTED);
    }
}

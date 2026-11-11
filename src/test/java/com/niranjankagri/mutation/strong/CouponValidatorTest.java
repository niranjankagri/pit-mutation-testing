package com.niranjankagri.mutation.strong;

import com.niranjankagri.mutation.pricing.Coupon;
import com.niranjankagri.mutation.pricing.CouponCheck;
import com.niranjankagri.mutation.pricing.CouponValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.niranjankagri.mutation.testdata.Amounts.cents;
import static com.niranjankagri.mutation.testdata.CouponBuilder.FIRST_DAY;
import static com.niranjankagri.mutation.testdata.CouponBuilder.LAST_DAY;
import static com.niranjankagri.mutation.testdata.CouponBuilder.MID_MONTH;
import static com.niranjankagri.mutation.testdata.CouponBuilder.aCoupon;
import static org.assertj.core.api.Assertions.assertThat;

/** The exact reason for each refusal, and the first and last valid day (both inclusive). */
@DisplayName("Coupon validator")
class CouponValidatorTest {

    private final CouponValidator validator = new CouponValidator();
    private final Coupon coupon = aCoupon().minimum("30.00").build();

    @Test
    @DisplayName("works on its first and last day, at exactly the minimum")
    void edgesThatAreAccepted() {
        assertThat(validator.check(coupon, cents("30.00"), FIRST_DAY)).isEqualTo(CouponCheck.ACCEPTED);
        assertThat(validator.check(coupon, cents("30.00"), LAST_DAY)).isEqualTo(CouponCheck.ACCEPTED);
    }

    @Test
    @DisplayName("the day before it starts: not yet valid")
    void dayBeforeStart() {
        assertThat(validator.check(coupon, cents("50.00"), FIRST_DAY.minusDays(1))).isEqualTo(CouponCheck.NOT_YET_VALID);
    }

    @Test
    @DisplayName("the day after it ends: expired")
    void dayAfterEnd() {
        assertThat(validator.check(coupon, cents("50.00"), LAST_DAY.plusDays(1))).isEqualTo(CouponCheck.EXPIRED);
    }

    @Test
    @DisplayName("one cent below the minimum: below minimum")
    void oneCentBelowMinimum() {
        assertThat(validator.check(coupon, cents("29.99"), MID_MONTH)).isEqualTo(CouponCheck.BELOW_MINIMUM);
    }

    @Test
    @DisplayName("once used, the same code is refused, and other codes are not affected")
    void usedOnce() {
        Coupon other = aCoupon().code("SPRING10").build();
        validator.markUsed(coupon);

        assertThat(validator.check(coupon, cents("50.00"), MID_MONTH)).isEqualTo(CouponCheck.ALREADY_USED);
        assertThat(validator.check(other, cents("50.00"), MID_MONTH)).isEqualTo(CouponCheck.ACCEPTED);
    }

    @ParameterizedTest(name = "{0} is not accepted")
    @EnumSource(value = CouponCheck.class, names = "ACCEPTED", mode = EnumSource.Mode.EXCLUDE)
    void onlyAcceptedGivesADiscount(CouponCheck refusal) {
        assertThat(refusal.accepted()).isFalse();
        assertThat(CouponCheck.ACCEPTED.accepted()).isTrue();
    }
}

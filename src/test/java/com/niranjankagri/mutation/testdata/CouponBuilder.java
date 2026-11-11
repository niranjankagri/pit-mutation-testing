package com.niranjankagri.mutation.testdata;

import com.niranjankagri.mutation.pricing.Coupon;

import java.time.LocalDate;

import static com.niranjankagri.mutation.testdata.Amounts.cents;

/**
 * Test data builder for coupons. The default is valid for all of November 2026 with no minimum, so a test changes
 * only the field it is about.
 */
public final class CouponBuilder {

    public static final LocalDate FIRST_DAY = LocalDate.of(2026, 11, 1);
    public static final LocalDate LAST_DAY = LocalDate.of(2026, 11, 30);
    /** A day inside the default validity. */
    public static final LocalDate MID_MONTH = LocalDate.of(2026, 11, 15);

    private String code = "WELCOME15";
    private int percentOff = 15;
    private long minimumCents = 0;
    private LocalDate validFrom = FIRST_DAY;
    private LocalDate validUntil = LAST_DAY;

    private CouponBuilder() {
    }

    public static CouponBuilder aCoupon() {
        return new CouponBuilder();
    }

    public CouponBuilder code(String code) {
        this.code = code;
        return this;
    }

    public CouponBuilder percentOff(int percentOff) {
        this.percentOff = percentOff;
        return this;
    }

    public CouponBuilder minimum(String amount) {
        this.minimumCents = cents(amount);
        return this;
    }

    public Coupon build() {
        return new Coupon(code, percentOff, minimumCents, validFrom, validUntil);
    }
}

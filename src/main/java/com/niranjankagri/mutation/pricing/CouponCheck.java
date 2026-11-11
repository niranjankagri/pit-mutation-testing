package com.niranjankagri.mutation.pricing;

/** Outcome of checking a coupon. Only {@link #ACCEPTED} gives a discount. */
public enum CouponCheck {
    ACCEPTED,
    NOT_YET_VALID,
    EXPIRED,
    BELOW_MINIMUM,
    ALREADY_USED;

    public boolean accepted() {
        return this == ACCEPTED;
    }
}

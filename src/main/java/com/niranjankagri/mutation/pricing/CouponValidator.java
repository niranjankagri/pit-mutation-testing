package com.niranjankagri.mutation.pricing;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Decides whether a coupon may be used, and remembers used ones (each code works once).
 *
 * <p>The checks run in a fixed order, so a customer sees one clear reason: already used, then dates, then the
 * minimum subtotal.
 */
public final class CouponValidator {

    private final Set<String> usedCodes = new HashSet<>();

    /**
     * @param coupon        the coupon to check
     * @param subtotalCents order subtotal
     * @param today         the order date
     * @return {@link CouponCheck#ACCEPTED} or the first reason it is refused
     */
    public CouponCheck check(Coupon coupon, long subtotalCents, LocalDate today) {
        if (usedCodes.contains(coupon.code())) {
            return CouponCheck.ALREADY_USED;
        }
        if (today.isBefore(coupon.validFrom())) {
            return CouponCheck.NOT_YET_VALID;
        }
        if (today.isAfter(coupon.validUntil())) {
            return CouponCheck.EXPIRED;
        }
        if (subtotalCents < coupon.minimumCents()) {
            return CouponCheck.BELOW_MINIMUM;
        }
        return CouponCheck.ACCEPTED;
    }

    /** Marks the coupon as used; later checks of the same code return {@link CouponCheck#ALREADY_USED}. */
    public void markUsed(Coupon coupon) {
        usedCodes.add(coupon.code());
    }
}

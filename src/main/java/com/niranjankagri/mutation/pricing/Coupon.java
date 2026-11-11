package com.niranjankagri.mutation.pricing;

import java.time.LocalDate;

/**
 * A discount code.
 *
 * @param code          what the customer types, for example {@code WELCOME15}
 * @param percentOff    discount in percent
 * @param minimumCents  smallest subtotal the coupon works for (inclusive)
 * @param validFrom     first day it works (inclusive)
 * @param validUntil    last day it works (inclusive)
 */
public record Coupon(String code, int percentOff, long minimumCents, LocalDate validFrom, LocalDate validUntil) {
}

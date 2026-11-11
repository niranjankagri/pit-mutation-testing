package com.niranjankagri.mutation.pricing;

/**
 * Loyalty discount, in percent, for an order.
 *
 * <ul>
 *   <li>Subtotal from 100.00: 5%. From 500.00: 10%. The thresholds are inclusive.</li>
 *   <li>Gold members get 5 points more.</li>
 *   <li>Never more than 12% in total.</li>
 * </ul>
 *
 * <p>Every rule here is a boundary ({@code >=}) or a cap ({@code min}). That is where tests that only "run the line"
 * miss bugs: PIT's conditional-boundary mutator turns {@code >=} into {@code >}, and only a test at exactly
 * 100.00 notices.
 */
public final class DiscountPolicy {

    static final long FIVE_PERCENT_FROM_CENTS = 10_000;
    static final long TEN_PERCENT_FROM_CENTS = 50_000;
    static final int GOLD_BONUS_PERCENT = 5;
    static final int MAX_PERCENT = 12;

    /**
     * @param subtotalCents order subtotal before discount
     * @param tier          customer's loyalty tier
     * @return discount in percent, 0 to {@value #MAX_PERCENT}
     */
    public int percentFor(long subtotalCents, CustomerTier tier) {
        int percent = 0;
        if (subtotalCents >= TEN_PERCENT_FROM_CENTS) {
            percent = 10;
        } else if (subtotalCents >= FIVE_PERCENT_FROM_CENTS) {
            percent = 5;
        }
        if (tier == CustomerTier.GOLD) {
            percent += GOLD_BONUS_PERCENT;
        }
        return Math.min(percent, MAX_PERCENT);
    }
}

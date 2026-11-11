package com.niranjankagri.mutation.pricing;

/**
 * Shipping fee for a parcel.
 *
 * <ul>
 *   <li>Standard shipping is free from a subtotal of 50.00 (inclusive).</li>
 *   <li>Otherwise 4.99 for the first kilogram and 1.50 for each started kilogram after it
 *       (1,001 g is two kilograms).</li>
 *   <li>Express costs double and is never free.</li>
 * </ul>
 */
public final class ShippingCalculator {

    static final long FREE_FROM_CENTS = 5_000;
    static final long FIRST_KG_CENTS = 499;
    static final long EXTRA_KG_CENTS = 150;
    private static final int GRAMS_PER_KG = 1_000;

    /**
     * @param subtotalCents order subtotal, used for the free-shipping rule
     * @param weightGrams   parcel weight, at least 1 g
     * @param speed         standard or express
     * @return fee in cents
     * @throws IllegalArgumentException for a weight below 1 g
     */
    public long feeCents(long subtotalCents, int weightGrams, ShippingSpeed speed) {
        if (weightGrams < 1) {
            throw new IllegalArgumentException("Weight must be at least 1 g: " + weightGrams);
        }
        if (speed == ShippingSpeed.STANDARD && subtotalCents >= FREE_FROM_CENTS) {
            return 0;
        }
        long fee = FIRST_KG_CENTS + (startedKilograms(weightGrams) - 1) * EXTRA_KG_CENTS;
        if (speed == ShippingSpeed.EXPRESS) {
            fee = fee * 2;
        }
        return fee;
    }

    /** Kilograms, rounded up: 1 g to 1,000 g is 1, 1,001 g is 2. */
    static int startedKilograms(int weightGrams) {
        return (weightGrams + GRAMS_PER_KG - 1) / GRAMS_PER_KG;
    }
}

package com.niranjankagri.mutation.testdata;

import java.math.BigDecimal;

/**
 * Turns readable amounts into cents, so tests say {@code cents("99.99")} instead of {@code 9_999}.
 * A boundary test is only convincing when the reader can see it sits one cent below the rule.
 */
public final class Amounts {

    private Amounts() {
    }

    /** "99.99" to 9999. Rejects more than two decimals, so a typo cannot silently round. */
    public static long cents(String amount) {
        return new BigDecimal(amount).movePointRight(2).longValueExact();
    }
}

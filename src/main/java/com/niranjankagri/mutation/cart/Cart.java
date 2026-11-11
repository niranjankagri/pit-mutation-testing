package com.niranjankagri.mutation.cart;

import java.util.ArrayList;
import java.util.List;

/**
 * A shopping cart: the lines a customer wants to buy.
 *
 * <p>Amounts are whole cents in a {@code long}, never {@code double} (no rounding drift). It also means PIT's math
 * mutators can change {@code +} into {@code -} or {@code *} into {@code /} here; on {@code BigDecimal} the same
 * arithmetic would be method calls, which PIT can only remove, not flip.
 */
public final class Cart {

    private final List<CartLine> lines = new ArrayList<>();

    /**
     * Adds a line.
     *
     * @param sku            product code
     * @param unitPriceCents price of one unit in cents; 0 is allowed (free gift), negative is not
     * @param quantity       units, at least 1
     * @return this cart, for chaining
     * @throws IllegalArgumentException for a negative price or a quantity below 1
     */
    public Cart add(String sku, long unitPriceCents, int quantity) {
        if (unitPriceCents < 0) {
            throw new IllegalArgumentException("Price must not be negative: " + unitPriceCents);
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1: " + quantity);
        }
        lines.add(new CartLine(sku, unitPriceCents, quantity));
        return this;
    }

    /** Sum of price x quantity over all lines, in cents. */
    public long subtotalCents() {
        long total = 0;
        for (CartLine line : lines) {
            total += line.unitPriceCents() * line.quantity();
        }
        return total;
    }

    /** Number of units across all lines (two lines of 3 = 6). */
    public int itemCount() {
        int count = 0;
        for (CartLine line : lines) {
            count += line.quantity();
        }
        return count;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    /** The lines in the order they were added; read-only. */
    public List<CartLine> lines() {
        return List.copyOf(lines);
    }
}

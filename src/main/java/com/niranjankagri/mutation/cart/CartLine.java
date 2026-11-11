package com.niranjankagri.mutation.cart;

/**
 * One line of a cart.
 *
 * @param sku            product code
 * @param unitPriceCents price of one unit in cents
 * @param quantity       number of units
 */
public record CartLine(String sku, long unitPriceCents, int quantity) {
}

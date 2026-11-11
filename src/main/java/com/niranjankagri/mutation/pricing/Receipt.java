package com.niranjankagri.mutation.pricing;

/**
 * The priced order, all amounts in cents.
 *
 * @param subtotalCents   sum of the cart lines
 * @param discountPercent the discount that was applied (loyalty or coupon, whichever is higher)
 * @param discountCents   amount taken off the subtotal
 * @param taxCents        tax on the discounted subtotal
 * @param shippingCents   shipping fee (not taxed)
 * @param totalCents      what the customer pays
 * @param couponUsed      whether the coupon gave the discount and was used up
 */
public record Receipt(long subtotalCents, int discountPercent, long discountCents, long taxCents, long shippingCents,
                      long totalCents, boolean couponUsed) {
}

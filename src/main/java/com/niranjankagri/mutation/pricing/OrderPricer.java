package com.niranjankagri.mutation.pricing;

import com.niranjankagri.mutation.cart.Cart;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Prices an order: discount, tax, shipping and total.
 *
 * <ul>
 *   <li>Discount: the loyalty discount or the coupon, whichever is higher; never both.</li>
 *   <li>Tax: 18% of the discounted subtotal, rounded half up to the cent. Shipping is not taxed.</li>
 *   <li>A coupon is used up only when it gave the discount.</li>
 * </ul>
 */
public final class OrderPricer {

    static final int TAX_PERCENT = 18;

    private final DiscountPolicy discountPolicy;
    private final ShippingCalculator shipping;
    private final CouponValidator coupons;

    public OrderPricer(DiscountPolicy discountPolicy, ShippingCalculator shipping, CouponValidator coupons) {
        this.discountPolicy = discountPolicy;
        this.shipping = shipping;
        this.coupons = coupons;
    }

    /**
     * @param cart        what is bought; must not be empty
     * @param tier        customer's loyalty tier
     * @param weightGrams parcel weight
     * @param speed       shipping speed
     * @param coupon      coupon typed by the customer, if any
     * @param today       order date, for the coupon's validity
     * @return the receipt
     * @throws IllegalArgumentException for an empty cart
     */
    public Receipt price(Cart cart, CustomerTier tier, int weightGrams, ShippingSpeed speed,
                         Optional<Coupon> coupon, LocalDate today) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cannot price an empty cart");
        }
        long subtotal = cart.subtotalCents();

        int loyaltyPercent = discountPolicy.percentFor(subtotal, tier);
        int couponPercent = coupon
                .filter(c -> coupons.check(c, subtotal, today).accepted())
                .map(Coupon::percentOff)
                .orElse(0);
        boolean couponWins = couponPercent > loyaltyPercent;
        int percent = couponWins ? couponPercent : loyaltyPercent;
        if (couponWins) {
            // Used up only when it actually gave the discount; a losing coupon stays available
            coupons.markUsed(coupon.orElseThrow());
        }

        long discount = percentOf(subtotal, percent);
        long taxable = subtotal - discount;
        long tax = percentOf(taxable, TAX_PERCENT);
        long shippingFee = shipping.feeCents(subtotal, weightGrams, speed);
        return new Receipt(subtotal, percent, discount, tax, shippingFee, taxable + tax + shippingFee, couponWins);
    }

    /** {@code percent}% of an amount, rounded half up to the cent (amounts are never negative here). */
    static long percentOf(long amountCents, int percent) {
        return (amountCents * percent + 50) / 100;
    }
}

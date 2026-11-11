package com.niranjankagri.mutation.testdata;

import com.niranjankagri.mutation.pricing.CouponValidator;
import com.niranjankagri.mutation.pricing.DiscountPolicy;
import com.niranjankagri.mutation.pricing.OrderPricer;
import com.niranjankagri.mutation.pricing.ShippingCalculator;

/**
 * The real collaborators wired together, fresh per test (no mocks: the rules are small and pure, and a mock would
 * hide exactly the arithmetic PIT mutates). The coupon validator is exposed so a test can check what was used up.
 */
public final class PricingFixture {

    public final CouponValidator coupons = new CouponValidator();
    public final OrderPricer pricer = new OrderPricer(new DiscountPolicy(), new ShippingCalculator(), coupons);
}

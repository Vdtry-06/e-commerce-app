package com.vdtry06.ecommerce.payment;

import com.vdtry06.ecommerce.customer.CustomerResponse;
import com.vdtry06.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
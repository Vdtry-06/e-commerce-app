package com.vdtry06.ecommerce.kafka;

import com.vdtry06.ecommerce.customer.CustomerResponse;
import com.vdtry06.ecommerce.order.PaymentMethod;
import com.vdtry06.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
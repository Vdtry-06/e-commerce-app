package com.vdtry06.ecommerce.order;


import com.vdtry06.ecommerce.customer.CustomerClient;
import com.vdtry06.ecommerce.exception.BusinessException;
import com.vdtry06.ecommerce.kafka.OrderConfirmation;
import com.vdtry06.ecommerce.kafka.OrderProducer;
import com.vdtry06.ecommerce.orderline.OrderLineRequest;
import com.vdtry06.ecommerce.orderline.OrderLineService;
import com.vdtry06.ecommerce.payment.PaymentClient;
import com.vdtry06.ecommerce.payment.PaymentRequest;
import com.vdtry06.ecommerce.product.ProductClient;
import com.vdtry06.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository repository;
    OrderMapper mapper;
    CustomerClient customerClient;
    PaymentClient paymentClient;
    ProductClient productClient;
    OrderLineService orderLineService;
    OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        // 1. check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        // 2. purchase the products --> product-ms
        var purchasedProducts = productClient.purchaseProducts(request.products());

        // 3. persist order
        var order = this.repository.save(mapper.toOrder(request));

        // 4. persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // 5. todo start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // 6. send the order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}

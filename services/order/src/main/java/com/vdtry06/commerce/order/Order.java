package com.vdtry06.commerce.order;

import com.vdtry06.commerce.orderline.OrderLine;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue
    Integer id;

    String reference;

    BigDecimal totalAmount;

    @Enumerated(STRING)
    PaymentMethod paymentMethod;

    String customerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    List<OrderLine> orderLines;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    LocalDateTime lastModifiedDate;
}

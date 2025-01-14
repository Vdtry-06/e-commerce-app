package com.vdtry06.ecommerce.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    String street;
    String houseNumber;
    String zipCode;

}

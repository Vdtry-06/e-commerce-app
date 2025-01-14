package com.vdtry06.commerce.customer;

public record CustomerResponse(
        String id,
        String firstname,
        String lastname,
        String email
) {

}
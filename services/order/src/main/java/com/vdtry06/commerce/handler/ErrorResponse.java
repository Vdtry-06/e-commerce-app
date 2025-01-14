package com.vdtry06.commerce.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {

}
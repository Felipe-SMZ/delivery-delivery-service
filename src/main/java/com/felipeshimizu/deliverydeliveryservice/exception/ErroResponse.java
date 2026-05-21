package com.felipeshimizu.deliverydeliveryservice.exception;

import java.time.LocalDateTime;

public class ErroResponse {

    private final int status;
    private final String erro;
    private final String message;
    private final LocalDateTime timestamp;

    public ErroResponse(int status, String erro, String message) {
        this.status = status;
        this.erro = erro;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

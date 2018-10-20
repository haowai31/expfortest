package com.example.kurtqin.test.Account.exception;

public class InvalidResponseException extends CloudServiceException {

    public InvalidResponseException(String detailMessage) {
        super(detailMessage);
    }
}

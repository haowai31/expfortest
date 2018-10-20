package com.example.kurtqin.test.Account.exception;

public class InvalidCredentialException extends CloudServiceException {

    public InvalidCredentialException(String detailMessage) {
        super(detailMessage);
    }
}

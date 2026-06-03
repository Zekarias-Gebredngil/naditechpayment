package com.naditech.naditech.domain.exception;

public class AccountLockedException extends DomainException {
    public AccountLockedException(String message) {
        super(message);
    }
}

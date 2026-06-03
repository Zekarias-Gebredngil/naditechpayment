package com.naditech.naditech.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String password;
    private String accountNumber;
    private String currency;
    private Role role;
    private AccountStatus accountStatus;
    private Integer failedLoginAttempts;
    private LocalDateTime lockoutExpiration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public boolean isAccountLocked() {
        if (this.accountStatus == AccountStatus.LOCKED) {
            if (this.lockoutExpiration != null && LocalDateTime.now().isAfter(this.lockoutExpiration)) {
                unlock();
                return false;
            }
            return true;
        }
        return false;
    }

    public void unlock() {
        this.accountStatus = AccountStatus.ACTIVE;
        this.failedLoginAttempts = 0;
        this.lockoutExpiration = null;
    }

    public void recordFailedLogin(int maxFailedAttempts, int lockoutDurationMinutes) {
        this.failedLoginAttempts = (this.failedLoginAttempts == null ? 0 : this.failedLoginAttempts) + 1;
        if (this.failedLoginAttempts >= maxFailedAttempts) {
            this.accountStatus = AccountStatus.LOCKED;
            this.lockoutExpiration = LocalDateTime.now().plusMinutes(lockoutDurationMinutes);
        }
    }

    public void recordSuccessfulLogin() {
        this.failedLoginAttempts = 0;
        this.lockoutExpiration = null;
    }

    public boolean isSuspended() {
        return this.accountStatus == AccountStatus.SUSPENDED;
    }

    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }
}

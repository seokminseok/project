package com.lsm.declaration.results.user;

import com.lsm.declaration.results.Result;

public enum RegisterResult implements Result {
    FAILURE_DUPLICATE_CONTACT,
    FAILURE_DUPLICATE_EMAIL,
    FAILURE_DUPLICATE_NICKNAME,
    ;

    @Override
    public boolean isPresent() {
        return false;
    }
}

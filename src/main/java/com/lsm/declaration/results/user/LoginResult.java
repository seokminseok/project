package com.lsm.declaration.results.user;

import com.lsm.declaration.results.Result;

public enum LoginResult implements Result {
    FAILURE_NOT_VERIFIED,
    FAILURE_SUSPENDED;

    @Override
    public boolean isPresent() {
        return false;
    }
}

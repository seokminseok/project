package com.lsm.declaration.results.user;

import com.lsm.declaration.results.Result;

public enum ValidateEmailTokenResult implements Result {
    FAILURE_EXPIRED;

    @Override
    public boolean isPresent() {
        return false;
    }
}

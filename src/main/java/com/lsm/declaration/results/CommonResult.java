package com.lsm.declaration.results;

public enum CommonResult implements Result{
    FAILURE,
    FAILURE_UNSIGNED,
    SUCCESS;

    @Override
    public boolean isPresent() {
        return false;
    }
}

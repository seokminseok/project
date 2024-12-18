package com.lsm.declaration.results;

public interface Result {
    String NAME = "result";

    String name();

    default String nameToLower() {
        return name().toLowerCase();
    }

    boolean isPresent();
}

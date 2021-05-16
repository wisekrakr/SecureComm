package com.wisekrakr.wisesecurecomm.communication.user;

public enum Status {

    ONLINE(1), BUSY(2), AWAY(3);

    private final int value;

    Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

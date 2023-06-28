package com.adryde.mobile.displaysdk;

import java.io.Serializable;

public enum MediaType implements Serializable {

    IMAGE,

    VIDEO,

    JSON,

    UNKNOWN;

    public String getStatus() {
        return this.name();
    }
}
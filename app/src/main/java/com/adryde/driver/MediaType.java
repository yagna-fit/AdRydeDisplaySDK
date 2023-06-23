package com.adryde.driver;

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
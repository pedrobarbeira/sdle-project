package org.sdle.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemTypeDeserializer.class)
public enum ItemType {
    NUMERIC("numeric"), BOOLEAN("boolean");

    private final String type;

    ItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}


package org.sdle.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class ItemTypeDeserializer extends JsonDeserializer<ItemType> {
    @Override
    public ItemType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String typeString = node.asText();

        // Map the string to the ItemType enum value
        if ("numeric".equalsIgnoreCase(typeString)) {
            return ItemType.NUMERIC;
        } else if ("boolean".equalsIgnoreCase(typeString)) {
            return ItemType.BOOLEAN;
        }

        throw new IllegalArgumentException("Invalid ItemType value: " + typeString);
    }
}

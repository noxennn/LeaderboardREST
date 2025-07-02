package com.yebyrkc.LeaderboardREST.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ToIntegerFormatSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // Format the value to show only the integer part (no decimals)
            String formattedValue = String.format("%.0f", value);  // %.0f rounds and removes decimal places
            gen.writeString(formattedValue);  // Write as string
        } else {
            gen.writeNull();
        }
    }
}


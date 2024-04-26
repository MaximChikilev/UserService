package com.example.userservice.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<Date> {
  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String dateStr = p.getText();
    try {
      return formatter.parse(dateStr);
    } catch (ParseException e) {
      throw new InvalidFormatException(p, "Expected date format yyyy-MM-dd", dateStr, Date.class);
    }
  }
}

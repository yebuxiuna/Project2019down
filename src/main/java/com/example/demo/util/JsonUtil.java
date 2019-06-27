package com.example.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String getJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    public static Object getObject(String json,Class type) throws IOException {
        return objectMapper.readValue(json,type);
    }

}

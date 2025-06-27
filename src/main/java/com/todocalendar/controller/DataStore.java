package com.todocalendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;

public class DataStore {
    private static final String DATA_FILE = "data.json";
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void save(AppData data) throws Exception {
        mapper.writeValue(new File(DATA_FILE), data);
    }

    public static AppData load() throws Exception {
        File file = new File(DATA_FILE);
        if (!file.exists())
            return null;
        return mapper.readValue(file, AppData.class);
    }
}
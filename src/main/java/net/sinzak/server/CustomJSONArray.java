package net.sinzak.server;

import org.json.simple.JSONArray;

import java.util.Map;

public class CustomJSONArray extends JSONArray {
    public CustomJSONArray(Long id, String word) {
        super();
        add(id);
        add(word);
    }
}

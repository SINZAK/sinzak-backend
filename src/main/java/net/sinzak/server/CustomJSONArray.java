package net.sinzak.server;

import org.json.simple.JSONArray;

import java.util.Map;

public class CustomJSONArray extends JSONArray {
    public CustomJSONArray(Map<Long, String> map) {
        super();
        add(map);
    }
}

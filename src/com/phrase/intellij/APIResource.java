package com.phrase.intellij;

import org.json.JSONObject;

/**
 * Created by kolja on 15.10.15.
 */
public class APIResource {

    private final String id;
    private final String name;
    private final String code;

    public APIResource(String id, String name) {
        this(id, name, null);
    }

    public APIResource(String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public APIResource(JSONObject jsonObject) {

        this.id = jsonObject.has("id")
                ? jsonObject.getString("id")
                : null;
        this.name = jsonObject.has("name")
                ? jsonObject.getString("name")
                : null;
        this.code = jsonObject.has("code")
                ? jsonObject.getString("code")
                : null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}

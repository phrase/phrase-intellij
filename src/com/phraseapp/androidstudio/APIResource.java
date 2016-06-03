package com.phraseapp.androidstudio;

/**
 * Created by kolja on 15.10.15.
 */
public class APIResource {

    private final String id;
    private final String name;
    private final String code;

    public APIResource(String id, String name){
        this(id,name, null);
    }

    public APIResource(String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
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

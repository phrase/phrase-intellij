package com.phraseapp.androidstudio;

/**
 * Created by kolja on 15.10.15.
 */
public class APIResource {

    private final String id;
    private final String name;

    public APIResource(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

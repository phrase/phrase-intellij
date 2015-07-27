package com.phraseapp.androidstudio;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class API {

    public static class Holder {
        public static final API HOLDER_INSTANCE = new API();
    }

    public static API getInstance() {
        return Holder.HOLDER_INSTANCE;
    }

    public static final String PHRASEAPP_API_BASEURL = "https://api.phraseapp.com/v2/";
    public static final String PHRASEAPP_USER_AGENT = "PhraseApp AndroidStudio 2.0";



    public Unirest buildClient(){
        Unirest client = new Unirest();
        client.setDefaultHeader("Authorization", "token " + TokenRepository.getInstance().getAccessToken());
        client.setDefaultHeader("User-Agent", PHRASEAPP_USER_AGENT);
        return client;
    }


    // Find LocaleID for given localeName
    public String findLocaleId(String localeName){
        HttpResponse<JsonNode> rsp = null;
        try {
            rsp = buildClient().get(PHRASEAPP_API_BASEURL + "projects/" + TokenRepository.getInstance().getProjectId() + "/locales")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONArray locales = rsp.getBody().getArray();
        for(int i = 0; i < locales.length(); i++){
            JSONObject loc = (JSONObject)locales.get(i);
            if(loc.get("name").toString().equals(localeName)){
                return loc.get("id").toString();
            }
        }
        return "";
    }


    // Get all locales
    public LinkedList<String> getLocales(){
        HttpResponse<JsonNode> rsp = null;
        try {
            rsp = buildClient().get(PHRASEAPP_API_BASEURL + "projects/" + TokenRepository.getInstance().getProjectId() + "/locales")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        LinkedList<String> localeList = new LinkedList<String>();

        JSONArray locales = rsp.getBody().getArray();
        for(int i = 0; i < locales.length(); i++){
            JSONObject loc = (JSONObject)locales.get(i);
            localeList.add(loc.get("name").toString());
        }

        return localeList;
    }


    // Create a locale
    public void createLocale(String name, String code){
        try {
            HttpResponse<JsonNode>rsp = buildClient().post(PHRASEAPP_API_BASEURL + "projects/" + TokenRepository.getInstance().getProjectId() + "/locales")
                    .field("name", name)
                    .field("code", code)
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    public boolean uploadLocale(String filePath, String localeName){
        String localeCode =  localeName.replace("-r", "-"); // this is to prevent invalid PhraseApp localeIDs like de-rCH, pl-rPL
        createLocale(localeName, localeCode);

       HttpResponse rsp = null;
        try {
            rsp = buildClient().post(PHRASEAPP_API_BASEURL + "projects/" + TokenRepository.getInstance().getProjectId() + "/uploads")
                    .field("locale_id", findLocaleId(localeName))
                    .field("format", "xml")
                    .field("update_translations", true)
                    .field("file", new File(filePath))
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return rsp.getStatus() == 201;

    }


    public String downloadLocale(String localeName){
        HttpResponse rsp = null;
        try {
            rsp = buildClient().get(PHRASEAPP_API_BASEURL + "projects/" + TokenRepository.getInstance().getProjectId() + "/locales/" + findLocaleId(localeName) + "/download")
                    .queryString("file_format", "xml")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return rsp.getBody().toString();
    }
}

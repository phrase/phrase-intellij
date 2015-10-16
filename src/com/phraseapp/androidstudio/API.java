package com.phraseapp.androidstudio;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class API {

    public static final String PHRASEAPP_VERSION = "2.3";
    public static final String PHRASEAPP_API_BASEURL = "https://api.phraseapp.com/v2/";
    public static final String PHRASEAPP_USER_AGENT = "PhraseApp AndroidStudio " + PHRASEAPP_VERSION;
    private final String access_token;

    public API(String access_token){
        this.access_token = access_token;
    }


    public Unirest buildClient(){
        Unirest client = new Unirest();
        client.setDefaultHeader("Authorization", "token " + access_token);
        client.setDefaultHeader("User-Agent", PHRASEAPP_USER_AGENT);
        return client;
    }


    // Get all locales
    public APIResourceListModel getLocales(String id){
        HttpResponse<JsonNode> rsp = null;
        try {
            rsp = buildClient().get(PHRASEAPP_API_BASEURL + "projects/" + id + "/locales")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        APIResourceListModel localeList = new APIResourceListModel();

        JSONArray locales = rsp.getBody().getArray();
        for(int i = 0; i < locales.length(); i++){
            JSONObject loc = (JSONObject)locales.get(i);
            localeList.addElement(new APIResource((String) loc.get("id"), (String) loc.get("name")));
        }

        return localeList;
    }


    // Get all projects
    public APIResourceListModel getProjects(){
        HttpResponse<JsonNode> rsp = null;
        try {
            rsp = buildClient().get(PHRASEAPP_API_BASEURL + "projects")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        if (rsp != null && rsp.getStatus() == 200) {
            APIResourceListModel projectList = new APIResourceListModel();

            JSONArray projects = rsp.getBody().getArray();
            for (int i = 0; i < projects.length(); i++) {
                JSONObject pro = (JSONObject) projects.get(i);
                projectList.addElement(new APIResource((String) pro.get("id"), (String) pro.get("name")));
            }

            return projectList;
        } else {
            return null;
        }
    }
}
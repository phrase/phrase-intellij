package com.phraseapp.androidstudio;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.LinkedList;

import static org.apache.commons.httpclient.params.HttpMethodParams.COOKIE_POLICY;


public class API {

    public static class Holder {
        public static final API HOLDER_INSTANCE = new API();
    }

    public static API getInstance() {
        return Holder.HOLDER_INSTANCE;
    }

    public static final String PHRASE_API_BASEURL = "https://phraseapp.com/api/v1/";
    public static final String PHRASE_USER_AGENT = "PhraseApp Android Studio Plugin";


    /**
     * HttpClient for all requests
     *
     * @return
     */
    private HttpClient apiClient() {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        client.getParams().setParameter(HttpMethodParams.USER_AGENT, PHRASE_USER_AGENT);
        return client;
    }


    /**
     * Helper for Get Requests
     *
     * @return NV-Pair with auth_token
     */
    private NameValuePair[] addAuthToken() {
        return (new NameValuePair[]{
                new NameValuePair("auth_token", TokenRepository.getInstance().getToken())}
        );
    }


    /**
     * Download a locale file (.xml) from PhraseApp
     *
     * @param localecode Target-locale
     * @return contents of locale file as UTF-8 string
     */
    public String downloadLocaleFile(String localecode) {
        GetMethod method = new GetMethod(PHRASE_API_BASEURL + "locales/" + localecode + ".xml");
        method.setQueryString(addAuthToken());
        try {
            int status = apiClient().executeMethod(method);
            if (status == 200) {
                return method.getResponseBodyAsString();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Upload a locale file to PhraseApp
     *
     * @param locale  Target-locale
     * @param content Content of file to upload
     * @param forceUpload
     * @return success true/false
     */
    public boolean uploadLocaleFile(String locale, String content, boolean forceUpload) {
        PostMethod method = new PostMethod(PHRASE_API_BASEURL + "file_imports");
        method.getParams().setContentCharset("UTF-8");
        method.setParameter("auth_token", TokenRepository.getInstance().getToken());
        method.setParameter("file_import[locale_code]", locale);
        if (forceUpload) {
            method.setParameter("file_import[update_translations]", "1");
        }
        method.setParameter("file_import[format]", "xml");
        method.setParameter("file_import[filename]", "strings.xml");
        method.setParameter("file_import[file_content]", content);

        try {
            int status = apiClient().executeMethod(method);
            if (status == 200 && method.getResponseBodyAsString().contains("\"success\":true")) {
                return true;
            } else {
                System.out.println(method.getResponseBodyAsString());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Get all available locales from PhraseApp
     *
     * @return List with locales
     */
    public LinkedList<String> getLocales() {
        GetMethod method = new GetMethod(PHRASE_API_BASEURL + "locales");
        method.setQueryString(addAuthToken());

        LinkedList<String> locales = new LinkedList<String>();

        try {
            apiClient().executeMethod(method);
            Object obj = JSONValue.parse(method.getResponseBodyAsString());
            JSONArray array = (JSONArray) obj;
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj2 = (JSONObject) array.get(i);
                locales.add((String) obj2.get("code"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locales;
    }

}

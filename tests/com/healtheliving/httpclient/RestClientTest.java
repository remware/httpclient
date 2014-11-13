package com.healtheliving.httpclient;

import static org.junit.Assert.*;
import java.util.Formatter;
import java.util.HashMap;

import org.apache.http.HttpResponse;

import com.google.gson.Gson;
import com.healtheliving.httpclient.RestClient;

import junit.framework.TestCase;

public class RestClientTest {
    // geo address service
    protected static String BASE_URL = "http://maps.googleapis.com/maps/api/geocode/json?address=";


    public static final String URL_ENDPOINT = "Tampere";

    protected Gson gson;

    @org.junit.Before
    public void setUp() throws Exception {
        this.gson = new Gson();
    }

    @org.junit.Test
    public void testConvertStreamToString() throws Exception {

    }

    protected String formatUri(String endPoint) {
        StringBuilder sb = new StringBuilder();
        Formatter urlFormatter = new Formatter(sb);
        urlFormatter.format("%s/%s", BASE_URL, endPoint);
        return sb.toString();
    }

    @org.junit.Test
    // test Http Get()
    public void testRequest() throws Exception {
        String body = "{\"user\": {\"email\": \"patient@heli.com\", \"password\": \"patient\"}}";
        try {
            HttpResponse httpResponse = RestClient.requestWithJSONBody(RestClient.Method.POST, formatUri(URL_ENDPOINT), body);
            assertNotNull(httpResponse.getEntity());
            String strResponse = RestClient.convertStreamToString(httpResponse);
            assertNotNull(strResponse);

            HashMap<String, String> keyValuePairs = this.gson.fromJson(strResponse, HashMap.class);
            assertEquals("OK", keyValuePairs.get("status"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
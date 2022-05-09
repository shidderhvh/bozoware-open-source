package bozoware.visual.screens.alt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Copyright Pablo Matias 2022
 * None of this code to be reused without my written permission
 * Intellectual Rights owned by Pablo Matias
 **/
public class DrilledAPI {
    private static final String DRILLED_API_LINK = "http://drilledalts.xyz/api/gen?key=";
    private static final String DRILLED_API_KEY = "";

    public static Alt getAlt() {
        String altString = readUrl(DRILLED_API_LINK + DRILLED_API_KEY);
        if (altString == null) return null;
        JsonObject info = new JsonParser().parse(altString).getAsJsonObject();
        try {
            return new Alt(info.get("email").getAsString(), info.get("password").getAsString());
        } catch (Exception e) {
            return null;
        }
    }

    private static String readUrl(String urlString) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ignored) {
            }
        }
        return null;
    }

}
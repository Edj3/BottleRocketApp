package com.mannmade.bottlerocketapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by EJ Mann on 9/18/2015.
 */
public class ConnectionManager{
    Context mainContext;

    public ConnectionManager(Context ctext){
        this.mainContext = ctext;
    }
    public String connectToURL(String givenLink){
        String jsonFromPage = "";

        try{
            //Connect to given URL
            URL givenURL = new URL(givenLink);
            HttpURLConnection connection = (HttpURLConnection) givenURL.openConnection();
            InputStream iStream = connection.getInputStream();

            //Read in page
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
            jsonFromPage = "";
            String lineFromFile = "";

            //loop through and create string by concatenating all lines from page
            while ((lineFromFile = reader.readLine()) != null){
                jsonFromPage = jsonFromPage + lineFromFile + "\n";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return jsonFromPage;
    }
}

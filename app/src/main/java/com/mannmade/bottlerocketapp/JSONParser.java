package com.mannmade.bottlerocketapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by EJ Mann on 9/18/2015.
 */
public class JSONParser {
    public ArrayList<LinkedHashMap<String, String>> getJSONforString(String json){
        //One Array list to house all mappings of key value pairs
        ArrayList<LinkedHashMap<String, String>> storeList = new ArrayList<LinkedHashMap<String, String>>();
        try{
            //create JSON Object
            JSONObject readObject = new JSONObject(json);
            JSONArray storeArray = readObject.getJSONArray("stores");
            //loop thru each item in jsonArray and store key value pairs in map for each object
            for(int i = 0; i < storeArray.length(); i++){
                JSONObject jsonItem = storeArray.getJSONObject(i);
                System.out.println("Item " + i);
                Iterator<String> keys = jsonItem.keys();
                LinkedHashMap<String, String> itemMap = new LinkedHashMap<String, String>();

                while(keys.hasNext()){
                    String key = keys.next();
                    String value = jsonItem.getString(key);
                    itemMap.put(key, value);
                }
                storeList.add(itemMap);
            }
            System.out.println("The count of the items in your array list is = " + storeList.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        return storeList;
    }
}

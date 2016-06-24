package com.example.quont.queueband;

import org.json.JSONObject;

/**
 * Created by quont on 6/21/2016.
 */
public class SuggestionEvent {
    public JSONObject jsonObject;
    public SuggestionEvent(JSONObject jsonObject)  {
        this.jsonObject = jsonObject;
    }
}

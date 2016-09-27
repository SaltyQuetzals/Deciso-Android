package com.example.quont.queueband;

import org.json.JSONObject;

/**
 * Created by quont on 6/24/2016.
 */
public class VoteUpdateEvent {
    public JSONObject jsonObject;
    public VoteUpdateEvent(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}

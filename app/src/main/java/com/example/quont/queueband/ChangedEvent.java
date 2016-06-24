package com.example.quont.queueband;

import org.json.JSONObject;

/**
 * Created by quont on 6/16/2016.
 */
public class ChangedEvent {
    public JSONObject jsonObject;
    public float pos;

    public ChangedEvent(JSONObject jsonObject, float pos)   {
        this.jsonObject = jsonObject;
        this.pos = pos;
    }
}

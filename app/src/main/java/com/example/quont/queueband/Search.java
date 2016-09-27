package com.example.quont.queueband;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Search extends AppCompatActivity {
    ImageButton mButton;
    ListView mListView;
    EditText mEditText;
    ArrayList<Map<String, String>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mButton = (ImageButton) findViewById(R.id.imageButton);
        mEditText = (EditText) findViewById(R.id.editText2);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchQuery = mEditText.getText().toString();
                search(searchQuery);
            }
        });
        mListView = (ListView) findViewById(R.id.listView);
    }

    private void search(String searchQuery) {
        RequestQueue queue = Volley.newRequestQueue(Search.this);
        searchQuery = searchQuery.replace(" ", "+");
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + searchQuery + "&key=AIzaSyCNbREnG2HHx8AhpsgFoytAqt7F_iYZTh0";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("yt-response", "YTResponse = " + response);
                        Map<String, String> map = new HashMap<String, String>();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray =new JSONObject(response).getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                map.put("title", jsonArray.getJSONObject(i).getJSONObject("snippet").getString("title"));
                                map.put("artist", jsonArray.getJSONObject(i).getJSONObject("snippet").getString("channelTitle"));
                                map.put("source", "YouTube");
                                map.put("id", jsonArray.getJSONObject(i).getJSONObject("id").getString("videoId"));
                                list.add(map);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mListView = (ListView) findViewById(R.id.listView);
                        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.title_author_layout, new String[]{"title", "artist", "source"}, new int[]{R.id.voting_title, R.id.voting_artist, R.id.source});
                        mListView.setAdapter(simpleAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);   //...dunno. Just works.
        queue.start();  //...dunno.
    }
}

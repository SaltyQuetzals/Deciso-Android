package com.example.quont.queueband;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomRegistration extends AppCompatActivity {
    private int errorCode;
    private Button mButton;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_registration);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.editText);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mEditText.getText().toString();
                makeJsonObjectRequest(code);
            }
        });
    }

    private void transferToAddeo(String code, String uID) throws JSONException {
        errorCode = 200;
        if (errorCode != 200) {

        } else {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("code", code);
            mBundle.putString("userid", uID);
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }

    private void makeJsonObjectRequest(final String code) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://deciso.audio/" + code + ".json";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("onResponse", response.toString());


                try {
                    // Parsing json object response
                    // response will be a json object
                    String userid = response.getString("userid");
                    transferToAddeo(code, userid);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("onErrorResponse", "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
        queue.start();
    }
}

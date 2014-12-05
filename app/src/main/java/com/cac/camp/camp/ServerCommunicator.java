package com.cac.camp.camp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by Birk on 03-12-2014.
 */
public class ServerCommunicator {

    private RequestQueue queue;
    private String url = "http://www.noerskovlund-forsamlingshus.dk/contextaware/handleRequest.php";

    public  ServerCommunicator(Context c) {
        queue = Volley.newRequestQueue(c);
    }

    public void createUser(String id, ArrayList chillArray, ArrayList calmPartyArray, ArrayList normalPartyArray, ArrayList wildPartyArray) {
        JSONObject json = new JSONObject();
        JSONObject values = new JSONObject();
        JSONArray chill = new JSONArray(chillArray);
        JSONArray calmParty = new JSONArray(calmPartyArray);
        JSONArray normalParty = new JSONArray(normalPartyArray);
        JSONArray wildParty = new JSONArray(wildPartyArray);
        Log.d("sc", "CreateUser Called");
        try {
            values.put("chill", chill);
            values.put("calmParty", calmParty);
            values.put("normalParty", normalParty);
            values.put("wildParty", wildParty);

            json.put("type", "createUser");
            json.put("id", id);
            json.put("values", values);
        } catch (JSONException e) {
            //Handle error in creating
        }

        requestServer(json);
    }



    public void requestServer(JSONObject json) {
        Log.d("sc", "requestServer Called");
        Log.d("sc", json.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("sc", "request worked");

                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("sc", "Error in request");
                Log.d("Error: ", error.getMessage());

            }
        });
        queue.add(jsObjRequest);
    }

}

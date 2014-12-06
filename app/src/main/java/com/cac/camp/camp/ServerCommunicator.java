package com.cac.camp.camp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Birk on 03-12-2014.
 */
public class ServerCommunicator {

    private RequestQueue queue;
    private String url = "http://www.noerskovlund-forsamlingshus.dk/contextaware/testRequest.php";

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

    public void createPlaylist(ArrayList userList, String context) {
        JSONObject json = new JSONObject();
        JSONArray users = new JSONArray(userList);

        Log.d("sc", "CreatePlaylist Called");
        try {

            json.put("type", "createPlaylist");
            json.put("context", context);
            json.put("users", users);
        } catch (JSONException e) {
            //Handle error in creating
        }
        requestServer(json);
    }

    public void updatePlaylist(ArrayList userList, String context, String playlistID) {
        JSONObject json = new JSONObject();
        JSONArray users = new JSONArray(userList);

        Log.d("sc", "UpdatePlaylist Called");
        try {

            json.put("type", "createPlaylist");
            json.put("context", context);
            json.put("users", users);
            json.put("playlistID", playlistID);
        } catch (JSONException e) {
            //Handle error in creating
        }
        requestServer(json);
    }

    public void getPlaylist(String playlistID) {
        JSONObject json = new JSONObject();

        Log.d("sc", "UpdatePlaylist Called");
        try {

            json.put("type", "createPlaylist");
            json.put("playlistID", playlistID);
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

                    Log.d("sc", response.toString());
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

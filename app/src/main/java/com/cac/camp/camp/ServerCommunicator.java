package com.cac.camp.camp;

import android.app.Activity;
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
import java.util.List;
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

    public void updateContextAndPosition(String id, String lat, String lon, String classification, String confidence, StartScreenActivity activity) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "updateContextAndPosition");
            json.put("id", id);
            json.put("lat", lat);
            json.put("lon", lon);
            json.put("context", classification);
            json.put("confidence", confidence);
        } catch(Exception e) {
            Log.d("JSON ERROR", "error in the json");
        }
        requestServer(json, activity);
    }

    public void getContexts(String lat, String lon, StartScreenActivity activity) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "getUsersAndContexts");
            json.put("lat", lat);
            json.put("lon", lon);
        } catch(Exception e) {
            Log.d("JSON ERROR", "error in the json");
        }
        requestServer(json, activity);
    }

    public void createUser(String id, ArrayList chillArray, ArrayList calmPartyArray, ArrayList normalPartyArray, ArrayList wildPartyArray, StartScreenActivity activity) {
        JSONObject json = new JSONObject();
        JSONObject values = new JSONObject();
        JSONArray chill = new JSONArray(chillArray);
        JSONArray calmParty = new JSONArray(calmPartyArray);
        JSONArray normalParty = new JSONArray(normalPartyArray);
        JSONArray wildParty = new JSONArray(wildPartyArray);
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

        requestServer(json, activity);
    }

    public void createPlaylist(ArrayList userList, String context, ClientActivity activity) {
        JSONObject json = new JSONObject();
        JSONArray users = new JSONArray(userList);

        try {

            json.put("type", "createPlaylist");
            json.put("context", context);
            json.put("users", users);
        } catch (JSONException e) {
            //Handle error in creating
            Log.d("ERROR", "CREATE PLAYLIST ERROR");
            return;
        }
        requestServer(json, activity);
    }

    public void updatePlaylist(ArrayList userList, String context, String playlistID, ClientActivity activity) {
        JSONObject json = new JSONObject();
        JSONArray users = new JSONArray(userList);

        try {

            json.put("type", "createPlaylist");
            json.put("context", context);
            json.put("users", users);
            json.put("playlistID", playlistID);
        } catch (JSONException e) {
            //Handle error in creating
            Log.d("ERROR", "UPDATE PLAYLIST ERROR");
            return;
        }
        requestServer(json, activity);
    }

    public void getPlaylist(String playlistID, ClientActivity activity) {
        JSONObject json = new JSONObject();

        try {

            json.put("type", "createPlaylist");
            json.put("playlistID", playlistID);
        } catch (JSONException e) {
            //Handle error in creating
        }
        requestServer(json, activity);
    }



    public void requestServer(JSONObject json, final ClientActivity activity) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String id = response.getString("playlistID");

                        JSONArray jsonPlaylist = response.getJSONArray("playlist");
                        ArrayList<String> playlist = new ArrayList<String>();
                        for(int i = 0; i < jsonPlaylist.length(); i++){
                            playlist.add(jsonPlaylist.get(i).toString());
                        }
                        activity.setCurrentPlaylist(id, playlist);
                    } catch (JSONException e) {
                        try {
                            JSONArray users = response.getJSONArray("users");
                            ArrayList<String> usersList = new ArrayList<String>();
                            for(int i = 0; i < users.length(); i++){
                                usersList.add(users.get(i).toString());
                            }
                            JSONArray contexts = response.getJSONArray("contexts");
                            ArrayList<String> contextsList = new ArrayList<String>();
                            for(int i = 0; i < contexts.length(); i++) {
                                contextsList.add(contexts.get(i).toString());
                            }
                            activity.deriveCommonContext(usersList, contextsList);
                        } catch (JSONException f) {
                            Log.d("ignored response", "Response was ignored no need");
                        }
                    }

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

package com.cac.camp.camp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nikolaiollegaard on 05/12/14.
 */
public class SpotifyAPIConnector extends APIConnector {

    public SpotifyAPIConnector(Context c){
        super(c);
        baseURL = "https://api.spotify.com/v1/";
    }

    //Get request
    public void getTrackFromID(final String id){
        String url = baseURL+"tracks/"+id;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("sc", "request worked");
                        try {
                            String songName = response.getString("name");
                            JSONArray artists = response.getJSONArray("artists");
                            JSONObject artist = artists.getJSONObject(0);
                            String artistName = artist.getString("name");
                            Log.e("sc", songName + " by " + artistName);
                            Track track = new Track(id,songName,artistName);
                            SpotifySingleton.getInstance().handleTrackResponse(track);


                        } catch (JSONException e){
                            e.printStackTrace();
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

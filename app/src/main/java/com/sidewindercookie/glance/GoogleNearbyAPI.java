package com.sidewindercookie.glance;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hydrabolt on 2/20/18.
 */

public class GoogleNearbyAPI {
    RequestQueue queue;
    LocationWatcher service;
    public GoogleNearbyAPI (LocationWatcher service) {
        this.service = service;
        queue = Volley.newRequestQueue(service);
    }

    private String convertToGoogleName(String name) {
        return TextUtils.join("_", name.substring(2).toLowerCase().split(" "));
    }

    public void findNearby(final int proximity, final Location location, final LocationTrigger trigger, String keyword) {
        String type = convertToGoogleName(trigger.getInformalLocation().getName());
        // hi, stay away from our API key or i'll send pitchforks and angry doggos
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyDuszA5EeytLNVPDMDlLzn5LhFmLkG80ak&rankby=distance&type="+type+"&location="+location.getLatitude()+","+location.getLongitude();
        if (keyword != "") {
            url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyDuszA5EeytLNVPDMDlLzn5LhFmLkG80ak&rankby=distance&keyword="+keyword+"&location="+location.getLatitude()+","+location.getLongitude();
        }
        Log.d("GLANCE", url);
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            if (results.length() > 0) {
                                JSONObject result = results.getJSONObject(0);
                                String name = result.getString("name");
                                JSONObject jsonLocation = result.getJSONObject("geometry").getJSONObject("location");
                                Location placeLocation = new Location("");
                                placeLocation.setLongitude((float) jsonLocation.getDouble("lng"));
                                placeLocation.setLatitude((float) jsonLocation.getDouble("lat"));
                                if (placeLocation.distanceTo(location) <= proximity) {
                                    service.sendNotification(trigger, name);
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(service, "Something broke - blame Amish :(", Toast.LENGTH_LONG);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(service, "Something broke - blame Amish :(", Toast.LENGTH_LONG);
                    }
                });
        queue.add(request);
    }
}

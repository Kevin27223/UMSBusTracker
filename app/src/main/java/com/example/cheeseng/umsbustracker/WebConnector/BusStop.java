package com.example.cheeseng.umsbustracker.WebConnector;

import android.os.AsyncTask;
import android.util.Log;

import com.example.cheeseng.umsbustracker.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.jar.Attributes;

/**
 * Created by Chee Seng on 08-Apr-17.
 */

public class BusStop extends AsyncTask<Void, Void, JSONObject> {
    private JSONObject jObj;
    private Marker[] marker;
    private double []latitude;
    private double []longitude;
    private String[] name;
    private int currentRouteId;

    public BusStop(int currentRouteId, Marker[] marker){
        this.currentRouteId = currentRouteId;
        this.marker = marker;
    }

    @Override
    protected JSONObject doInBackground(Void... voids){
        String json;
        try{
            String link = "http://umsbustrack.esy.es/Mobile/information.php?source=bus_stop&route_id=" + currentRouteId;

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line;

            while((line = in.readLine()) != null){
                sb.append(line);
            }

            in.close();
            json = sb.toString();
            jObj = new JSONObject(json);
        }
        catch (Exception e){
            Log.e("BusStop.background", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        JSONArray jArray;
        JSONObject jObj;
        int length = 0;
        try{
            jArray = jsonObject.getJSONArray("BusStop");
            length = jArray.length();
            latitude = new double[length];
            longitude = new double[length];
            name = new String[length];
            for(int i=0; i<jArray.length(); i++){
                jObj = jArray.getJSONObject(i);
                latitude[i] = Double.parseDouble(jObj.getString("Latitude"));
                longitude[i] = Double.parseDouble(jObj.getString("Longitude"));
                name[i] = jObj.getString("Name");

                /*mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude))
                .title(jObj.getString("Name")));*/
            }
        }
        catch (Exception e){
            Log.e("BusStop.post", "Error parsing data " + e.toString());
        }

        for(int i=0; i<length; i++) {
            marker[i].setVisible(true);
            marker[i].setPosition(new LatLng(latitude[i], longitude[i]));
            marker[i].setTitle(name[i]);
        }

        for(int i=length; i<marker.length; i++){
            marker[i].setVisible(false);
        }
    }
}

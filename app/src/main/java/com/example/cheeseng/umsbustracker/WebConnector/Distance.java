package com.example.cheeseng.umsbustracker.WebConnector;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by Chee Seng on 21-May-17.
 */

public class Distance extends AsyncTask<Void, Void, JSONObject> {

    private double latitude, longitude, retLat, retLong;
    private Bus bus;
    private JSONObject jObj;

    public Distance(Bus bus, double latitude, double longitude){
        this.bus = bus;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        String json;
        try{
            String link = "http://umsbustrack.esy.es/Mobile/distance.php?latitude=" + latitude +
                    "&longitude=" + longitude;

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
        catch(Exception e){
            Log.e("Distance.Background", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        JSONArray jArray = new JSONArray();
        JSONObject jObj = new JSONObject();
        try{
            jArray = jsonObject.getJSONArray("Distance");
            for(int i=0; i<jArray.length(); i++) {
                jObj = jArray.getJSONObject(i);
                retLat = jObj.getDouble("latitude");
                retLong = jObj.getDouble("longitude");
            }
        }
        catch(Exception e){
            Log.e("Distance.PostExecute", "Error parsing data " + e.toString());
        }
        bus.updateDestinationCoordinate(retLat,retLong);
    }
}

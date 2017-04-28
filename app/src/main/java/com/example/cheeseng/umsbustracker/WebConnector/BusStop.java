package com.example.cheeseng.umsbustracker.WebConnector;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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

/**
 * Created by Chee Seng on 08-Apr-17.
 */

public class BusStop extends AsyncTask<Void, Void, JSONObject> {
    private JSONObject jObj;
    private GoogleMap mMap;

    public BusStop(GoogleMap mMap){
        this.mMap = mMap;
    }

    @Override
    protected JSONObject doInBackground(Void... voids){
        String json;
        try{
            String link = "http://umsbustrack.esy.es/Mobile/information.php?source=bus_stop";

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
        double latitude,longitude;
        try{
            jArray = jsonObject.getJSONArray("BusStop");
            for(int i=0; i<jArray.length(); i++){
                jObj = jArray.getJSONObject(i);
                latitude = Double.parseDouble(jObj.getString("Latitude"));
                longitude = Double.parseDouble(jObj.getString("Longitude"));

                mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude))
                .title(jObj.getString("Name")));
            }
        }
        catch (Exception e){
            Log.e("BusStop.post", "Error parsing data " + e.toString());
        }
    }
}

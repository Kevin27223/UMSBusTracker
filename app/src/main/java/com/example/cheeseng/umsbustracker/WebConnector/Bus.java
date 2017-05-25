package com.example.cheeseng.umsbustracker.WebConnector;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.cheeseng.umsbustracker.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chee Seng on 14-Mar-17.
 * Get bus location and set marker
 */

public class Bus extends AsyncTask<Void, Void, JSONObject>{
    private int route_id;
    private double []latitude;
    private double []longitude;
    //private double latitude, longitude;
    private double dLat = 0;
    private double dLng = 0;
    private JSONObject jObj;
    private TextView eta_value;
    private String key;
    private Marker[] marker;
    private MainActivity activity;
    private Polyline polyLine;
    Distance distance;

    public interface AsyncResponse {
        void setLatLang(double x, double y);
    }

    public AsyncResponse delegate = null;

    public Bus(MainActivity activity, Polyline polyLine, Marker[] marker, int route_id,
               TextView eta_value, double dLat, double dLng, String key){
        this.activity = activity;
        this.polyLine = polyLine;
        this.marker = marker;
        this.route_id = route_id;
        this.eta_value = eta_value;
        this.dLat = dLat;
        this.dLng = dLng;
        this.key = key;
    }

    @Override
    protected JSONObject doInBackground(Void... arg0) {
        String json;
        try{
            String link = "http://umsbustrack.esy.es/Mobile/bus.php?action=retrieve&route_id=" + route_id;

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
            Log.e("Bus.doInBackground", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject result){
        JSONArray jArray = null;
        JSONObject jObj;
        String status = "";
        int length = 0;

        try {
            status = result.getString("status");
            Log.i("bus", "status:" + status);
            if (status.equals("OK")) {
                jArray = result.getJSONArray("location");
                length = jArray.length();
                latitude = new double[length];
                longitude = new double[length];
                for(int i=0; i<length; i++){
                    jObj = jArray.getJSONObject(i);
                    latitude[i] = jObj.getDouble("latitude");
                    longitude[i] = jObj.getDouble("longitude");
                }
                /*jObj = jArray.getJSONObject(0);
                latitude = jObj.getDouble("latitude");
                longitude = jObj.getDouble("longitude");*/
            }
        }
        catch(Exception e){
            Log.e("Bus.onPostExecute", "Error parsing data " + e.toString());
        }
        //delegate.setLatLang(latitude,longitude);

        /*String msg = "Latitude:" + latitude + "; Longitude:" + longitude;
        Log.i("Bus", msg);*/

        if(status.equals("OK")){
            // get estimated time using Google Distance Matrix
            if (dLat != 0 && dLng != 0) {
                distance = new Distance(this,route_id,latitude,longitude,dLat,dLng);
                distance.execute();
                /*String msg = "Latitude:" + dLat + "; Longitude:" + dLng;
                Log.i("Bus", msg);*/
            }

            String msg = "Length: " + length;
            Log.i("Bus", msg);

            // set marker on map
            for(int i=0; i<length; i++) {
                marker[i].setVisible(true);
                marker[i].setPosition(new LatLng(latitude[i], longitude[i]));
            }

            //hide unused marker
            for(int i=length; i<marker.length; i++) {
                marker[i].setVisible(false);
            }
        }
        else{
            delegate.setLatLang(dLat,dLng);
            for(int i=0; i<marker.length; i++) {
                marker[i].setVisible(false);
            }
            List<LatLng> list = new ArrayList<LatLng>();
            list.add(new LatLng(0,0));
            list.add(new LatLng(0,0));
            polyLine.setPoints(list);
            eta_value.setText("");
            activity.showMsg(status);
            //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        }
    }

    public void updateDestinationCoordinate(double bus_latitude, double bus_longitude,
                                            double latitude, double longitude){
        dLat = latitude;
        dLng = longitude;
        delegate.setLatLang(bus_latitude,bus_longitude);
        if(bus_latitude != dLat && bus_longitude != dLng) {
            new TravelTime(eta_value, polyLine, bus_latitude, bus_longitude, dLat, dLng, key).execute();
        }
    }
}
package com.example.cheeseng.umsbustracker.WebConnector;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chee Seng on 17-Mar-17.
 * Query Google Distance Matrix for estimated time of arrival
 */

public class TravelTime extends AsyncTask <Void, Void, JSONObject> {

    /**
     * oLat,oLng = original latitude and longitude
     * dLat,dLng = destination latitude and longitude
     */
    private double oLat, oLng, dLat, dLng;
    private String key;
    private JSONObject jObj;
    private TextView eta_value;
    private Polyline polyLine;

    public TravelTime(TextView eta_value, Polyline polyLine, double oLat, double oLng,
                      double dLat, double dLng, String key){
        this.eta_value = eta_value;
        this.polyLine = polyLine;
        this.oLat = oLat;
        this.oLng = oLng;
        this.dLat = dLat;
        this.dLng = dLng;
        this.key = key;
        /*String msg = "Original:(" + oLat + "," + oLng + "); Des:(" + dLat + "," + dLng + ")";
        Log.i("Time", msg);*/
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        String json;
        try{
            /*String link = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + oLat
                    + "," + oLng + "&destinations=" + dLat + "," + dLng + "&key=" + key;*/
            String link = "https://maps.googleapis.com/maps/api/directions/json?origin=" + oLat
                    + "," + oLng + "&destination=" + dLat + "," + dLng +
                    "&sensor=false&mode=driving&alternatives=true" + "&key=" + key;

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
            Log.e("TravelTime.Background", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        JSONArray legs;
        JSONObject details,oDuration;
        String text;
       /* try{
            status = jsonObject.getString("status");
            if(status.equals("OK")){
                row = jsonObject.getJSONArray("rows");
                for(int i=0;i<row.length(); i++){
                    details = row.getJSONObject(i);
                    elements = details.getJSONArray("elements");
                    for(int j=0; j<elements.length(); j++) {
                        eDetails = elements.getJSONObject(j);
                        eStatus = eDetails.getString("status");
                        if(eStatus.equals("OK")) {
                            oDuration = eDetails.getJSONObject("duration");
                            text = oDuration.getString("text");
                            Log.i("Text", text);
                            eta_value.setText(text);
                        }
                    }
                }
            }
        }
        catch(Exception e){
            Log.e("TravelTime.PostExecute", "Error parsing data " + e.toString());
        }*/
        try {
            // Get and draw route
            JSONArray routeArray = jsonObject.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            polyLine.setPoints(list);

            // Get time needed
            legs = routes.getJSONArray("legs");
            details = legs.getJSONObject(0);
            oDuration = details.getJSONObject("duration");
            text = oDuration.getString("text");
            eta_value.setText(text);
        }
        catch (JSONException e) {
            Log.e("TravelTime.PostExecute", "Error parsing data " + e.toString());
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }
        return poly;
    }
}
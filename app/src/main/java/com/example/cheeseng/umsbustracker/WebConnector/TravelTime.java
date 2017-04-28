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

    public TravelTime(TextView eta_value,double oLat, double oLng, double dLat, double dLng, String key){
        this.eta_value = eta_value;
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
            String link = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + oLat
                    + "," + oLng + "&destinations=" + dLat + "," + dLng + "&key=" + key;

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
        JSONArray row,elements;
        JSONObject details,eDetails,oDuration;
        String status, text, eStatus;
        try{
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
        }
    }
}
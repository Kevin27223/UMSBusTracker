package com.example.cheeseng.umsbustracker.WebConnector;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
 * Created by Chee Seng on 18-Mar-17.
 * Get available route from database
 */

public class Route extends AsyncTask <Void, Void, JSONObject> {
    private JSONObject jObj;
    private Context context;
    private Spinner spinner;

    public Route(Context context, Spinner spinner){
        this.context = context;
        this.spinner = spinner;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        String json;
        try{
            String link = "http://umsbustrack.esy.es/Mobile/information.php?source=route";

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
            Log.e("Route.background", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        List<String> route_menu = new ArrayList<String>();
        JSONArray jArray;
        JSONObject jObj;
        try{
            jArray = jsonObject.getJSONArray("Route");
            for(int i=0; i<jArray.length(); i++){
                jObj = jArray.getJSONObject(i);
                route_menu.add(jObj.getString("Name"));
            }
        }
        catch (Exception e){
            Log.e("Route.post", "Error parsing data " + e.toString());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, route_menu);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
}

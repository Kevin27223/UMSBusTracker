package com.example.cheeseng.umsbustracker.WebConnector;

import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

import android.util.Log;

/**
 * Created by Chee Seng on 29-Dec-16.
 */

public class Announcement extends AsyncTask<String, Void, JSONObject> {
    private TextView announcement_title1, announcement_title2, announcement_title3,
            announcement_content1, announcement_content2, announcement_content3,
            announcement_date1, announcement_date2, announcement_date3;
    private String json = "";
    private JSONObject jobj;

    public Announcement(TextView title1, TextView title2, TextView title3, TextView content1,
                        TextView content2, TextView content3, TextView date1, TextView date2,
                        TextView date3){
        announcement_title1 = title1;
        announcement_title2 = title2;
        announcement_title3 = title3;
        announcement_content1 = content1;
        announcement_content2 = content2;
        announcement_content3 = content3;
        announcement_date1 = date1;
        announcement_date2 = date2;
        announcement_date3 = date3;
    }

    @Override
    protected JSONObject doInBackground(String... arg0) {
        try{
            String link = "http://umsbustrack.esy.es/Mobile/announcement.php";

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";

            while((line = in.readLine()) != null){
                sb.append(line);
            }

            in.close();
            json = sb.toString();
            jobj = new JSONObject(json);
        }
        catch(Exception e){
            Log.e("Announcement.java", "Error parsing data " + e.toString());
        }
        return jobj;
    }

    @Override
    protected void onPostExecute(JSONObject result){
        JSONArray jArray = new JSONArray();
        JSONObject jObj = new JSONObject();
        String title1 = "";
        String title2 = "";
        String title3 = "";
        String content1 = "";
        String content2 = "";
        String content3 = "";
        String date1 = "";
        String date2 = "";
        String date3 = "";
        try {
            jArray = result.getJSONArray("Announcement");

            for(int i=0; i<jArray.length(); i++) {
                jObj = jArray.getJSONObject(i);
                int id = jObj.getInt("id");
                if(id == 0) {
                    title1 = jObj.getString("Title");
                    content1 = jObj.getString("Content");
                    date1 = jObj.getString("Date");
                }
                else if(id==1){
                    title2 = jObj.getString("Title");
                    content2 = jObj.getString("Content");
                    date2 = jObj.getString("Date");
                }
                else{
                    title3 = jObj.getString("Title");
                    content3 = jObj.getString("Content");
                    date3 = jObj.getString("Date");
                }
            }
        }
        catch(JSONException e){
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        this.announcement_title1.setText(title1);
        this.announcement_title2.setText(title2);
        this.announcement_title3.setText(title3);
        this.announcement_content1.setText(content1);
        this.announcement_content2.setText(content2);
        this.announcement_content3.setText(content3);
        this.announcement_date1.setText(date1);
        this.announcement_date2.setText(date2);
        this.announcement_date3.setText(date3);
    }
}

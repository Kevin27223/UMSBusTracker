package com.example.cheeseng.umsbustracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cheeseng.umsbustracker.WebConnector.Announcement;
/*import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;*/
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Chee Seng on 24-Nov-16.
 */

public class AnnouncementActivity extends AppCompatActivity {

    private TextView announcement_title1, announcement_title2, announcement_title3,
            announcement_content1, announcement_content2, announcement_content3,
            announcement_date1, announcement_date2, announcement_date3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Toolbar toolbar = (Toolbar) findViewById(R.id.announcement_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Button button = (Button) findViewById(R.id.announcement_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse("http://umsbustrack.esy.es/index.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        announcement_title1 = (TextView) findViewById(R.id.announcement_title1);
        announcement_content1 = (TextView) findViewById(R.id.announcement_content1);
        announcement_date1 = (TextView) findViewById(R.id.announcement_date1);

        announcement_title2 = (TextView) findViewById(R.id.announcement_title2);
        announcement_content2 = (TextView) findViewById(R.id.announcement_content2);
        announcement_date2 = (TextView) findViewById(R.id.announcement_date2);

        announcement_title3 = (TextView) findViewById(R.id.announcement_title3);
        announcement_content3 = (TextView) findViewById(R.id.announcement_content3);
        announcement_date3 = (TextView) findViewById(R.id.announcement_date3);

        new Announcement(announcement_title1, announcement_title2, announcement_title3,
                announcement_content1, announcement_content2, announcement_content3,
                announcement_date1, announcement_date2, announcement_date3).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

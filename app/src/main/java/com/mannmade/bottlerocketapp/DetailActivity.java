package com.mannmade.bottlerocketapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    Bundle extras;
    ViewGroup detailLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailLayout = (ViewGroup) findViewById(R.id.detail_layout);
        extras = getIntent().getExtras();
        if (extras != null){
            setTitle(extras.getString("name"));
            for (String key: extras.keySet()){
                //Loop thru and create dynamic text views for data returned
                String detailPair = key + ": " + extras.getString(key);
                TextView newTextView = new TextView(this);
                newTextView.setText(detailPair);
                newTextView.setTextColor(Color.parseColor("#4169E1"));
                newTextView.setTextSize(28);
                newTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // insert into ViewGroup
                detailLayout.addView(newTextView);
                System.out.println(extras.get(key));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            AlertDialog.Builder infoBuilder = new AlertDialog.Builder(this);
            infoBuilder.setTitle("About My Logic");
            infoBuilder.setMessage("This application was made for APIs 16 to 23 and demonstrates separation of concerns, listviews, layouts, connection logic, adapters, viewholder pattern, recycling of views, MVC, JSON Parsing, animations, SharedPreferences, fragments, intents, dialogs, and much more.");
            infoBuilder.setPositiveButton(R.string.very_cool, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            infoBuilder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

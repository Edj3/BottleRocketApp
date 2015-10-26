package com.mannmade.bottlerocketapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/* Created by EJ Mann 9/19/2015 */
public class MainActivity extends AppCompatActivity {
    String json;
    ConnectionManager cManage;
    ListView mainListView;
    ListAdapter storeAdapter;
    ArrayList<LinkedHashMap<String, String>> storeList;
    ArrayList<Bitmap> storeImageList;
    boolean connected;
    String fileStores = "bottleStores.txt";
    Fragment loadingFragment;
    FragmentManager fm;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainListView = (ListView) findViewById(R.id.store_list);
        connected = checkConnectivity();
        fm = getFragmentManager();
        loadingFragment = fm.findFragmentById(R.id.loading_progress);
        if(connected){
            cManage = new ConnectionManager(this.getApplicationContext());
            json = "";
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean cacheLoaded = prefs.getBoolean("cacheLoaded", false);
            //grab data from cache internal storage
            if (cacheLoaded && (storeList == null || storeList.isEmpty())){
                storeList = readStoresFromInternalStorage();
            }

            //if list is null after caching, throw toast message and perform download
            if (storeList == null || storeList.isEmpty()){
                Toast.makeText(this, R.string.cache_unavailable, Toast.LENGTH_SHORT).show();
                new ConnectToWebTask().execute();
            }
            //make images persist after first download, if list does not exist in savedstate, then download images
            if (savedInstanceState != null && savedInstanceState.containsKey("storeImageList")){
                storeImageList = (ArrayList<Bitmap>) savedInstanceState.getSerializable("storeImages");
                createListAdapter();
            }else{
                new DownloadImageTask().execute();
            }

        }else{
            AlertDialog.Builder connectBuilder = new AlertDialog.Builder(this);
            connectBuilder.setTitle("Connection Unavailable");
            connectBuilder.setMessage("An internet connection is unavailable to run this application. Please reopen the app after enabling a working internet connection.");
            connectBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            connectBuilder.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //outState.putSerializable("stores", storeList);
        outState.putSerializable("storesImages", storeImageList);
    }

    public void saveStoresToInternalStorage() {
        try {
            FileOutputStream fos = this.openFileOutput(fileStores, Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(storeList);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        prefs.edit().putBoolean("cacheLoaded", true).apply();
    }

    public ArrayList<LinkedHashMap<String,String>> readStoresFromInternalStorage() {
        Object objectList;
        try {
            FileInputStream fis = this.openFileInput(fileStores);
            ObjectInputStream oi = new ObjectInputStream(fis);
            objectList = oi.readObject();
            oi.close();
            return (ArrayList<LinkedHashMap<String,String>>) objectList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean checkConnectivity(){
        ConnectivityManager userConnection = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connectInfo = userConnection.getActiveNetworkInfo();

        return (connectInfo != null && connectInfo.isConnectedOrConnecting());
    }

    protected static class ViewHolderItem {
        ImageView logoViewItem;
        TextView phoneViewItem;
        TextView addressViewItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            infoBuilder.setMessage("This application was made for APIs 16 to 23 and demonstrates separation of concerns, listviews, layouts, connection logic, adapters, viewholder pattern, recycling of views, MVC, JSON Parsing, Animations, SharedPreferences, fragments, intents, dialogs, and much more.");
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

    private void createListAdapter(){
        //Create adapter and generate list of getting json data
        storeAdapter = new BaseAdapter() {
            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int i) {
                return true;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public int getCount() {
                return storeList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                //I did not have to use view recycling for this application, but I did for efficiency and demonstration
                //Use view holder for getView function of all lists for smoother scrolling
                ViewHolderItem viewholder;
                if (view == null) {
                    //Inflate the view whenever it is null (this will happen for all visible list items on initial display of screen
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.store_list_item, viewGroup, false);
                    //only create view holder when view is null
                    viewholder = new ViewHolderItem();
                    viewholder.logoViewItem = (ImageView) view.findViewById(R.id.store_logo);
                    viewholder.addressViewItem = (TextView) view.findViewById(R.id.store_address);
                    viewholder.phoneViewItem = (TextView) view.findViewById(R.id.store_phone);
                    //setting the view tag to the viewholder here so that it always uses the default settings of the imageview
                    view.setTag(viewholder);

                    viewholder.addressViewItem.setText(storeList.get(i).get("address"));
                    viewholder.phoneViewItem.setText(storeList.get(i).get("phone"));
                    viewholder.logoViewItem.setImageBitmap(storeImageList.get(i));
                } else {
                    //Any recycled views (when you scroll past end of screen) will borrow the existing layout from the viewholder and will need to have their data set updated
                    viewholder = (ViewHolderItem) view.getTag();

                    viewholder.addressViewItem.setText(storeList.get(i).get("address"));
                    viewholder.phoneViewItem.setText(storeList.get(i).get("phone"));
                    viewholder.logoViewItem.setImageBitmap(storeImageList.get(i));

                }
                return view;
            }

            @Override
            public int getItemViewType(int i) {
                return 1;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
        //remove loading fragment
        fm.beginTransaction().hide(loadingFragment).commit();
        mainListView.setAdapter(storeAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getBaseContext(), DetailActivity.class);
                for (String key : storeList.get(position).keySet()) {
                    detailIntent.putExtra(key, storeList.get(position).get(key));
                }
                startActivity(detailIntent);
            }
        });
    }

    public void refresh(View v){
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        v.startAnimation(rotateAnimation);
        storeList = new ArrayList<>();
        storeImageList = new ArrayList<>();
        new ConnectToWebTask().execute();
        new DownloadImageTask().execute();
    }

    private class ConnectToWebTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fm.beginTransaction().show(loadingFragment).commit();
        }

        protected Boolean doInBackground(Void... voids) {
            try{
                //only do this if cache did not populate list of stores
                if (storeList == null || storeList.isEmpty()){
                    json = cManage.connectToURL("http://sandbox.bottlerocketapps.com/BR_Android_CodingExam_2015_Server/stores.json");
                    JSONParser jParser = new JSONParser();
                    storeList = jParser.getJSONforString(json);
                    //save Stores & Images to Internal Storage cache
                    saveStoresToInternalStorage();
                }
                return false;
            }catch(Exception e){
                e.printStackTrace();
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean isError) {
            super.onPostExecute(isError);
            if (isError){
                AlertDialog.Builder connectBuilder = new AlertDialog.Builder(getApplicationContext());
                connectBuilder.setTitle("List Download Failed");
                connectBuilder.setMessage("A problem occurred when downloading the list of stores. Please click the refresh icon to reattempt the download.");
                connectBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                connectBuilder.show();
            }else{
                System.out.println("The JSON returned =");
                System.out.println(json);
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fm.beginTransaction().show(loadingFragment).commit();
        }

        protected Boolean doInBackground(Void... Void) {
            try{
                //download all images into the list that holds
                if (storeImageList == null || storeImageList.isEmpty()){
                    storeImageList = new ArrayList<>();
                    for (int i = 0; i < storeList.size(); i++){
                        URL logoURL = new URL(storeList.get(i).get("storeLogoURL"));
                        storeImageList.add(BitmapFactory.decodeStream(logoURL.openConnection().getInputStream()));
                    }
                }
                return false;
            }catch(Exception e){
                e.printStackTrace();
               return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean isError) {
            super.onPostExecute(isError);
            if (!isError) {
                createListAdapter();
            }
        }
    }
}

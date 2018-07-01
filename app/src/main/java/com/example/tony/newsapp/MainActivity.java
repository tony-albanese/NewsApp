package com.example.tony.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {
    private static String searchUrl;
    private StoryListAdapter adapter;
    private LoaderManager manager;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private TextView emptyView;
    private View loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up the default Url
        searchUrl =getString(R.string.default_url);
        loadingIndicator = findViewById(R.id.progress_bar);

        //Set up the ListView and Adapter and bind them.
        ListView storyListView = (ListView) findViewById(R.id.storyListView);
        adapter = new StoryListAdapter(this, new ArrayList<Story>());
        storyListView.setAdapter(adapter);

        //Set the onClickListener so the website for a particular story is opened when the user clicks on it.
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Story story = adapter.getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(story.getStoryUrl()));
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_message), Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Setup listener on the button.The search function is called when the button is pressed.
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        //Setup the empty view.
        emptyView = (TextView) findViewById(R.id.emptyView);
        storyListView.setEmptyView(emptyView);

        //Set up the network information objects.
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        //Check if the network is connected and load the manager if it is. Display an error if not.
        if (networkInfo != null && networkInfo.isConnected()) {
            manager = getLoaderManager();
            manager.initLoader(1, null, this);

        } else {
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);
            emptyView.setText(R.string.noInternet);
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        emptyView.setVisibility(View.GONE);
        URL url = StoryUtilties.createURL(searchUrl);
        return new StoryLoader(MainActivity.this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> loadedList) {
        loadingIndicator.setVisibility(View.GONE);
        adapter.clear();
        if (loadedList != null && !loadedList.isEmpty()) {
            adapter.addAll(loadedList);
        }else{
            TextView emptyView = (TextView) findViewById(R.id.emptyView);
            emptyView.setText(R.string.noResults);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        adapter.clear();
    }

    //This method is to inflate the menu for the settings. It is taken from the Udacity course lesson.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //This method opens the settings Activity when the user clicks the settings button. It is from the Udacity lesson.
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(){

        networkInfo = connectivityManager.getActiveNetworkInfo();
        //Check if there is still an internet connection. Only bother searching if connection is active.
        if(networkInfo !=null && networkInfo.isConnected()){
            adapter.clear();
            loadingIndicator.setVisibility(View.VISIBLE);
            //Retrieve the user's search terms from the EditText view.
            EditText searchTermsView = (EditText) findViewById(R.id.search_terms);
            String searchString = searchTermsView.getText().toString();

            //Strip the search terms of leading and trailing whitespace.
            searchString = searchString.trim();
            searchString = searchString.replace(" ", " AND ");

            //Set up the url based on the user's input terms.
            searchUrl = StoryUtilties.generateUrl(this,searchString);
            manager.restartLoader(1,null, this);
        } else{
            emptyView.setText(R.string.noInternet);
            adapter.clear();
        }



    }



}

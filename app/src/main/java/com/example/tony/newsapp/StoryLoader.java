package com.example.tony.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 22.10.17.
 */

public class StoryLoader extends AsyncTaskLoader<List<Story>> {
    private URL searchUrl;

    public StoryLoader(Context context, URL url){
        super(context);
        searchUrl = url;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground(){
        if(searchUrl == null){
            List<Story> emptyList = new ArrayList<>();
            return emptyList;
        }

        String jsonResponse = StoryUtilties.makeHttpRequest(searchUrl);
        List<Story> list = StoryUtilties.parseJsonData(jsonResponse);
        return list;
    }
}

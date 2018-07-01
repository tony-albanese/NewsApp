package com.example.tony.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tony on 21.10.17.
 */

public abstract class StoryUtilties {

    private static final String LOG_TAG = StoryUtilties.class.getSimpleName();

    //This method converts a string to URL object. It is taken from the Soonami app.
    public static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating URL: ", e);
            return null;
        }

        return url;
    }

    //This method makes the HTTP request to get the JSON data. It is taken from the BookReport app.
    public static String makeHttpRequest(URL url) {
        String jsonResponse = "";

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Problem closing stream", e);
                }

            }
        }
        return jsonResponse;
    }

    //This method reads data from the HTTP stream and buffers it. It has been reused from the BookList app.
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

   public static ArrayList<Story> parseJsonData(String jsonResponse){
        ArrayList<Story> stories = new ArrayList<Story>();
        String title;
        String publicationDate;
        String author = null;
        String section;
        String url;

       try{
           JSONObject object = new JSONObject(jsonResponse);

           if(object.has("response")){
               JSONObject response = object.getJSONObject("response");
               if(response.has("results")){
                   JSONArray results = response.getJSONArray("results");
                   for(int i = 0; i < results.length(); i++) {
                       JSONObject story = results.getJSONObject(i);
                       if(story.has("webTitle")){
                           title = story.getString("webTitle");
                       } else{title = "No title available";}

                       if(story.has("webPublicationDate")){
                           publicationDate = StoryUtilties.generateDate(story.getString("webPublicationDate"));
                       } else{publicationDate = "No date info.";}

                       if(story.has("sectionName")){
                           section = story.getString("sectionName");
                       }else{section = "Misc";}

                       if(story.has("webUrl")){
                           url = story.getString("webUrl");
                       } else{url = null;}

                       //We have to get the JSON Array that holds all the tags
                       if(story.has("tags")){
                           JSONArray tagsArray = story.getJSONArray("tags");
                           ArrayList<String> authorList = new ArrayList<>();
                           for(int j = 0; j < tagsArray.length(); j++){
                               JSONObject currentTag = tagsArray.getJSONObject(j);
                               if(currentTag.has("webTitle")){
                                   authorList.add(currentTag.optString("webTitle"));
                               }

                           }
                           author = TextUtils.join(", ", authorList);
                       }else{
                          author = "No Author";
                       }

                       stories.add(new Story(title, section, publicationDate, url, author));

                   }
               }else{return stories;}

           } else {
               return stories;
           }

       }catch(JSONException e){
           Log.e(LOG_TAG, e.toString());

       }
        return stories;
    }

    //This method parses the date provided by the API and formats to a more reader friendly format.
    public static String generateDate(String stringDate){
        String pubDate = stringDate.substring(0, stringDate.indexOf("T"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date date = dateFormat.parse(stringDate);
            SimpleDateFormat finalFormat = new SimpleDateFormat("EEE MMM d, yyyy");
            pubDate = finalFormat.format(date);

        }catch (ParseException e){
            Log.e(LOG_TAG, e.toString());
            return "No date info";
        }
        return pubDate;
    }

    //This method takes the user's search terms and saved preferences and builds a search Url.
    public static String generateUrl(Context c, String searchTerms){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        String num_stories = sharedPreferences.getString(c.getString(R.string.settings_number_of_stories_key), c.getString(R.string.settings_number_of_stories_default));

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("content.guardianapis.com").appendPath("search");
        builder.appendQueryParameter("q" , searchTerms);
        builder.appendQueryParameter("show-tags", "contributor");
        builder.appendQueryParameter("page-size", num_stories);
        builder.appendQueryParameter("api-key", c.getString(R.string.api_key));

        return builder.toString();
    }
}

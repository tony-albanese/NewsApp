package com.example.tony.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tony on 21.10.17.
 */

public class StoryListAdapter extends ArrayAdapter<Story> {

        public StoryListAdapter(Context context, ArrayList<Story> stories){
            super(context, 0, stories);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View listItemView = convertView;
            if(listItemView == null){
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.story_list_item_view, parent, false);
            }

            Story currentStory = getItem(position);

            TextView storyTitleView = (TextView) listItemView.findViewById(R.id.storyTitle);
            TextView storySectionView = (TextView) listItemView.findViewById(R.id.storySection);
            TextView storyPublicationView = (TextView) listItemView.findViewById(R.id.storyPubDate);
            TextView storyAuthorView = (TextView)listItemView.findViewById(R.id.authorView);

            storyTitleView.setText(currentStory.getStoryTitle());
            storySectionView.setText(currentStory.getStorySectionName());
            storyPublicationView.setText(currentStory.getStoryPublicationDate());
            storyAuthorView.setText(currentStory.getStoryAuthor());

            return listItemView;
        }
}

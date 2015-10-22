package com.example.yasu.nicodicspeaker.nicodic;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Load new words using ScrapNewWordAPI
 * Created by yasu on 15/10/23.
 */
public class ScrapNewWordLoader extends AsyncTaskLoader<ArrayList<String>>{
    private final String LOG_TAG = ScrapNewWordLoader.class.getSimpleName();

    public ScrapNewWordLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<String> loadInBackground() {
        try {
            return ScrapNewWordAPI.getNewWordList();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}

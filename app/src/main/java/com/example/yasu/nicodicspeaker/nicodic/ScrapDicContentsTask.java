package com.example.yasu.nicodicspeaker.nicodic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yasu.nicodicspeaker.MainActivity;
import com.example.yasu.nicodicspeaker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yasu on 15/09/29.
 */
public class ScrapDicContentsTask extends AsyncTask<String, Void, ArrayList<String>> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Context context;

    public ScrapDicContentsTask(Context context) {
        this.context = context;
    }

    //最近更新された単語を取得する
    public ArrayList<String> getDicContents(String word) throws IOException {
        final String baseUrl = "http://dic.nicovideo.jp/a/";
        Document document = Jsoup.connect(baseUrl + word).get();

        Elements elements = document.select("div.article p");

        ArrayList<String> list = new ArrayList<>();
        for (Element element : elements) {
            list.add(element.text());
        }
        return list;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        String word = params[0];
        try {
            return getDicContents(word);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> list) {
        for(String s : list){
            Log.i(LOG_TAG, s);
        }
    }

}

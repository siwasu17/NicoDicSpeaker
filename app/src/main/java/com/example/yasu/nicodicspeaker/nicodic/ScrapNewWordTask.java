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


public class ScrapNewWordTask extends AsyncTask<String, Void, ArrayList<String>> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Context context;

    public ScrapNewWordTask(Context context) {
        this.context = context;
    }

    //最近更新された単語を取得する
    public ArrayList<String> getNewWordList() throws IOException {
        final String dicListUrl = "http://dic.nicovideo.jp/m/u/a/1-";
        Document document = Jsoup.connect(dicListUrl).get();

        Elements elements = document.select("div.overflow-title a");

        ArrayList<String> list = new ArrayList<>();
        for (Element element : elements) {
            list.add(element.text());
        }
        return list;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        try {
            return getNewWordList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1);
        for (String s : list) {
            Log.i(LOG_TAG, s);
            adapter.add(s);
        }

        //TODO: このへんの画面更新系を分離する方法が知りたい
        MainActivity activity = (MainActivity) this.context;
        ListView listView = (ListView) activity.findViewById(R.id.nico_word_list);
        // アダプターを設定します
        listView.setAdapter(adapter);

        //アイテムが選択された時の動作
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                Log.i(LOG_TAG, "SELECT: " + item);

                //辞書コンテンツの呼び出し
                //Taskをネストしていいのだろうか？？
                ScrapDicContentsTask dicContentsTask = new ScrapDicContentsTask(view.getContext());
                dicContentsTask.execute(item);
            }
        });
    }
}


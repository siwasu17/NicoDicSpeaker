package com.example.yasu.nicodicspeaker;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yasu.nicodicspeaker.nicodic.ScrapDicContentsTask;
import com.example.yasu.nicodicspeaker.nicodic.ScrapNewWordLoader;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>>{
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    //Loaderの初期化と起動
    public void startNewWordLoad(){
        Bundle args = new Bundle();
        getLoaderManager().initLoader(0,args,this);
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        //どのLoaderを呼び出すか指定
        return new ScrapNewWordLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        //Load完了時にデータが渡ってくるのでよしなに表示する
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for (String s : data) {
            Log.i(LOG_TAG, s);
            adapter.add(s);
        }

        ListView listView = (ListView)findViewById(R.id.nico_word_list);
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
                ScrapDicContentsTask dicContentsTask = new ScrapDicContentsTask(view.getContext());
                dicContentsTask.execute(item);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {
        //Nothing to do
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startNewWordLoad();
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

        switch(id) {
            case R.id.action_settings:
                Intent settingItt = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingItt);

                break;
            case R.id.action_random:
                Log.i(LOG_TAG, "SLIDE SHOW MODE");

                ListView listView = (ListView) findViewById(R.id.nico_word_list);
                int itemCount = listView.getCount();
                //とりあえずシーケンシャルに再生
                for (int i = 0; i < itemCount; i++) {
                    String word = (String) listView.getItemAtPosition(i);
                    Intent itt = new Intent();
                    itt.setClass(MainActivity.this, DicSpeakIntentService.class);
                    itt.setAction(DicSpeakIntentService.ACTION_SPEAK);
                    itt.putExtra(DicSpeakIntentService.EXTRA_PARAM_WORD, word);
                    startService(itt);
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

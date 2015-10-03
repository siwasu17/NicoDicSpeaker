package com.example.yasu.nicodicspeaker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.yasu.nicodicspeaker.nicodic.ScrapDicContentsTask;
import com.example.yasu.nicodicspeaker.nicodic.ScrapNewWordTask;

public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrapNewWordTask newWordTask = new ScrapNewWordTask(MainActivity.this);
        newWordTask.execute();

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
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_random) {
            Log.i(LOG_TAG, "SLIDE SHOW MODE");

            ListView listView = (ListView)findViewById(R.id.nico_word_list);
            int itemCount = listView.getCount();
            //とりあえずシーケンシャルに
            for(int i = 0;i < itemCount;i++){
                String word = (String) listView.getItemAtPosition(i);
                Intent itt = new Intent();
                itt.setClass(this,DicSpeakIntentService.class);
                itt.setAction(DicSpeakIntentService.ACTION_SPEAK);
                itt.putExtra(DicSpeakIntentService.EXTRA_PARAM_WORD,word);
                startService(itt);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

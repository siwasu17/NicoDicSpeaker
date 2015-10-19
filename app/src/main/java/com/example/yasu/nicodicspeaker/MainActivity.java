package com.example.yasu.nicodicspeaker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.yasu.nicodicspeaker.docomo.VoicePlayer;
import com.example.yasu.nicodicspeaker.nicodic.ScrapDicContentsTask;
import com.example.yasu.nicodicspeaker.nicodic.ScrapNewWordTask;

import de.greenrobot.event.EventBus;

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
    protected void onStart() {
        super.onStart();

        //音声プレイヤーにイベントが行くように設定
        EventBus.getDefault().register(VoicePlayer.getInstance());
    }

    @Override
    protected void onStop() {
        super.onStop();

        //音声プレイヤーのイベントバス登録を解除
        EventBus.getDefault().unregister(VoicePlayer.getInstance());
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

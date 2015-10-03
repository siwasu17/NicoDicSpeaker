package com.example.yasu.nicodicspeaker;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.yasu.nicodicspeaker.docomo.AiTalkTask;
import com.example.yasu.nicodicspeaker.nicodic.ScrapDicContentsTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DicSpeakIntentService extends IntentService {
    static final String LOG_TAG = DicSpeakIntentService.class.getSimpleName();
    public static final String ACTION_SPEAK = "speak";

    public static final String EXTRA_PARAM_WORD = "word";

    public DicSpeakIntentService() {
        super("DicSpeakIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SPEAK.equals(action)) {
                final String word = intent.getStringExtra(EXTRA_PARAM_WORD);
                Log.i(LOG_TAG,"Start to speak @IntentService: " + word);
                ScrapDicContentsTask dicContentsTask = new ScrapDicContentsTask(this);
                try {
                    //AsyncTaskをここで呼べないので、生の動作をさせる
                    ArrayList<String> contents =  dicContentsTask.getDicContents(word);
                    AiTalkTask talkTask = new AiTalkTask(this);
                    for(String w: contents){
                        talkTask.callAiTalkAPI(w);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

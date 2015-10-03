package com.example.yasu.nicodicspeaker.docomo;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import com.example.yasu.nicodicspeaker.R;

import jp.ne.docomo.smt.dev.aitalk.AiTalkTextToSpeech;
import jp.ne.docomo.smt.dev.aitalk.data.AiTalkSsml;
import jp.ne.docomo.smt.dev.common.http.AuthApiKey;

/**
 * Created by yasu on 15/09/29.
 */
public class AiTalkTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = AiTalkTask.class.getSimpleName();
    private final String API_KEY;

    private Context context;

    public AiTalkTask(Context context) {
        this.context = context;
        this.API_KEY = context.getResources().getString(R.string.aitalk_api_key);
    }


    public void speech(byte[] data){
        int bps = 16000;
        // バッファサイズを取得
        int bufSize = AudioTrack.getMinBufferSize(bps, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // AudioTrackインスタンスを生成
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, bps, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STREAM);
        // 再生
        audioTrack.play();
        audioTrack.write(data, 0, data.length);
    }

    //外から呼ぶ用
    public void callAiTalkAPI(String word){
        //認証
        AuthApiKey.initializeAuth(API_KEY);

        // SSMLテキスト作成
        AiTalkSsml ssml = new AiTalkSsml();
        ssml.startVoice("maki");
        ssml.addText(word);
        ssml.endVoice();
        // SSMLテキストの音声変換
        // 戻り値は音声ＰＣＭリニア（１６ビット１６０００ＢＰＳモノラル）のバイト配列
        AiTalkTextToSpeech speech = new AiTalkTextToSpeech();
        try {
            byte[] resultData = speech.requestAiTalkSsmlToSound(ssml.makeSsml());
            speech.convertByteOrder16(resultData);
            Log.i(LOG_TAG, "PCM Audio data load OK [" + resultData.length + " Bytes]");
            speech(resultData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String sentence = params[0];
        callAiTalkAPI(sentence);
        return null;
    }

}

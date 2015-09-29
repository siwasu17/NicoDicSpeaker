package com.example.yasu.nicodicspeaker.docomo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import jp.ne.docomo.smt.dev.aitalk.AiTalkTextToSpeech;
import jp.ne.docomo.smt.dev.aitalk.data.AiTalkSsml;
import jp.ne.docomo.smt.dev.common.http.AuthApiKey;

/**
 * Created by yasu on 15/09/29.
 */
public class AiTalkTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    final String API_KEY = "XXX";

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

    @Override
    protected Void doInBackground(String... params) {
        String sentence = params[0];

        //認証
        AuthApiKey.initializeAuth(API_KEY);

        // SSMLテキスト作成
        AiTalkSsml ssml = new AiTalkSsml();
        ssml.startVoice("maki");
        ssml.addText(sentence);
        ssml.endVoice();
        // SSMLテキストの音声変換
        // 戻り値は音声ＰＣＭリニア（１６ビット１６０００ＢＰＳモノラル）のバイト配列
        AiTalkTextToSpeech speech = new AiTalkTextToSpeech();
        try {
            byte[] resultData = speech.requestAiTalkSsmlToSound(ssml.makeSsml());
            speech.convertByteOrder16(resultData);
            Log.i(LOG_TAG, "PCM Audio data load OK");
            speech(resultData);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}

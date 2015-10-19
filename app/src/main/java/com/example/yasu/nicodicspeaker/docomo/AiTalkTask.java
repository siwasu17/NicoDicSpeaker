package com.example.yasu.nicodicspeaker.docomo;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.yasu.nicodicspeaker.R;
import com.example.yasu.nicodicspeaker.SettingsActivity;

import de.greenrobot.event.EventBus;
import jp.ne.docomo.smt.dev.aitalk.AiTalkTextToSpeech;
import jp.ne.docomo.smt.dev.aitalk.data.AiTalkSsml;
import jp.ne.docomo.smt.dev.common.http.AuthApiKey;

/**
 * Created by yasu on 15/09/29.
 */
public class AiTalkTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = AiTalkTask.class.getSimpleName();
    private final String API_KEY;
    private final String VOICE_TYPE;
    private final String VOICE_PITCH;
    private final String VOICE_PITCH_RANGE;
    private final String VOICE_SPEED;

    private Context context;

    public AiTalkTask(Context context) {
        this.context = context;
        this.API_KEY = context.getResources().getString(R.string.aitalk_api_key);

        //各種設定読み込み
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.VOICE_TYPE = sp.getString(SettingsActivity.PREF_KEY_VOICE_TYPE,"maki");
        this.VOICE_PITCH = sp.getString(SettingsActivity.PREF_KEY_VOICE_PITCH,"1.0");
        this.VOICE_PITCH_RANGE = sp.getString(SettingsActivity.PREF_KEY_VOICE_PITCH_RANGE,"1.0");
        this.VOICE_SPEED = sp.getString(SettingsActivity.PREF_KEY_VOICE_SPEED,"1.0");

        Log.i(LOG_TAG,"VOICE TYPE: " + VOICE_TYPE);
        Log.i(LOG_TAG,"PITCH: " + VOICE_PITCH
                + ", PITCH_RANGE: " + VOICE_PITCH_RANGE
                + ", SPEED: " + VOICE_SPEED);

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
        /*
            pitch - テキストを読み上げるベースラインピッチ(声の高さ？) 　　　　　　　　基準値は1.0 で、0.50から2.00 の範囲で指定できます。
            range - テキストを読み上げるピッチレンジ (抑揚？)　　　　　　　　基準値は1.0 で、0.00から2.00の範囲で指定できます。
            rate - テキストを読み上げる速度 　　　　　　　　基準値は1.0 で、0.50から4.00 の範囲で指定できます。
            volume - テキストを読み上げる音量 　　　　　　　　基準値は1.0 で、0.00から2.00 の範囲で指定できます。
            以下メソッド呼び出しの順序重要(だけどマニュアルPDFのほうは間違ってたorz
            Javadocによると=>使用する場合は、startVoice()の後に、startProsody()を実行してください。終了する前は、必ずendProsody()を実行してください。
         */
        ssml.startVoice(this.VOICE_TYPE);
        ssml.startProsody(
                Float.parseFloat(this.VOICE_PITCH),
                Float.parseFloat(this.VOICE_PITCH_RANGE),
                Float.parseFloat(this.VOICE_SPEED),
                1.0f);

        ssml.addText(word);
        ssml.endProsody();
        ssml.endVoice();
        // SSMLテキストの音声変換
        // 戻り値は音声ＰＣＭリニア（１６ビット１６０００ＢＰＳモノラル）のバイト配列
        AiTalkTextToSpeech speech = new AiTalkTextToSpeech();
        try {
            byte[] resultData = speech.requestAiTalkSsmlToSound(ssml.makeSsml());
            speech.convertByteOrder16(resultData);
            Log.i(LOG_TAG, "PCM Audio data load OK [" + resultData.length + " Bytes]");
            //Send voice data
            EventBus.getDefault().post(new VoiceData(resultData));
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

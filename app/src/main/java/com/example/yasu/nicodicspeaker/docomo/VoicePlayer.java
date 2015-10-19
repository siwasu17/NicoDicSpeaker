package com.example.yasu.nicodicspeaker.docomo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by yasu on 15/10/20.
 */
public class VoicePlayer {
    private final String LOG_TAG = AiTalkTask.class.getSimpleName();
    //private ArrayList<VoiceData> voices = new ArrayList<>();
    private final int BPS = 16000;
    private AudioTrack audioTrack;
    private boolean isPlaying = false;

    private static VoicePlayer instance = new VoicePlayer();

    public static VoicePlayer getInstance(){
        return instance;
    }

    private VoicePlayer(){
        // バッファサイズを取得
        int bufSize = AudioTrack.getMinBufferSize(BPS, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // AudioTrackインスタンスを生成
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, BPS, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STREAM);
    }

    public void onEvent(VoiceData voiceData){
        Log.i(LOG_TAG, "Voice Event");

        if(!isPlaying){
            audioTrack.play();
            isPlaying = true;
        }
        audioTrack.write(voiceData.getVoiceBinary(),0,voiceData.getLength());
    }

    public void stop(){
        //TODO:メインスレッドが動いてしまうのでうまく止まらないので治したい
        audioTrack.flush();
        audioTrack.stop();
        isPlaying = false;
    }

}

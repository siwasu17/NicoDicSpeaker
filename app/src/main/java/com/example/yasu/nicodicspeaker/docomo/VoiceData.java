package com.example.yasu.nicodicspeaker.docomo;

/**
 * Created by yasu on 15/10/20.
 */
public class VoiceData {
    private byte[] voiceBinary;

    public VoiceData(byte[] data){
        voiceBinary = data;
    }

    public byte[] getVoiceBinary(){
        return voiceBinary;
    }

    public int getLength(){
        return voiceBinary.length;
    }
}

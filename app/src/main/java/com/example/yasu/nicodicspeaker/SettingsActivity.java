package com.example.yasu.nicodicspeaker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;


public class SettingsActivity extends ActionBarActivity {
    //設定値を取得するときのキー
    public static final String PREF_KEY_VOICE_TYPE = "key_voice_type";
    public static final String PREF_KEY_VOICE_PITCH = "key_voice_pitch";
    public static final String PREF_KEY_VOICE_PITCH_RANGE = "key_voice_pitch_range";
    public static final String PREF_KEY_VOICE_SPEED = "key_voice_speed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //PrefFragmentの呼び出し
        getFragmentManager()
                .beginTransaction()
                .replace(
                        android.R.id.content,
                        new PrefFragment()
                )
                .commit();
    }


    public static class PrefFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_settings);
        }

        // 設定値が変更されたときのリスナーを登録
        @Override
        public void onResume() {
            super.onResume();
            //現在のsharedPreferenceを取得？すでにxmlを関連付けてるからファイル名とかいらないのだろうか
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            sp.registerOnSharedPreferenceChangeListener(listener);

            setSummaryVoice();
        }

        // 設定値が変更されたときのリスナー登録を解除
        @Override
        public void onPause() {
            super.onPause();
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            sp.unregisterOnSharedPreferenceChangeListener(listener);
        }

        private SharedPreferences.OnSharedPreferenceChangeListener listener
                = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                setSummaryVoice();
            }
        };

        //設定項目を表示する(ボイス関連)
        private void setSummaryVoice(){
            ListPreference prefVoiceType = (ListPreference)findPreference(PREF_KEY_VOICE_TYPE);
            prefVoiceType.setSummary(prefVoiceType.getEntry());

            ListPreference prefVoicePitch = (ListPreference)findPreference(PREF_KEY_VOICE_PITCH);
            prefVoicePitch.setSummary(prefVoicePitch.getEntry());

            ListPreference prefVoicePitchRange = (ListPreference)findPreference(PREF_KEY_VOICE_PITCH_RANGE);
            prefVoicePitchRange.setSummary(prefVoicePitchRange.getEntry());

            ListPreference prefVoiceSpeed = (ListPreference)findPreference(PREF_KEY_VOICE_SPEED);
            prefVoiceSpeed.setSummary(prefVoiceSpeed.getEntry());


        }
    }
}

package com.example.yasu.nicodicspeaker.lib;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class APIClient {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String ENTRY_POINT;

    public static class Builder {
        //必須パラメータ
        //WebAPI EntryPoint
        private final String entryPoint;

        //オプションパラメータ
        private List<NameValuePair> params = new ArrayList<>();

        public Builder(String entryPoint) {
            this.entryPoint = entryPoint;
        }

        public Builder params(String key, String value) {
            params.add(new BasicNameValuePair(key, value));
            return this;
        }

        public APIClient build() {
            return new APIClient(this);
        }
    }

    /**
     * デフォルトコンストラクタでは呼び出さない
     */
    private APIClient(){
        ENTRY_POINT = null;
    }

    public APIClient(Builder builder) {
        ENTRY_POINT = builder.entryPoint + "?" + URLEncodedUtils.format(builder.params, "UTF-8");
    }

    public String getEntryPoint(){
        return this.ENTRY_POINT;
    }

    /**
     * APIにGETでリクエストする
     * @return
     * @throws IOException
     */
    public String doGET() throws IOException {
        URL url = new URL(this.ENTRY_POINT);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return "";
    }

    /**
     * APIにPOSTでリクエストする
     * @param data
     * @return
     * @throws IOException
     */
    public String doPOST(String data) throws IOException{
        URL url = new URL(this.ENTRY_POINT);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setFixedLengthStreamingMode(data.getBytes().length);
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        urlConnection.connect();

        try {
            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.write(data.getBytes("UTF-8"));
            os.flush();
            os.close();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
        return "";
    }



    public String readStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        return sb.toString();
    }
}

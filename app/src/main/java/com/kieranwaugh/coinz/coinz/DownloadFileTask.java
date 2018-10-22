package com.kieranwaugh.coinz.coinz;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFileTask extends AsyncTask<String, Void, String> {

    private String tag = "DownloadFileTask";

    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadFileFromNetwork(urls[0]);
        } catch (IOException e) {
            return "Unable to load content. Check your network connection.";
        }
    }

    private String loadFileFromNetwork(String urlString) throws IOException {
        Log.d(tag, "[loadFileFromNetwork] getting file from " + urlString);
        return readStream(downloadURL(new URL(urlString)));
    }

    private InputStream downloadURL(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    @NonNull
    private String readStream(InputStream stream) throws IOException {
        Log.d(tag, "[readStream] converting file to string");
        java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\Z"); //https://stackoverflow.com/questions/3402735/what-is-simplest-way-to-read-a-file-into-string
        String str = s.hasNext() ? s.next() : "";
        stream.close();
        return str;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        DownloadCompleteRunner.downloadComplete(result);

    }

}

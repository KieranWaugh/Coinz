package com.kieranwaugh.coinz.coinz;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.common.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFileTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadFileFromNetwork(urls[0]);
        } catch (IOException e) {
            return "Unable to load content. Check your network connection.";
        }
    }

    private String loadFileFromNetwork(String urlString) throws IOException {
        System.out.println("load file " + urlString);
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
        //read input from stream, build result as a string
        //java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
        java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\Z");
        String str = s.hasNext() ? s.next() : "";
        System.out.println("read stream " + str);
        return str;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("on post " + result);
        DownloadCompleteRunner.downloadComplete(result);

    }

}

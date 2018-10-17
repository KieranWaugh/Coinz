package com.kieranwaugh.coinz.coinz;

public class DownloadCompleteRunner {

    static String result;
    static Boolean comp = false;

    public static void downloadComplete(String result){
        DownloadCompleteRunner.result = result;
        comp = true;
    }
}



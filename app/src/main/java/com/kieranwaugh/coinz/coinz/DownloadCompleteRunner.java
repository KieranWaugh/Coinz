package com.kieranwaugh.coinz.coinz;

public class DownloadCompleteRunner {

    static String result;

    public static void downloadComplete(String result){
        DownloadCompleteRunner.result = result;
        System.out.println("dlc " + result);

    }
}



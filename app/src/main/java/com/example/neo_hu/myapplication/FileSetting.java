package com.example.neo_hu.myapplication;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;

/**
 * Created by neo_hu on 2015/11/4.
 */
public class FileSetting {

    //Environment.getExternalStorageDirectory()傳回File物件，
    //File.getAbsolutePath()取得SD卡路徑字串
    public static final String sdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String filepath="/我的錄音檔/";
    public static File file;
    public static String filename ;

    public static MediaRecorder recorder;

    private static ArrayList<String> totalname = new ArrayList<String>();

    /**
     * 創建資料夾
     * @return
     */
    public static boolean FileDirCreate(){
        boolean isCreate = false;
        //建立存放錄音檔的資料夾
        if(!new File(sdcardpath + filepath).exists()){
            new File(sdcardpath + filepath).mkdir();
            isCreate = true;
            Log.d("FileSetting:","創建資料夾---" + filepath);
        }

        return isCreate;
    }

    /**
     * 輸出檔案的設置
     */
    public static void MediaRecorderSetting(){
        file=new File(sdcardpath + filepath + filename);

        //設定檔案格式、編碼、資訊
        recorder=new MediaRecorder();
        //設定聲音來源
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //設定輸出錄音檔格式
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        //設定錄音檔存放位置
        recorder.setOutputFile(file.getAbsolutePath());
        //設定錄音的編碼方式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }

    /**
     * 取得資料夾內的錄音檔案
     * @return
     */
    public static ArrayList<String> getFilename(){
        totalname.clear();
//        file = new File(sdcardpath + filepath);
//        File filelist[] = file.listFiles();

        for(int i = 0; i < filepath().length; i++) {
            totalname.add(filepath()[i].getName());
        }
        Log.d("FileSetting:", "資料夾內檔案---" + totalname);
        return totalname;
    }

    /**
     * 抓取檔案位置
     * @return
     */
    public static File[] filepath(){
        file = new File(sdcardpath + filepath);
        File filelist[] = file.listFiles();
        return filelist;
    }
}

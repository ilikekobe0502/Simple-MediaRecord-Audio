package com.example.neo_hu.myapplication;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MainActivity extends ActionBarActivity {
    private Button mButton_Record, mButton_Stop, mButton_Play;
    private ImageButton mImageButton_mic;
    private TextView mTextView_count;

    private String str;

    MediaRecorder recorder;
    String filepath="/我的錄音檔/";
    String filename ;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton_Record = (Button)findViewById(R.id.button_Record);
        mButton_Stop = (Button)findViewById(R.id.button_stop);
        mButton_Play = (Button)findViewById(R.id.button_play);
        mImageButton_mic = (ImageButton)findViewById(R.id.imageButton_mic);
        mTextView_count = (TextView) findViewById(R.id.textView_count);

        //Environment.getExternalStorageDirectory()傳回File物件，
        //File.getAbsolutePath()取得SD卡路徑字串
        final String sdcardpath=Environment.getExternalStorageDirectory().getAbsolutePath();
        //建立存放錄音檔的資料夾
        if(!new File(sdcardpath+filepath).exists()){
            new File(sdcardpath+filepath).mkdir();
            Toast.makeText(this, "建立資料夾...", Toast.LENGTH_SHORT).show();
        }


        mButton_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mButton_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mImageButton_mic.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        NowTime();
                        file=new File(sdcardpath + filepath + filename);

                        mImageButton_mic.setBackground(getResources().getDrawable(R.drawable.mic_click));
                        recorder=new MediaRecorder();
                        //設定聲音來源
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        //設定輸出錄音檔格式
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                        //設定錄音檔存放位置
                        recorder.setOutputFile(file.getAbsolutePath());
                        //設定錄音的編碼方式
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


                        // prepare()完成錄音之準備工作，必須在try catch下完成
                        try {
                            recorder.prepare();
                            recorder.start();
                            Log.d("Status:","錄音中.." + recorder);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.d("Status:", "錄音失敗.." + recorder.toString());
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        //記得停止撥放時要釋放資源
                        if(recorder!=null){
                            mImageButton_mic.setBackground(getResources().getDrawable(R.drawable.mic));

                            recorder.stop();
                            recorder.reset();
                            recorder.release();
                            recorder = null;
                            Log.d("Status:", "錄音結束..." + filename);
                        }
                        break;
                }
                return false;
            }
        });
    }

    public void NowTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間

        str = formatter.format(curDate);
        filename = str + ".aac";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

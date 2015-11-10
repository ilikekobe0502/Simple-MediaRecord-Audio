package com.example.neo_hu.myapplication;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    private ListView mListView_playList;
    private Button mButton_Play;
    private ImageButton mImageButton_mic;
    private ImageView mImageView_voicedb;
    private TextView mTextView_count;

    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private String str;

    private int tsec = 0, csec = 0, cmin = 0;

    //宣告Timer
    Timer mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton_Play = (Button)findViewById(R.id.button_play);
        mImageButton_mic = (ImageButton)findViewById(R.id.imageButton_mic);
        mTextView_count = (TextView) findViewById(R.id.textView_count);
        mImageView_voicedb = (ImageView)findViewById(R.id.imageView_voicedb);
        mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.a));
        mListView_playList = (ListView)findViewById(R.id.listView_playList);

        ShowList();//顯示列表清單

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
                    case MotionEvent.ACTION_DOWN://按下時

                        NowTime();//抓取現在時間
                        FileSetting.MediaRecorderSetting();//設定好輸出格式

                        mImageButton_mic.setBackground(getResources().getDrawable(R.drawable.mic_click));

                        //Timer設置
                        mTime = new Timer();
                        Mytask task=new Mytask();
                        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
                        mTime.schedule(task, 0, 1000);
                        updateMicStatus();

                        // prepare()完成錄音之準備工作，必須在try catch下完成
                        try {
                            FileSetting.recorder.prepare();
                            FileSetting.recorder.start();
                            Log.d("Status:","錄音中.." );

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.d("Status:", "錄音失敗..");
                        }
                        break;
                    case MotionEvent.ACTION_UP://放開時

                        //記得停止撥放時要釋放資源
                        if(FileSetting.recorder!=null){
                            mImageButton_mic.setBackground(getResources().getDrawable(R.drawable.mic));
                            if(mTime != null){
                                mTime.cancel();//取消Task
                            }

                            //釋放紀錄器
                            FileSetting.recorder.stop();
                            FileSetting.recorder.reset();
                            FileSetting.recorder.release();
                            FileSetting.recorder = null;

                            if(tsec < 2) {//小於兩秒就刪除資料不儲存
                                FileSetting.file.delete();
                                Toast.makeText(MainActivity.this, "取消錄音", Toast.LENGTH_SHORT).show();
                                Log.d("Status:", "取消錄音...");
                            }else {
                                ShowList();//更新列表
                                Log.d("Status:", "錄音結束..." + FileSetting.filename);
                            }

                            tsec=0;
                            //TextView 初始化
                            mTextView_count.setText("");
                            mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.a));

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
        FileSetting.filename = str + ".aac";
    }

    public void ShowList(){
        if(FileSetting.FileDirCreate() == true){
            Toast.makeText(this, "建立資料夾...", Toast.LENGTH_SHORT).show();
        }

        //將資料放入ListView
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FileSetting.getFilename());
        mListView_playList.setAdapter(mAdapter);

        mListView_playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mMediaPlayer.setDataSource(FileSetting.sdcardpath + FileSetting.filepath + FileSetting.filepath()[position].getName());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    Log.d("MainActivity", "播放" + FileSetting.filepath()[position].getName());

                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // 停止播放
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();
                            Log.d("MainActivity", "播放結束");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("MainActivity", "發生異常");
                }catch (IllegalStateException e){
                    e.printStackTrace();
                    Log.d("MainActivity", "讀取異常");
                }
            }
        });
    }

    /**
     * 更新話筒狀態
     *
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private void updateMicStatus() {
        if (FileSetting.recorder != null) {
            double ratio = (double)FileSetting.recorder.getMaxAmplitude() /BASE;
            double db = 0;// 分贝
            if (ratio > 1)
                db = 20 * Math.log10(ratio);

            switch ((int) (db / 10)){
                case 0:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.a));
                    break;
                case 1:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.b));
                    break;
                case 2:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.c));
                    break;
                case 3:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.d));
                    break;
                case 4:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.e));
                    break;
                case 5:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.f));
                    break;
                case 6:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.g));
                    break;
                case 7:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.h));
                    break;
                case 8:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.i));
                    break;
                case 9:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.j));
                    break;
                case 10:
                    mImageView_voicedb.setImageDrawable(getResources().getDrawable(R.drawable.k));
                    break;
            }

            Log.d("MainActivity","分貝值："+db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    /**
     * TimerTask
     */
    private class Mytask extends TimerTask{
        @Override
        public void run() {
            // TODO Auto-generated method stub
                //如果startflag是true則每秒tsec+1
                tsec++;
                Message message = new Message();
                //傳送訊息1
                message.what =1;
                handler.sendMessage(message);
        }
    }

    /**
     *TimerTask無法直接改變元件因此要透過Handler來當橋樑
     */
    private Handler handler = new Handler(){
        public  void  handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    csec=tsec%60;
                    cmin=tsec/60;
                    String s="";
                    if(cmin <10){
                        s="0"+cmin;
                    }else{
                        s=""+cmin;
                    }
                    if(csec < 10){
                        s=s+":0"+csec;
                    }else{
                        s=s+":"+csec;
                    }

                    //s字串為00:00格式
                    mTextView_count.setText(s);
                    Log.d("時間經過",s.toString());
                    break;
            }
        }
    };



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

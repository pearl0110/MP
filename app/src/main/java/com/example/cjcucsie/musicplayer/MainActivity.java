package com.example.cjcucsie.musicplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView repeat,prevoius,play,next,ren;
    SeekBar volumeBar,progressBat;
    MediaPlayer mediaPlayer;
    TextView startTime,endTime;
    int r=1; //循環鍵計算點擊的次數
    int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repeat=findViewById(R.id.repeat);
        prevoius=findViewById(R.id.prevoius);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        ren=findViewById(R.id.ren);
        volumeBar=findViewById(R.id.volume);
        progressBat=findViewById(R.id.ProgressBat);
        startTime=findViewById(R.id.StartTime);
        endTime=findViewById(R.id.EndTime);


        //放入音樂
        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.the_bluest_star);

        mediaPlayer.setVolume(0.5f,0.5f);

        //計算音樂的時間
        mediaPlayer.seekTo(0);
        totalTime=mediaPlayer.getDuration();
        progressBat.setMax(totalTime);

        //按下播放鍵
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mediaPlayer.isPlaying())
                {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }
                else
                {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }
            }
        });

        //按下音量
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //調整音量
                float volume = i/10f;
                mediaPlayer.setVolume(volume,volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //按下循環鍵
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r%2==0)
                {
                    mediaPlayer.setLooping(false);
                    repeat.setImageResource(R.drawable.repeat);
                    r++;
                }
                else
                {
                    mediaPlayer.setLooping(true);
                    repeat.setImageResource(R.drawable.repeat_up);
                    r++;
                }
            }
        });

        //音樂長度
        progressBat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                {
                    mediaPlayer.seekTo(i);
                    progressBat.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /*使用Thread讓大量運算在背景跑, 卻不影響使用者操作的畫面,
        而如果需要畫面更新, 則會透過Handler機制去更新。*/
        /*Thread==>開啟匿名class*/
        /*Runnable==>interface Runnable*/
        /*SendMessage函數發送消息，等待消息處理完成後，SendMessage才返回*/
        new Thread(new Runnable() {


            @Override
            public void run() {
                while (mediaPlayer!=null)
                {
                    try
                    {
                        Message message = new Message();
                        message.what=mediaPlayer.getCurrentPosition();
                        handler.sendMessage(message); //把資料傳入handler
                        Thread.sleep(1000);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        }).start();
    }

    //Handler負責派送訊息
    private Handler handler = new Handler()
    {
        @Override //handleMessage==>處理訊息
        public void handleMessage(Message message) {
            int currentPosition=message.what; //把資料給currentPosition
            progressBat.setProgress(currentPosition); //setProgress()==>寫入資料

            String time=createTime(currentPosition);
            startTime.setText(time);
            String endtime=createTime(totalTime-currentPosition);
            endTime.setText("-"+endtime);
        }
    };

    //計算結束音樂的長度數字
    public String createTime(int time)
    {
        String timelevel="";
        int min=time/1000/60;
        int sec=time/1000%60;
        timelevel=min+":";
        if (sec<10)
        {
            timelevel+="0"+sec;
        }
        return timelevel;
    }
}

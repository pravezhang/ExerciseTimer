package com.prave.tools.exercisetimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ExercisingActivity extends AppCompatActivity implements View.OnClickListener {

    //控件
    LinearLayout welcome,content;
    TextView sport_info,current_exercise_name,current_count,current_music,start_text;
    ImageButton start,pause,last_exercise,next_exercise,last_song,next_song;
    ImageView start_321;
    ProgressBar exercise_period,sport_period;

    //时钟晶振
    Timer overAllTimer;
    TimerTask tictok;
    //音乐播放器
    MediaPlayer musicPlayer;
    MediaPlayer soundPlayer;

    //按键状态信息码
    private static final int CLICK_START= 100;
    private static final int REAL_START= 200;
    private static final int FINISH=300;
    private static final int PAUSE=400;
    private static final int UPDATE_EXE_NAME=501;
    private static final int UPDATE_SONG_NAME=502;
    private static final int UPDATE_EXE_PERIOD=601;
    private static final int UPDATE_TOTAL_PERIOD=602;
    private static final int TIC_TOK=777;

    private List<String> SONGS;
    private List<Pair<Exercise,Integer>> sport;

    private int nowPlaying=0;
    private int nowExercising=0;
    //321倒计时的数字
    private int nowCounting321=2;
    int[] djs_pics={R.drawable.startin1,R.drawable.startin2,R.drawable.startin3};

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercising);
        sport_info=(TextView) findViewById(R.id.exercising_sport_info);
        current_exercise_name=(TextView)findViewById(R.id.exercising_current_exercise_name);
        current_count=(TextView)findViewById(R.id.exercising_now_sport_count);
        current_music=(TextView)findViewById(R.id.exercising_song_name);
        start_text=(TextView)findViewById(R.id.exercising_start_text);
        start=(ImageButton) findViewById(R.id.exercising_start);
        pause=(ImageButton)findViewById(R.id.exercising_resume_or_pause);
        last_exercise=(ImageButton)findViewById(R.id.exercising_last_exercise);
        next_exercise=(ImageButton)findViewById(R.id.exercising_next_exercise);
        last_song=(ImageButton)findViewById(R.id.exercising_last_song);
        next_song=(ImageButton)findViewById(R.id.exercising_next_song);
        start_321=(ImageView) findViewById(R.id.exercising_321_start);
        exercise_period=(ProgressBar) findViewById(R.id.exercising_now_exercise_period);
        sport_period=(ProgressBar) findViewById(R.id.exercising_now_sport_period);
        welcome=(LinearLayout)findViewById(R.id.exercising_welcome);
        content=(LinearLayout)findViewById(R.id.exercising_content);
        start.setOnClickListener(this);
        start_text.setOnClickListener(this);
        next_song.setOnClickListener(this);
        next_exercise.setOnClickListener(this);
        last_exercise.setOnClickListener(this);
        last_song.setOnClickListener(this);
        pause.setOnClickListener(this);

        tictok=new TimerTask() {
            @Override
            public void run() {
                Bundle b=new Bundle();
                b.putInt("operation",TIC_TOK);
                Message message=new Message();
                message.setData(b);
                handler.sendMessage(message);
            }
        };
        overAllTimer=new Timer();


        SONGS=new ArrayList<>();
        try {
            for (String file:getAssets().list("")){
                if(file.contains("sportMusic="))
                    SONGS.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String name=getIntent().getExtras().getString("NAME");
        String url=SundryFunctions.saveFolder+"/"+name+".sport";
        setTitle(name);


        //运动流程文件 加载
        try {
            sport=SundryFunctions.getSport(url);
            int i=0;
            for (Pair<Exercise,Integer> ex:sport){
                System.out.print(ex.first.name+ ex.second+"\t");
                if(++i%5==0) System.out.print("\n");
            }
        } catch (Exception e) {
        }

        soundPlayer=new MediaPlayer();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(! (keyCode==KeyEvent.KEYCODE_BACK))
            return super.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exercising_start: {
            }
            case R.id.exercising_start_text: {
                Bundle b=new Bundle();
                b.putInt("operation",CLICK_START);
                Message message=new Message();
                message.setData(b);
                handler.sendMessage(message);
                //设置背景音乐
                next_song.callOnClick();
                break;
            }
            case R.id.exercising_next_song: {
                if(musicPlayer!=null){
                    if(musicPlayer.isPlaying())
                        musicPlayer.stop();
                    musicPlayer.release();
                    musicPlayer=null;
                }
                musicPlayer=new MediaPlayer();
                try {
                    nowPlaying=nowPlaying==SONGS.size()-1?0:nowPlaying+1;
                    musicPlayer.setDataSource(getAssets().openFd(SONGS.get(nowPlaying)));
                    musicPlayer.prepare();
                    musicPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bundle b=new Bundle();
                b.putInt("operation",UPDATE_SONG_NAME);
                Message message=new Message();
                message.setData(b);
                handler.sendMessage(message);
                break;
            }
            case R.id.exercising_last_song: {
                if(musicPlayer!=null){
                    if(musicPlayer.isPlaying())
                        musicPlayer.stop();
                    musicPlayer.release();
                    musicPlayer=null;
                }
                musicPlayer=new MediaPlayer();
                try {
                    nowPlaying=nowPlaying==0?SONGS.size()-1:nowPlaying-1;
                    musicPlayer.setDataSource(getAssets().openFd(SONGS.get(nowPlaying)));
                    musicPlayer.prepare();
                    musicPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bundle b=new Bundle();
                b.putInt("operation",UPDATE_SONG_NAME);
                Message message=new Message();
                message.setData(b);
                handler.sendMessage(message);
                break;
            }
            case R.id.exercising_last_exercise: {

                break;
            }
            case R.id.exercising_next_exercise: {

                break;
            }
            case R.id.exercising_resume_or_pause: {

            }
        }//switch
    }
    private void startCounting321(){
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Bundle bundle=new Bundle();
                bundle.putInt("operation",nowCounting321==-1?REAL_START:CLICK_START);
                Message message=new Message();
                message.setData(bundle);
                handler.sendMessage(message);
            }
        },1000);
    }

    public void playSound(Exercise eee){
        try {
            if(soundPlayer!=null) {
                soundPlayer.release();
                soundPlayer = null;
            }
            soundPlayer=new MediaPlayer();
            soundPlayer.setDataSource(ExercisingActivity.this.getResources().openRawResourceFd(eee.voiceID));
            soundPlayer.prepare();
            soundPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            switch (bundle.getInt("operation")){
                case CLICK_START:{
                    if(nowCounting321==2)
                    {
                        welcome.setVisibility(View.GONE);
                        start_321.setVisibility(View.VISIBLE);
                    }
                    start_321.setImageResource(djs_pics[nowCounting321]);
                    nowCounting321--;
                    startCounting321();
                    break;
                }
                case REAL_START:{
                    start_321.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    //TODO 从这里开始
                    sport_period.setMax(SundryFunctions.getDuration(sport));
                    overAllTimer.schedule(tictok,0,1000);
                    break;
                }
                case FINISH:{

                    break;
                }
                case PAUSE:{
                    if(musicPlayer.isPlaying())
                        musicPlayer.pause();
                    else
                        musicPlayer.start();

                    break;
                }
                case UPDATE_EXE_NAME:{

                    break;
                }
                case UPDATE_SONG_NAME:{
                    String[] tm=SONGS.get(nowPlaying).split("=")[1].split("-");
                    current_music.setText(tm[1]+" - "+tm[0]);
                    break;
                }
                case UPDATE_EXE_PERIOD:{

                    break;
                }
                case UPDATE_TOTAL_PERIOD:{

                    break;
                }
                case TIC_TOK:{
                    Pair<Exercise,Integer> ep=sport.get(nowExercising);
                    playSound(ep.first);
                    exercise_period.setMax(ep.first.unit==0?ep.second:(ep.second*ep.first.unit));
                    exercise_period.setProgress(0);
                    sport_period.setProgress(SundryFunctions.getDuration(sport,0,nowExercising)+exercise_period.getProgress());
                    break;
                }

            } //switch
        }
    };
}

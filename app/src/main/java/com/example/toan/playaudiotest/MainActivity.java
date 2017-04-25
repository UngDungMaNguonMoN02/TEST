package com.example.toan.playaudiotest;

import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import java.util.*;

import android.os.Handler;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
{
    private Button bt1, bt2, bt3, bt4;
    private ImageView img_view;
    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler m_Handler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView  tx1, tx2, tx3;

    public static int oneTimeOnly = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt1 = (Button) findViewById(R.id.button);
        bt2 = (Button) findViewById(R.id.button2);
        bt3 = (Button)findViewById(R.id.button3);
        bt4 = (Button)findViewById(R.id.button4);
        img_view = (ImageView)findViewById(R.id.imageView);

        tx1 = (TextView)findViewById(R.id.textView2);
        tx2 = (TextView)findViewById(R.id.textView3);
        tx3 = (TextView)findViewById(R.id.textView4);
        tx3.setText("audio.mp3");

        mediaPlayer = new MediaPlayer();
        try  //Thay "audio.mp3" bằng đường dẫn khác
        {
            //AssetFileDescriptor afd = getAssets().openFd("audio.mp3");
            AssetFileDescriptor afd = getAssets().openFd("newfolder/audio2.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        }
        catch (Exception ex)
        {
            ;
        }

        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(true);
        bt2.setEnabled(false);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                m_Handler.removeCallbacks(UpdateAudioTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                m_Handler.removeCallbacks(UpdateAudioTime);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = seekBar.getProgress();

                mediaPlayer.seekTo(currentPosition);

                m_Handler.postDelayed(UpdateAudioTime, 100);
            }
        });

        bt3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_SHORT).show();
                try
                {
                    mediaPlayer.prepare();
                }
                catch (Exception ex)
                {
                    ;
                }

                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0)
                {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                tx2.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime), TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
                tx1.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) startTime), TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));

                seekbar.setProgress((int)startTime);
                m_Handler.postDelayed(UpdateAudioTime,100);
                bt2.setEnabled(true);
                bt3.setEnabled(false);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Audio paused",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                bt2.setEnabled(false);
                bt3.setEnabled(true);
            }
        });

        bt1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mediaPlayer.seekTo(0);
                seekbar.setProgress(0);
                if(!mediaPlayer.isPlaying())
                {
                    bt2.setEnabled(false);
                    bt3.setEnabled(true);
                    tx1.setText("0:00");
                }
                else
                {
                    bt2.setEnabled(true);
                    bt3.setEnabled(false);
                }
                Toast.makeText(getApplicationContext(),"Audio restarted", Toast.LENGTH_SHORT).show();
            }
        });
        bt4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mediaPlayer.seekTo(0);
                seekbar.setProgress(0);
                mediaPlayer.pause();
                Toast.makeText(getApplicationContext(),"Audio stopped",Toast.LENGTH_SHORT).show();
                tx1.setText("0:00");
                bt3.setEnabled(true);
                bt2.setEnabled(false);
            }
        });
    }

    private Runnable UpdateAudioTime = new Runnable()
    {
        public void run()
        {
            if(mediaPlayer.isPlaying())
            {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) startTime), TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
            seekbar.setProgress((int)startTime);
            m_Handler.postDelayed(this, 100);
            }
        }
    };

}

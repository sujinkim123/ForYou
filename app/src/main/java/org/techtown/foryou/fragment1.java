package org.techtown.foryou;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class fragment1 extends Fragment implements View.OnClickListener{

    private MediaPlayer MP1;
    private MediaPlayer MP2;
    private MediaPlayer MP3;
    private MediaPlayer MP4;
    private MediaPlayer MP5;
    private MediaPlayer MP6;

    private Chronometer mChronometer;
    private long timeWhenStopped = 0;
    private boolean stopClicked;
    private ViewGroup ViewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        MP1 = MediaPlayer.create(getActivity(), R.raw.music1);
        MP2 = MediaPlayer.create(getActivity(), R.raw.music2);
        MP3 = MediaPlayer.create(getActivity(), R.raw.music3);
        MP4 = MediaPlayer.create(getActivity(), R.raw.music4);
        MP5 = MediaPlayer.create(getActivity(), R.raw.music5);
        MP6 = MediaPlayer.create(getActivity(), R.raw.music6);

        final Button soundOn1 = ViewGroup.findViewById(R.id.장작소리);
        soundOn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MP1.isPlaying()) {
                    MP1.pause();
                } else
                {

                    MP1.start();
                }
            }

        });

        final Button soundOn2 = ViewGroup.findViewById(R.id.빗소리);
        soundOn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MP2.isPlaying()) {
                    MP2.pause();
                } else
                {

                    MP2.start();
                }
            }

        });

        final Button soundOn3 = ViewGroup.findViewById(R.id.귀뚜라미소리);
        soundOn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MP3.isPlaying()) {
                    MP3.pause();
                } else
                {

                    MP3.start();
                }
            }

        });

        final Button soundOn4 = ViewGroup.findViewById(R.id.바람소리);
        soundOn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MP4.isPlaying()) {
                    MP4.pause();
                } else
                {

                    MP4.start();
                }
            }

        });

        final Button soundOn5 = ViewGroup.findViewById(R.id.파도소리);
        soundOn5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MP5.isPlaying()) {
                    MP5.pause();
                } else
                {

                    MP5.start();
                }
            }

        });

        final Button soundOn6 = ViewGroup.findViewById(R.id.연필소리);
        soundOn6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MP6.isPlaying()) {
                    MP6.pause();
                } else
                {

                    MP6.start();
                }
            }

        });

        mChronometer = ViewGroup.findViewById(R.id.timeView);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int)(time / 3600000);
                int m = (int)(time - h * 3600000) / 60000;
                int s = (int)(time - h*3600000 - m*60000) / 1000;
                String t = (h < 10 ? "0" + h: h) + ":" + (m < 10 ? "0" + m: m) + ":" + (s < 10 ? "0" + s: s);
                chronometer.setText(t);
            }
        });

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setText("00:00:00");

        ImageButton startBtn = ViewGroup.findViewById(R.id.start);
        ImageButton pauseBtn = ViewGroup.findViewById(R.id.pause);
        ImageButton resetBtn = ViewGroup.findViewById(R.id.reset);


        startBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        return ViewGroup;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                mChronometer.start();
                stopClicked = false;
                break;
            case R.id.pause:
                if (!stopClicked) {
                    timeWhenStopped = mChronometer.getBase() -SystemClock.elapsedRealtime();
                    mChronometer.stop();
                    stopClicked = true;
                    break;
                }
            case R.id.reset:
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.stop();
                timeWhenStopped = 0;
                break;
        }
    }
}
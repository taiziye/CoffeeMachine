package com.netease.vendor.util;

import android.content.Context;
import android.media.AudioManager;

import com.baidu.speechsynthesizer.SpeechSynthesizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by niubolity on 2015/8/3.
 */
public class TTSPlayer {
    private static final TTSPlayer sTTSPlayer = new TTSPlayer();
    private boolean initialized;

    private SpeechSynthesizer speechSynthesizer;

    private List<Thread> threadList;

    public static TTSPlayer sharedInstance(Context context) {
        sTTSPlayer.init(context);
        return sTTSPlayer;
    }

    private void init(Context context) {
        if (initialized || null == context) {
            return;
        }
        // 初始化
        initialized = true;
        speechSynthesizer = SpeechSynthesizer.newInstance(SpeechSynthesizer.SYNTHESIZER_AUTO, context.getApplicationContext(), "holder", null);
        speechSynthesizer.setApiKey("ETkFoY2ExgE4oj7rhQZNFkX5", "e31ecc2c3ff806824933aaa59069a5b0");
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        setParams();

        threadList = new ArrayList<Thread>();
    }

    private void setParams() {
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "6");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "4");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE,
                SpeechSynthesizer.AUDIO_ENCODE_AMR);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE,
                SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85);
    }

    public void say(String say) {
        emptyCheck();
        Thread thread = new Thread(new Run(speechSynthesizer, say));
        threadList.add(thread);
        thread.start();
    }

    public void destroy() {
        speechSynthesizer.cancel();
        Iterator<Thread> iterator = threadList.iterator();
        while (iterator.hasNext()) {
            Thread thread = iterator.next();
            if (thread.isAlive()) {
                thread.interrupt();
            }
            iterator.remove();
        }
    }

    private void emptyCheck() {
        Iterator<Thread> iterator = threadList.iterator();
        while (iterator.hasNext()) {
            Thread thread = iterator.next();
            if (!thread.isAlive()) {
                iterator.remove();
            }
        }
    }
}

class Run implements Runnable {
    private SpeechSynthesizer speechSynthesizer;
    private String say;

    Run(SpeechSynthesizer speechSynthesizer, String say) {
        this.speechSynthesizer = speechSynthesizer;
        this.say = say;
    }

    @Override
    public void run() {
        speechSynthesizer.speak(say);
    }
}

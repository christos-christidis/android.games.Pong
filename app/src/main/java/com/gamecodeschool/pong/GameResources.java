package com.gamecodeschool.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

class GameResources {

    private SoundPool mSoundPool;
    private int mBeepID = -1;
    private int mBoopID = -1;
    private int mBopID = -1;
    private int mMissID = -1;

    GameResources(Context context) {
        // prepare the soundpool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSoundPool = new SoundPool.Builder().setMaxStreams(5)
                    .setAudioAttributes(audioAttributes).build();
        } else {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            mBeepID = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("boop.ogg");
            mBoopID = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bop.ogg");
            mBopID = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("miss.ogg");
            mMissID = mSoundPool.load(descriptor, 0);
        } catch (IOException e) {
            Log.d("error", "failed to load sound files");
        }
    }

    void playBeep() {
        mSoundPool.play(mBeepID, 1, 1, 0, 0, 1);
    }

    void playMiss() {
        mSoundPool.play(mMissID, 1, 1, 0, 0, 1);
    }

    void playBoop() {
        mSoundPool.play(mBoopID, 1, 1, 0, 0, 1);
    }

    void playBop() {
        mSoundPool.play(mBopID, 1, 1, 0, 0, 1);
    }
}

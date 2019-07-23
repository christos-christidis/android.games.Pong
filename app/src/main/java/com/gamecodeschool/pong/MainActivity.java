package com.gamecodeschool.pong;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {

    private PongGame mPongGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VisibilityManager.hideSystemUI(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getRealSize(screenSize);

        mPongGame = new PongGame(this, screenSize);
        setContentView(mPongGame);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            VisibilityManager.hideSystemUI(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPongGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPongGame.pause();
    }
}

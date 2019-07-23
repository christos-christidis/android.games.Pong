package com.gamecodeschool.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Locale;

@SuppressWarnings("ViewConstructor")
class PongGame extends SurfaceView implements Runnable {

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean DEBUGGING = true;
    // An improvement since SubHunter. Now I print FPS every 3 or so frames, not EVERY frame
    private long mTimeOfLastPrint = System.currentTimeMillis();
    private String mDebugString;

    private final SurfaceHolder mOurHolder;
    private final Paint mPaint;

    private long mFPS = 15;

    private final int mScreenWidth;
    private final int mScreenHeight;

    private final int mFontSize;
    private final int mFontMargin;

    private final Bat mBat;
    private final Ball mBall;

    private int mScore;
    private int mLives;

    private Thread mGameThread = null;
    private volatile boolean mPlaying;  // volatile means it can be access inside & outside the thread
    private boolean mPaused = true;

    private final GameResources mGameResources;

    PongGame(Context context, Point screenPixels) {
        super(context);

        mScreenWidth = screenPixels.x;
        mScreenHeight = screenPixels.y;

        mFontSize = mScreenWidth / 20;
        mFontMargin = mScreenWidth / 75;

        mOurHolder = getHolder();
        mPaint = new Paint();

        mBall = new Ball(screenPixels);
        mBat = new Bat(screenPixels);

        mGameResources = new GameResources(context);

        startNewGame();
    }

    @Override
    public void run() {
        // this gives finer control.than relying on whether the thread is running or not. Eg in pause(),
        // we may have to wait until thread is finally joined but mPlaying is set to false immediately.
        while (mPlaying) {
            long frameStartTime = System.currentTimeMillis();

            // If game ain't paused, update positions and calculate collisions. draw() will be called
            // anyway for some reason
            if (!mPaused) {
                update();
                detectCollisions();
            }

            draw();

            // How long did this frame/loop take?
            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame > 0) {
                // if the frame takes 20ms, then in a second we can fit 1000 / 20 = 50 FPS.
                final long MILLIS_IN_SECOND = 1000;
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    private void startNewGame() {
        mBall.reset();
        mScore = 0;
        mLives = 3;
    }

    private void update() {
        mBall.update(mFPS);
        mBat.update(mFPS);
    }

    // TODO: test if my improvements fixed the ball getting stuck in the wall
    private void detectCollisions() {
        // ball hits bat
        if (RectF.intersects(mBall.getCollider(), mBat.getCollider())) {
            mBall.bounceOffBat(mBat.getCollider());
            mBall.increaseVelocity();
            mScore++;
            mGameResources.playBeep();
        }

        // bottom
        if (mBall.getCollider().bottom > mScreenHeight) {
            mBall.setBottom(mScreenHeight);
            mBall.reverseVerticalDirection();
            mGameResources.playMiss();

            mLives--;
            if (mLives == 0) {
                mPaused = true;
                startNewGame();
            }
        }

        // top
        if (mBall.getCollider().top < 0) {
            mBall.setTop(0);
            mBall.reverseVerticalDirection();
            mGameResources.playBoop();
        }

        // left or right
        if (mBall.getCollider().left < 0 || mBall.getCollider().right > mScreenWidth) {
            mBall.reverseHorizontalDirection();
            mGameResources.playBop();

            if (mBall.getCollider().left < 0) {
                mBall.setLeft(0);
            } else if (mBall.getCollider().right > mScreenWidth) {
                mBall.setRight(mScreenWidth);
            }
        }
    }

    private void draw() {
        if (mOurHolder.getSurface().isValid()) {
            Canvas canvas = mOurHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            mPaint.setColor(Color.WHITE);

            if (DEBUGGING) {
                printDebuggingText(canvas);
            }

            canvas.drawRect(mBall.getCollider(), mPaint);
            canvas.drawRect(mBat.getCollider(), mPaint);

            mPaint.setTextSize(mFontSize);
            canvas.drawText(String.format(Locale.getDefault(),
                    "Score: %d   Lives: %d", mScore, mLives), mFontMargin, mFontSize, mPaint);

            mOurHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void printDebuggingText(Canvas canvas) {
        long timeSinceLastPrint = System.currentTimeMillis() - mTimeOfLastPrint;
        if (timeSinceLastPrint > 50) {
            mDebugString = "FPS: " + mFPS;
            mTimeOfLastPrint = System.currentTimeMillis();
        }

        int debugSize = mFontSize / 2;
        mPaint.setTextSize(debugSize);

        // SOS: contrary to documentation, the Rect returned by getTextBounds may NOT start at (0,0),
        // thus I should not use absolute values like rect.right, but rect.width() and rect.height()
        Rect textBounds = new Rect();
        mPaint.getTextBounds(mDebugString, 0, mDebugString.length(), textBounds);

        canvas.drawText(mDebugString, mScreenWidth - mFontMargin - textBounds.width(),
                mFontMargin + textBounds.height(), mPaint);
    }

    // Besides activities, SurfaceViews also listen for touches
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Note: if I use both fingers, I might get bugs. Later we'll see how to correctly handle this.
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mPaused = false;    // In case the game was paused, unpause
                if (event.getX() > mScreenWidth / 2) {
                    mBat.setMovementDirection(Bat.Movement.RIGHT);
                } else {
                    mBat.setMovementDirection(Bat.Movement.LEFT);
                }
                break;
            case MotionEvent.ACTION_UP:
                mBat.setMovementDirection(Bat.Movement.STOPPED);
                break;

        }

        return true;
    }
}

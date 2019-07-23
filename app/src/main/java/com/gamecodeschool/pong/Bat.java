package com.gamecodeschool.pong;

import android.graphics.Point;
import android.graphics.RectF;

class Bat {
    private final RectF mRect;
    private final float mWidth;
    private final float mBatSpeed;
    private final Point mScreenPixels;

    enum Movement {
        STOPPED, LEFT, RIGHT,
    }

    private Movement mMovement = Movement.STOPPED;

    Bat(Point screenPixels) {
        mScreenPixels = screenPixels;

        mWidth = (float) screenPixels.x / 8;
        float height = (float) screenPixels.y / 40;

        float positionLeft = (float) screenPixels.x / 2 - mWidth / 2;
        float positionTop = screenPixels.y - height;

        mRect = new RectF(positionLeft, positionTop, positionLeft + mWidth, positionTop + height);
        mBatSpeed = screenPixels.x;
    }

    void setMovementDirection(Movement movement) {
        mMovement = movement;
    }

    void update(long fps) {
        if (mMovement == Movement.LEFT) {
            mRect.left -= mBatSpeed / fps;
        }
        if (mMovement == Movement.RIGHT) {
            mRect.left += mBatSpeed / fps;
        }

        // stop the bat from exiting the screen
        if (mRect.left < 0) {
            mRect.left = 0;
        } else if (mRect.left + mWidth > mScreenPixels.x) {
            mRect.left = mScreenPixels.x - mWidth;
        }

        mRect.right = mRect.left + mWidth;
    }

    RectF getCollider() {
        return mRect;
    }
}

package com.gamecodeschool.pong;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

class Ball {
    private final RectF mRect;
    private final PointF mVelocity;
    private final float mWidth;
    private final float mHeight;
    private final Point mScreenPixels;

    Ball(Point screenPixels) {
        mScreenPixels = screenPixels;

        mWidth = (float) screenPixels.x / 100;
        mHeight = (float) screenPixels.x / 100;

        mRect = new RectF();
        mVelocity = new PointF();
    }

    // The mVelocity of the ball is defined in terms of "pixels/sec". Each frame takes a portion of
    // that sec. Therefore, the ball will move the equivalent portion of pixels.
    void update(long fps) {
        mRect.left += mVelocity.x / fps;
        mRect.top += mVelocity.y / fps;

        mRect.right = mRect.left + mWidth;
        mRect.bottom = mRect.top + mHeight;
    }

    void reverseVerticalDirection() {
        mVelocity.y = -mVelocity.y;
    }

    void reverseHorizontalDirection() {
        mVelocity.x = -mVelocity.x;
    }

    void reset() {
        mRect.left = (float) mScreenPixels.x / 2;
        mRect.top = 0;
        mRect.right = mRect.left + mWidth;
        mRect.bottom = mRect.top + mHeight;

        mVelocity.x = (float) mScreenPixels.y / 3;
        mVelocity.y = (float) mScreenPixels.y / 3;
    }

    void increaseVelocity() {
        mVelocity.x *= 1.1;
        mVelocity.y *= 1.1;
    }

    void bounceOffBat(RectF batPosition) {
        float batCenter = batPosition.left + batPosition.width() / 2;
        float ballCenter = mRect.left + mWidth / 2;

        boolean hitsLeftSide = batCenter - ballCenter > 0;
        if (hitsLeftSide) {    // go left
            mVelocity.x = -Math.abs(mVelocity.x);
        } else {
            mVelocity.x = Math.abs(mVelocity.x);
        }

        reverseVerticalDirection();
    }

    RectF getCollider() {
        return mRect;
    }

    void setBottom(int bottom) {
        mRect.bottom = bottom;
        mRect.top = bottom - mHeight;
    }

    void setTop(int top) {
        mRect.top = top;
        mRect.bottom = top + mHeight;
    }

    void setLeft(int left) {
        mRect.left = left;
        mRect.right = left + mWidth;
    }

    void setRight(int right) {
        mRect.right = right;
        mRect.left = right - mWidth;
    }
}

package com.bookwormng.android.Operations;

import android.view.MotionEvent;

public class SwipeDetector {
    static final int minimumSwipeDistance = 170;
    static final int minimumSwipeSpeed = 120;
    int swipeDistance, swipeSpeed;
    public SwipeDetector()
    {
        this.swipeSpeed = minimumSwipeSpeed;
        this.swipeDistance = minimumSwipeDistance;
    }
    public SwipeDetector(int dist, int speed)
    {
        this.swipeSpeed = speed;
        this.swipeDistance = dist;
    }
    public boolean userSwipedDown(MotionEvent e1,   MotionEvent e2, float velocityY)
    {
        if ((e2.getY() - e1.getY()) > this.swipeDistance)
        {
            if (velocityY > this.swipeSpeed)
            {
                return true;
            }
        }
        return  false;
    }
    public boolean userSwipedUp(MotionEvent e1,   MotionEvent e2, float velocityY)
    {
        if ((e1.getY() - e2.getY()) > this.swipeDistance)
        {
            if (velocityY > this.swipeSpeed)
            {
                return true;
            }
        }
        return  false;
    }
    public boolean userSwipedLeft(MotionEvent e1,   MotionEvent e2, float velocityX)
    {
        if (e1.getX() - e2.getX() > this.swipeDistance)
        {
            if (velocityX > this.swipeSpeed)
            {
                return true;
            }
        }
        return  false;
    }
    public boolean userSwipedRight(MotionEvent e1,   MotionEvent e2, float velocityX)
    {
        if (e2.getX() - e1.getX() > this.swipeDistance)
        {
            if (velocityX > this.swipeSpeed)
            {
                return true;
            }
        }
        return  false;
    }
}

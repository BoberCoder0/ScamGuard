package com.example.testapp2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.drawerlayout.widget.DrawerLayout;

public class SwipeDetectorView extends View {

    private DrawerLayout drawerLayout;
    private int swipeSensitivity = 80;

    public SwipeDetectorView(Context context) {
        super(context);
    }

    public SwipeDetectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeDetectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void attachDrawerLayout(DrawerLayout drawer) {
        this.drawerLayout = drawer;
    }

    public void setSwipeSensitivity(int sensitivity) {
        this.swipeSensitivity = sensitivity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            if (x < swipeSensitivity) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
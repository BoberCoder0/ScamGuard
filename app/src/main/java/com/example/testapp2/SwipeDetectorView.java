package com.example.testapp2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.drawerlayout.widget.DrawerLayout;

public class SwipeDetectorView extends View {

    private DrawerLayout drawerLayout;
    private int swipeSensitivity = 80;  // Настрой чувствительность свайпа

    public SwipeDetectorView(Context context) {
        super(context);
    }

    public SwipeDetectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeDetectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Метод для привязки к DrawerLayout
    public void attachDrawerLayout(DrawerLayout drawer) {
        this.drawerLayout = drawer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            if(x < swipeSensitivity){
                if(drawerLayout != null){
                    if (!drawerLayout.isDrawerOpen(drawerLayout.findViewById(R.id.nav_view)))
                        drawerLayout.openDrawer(drawerLayout.findViewById(R.id.nav_view));

                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }


}
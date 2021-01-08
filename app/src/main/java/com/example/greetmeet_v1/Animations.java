package com.example.greetmeet_v1;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class Animations {
    public static void fadeInAndShowImage(final ImageView img)
    {
        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(750);

        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {
                img.setVisibility(View.VISIBLE);
            }
        });

        img.startAnimation(fadeIn);
    }
}

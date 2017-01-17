package ru.mail.tp.callbackpal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;

public class SplashScreenActivity extends AppCompatActivity {
    static boolean backPressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        backPressed = false;

        ActivityStarter activityStarter = new ActivityStarter(this);
        ThreadSleep threadSleep = new ThreadSleep(activityStarter);
        threadSleep.start();
    }

    @Override
    protected void onStop() {
        backPressed = true;
        super.onStop();
    }

    private static class ThreadSleep extends Thread {
        private ActivityStarter activityStarter;

        @Override
        public void run() {
            try {
                sleep(2000);
                Log.d("flag", Boolean.toString(backPressed));
                if (!backPressed) {
                    activityStarter.sendMessage(new Message());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private ThreadSleep(ActivityStarter aS) {
            super();
            this.activityStarter = aS;
        }

    }

    private static class ActivityStarter extends Handler {
        private WeakReference<SplashScreenActivity> link;

        public ActivityStarter(SplashScreenActivity listener) {
            this.link = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashScreenActivity hardLink = this.link.get();
            if (hardLink != null) {
                Intent startSecondActivity = new Intent(hardLink, LoginActivity.class);
                hardLink.startActivity(startSecondActivity);
                hardLink.finish();
            }
        }

    }
}

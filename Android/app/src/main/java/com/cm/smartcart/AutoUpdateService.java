package com.cm.smartcart;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Update Services
 */
public class AutoUpdateService extends Service {
    // Update interval
    public static final long NOTIFY_INTERVAL = 20 * 1000;
    // Handler
    private Handler mHandler = new Handler();
    // Timer
    private Timer mTimer = null;
    // Broadcast Manager
    private LocalBroadcastManager mLocalBroadcastManager;
    // Binder info
    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // start/stop timer
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    // Timer tick
    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent broadcastIntent = new Intent("update_service_numbers");
                    mLocalBroadcastManager.sendBroadcast(broadcastIntent);
                }
            });
        }
    }

    public class LocalBinder extends Binder {
        public AutoUpdateService getServerInstance() {
            return AutoUpdateService.this;
        }
    }
}

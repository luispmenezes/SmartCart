package com.cm.smartcart;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Data for Parse connection
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "FaQp6c7edACgbMsPEycda3IOp4cbuNvutFePHihv", "veBRo8qzWojI7SuHpYdBRazv7RtDU0UD0u61kEYY");
    }
}

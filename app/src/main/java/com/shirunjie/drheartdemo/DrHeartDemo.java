package com.shirunjie.drheartdemo;

import android.app.Application;
import io.smooch.core.Smooch;

/**
 * Created by shirunjie on 16-04-26.
 */
public class DrHeartDemo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Smooch.init(this, "4x5c8tpzib3x75paw8cum9a2g");
    }
}

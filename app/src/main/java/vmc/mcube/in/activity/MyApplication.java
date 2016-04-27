package vmc.mcube.in.activity;

import android.app.Application;
import android.content.Context;

import vmc.mcube.in.database.MDatabase;

/**
 * Created by mukesh on 14/3/16.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;
    private static MDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAplicationContext() {
        return sInstance.getApplicationContext();
    }

    public synchronized static MDatabase getWritableDatabase() {
        if (mDatabase == null) {
            mDatabase = new MDatabase(getAplicationContext());
        }
        return mDatabase;
    }
}

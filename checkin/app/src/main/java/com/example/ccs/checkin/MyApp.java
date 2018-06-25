package com.example.ccs.checkin;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.sensoro.cloud.SensoroManager;

public class MyApp extends Application {

    private static final String TAG = MyApp.class.getSimpleName();
    SensoroManager sensoroManager;

    @Override
    public void onCreate() {
        initSensoro();
        super.onCreate();
    }

    private void initSensoro() {
        sensoroManager = SensoroManager.getInstance(getApplicationContext());
        sensoroManager.setCloudServiceEnable(false);
        sensoroManager.addBroadcastKey("01Y2GLh1yw3+6Aq0RsnOQ8xNvXTnDUTTLE937Yedd/DnlcV0ixCWo7JQ+VEWRSya80yea6u5aWgnW1ACjKNzFnig==");
        //try {
        //    sensoroManager.startService();
        //    Toast.makeText(MyApp.this,"Myapp",Toast.LENGTH_SHORT).show();
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //        sensoroManager.setForegroundScanPeriod(7000);
        //    }
        //} catch (Exception e) {
        //    Log.e(TAG, e.toString());
        //}

    }

    @Override
    public void onTerminate() {
        if (sensoroManager != null) {
            sensoroManager.stopService();

        }
        super.onTerminate();
    }
}

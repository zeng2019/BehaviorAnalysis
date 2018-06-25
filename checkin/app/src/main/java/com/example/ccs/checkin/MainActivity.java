package com.example.ccs.checkin;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {
    //日志
    public final static String TAG=MainActivity.class.getSimpleName();
    MyApp app;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    //LayoutInflater inflater;
    /*
     * Beacon Manager lister,use it to listen the appearence, disappearence and
     * updating of the beacons.
     */
    BeaconManagerListener beaconManagerListener;
    /*
     * Sensoro Manager
     */
    SensoroManager sensoroManager;
    /*
     * store beacons in onUpdateBeacon
     */
    CopyOnWriteArrayList<Beacon> beacons;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //测试蓝牙是否打开
        boolean status=isBlueEnable();
        if(status){
            Toast.makeText(MainActivity.this,"蓝牙已打开",Toast.LENGTH_SHORT).show();
        }
        init();

    }

    @Override
    protected void onDestroy(){
        sensoroManager.stopService();
        super.onDestroy();
    }
    private void init(){
       initCtrl();
       initSensoroLister();
       startSensoroService();
    }

    private void initCtrl(){
     app =(MyApp)getApplication();
     //实例化 sdk
     sensoroManager=app.sensoroManager;
     beacons = new CopyOnWriteArrayList<>();

    }
    //
    private void initBroadcast(){
     //   IntentFilter filter =
    }

    private void startSensoroService(){
        sensoroManager.setBeaconManagerListener(beaconManagerListener);
        try {
            sensoroManager.startService();
           // Toast.makeText(MainActivity.this,"Myapp",Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sensoroManager.setForegroundScanPeriod(7000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //





    /*
     * Beacon Manager lister,use it to listen the appearence, disappearence and
     * updating of the beacons.
     */
    private void initSensoroLister(){
        //
        Toast.makeText(MainActivity.this,"initSensoroListener",Toast.LENGTH_SHORT).show();
        //
        beaconManagerListener=new BeaconManagerListener() {
            @Override
            public void onNewBeacon(Beacon beacon) {
                if(beacon.getSerialNumber().equals("0117C597F511"))
                {
                    Log.i(TAG,"zhaodaol");


                }
                //获得扫描的设备的sn码并通过toast显示
              final String sn=beacon.getSerialNumber();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,sn, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onGoneBeacon(Beacon beacon) {
                final String sn=beacon.getSerialNumber();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"已离开"+sn, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUpdateBeacon(final ArrayList<Beacon> arg0) {

            }
        };
    }


    //判断蓝牙是否打开
    private boolean isBlueEnable() {
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        boolean status = bluetoothAdapter.isEnabled();
        if (!status) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }
            }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setTitle(R.string.ask_bt_open);
            builder.show();
        }

        return status;
    }

}

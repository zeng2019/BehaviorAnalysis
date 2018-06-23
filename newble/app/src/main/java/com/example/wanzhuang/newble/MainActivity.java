package com.example.wanzhuang.newble;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String TAG=MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    int REQUEST_ENABLE_BT = 1;

    BeaconsFragment beaconsFragment;
    NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 0;
    SharedPreferences sharedPreferences;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothManager bluetoothManager;
    BeaconsAdapter beaconsAdapter;
    String beaconFilter;
    String matchFormat;
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
    LayoutInflater inflater;
    Handler handler =new Handler();
    Runnable runnable;
    ArrayList<OnBeaconChangeListener> beaconListeners;
    MyApp app;
    ListView lv_beacon;
    Button btn_scan;
    boolean scan_flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化蓝牙
        init_ble();
        //初始化页面
        init();
    }

    /*
     * 初始化活动
     */
    private void init()
    {
        initCtrl();
        initBtn_scan();
        initSensoroListener();
        initRunnable();
    }

    private void initCtrl(){

      inflater = getLayoutInflater();
      app = (MyApp)getApplication();
      sensoroManager = app.sensoroManager;
      beacons = new CopyOnWriteArrayList<Beacon>();
      beaconListeners = new ArrayList<OnBeaconChangeListener>();

    }
    private void initBtn_scan(){
        btn_scan=findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initBroadcast();
            }
        });
    }
    private void initRunnable(){
       runnable = new Runnable(){
           @Override
            public void run(){
               initBeconsAdapter();
                handler.postAtTime(this,2000);
           }
       };
    }
    //自定义iBeacon适配器
    public  void initBeconsAdapter(){
        beaconsAdapter = new BeaconsAdapter(LayoutInflater.
                from(getApplicationContext()));
        lv_beacon.setAdapter(beaconsAdapter);
    }

    private void initBroadcast() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                    if (state == BluetoothAdapter.STATE_ON) {
                        startSensoroService();
                    }
                }
            }
        }, filter);
    }

    /*
     * start sensoro service
     */
    private void startSensoroService(){
        sensoroManager.setBeaconManagerListener(beaconManagerListener);
        try{
            sensoroManager.startService();
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                // set ForegroundScan
                sensoroManager.setForegroundScanPeriod(7000);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void stopSensoroService(){
        sensoroManager.setBeaconManagerListener(beaconManagerListener);
        try{
            sensoroManager.stopService();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
     *传感器数据监听
     */
    private void initSensoroListener()
    {
       beaconManagerListener = new BeaconManagerListener() {
          @Override
          public void onNewBeacon(Beacon arg0) {

          }

          @Override
          public void onGoneBeacon(Beacon arg0) {

          }

          @Override
          public void onUpdateBeacon(final ArrayList<Beacon> arg0) {



          }
       };

    }

    /*
     * 初始化化蓝牙，检查蓝牙是否打开
     * 需要重写在permission类里 可参考PermissionUtils
     */

    private void init_ble() {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "init_ble:不支持Ble");
            finish();
        }
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        bluetoothManager = (BluetoothManager)
               getSystemService(Context.BLUETOOTH_SERVICE);
         mBluetoothAdapter = bluetoothManager.getAdapter();

        // Android M Permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        //打开蓝牙
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "init_ble:请求打开蓝牙");
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }



    /*
     * Beacon Change Listener.Use it to notificate updating of beacons.
     */
    public interface OnBeaconChangeListener {
        public void onBeaconChange(ArrayList<Beacon> beacons);
    }
    /*
     * Register beacon change listener.
     */
    public void registerBeaconChangerListener(OnBeaconChangeListener onBeaconChangeListener) {
        if (beaconListeners == null) {
            return;
        }
        beaconListeners.add(onBeaconChangeListener);
    }

    /*
     * Unregister beacon change listener.
     */
    public void unregisterBeaconChangerListener(OnBeaconChangeListener onBeaconChangeListener) {
        if (beaconListeners == null) {
            return;
        }
        beaconListeners.remove(onBeaconChangeListener);
    }
}

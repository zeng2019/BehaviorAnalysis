package com.example.ccs.checkin;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckinActivity extends AppCompatActivity {
    //日志
    public final static String TAG=CheckinActivity.class.getSimpleName();
    private static boolean isExit=false;
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
    SimpleDateFormat simpleDateFormat;
    Handler handler;
    TextView lv_sn;
    TextView lv_id;
    TextView lv_name_sn;
    TextView lv_name_id;
    Context mContext;
    String timeMatchFormat;
    Button btn_http;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        mContext=getApplicationContext();
        //测试蓝牙是否打开
        boolean status=isBlueEnable();
        if(status){
            Toast.makeText(CheckinActivity.this,"蓝牙已打开",Toast.LENGTH_SHORT).show();
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
     timeMatchFormat="yyyy年MM月dd日 HH:mm:ss";
     //实例化 sdk
     sensoroManager=app.sensoroManager;
     beacons = new CopyOnWriteArrayList<>();
     initViews();
     initBtn();
    }
    //
    private void initViews(){
        lv_sn=(TextView)findViewById(R.id.tv_sn);
        lv_id=(TextView)findViewById(R.id.tv_id);
        lv_name_id=(TextView)findViewById(R.id.tv_name_id);
        lv_name_sn=(TextView)findViewById(R.id.tv_name_sn);
        btn_http=(Button)findViewById(R.id.btn_http_test);

    }
    //init btn_http
    private void initBtn(){
        btn_http.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent= new Intent(CheckinActivity.this,http_test.class);
             startActivity(intent);

            }
        });
    }
    private void initBroadcast(){
     //   IntentFilter filter =
    }




    /*
     *启动扫描服务
     */
    private void startSensoroService(){
        sensoroManager.setBeaconManagerListener(beaconManagerListener);
        try {
            sensoroManager.startService();
           // Toast.makeText(MainActivity.this,"Myapp",Toast.LENGTH_SHORT).show();
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //    sensoroManager.setForegroundScanPeriod(7000);

            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * Beacon Manager lister,use it to listen the appearence, disappearence and
     * updating of the beacons.
     */
    private void initSensoroLister(){
        //
       // Toast.makeText(MainActivity.this,"initSensoroListener",Toast.LENGTH_SHORT).show();
        //
        beaconManagerListener=new BeaconManagerListener() {
            @Override
            public void onNewBeacon(Beacon beacon) {

                //获得扫描的设备的sn码并通过toast显示
              final String sn=beacon.getSerialNumber();
              final String id=beacon.getMajor().toString()+beacon.getMinor().toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_sn.setText(sn);
                        writeText(sn);
                        lv_id.setText(id);


                       //handler.postDelayed(this,2000);
                       Toast.makeText(CheckinActivity.this,sn, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onGoneBeacon(Beacon beacon) {
                final String sn=beacon.getSerialNumber();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(CheckinActivity.this,"已离开"+sn, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUpdateBeacon(final ArrayList<Beacon> arg0) {

            }
        };
    }
    //get key

    public String getKey(Beacon beacon){
        if(beacon==null){
            return null;
        }
        String key= beacon.getSerialNumber();
        return key;
    }
   /*
    *获取当前系统时间
    */
    private String writeTime(){
        //设置时间样式
        simpleDateFormat=new SimpleDateFormat(timeMatchFormat, Locale.CHINA);
        //获取当前时间
        Date date =new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
       // lv_id.setText("Date获得当前日期"+time);

    }

    /*
     *将扫描到的信息写到text文本中
     *
     */

    private void writeText(String str){

        FileHelper fileHelper1=new FileHelper(mContext);
        String filename = "log.txt";
        String fileDetail = "sn:"+str+" "+writeTime()+"\n";
        try{
            fileHelper1.writeText(filename,fileDetail);
            Toast.makeText(mContext,"数据写入成功",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"数据写入失败",Toast.LENGTH_SHORT).show();
        }
        //return fileDetail;
    }

    /*
     *讲储存的文本读取出来
     */
    private String readText(){
        String detail="";
        FileHelper fileHelper2=new FileHelper(mContext);
        try{
            String filename="1.txt";
            detail=fileHelper2.readText(filename);
        }catch (Exception e){
            e.printStackTrace();
        }
        return detail;
    }
    /*
     *判断蓝牙是否打开
     */
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
    /*
     *
     */
    //private
    //private void setIsExit(){
     //   handler=new Handler()
       // {
         //   @Override
        //    public void handleMessage(Message msg){
          //      super.handleMessage(msg);
         //       isExit=false;
        //    }
     //   }
   // }
}

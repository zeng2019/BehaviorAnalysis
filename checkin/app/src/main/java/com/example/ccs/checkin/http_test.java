package com.example.ccs.checkin;


import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class http_test extends AppCompatActivity {
    private static final String TAG = http_test.class.getSimpleName();
    //Handler mhandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_test);
        init();
    }
    private void init(){

        String url="http://www.baidu.com";
        getDatasync(url);
    }
    /*
     *  get的同步请求
     */
    public void getDatasync(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //build okHttpClient instance
                    OkHttpClient client = new OkHttpClient();
                    // build request instance
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    // build response instance
                    Response response=client.newCall(request).execute();
                    int resCode=response.code();

                    Log.i(TAG, "run:请求码"+resCode);
                    //handler传递消息
                    Message msg=new Message();
                    msg.arg1=resCode;
                    Log.i(TAG, "run:msg "+msg.arg1);
                    mhandler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private final MyHandler mhandler=new MyHandler();




    //
    private static class MyHandler extends Handler{

      //  private final WeakReference<http_test> mActivity;

      //  public MyHandler(http_test activity){
      //
       //     mActivity=new WeakReference<http_test>(activity);
    //    }
        @Override
        public void handleMessage(Message msg){
            final int code=msg.arg1;
            Log.i(TAG, "handleMessage: "+code);
            // Toast.makeText(http_test.this,code,Toast.LENGTH_SHORT).show();
        }
    }
}

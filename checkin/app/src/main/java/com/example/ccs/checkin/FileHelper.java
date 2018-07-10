package com.example.ccs.checkin;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;


public class FileHelper {

    private Context mContext;


    public FileHelper(Context context){
        super();
         this.mContext = context;
    }

   /*
    *
    */
    public void writeText(String filename,String filecontent){
        //创建文本输出流
        try{
            //MODE.PRIVE 覆盖创建,MODE_APPEND 在已存在的文档中追加
            FileOutputStream output= mContext.openFileOutput(filename,Context.MODE_APPEND);
            //将字符串以字节流的形式写入到输出流中
            output.write(filecontent.getBytes());
            //关闭输出流
            output.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public String  readText(String filename){
        String readStr="";

        try{
        FileInputStream input=new FileInputStream(filename);
        byte[] temp=new byte[input.available()];
        readStr= new String(temp);
        input.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return readStr;
    }
}


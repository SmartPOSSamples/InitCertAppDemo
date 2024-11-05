package com.cloudpos.demo.cert;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Constant extends Activity{
	public static final String CERT_TERMINAL_ALIAS = "terminal";
	public static final String CERT_OWNER_ALIAS = "owner";
	public static final String CERT_APP_ALIAS = "app";
	public static final String CERT_COMM_ALIAS = "comm";
	public static final String CERT_PUB_ALIAS = "pub";
	public static final String CERT_KEYLOADER_ALIAS = "keyloader";
	public static final int TIME_OUT = 60000;
	
	public static final int STATUS_OFFLINE = 0;
	public static final int STATUS_AUTO = 1;
	public static final  int STATUS = STATUS_OFFLINE;	//
	
	protected static final int DEFAULT_LOG = 1;
	protected static final int SUCCESS_LOG = 2;
	protected static final int FAILED_LOG = 3;
	protected static final int CLEAR_LOG = 0;
	
	protected static  Handler mHandler = null;
	
	public static void writerInLog(String obj){
		if(mHandler != null){
			Message msg = new Message();
			msg.what = DEFAULT_LOG;
			msg.obj = obj;
			mHandler.sendMessage(msg);
		}else{
			Log.e("APP", obj);
		}
	}
	
	public static void writerInSuccessLog(String obj){
		if(mHandler != null){
			Message msg = new Message();
			msg.what = SUCCESS_LOG;
			msg.obj = obj;
			mHandler.sendMessage(msg);
		}else{
			Log.e("APP", obj);
		}
	}
	
	public static void writerInFailedLog(String obj){
		if(mHandler != null){
			Message msg = new Message();
			msg.what = FAILED_LOG;
			msg.obj = obj;
			mHandler.sendMessage(msg);
		}else{
			Log.e("APP", obj);
		}
	}
	
	public static void clear(){
		
		if(mHandler != null){
			Message msg = new Message();
			msg.what = CLEAR_LOG;
			mHandler.sendMessage(msg);
		}
	}

}

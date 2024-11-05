package com.cloudpos.demo.cert.offline;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.cloudpos.demo.cert.Constant;
import com.cloudpos.demo.cert.MainActivity;
import com.cloudpos.demo.cert.R;
import com.cloudpos.demo.cert.control.HSMModel;
import com.cloudpos.demo.cert.offline.utils.InitUtils;
import com.cloudpos.demo.cert.util.Logger;

import java.io.IOException;
import java.io.InputStream;


public class InitTerminalThread extends Thread{
	
	
	protected Context context;
	public InitTerminalThread(Context context) {
		this.context = context;
	}

	public  void run(){
		setUp();
		initTerminal();
		tearDown();
	}
	
	protected void initTerminal() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isSuccess = false;
		boolean isChecked = preferences.getBoolean(getString(R.string.cert_type_owner_pref), true);
		Logger.debug("cert_type_owner_pref is "+ (isChecked ?"is Checked":"is unchecked"));
		if(isChecked){
			String alias = getString(R.string.offline_owner_ailas_demo);
			String type = Constant.CERT_OWNER_ALIAS;
			String fileName = getString(R.string.offline_owner_file_name_demo);
			isSuccess = injectCert(fileName, alias, type);
			if(isSuccess){
				Editor editor = preferences.edit();
				editor.putBoolean(getString(R.string.cert_type_owner_pref), false);
				editor.commit();
			}
		}

		String type_0 = Constant.CERT_KEYLOADER_ALIAS;
		String alias_0 = getString(R.string.offline_keyloader_ailas_demo);
		String fileName_0 = getString(R.string.offline_keyloader_file_name_demo);
		injectCert(fileName_0, alias_0, type_0);


	}

	protected int setUp(){
		MainActivity.clear();
		InitUtils.checkTimeAndSetTime();
		InitUtils.checkTiminalCerts(context);
		return 0;
		
	}
	protected void tearDown(){
		
	}
	protected String getString(int id){
		return context.getResources().getString(id);
	}
	
	protected boolean injectCert(String fileName , String aliasPrefix, String type){
		HSMModel hsm = HSMModel.getInstance(context);
		byte[] buff = null;
		InputStream in = null;
		try {
			in = context.getAssets().open(fileName);
			int length = in.available();
			buff = new byte[length];
			in.read(buff);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			if(in  != null){
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
		boolean isSuccess = false;
		if(buff != null){ 
			isSuccess = hsm.injectCertNoPrefix(buff, aliasPrefix, type);
		}
		if(!isSuccess){
			MainActivity.writerInFailedLog( "inject(" + getTypeValue(type) + ") failed...\nFile Name : " + fileName);
		}else{
			MainActivity.writerInSuccessLog("initialize("+getTypeValue(type)+") success");
		}
		return isSuccess;
		
	}

	protected boolean deleteCert(String type,String alias){
		HSMModel hsm = HSMModel.getInstance(context);
		boolean isSuccess = hsm.deleteCertificate(type,alias);
		return isSuccess;
	}
	
	protected String getTypeValue(String type){
		String value = "";
		if(type.equals(Constant.CERT_OWNER_ALIAS)){
			value = "Terminal Owner Cert";
		}else if(type.equals(Constant.CERT_COMM_ALIAS)){
			value = "Server Comm Cert";
		}else if(type.equals(Constant.CERT_PUB_ALIAS)){
			value = "Public Cert";
		}else if(type.equals(Constant.CERT_APP_ALIAS)){
			value = "Aplication Signing Cert";
		}else if(type.equals(Constant.CERT_KEYLOADER_ALIAS)){
			value = "keyloader Cert";
		}
		return value;
	}

}

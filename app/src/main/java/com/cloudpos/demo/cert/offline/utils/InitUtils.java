package com.cloudpos.demo.cert.offline.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSONObject;
import com.cloudpos.demo.cert.MainActivity;
import com.cloudpos.demo.cert.R;
import com.cloudpos.demo.cert.control.HSMModel;
import com.cloudpos.demo.cert.util.SystemUtil;
import com.cloudpos.hsm.HSMDevice;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InitUtils {

	public static void checkTimeAndSetTime() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy/MM/dd/ HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		MainActivity.writerInLog("time：" + str);
	}

	public static void checkTiminalPropertys() {
		MainActivity.writerInLog("Model:\t\t\t\t\t\t\t\t\t\t\t"
				+ SystemUtil.getSystemModel().toUpperCase());
		MainActivity.writerInLog("Type:\t\t\t\t\t\t\t\t\t\t\t\t"
				+ SystemUtil.getProperty("ro.firmware.type").toUpperCase());
		MainActivity.writerInLog("Logo:\t\t\t\t\t\t\t\t\t\t\t\t"
				+ SystemUtil.getProperty("ro.wp.logo").toUpperCase());
		MainActivity.writerInLog("HSM version:\t\t\t\t\t\t\t\t"
				+ SystemUtil.getProperty("ro.wp.hsm.ver").toUpperCase());
		//
	}

	public static void checkTiminalCerts(Context context) {
		HSMModel hsm = HSMModel.getInstance(context);
		hsm.open();
		String[] ownerAlias = hsm.queryCertAlias(HSMDevice.CERT_TYPE_TERMINAL_OWNER);
		if(ownerAlias == null){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = preferences.edit();
			editor.putBoolean(context.getResources().getString(R.string.cert_type_owner_pref), true);
			editor.commit();
		}
		MainActivity.writerInLog("Owner Cert Alias:\t\t\t\t\t\t" + JSONObject.toJSONString(ownerAlias));
		String[] mAppAlias = hsm.queryCertAlias(HSMDevice.CERT_TYPE_APP_ROOT);
		MainActivity.writerInLog("App Cert Alias:\t\t\t\t\t\t\t" + JSONObject.toJSONString(mAppAlias));
		String[] mCommunicateAlias = hsm
				.queryCertAlias(HSMDevice.CERT_TYPE_COMM_ROOT);
		MainActivity.writerInLog("Communicate Cert Alias:\t\t"
				+ JSONObject.toJSONString(mCommunicateAlias));
		String[] mKeyloaderAlias = hsm
				.queryCertAlias(HSMDevice.CERT_TYPE_KEYLOADER_ROOT);
		MainActivity.writerInLog("Keyloader Cert Alias:\t\t"
				+ JSONObject.toJSONString(mKeyloaderAlias));
		hsm.close();
	}
	public static boolean verifyOwnerCert(Context context,String ownerAlias) {
		boolean existOwnerCert = HSMModel.getInstance(context).existOwnerCert(
				ownerAlias);
		return existOwnerCert;
	}
	public static boolean verifyOwnerCertIsNull(Context context) {
		boolean existOwnerCert = HSMModel.getInstance(context).existOwnerCert("");
		return existOwnerCert;
	}

}

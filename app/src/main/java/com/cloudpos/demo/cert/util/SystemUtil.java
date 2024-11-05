package com.cloudpos.demo.cert.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SystemUtil {
	
	
	/**
	 * 获取apk的版本号 currentVersionCode
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getAPPVersionCodeFromAPP(Context ctx) {
		int currentVersionCode = 0;
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			String appVersionName = info.versionName; // 版本名
			currentVersionCode = info.versionCode; // 版本号
			System.out.println(currentVersionCode + " " + appVersionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch blockd
			e.printStackTrace();
		}
		return currentVersionCode;
	}

	private static final String LOG_TAG = "SystemUtil";
	public static final String KERNEL_VERSION = "kernelVersion";
	public static final String SYSTEM_VERSION = "systemVersion";
	public static final String BOOTLOADER_VERSION = "bootloaderVersion";

	public static boolean isCUP() {
		return "cup".equals(SystemProperties.get("ro.wp.logo"));
	}
	
	public static boolean isMinsheng() {
		return "MINSHENG".equals(SystemProperties.get("ro.wp.logo").toUpperCase());
	}

	private static final String FILENAME_PROC_VERSION = "/proc/version";

	@SuppressLint("NewApi")
	public static String getFormattedKernelVersion() {
		String procVersionStr;

		try {
			procVersionStr = readLine(FILENAME_PROC_VERSION);

			final String PROC_VERSION_REGEX = "\\w+\\s+" + /* ignore: Linux */
			"\\w+\\s+" + /* ignore: version */
			"([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
			"\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
														 * group 2:
														 * (xxxxxx@xxxxx
														 * .constant)
														 */
			"\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /*
													 * ignore: (gcc ..)
													 */
			"([^\\s]+)\\s+" + /* group 3: #26 */
			"(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
			"(.+)"; /* group 4: date */

			Pattern p = Pattern.compile(PROC_VERSION_REGEX);
			Matcher m = p.matcher(procVersionStr);

			if (!m.matches()) {
				Log.e(LOG_TAG, "Regex did not match on /proc/version: "
						+ procVersionStr);
				return "Unavailable";
			} else if (m.groupCount() < 4) {
				Log.e(LOG_TAG, "Regex match on /proc/version only returned "
						+ m.groupCount() + " groups");
				return "Unavailable";
			} else {
				// add by hans for eng user version
				String eng_user = ("1".equals(SystemProperties
						.get("ro.checkcert")) || (SystemProperties.get(
						"ro.checkcert").isEmpty() && "1"
						.equals(SystemProperties.get("ro.secure", "1")))) ? " user"
						: " eng";
				return (new StringBuilder(m.group(1)).append(" ")
						/*.append(m.group(2)).append("")*/.append(m.group(3))
						.append(" ")/*.append(m.group(4))*/.append(eng_user))
						.toString();
			}
		} catch (IOException e) {
			Log.e(LOG_TAG,
					"IO Exception when getting kernel version for Device Info screen",
					e);

			return "Unavailable";
		}
	}
	
	/**
     * Reads a line from the specified file.
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
    /**
     * 当前activity是否在前台
     * @param context : 当前activity
     * @return 结果
     * */
    public static boolean isRunningForeground(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
			return true;
		}
		return false;
	}
    
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager =  context.getPackageManager();
            PackageInfo info = manager.getPackageInfo( context.getPackageName(), 0);
            int versionCode = info.versionCode;
//            String version = info.versionName;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * 通过包名查看pid
     * 
     * */
    public static String getPackageNameByPid(String pid, ActivityManager activityManager){
		try {
			int iPid = Integer.parseInt(pid);
			List<RunningAppProcessInfo> apps = activityManager.getRunningAppProcesses();
			for(RunningAppProcessInfo info:apps){
				if(info.pid == iPid){
					Log.v(LOG_TAG, "pid 的包名"+info.processName);
					return info.processName;
				}
			}
		} catch (Exception e) {
			Log.v(LOG_TAG, "未能找到该"+pid+"对应的程序包名！");
		}
		return null;
	}
    
    
    
    public static boolean isWizarpos(){
        String model = SystemProperties.get("ro.product.model").trim();
        model = model.toUpperCase();
        if (model.equals("WIZARPOS 1")) {
            return true;
        }
        return false;
    }
    
    public static boolean isWizarpad(){
        String model = SystemProperties.get("ro.product.model").trim();
        model = model.toUpperCase();
        if (model.equals("WIZARPAD 1")) {
            return true;
        }
        return false;
    }
    public static boolean isQ1(){
        String model = SystemProperties.get("ro.product.model").trim();
        model = model.toUpperCase();
        if (model.equals("WIZARHAND_Q1")) {
            return true;
        }
        return false;
    }
    public static boolean networkStatusOK(Context mContext) {
    	  boolean netStatus = false;
    	  try{
    	   ConnectivityManager connectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    	   NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();
    	   if (activeNetworkInfo != null) {
    	    if (activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) {
    	     netStatus = true;
    	    }
    	   }
    	  } catch (Exception e) {e.printStackTrace();}
    	  return netStatus;
    	 }
    
    public static boolean isMatchedWizarposSN(String sn){
        boolean isMatched = false;
        Pattern pattern = Pattern.compile("^WP[0-9]{14}$"); // 从头匹配
        Matcher isWizarpos = pattern.matcher(sn);
        if(isWizarpos.matches()){
        	// this is wizarpos pos
        	if(sn.length() == 16){
        		isMatched = true;
        	}
        }
        return isMatched;
	}
    public static String getSystemModel(){
    	String model = SystemProperties.get("ro.product.model");
		model = model.toUpperCase();
    	return model;
    }
    
    public static String getProperty(String key){
//    	"ro.product.model"
    	String model = SystemProperties.get(key);
		model = model.toUpperCase();
    	return model;
    }
    
    public static void setDateTime(int year, int month, int day, int hour, int minute) throws IOException, InterruptedException {
		 
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
         
        long when = c.getTimeInMillis();
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - when < 0) {
            SystemClock.setCurrentTimeMillis(c.getTimeInMillis());
        }
        Log.d("SystemUtil", "set tm="+when + ", now tm="+now);
    }
    
    /**
     * addeb by Stone for uninstall third offline application,if device version is "user" or "userdebug",return true,else return false.
     */
    public static boolean isUserVersion(){
		boolean isUser = false;
		try {
			Class<?> POSSecurity = Class.forName("com.cloudpos.android.core.util.POSSecurity");
			Object resultObj = POSSecurity.getMethod("getPosTypeLable").invoke(POSSecurity);
			if(resultObj != null){
				String type = resultObj.toString();
				isUser = type.equals("user") /*|| type.equals("user-adb")*/;
			}
		}catch(Exception e){
			Log.e(LOG_TAG, "getPosTypeLable has failed . happen exception ");
			isUser = false;
		}
		return isUser;
	}
    /**
     * 检测该包名所对应的应用是否存在
     * @param packageName
     * @return
     */

    public static boolean checkPackageIsInstalled(String packageName , Context context){ 
        if (packageName == null || "".equals(packageName)) {
        	return false; 
        }
        try{ 
        	context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES); 
            return true; 
        }catch (NameNotFoundException e){ 
            return false; 
        } 

    }
}

package com.cloudpos.demo.cert.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextViewUtil {
	
	
	public static void infoColorfulTextView( TextView log_text, String msg ,int textColor){
		int start = log_text.getText().length();
		
		log_text.append(msg);
		Spannable style = (Spannable) log_text.getText();
		int end = start + msg.length();
		ForegroundColorSpan color;
		color = new ForegroundColorSpan(textColor);
		style.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		int offset=log_text.getLineCount()*log_text.getLineHeight();
		
		if(offset  > log_text.getHeight()){
			int size = offset-log_text.getHeight();
			log_text.scrollTo(0,size*10);
		}
		
//		moveScroller(log_text);
	}
	
	public static void infoRedTextView( TextView log_text, String msg ){
		infoColorfulTextView(log_text, msg, Color.RED);
	}
	
	public static void infoBlueTextView( TextView log_text, String msg ){
		infoColorfulTextView(log_text, msg, Color.BLUE);
	}
	
	public static void infoMAGENTATextView( TextView log_text, String msg ){
		infoColorfulTextView(log_text, msg, Color.BLACK);
	}

}

package com.cloudpos.demo.cert;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cloudpos.demo.cert.offline.InitTerminalThread;
import com.cloudpos.demo.cert.util.PackageUtils;
import com.cloudpos.demo.cert.util.TabToast;
import com.cloudpos.demo.cert.util.TextViewUtil;

public class MainActivity extends Constant implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button settings_btn = (Button) this.findViewById(R.id.settings);
        Button run_btn = (Button) this.findViewById(R.id.run_cert);
        Button runAPKbtn = (Button) this.findViewById(R.id.run_apk);
        Button test_btn = (Button) this.findViewById(R.id.test);
        settings_btn.setOnClickListener(this);
        runAPKbtn.setOnClickListener(this);
        test_btn.setOnClickListener(this);

        runAPKbtn.setVisibility(View.GONE);
        run_btn.setOnClickListener(this);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TextView log_text = (TextView) MainActivity.this.findViewById(R.id.logcat);
                if (msg.what == DEFAULT_LOG) {
                    String str = "" + msg.obj + "\n";
                    TextViewUtil.infoMAGENTATextView(log_text, str);
                } else if (msg.what == SUCCESS_LOG) {
                    String str = "\t" + msg.obj + "\n";
                    TextViewUtil.infoBlueTextView(log_text, str);
                } else if (msg.what == FAILED_LOG) {
                    String str = "\t" + msg.obj + "\n";
                    TextViewUtil.infoRedTextView(log_text, str);
                } else if (msg.what == CLEAR_LOG) {
                    log_text.setText("");
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void installCert(boolean isInstallApk) {
        if (initThread == null || initThread.getState() == Thread.State.TERMINATED) {
            initThread = new InitTerminalThread(this);
            initThread.start();
        } else {
            if (initThread.getState() == Thread.State.RUNNABLE) {
                TabToast.makeText(this, this.getString(R.string.init_finish_err));
            }
        }
    }

    Thread initThread = null;

    @Override
    public void onClick(View arg0) {
        int index = arg0.getId();
        if (index == R.id.run_cert) {
            installCert(false);

        } else if (index == R.id.run_apk) {
            installCert(true);

        } else if (index == R.id.test) {
            if (initThread != null && initThread.getState() != Thread.State.TERMINATED) {
                TabToast.makeText(this, this.getString(R.string.init_finish_err));
            } else {
                PackageUtils.uninstallNormal(this, this.getPackageName());
            }
        } else if (index == R.id.settings) {
            if (initThread != null && initThread.getState() != Thread.State.TERMINATED) {
                TabToast.makeText(this, this.getString(R.string.init_finish_err));
            } else {
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
            }
        }
    }

}

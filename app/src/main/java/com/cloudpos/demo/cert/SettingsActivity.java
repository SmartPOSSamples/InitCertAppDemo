package com.cloudpos.demo.cert;

import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.cloudpos.demo.cert.control.HSMModel;
import com.cloudpos.hsm.HSMDevice;

import org.bouncycastle.openssl.PEMReader;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends PreferenceActivity {

    public static final String APP_TAG = "Preference";
    static Map<String, X509Certificate> certsMap = new HashMap<String, X509Certificate>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings_preference);
            // initialize the preferences
            init();

        }

        private void init() {
            Preference cert_list_pref = findPreference(this.getString(R.string.certmgr_cert_list_pref));
            cert_list_pref.setOnPreferenceClickListener(this);
            handler.post(run);
        }

        Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    addCertList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        private void addCertList() {
            boolean isOpen = HSMModel.getInstance(this.getContext()).open();
            if (isOpen) {
                addCertPreference(HSMDevice.CERT_TYPE_TERMINAL_OWNER, "owner key");
                addCertPreference(HSMDevice.CERT_TYPE_APP_ROOT, "app root key");
                addCertPreference(HSMDevice.CERT_TYPE_COMM_ROOT, "communicate key");
                addCertPreference(HSMDevice.CERT_TYPE_PUBLIC_KEY, "public key");
            } else {
//				CertNotifHandlerUtil.notifErrorMessage(this, this.getString(R.string.certmgr_notif_error_could_not_execute_query)/* "无法查询证书，请查看终端是否合法或联系管理员！" */);
            }
            HSMModel.getInstance(this.getContext()).close();
        }

        private void addCertPreference(int type, String typeName) {
            int result = HSMModel.getInstance(this.getContext()).queryCertsCount(type);
            String title = this.getString(R.string.certmgr_cert_alias);
            String summary = this.getString(R.string.certmgr_cert_type);
            if (result > 0) {
                String[] lables = HSMModel.getInstance(this.getContext()).queryCerts(type);
                if (lables == null) return;
                Log.d(APP_TAG, result + "," + typeName + "," + JSONObject.toJSONString(lables));
                if (lables.length == 1) {
                    Preference preference = new Preference(this.getActivity());
                    preference.setPersistent(false);
                    String key = lables[0] + type;
                    preference.setKey(lables[0] + type);
                    preference.setTitle(title + lables[0]);
                    preference.setSummary(summary + typeName);
                    preference.setOnPreferenceClickListener(this);
                    ((PreferenceGroup) findPreference(this.getString(R.string.certmgr_cert_list_pref))).addPreference(preference);
                    addMap(type, key, lables[0]);
                } else {
                    for (String lable : lables) {
                        Preference preference = new Preference(this.getActivity());
                        preference.setPersistent(false);
                        String key = lable + type;
                        preference.setKey(key);
                        preference.setTitle(title + lable);
                        preference.setSummary(summary + typeName);
                        preference.setOnPreferenceClickListener(this);
                        ((PreferenceGroup) findPreference(this.getString(R.string.certmgr_cert_list_pref))).addPreference(preference);
                        addMap(type, key, lable);
                    }
                }
            }
        }

        private void addMap(int type, String key, String laybel) {
            byte[] certBuff = HSMModel.getInstance(this.getContext()).getCertificate(type, laybel);
            if (certBuff != null) {
                try {
                    @SuppressWarnings("resource")
                    PEMReader pemReader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(certBuff)));
                    Object obj;
                    List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
                    while ((obj = pemReader.readObject()) != null) {
                        if (obj instanceof X509Certificate) {
                            x509Certificates.add((X509Certificate) obj);
                        }
                    }
                    if (x509Certificates.size() != 0) {
                        X509Certificate certificate = x509Certificates.get(0);
                        certsMap.put(key, certificate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (certsMap.containsKey(preference.getKey())) {
                X509Certificate cert = certsMap.get(preference.getKey());
                SslCertificate mSslCert = new SslCertificate(cert);
                CertDetails.showCertDialog(this.getActivity(), mSslCert);
                return true;
            }
            return false;
        }
    }

}

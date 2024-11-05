package com.cloudpos.demo.cert.control;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.demo.cert.Constant;
import com.cloudpos.demo.cert.util.Logger;
import com.cloudpos.hsm.HSMDevice;

public class HSMModel {

    public static final String APP_TAG = "HSMModel";

    private HSMDevice device;

    private HSMModel(Context mContext) {
        if (device == null) {
            device = (HSMDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.hsm");
        }
    }

    public static HSMModel getInstance(Context mContext) {
        if (instance == null) {
            instance = new HSMModel(mContext);
        }
        return instance;
    }

    private static HSMModel instance;

    public boolean injectOwnerCert(String alias, byte[] bufCert) throws Exception {
        Log.d(APP_TAG, "alias：" + alias + ", bufCert " + bufCert);
        boolean isSuccess = false;
        if (bufCert == null) {
            return false;
        }
        isSuccess = device.injectRootCertificate(device.CERT_TYPE_TERMINAL_OWNER, alias, bufCert, device.CERT_FORMAT_PEM);
        Log.d(APP_TAG, "inject result：" + isSuccess);
        return isSuccess;
    }

    public boolean injectCommCert(String alias, byte[] bufCert) {
        boolean isSuccess = false;
        if (bufCert == null) {
            return false;
        }
        try {
            isSuccess = device.injectRootCertificate(device.CERT_TYPE_COMM_ROOT, alias, bufCert, device.CERT_FORMAT_PEM);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        Log.d(APP_TAG, "inject result：" + isSuccess);
        return isSuccess;
    }

    public boolean injectKeyloaderCert(String alias, byte[] bufCert) {
        boolean isSuccess = false;
        if (bufCert == null) {
            return false;
        }
        Logger.debug("int certType = %s, String alias = %s, byte[] bufCert = %s, int bufLength = %s, int dataFormat = %s",
                device.CERT_TYPE_KEYLOADER_ROOT, alias, bufCert, bufCert.length, device.CERT_FORMAT_PEM);
        try {
            isSuccess = device.injectRootCertificate(device.CERT_TYPE_KEYLOADER_ROOT, alias, bufCert, device.CERT_FORMAT_PEM);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public boolean injectAppCert(String alias, byte[] bufCert) {
        boolean isSuccess = false;
        if (bufCert == null) {
            return false;
        }
        try {
            isSuccess = device.injectRootCertificate(device.CERT_TYPE_APP_ROOT, alias, bufCert, device.CERT_FORMAT_PEM);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        Log.d(APP_TAG, "inject result：" + isSuccess);
        return isSuccess;
    }


    public boolean injectCertNoPrefix(byte[] buff, String alias, String type) {
        boolean isSuccess = false;
        if (open()) {
            try {
                if (type.equals(Constant.CERT_OWNER_ALIAS)) {
                    isSuccess = injectOwnerCert(alias, buff);
                } else if (type.equals(Constant.CERT_COMM_ALIAS)) {
                    isSuccess = injectCommCert(alias, buff);
                } else if (type.equals(Constant.CERT_PUB_ALIAS)) {
                    isSuccess = injectPubCert(alias, Constant.CERT_TERMINAL_ALIAS, buff);
                } else if (type.equals(Constant.CERT_APP_ALIAS)) {
                    isSuccess = injectAppCert(alias, buff);
                } else if (type.equals(Constant.CERT_KEYLOADER_ALIAS)) {
                    isSuccess = injectKeyloaderCert(alias, buff);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        close();
        return isSuccess;

    }

    public boolean injectPubCert(String alias, String aliasPrivateKey, byte[] bufCert) {
        boolean result = false;
        try {
            result = device.injectPublicKeyCertificate(alias, aliasPrivateKey, bufCert, device.CERT_FORMAT_PEM);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        Log.d(APP_TAG, "invoke injectPublicCert method , result = " + result);
        return result;
    }

    public int queryCertsCount(int type) {
        try {
            String[] certs = device.queryCertificates(type);
            if (certs == null) return 0;
            Log.d(APP_TAG, "invoke queryCertsCount method , result = " + certs.length);
            return certs.length;
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] queryCerts(int type) {
        try {
            String[] certs = device.queryCertificates(type);
            Log.d(APP_TAG, "invoke queryCerts method , result = " + JSONObject.toJSONString(certs));
            return certs;
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getCertificate(int type, String ailas) {
        byte[] result = null;
        try {
            result = device.getCertificate(type, ailas.toString(), device.CERT_FORMAT_PEM);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean open() {
        try {
            device.open();
            Log.d(APP_TAG, "invoke open method , result: true");
            return true;
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean close() {
        try {
            device.close();
            Log.d(APP_TAG, "invoke close method , result = true");
            return true;
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] queryCertAlias(int indexType) {
        try {
            String[] result = device.queryCertificates(indexType);
            return result;
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCertificate(String type, String alias) {
        boolean isSuccess = false;
        if (open()) {
            try {
                if (type.equals(Constant.CERT_OWNER_ALIAS)) {
                    isSuccess = device.deleteCertificate(device.CERT_TYPE_TERMINAL_OWNER, "MyOwner");
                } else if (type.equals(Constant.CERT_COMM_ALIAS)) {
                    isSuccess = device.deleteCertificate(device.CERT_TYPE_COMM_ROOT, alias);
                } else if (type.equals(Constant.CERT_PUB_ALIAS)) {
                    isSuccess = device.deleteCertificate(device.CERT_TYPE_PUBLIC_KEY, alias);
                } else if (type.equals(Constant.CERT_APP_ALIAS)) {
                    isSuccess = device.deleteCertificate(device.CERT_TYPE_APP_ROOT, alias);
                } else if (type.equals(Constant.CERT_KEYLOADER_ALIAS)) {
                    isSuccess = device.deleteCertificate(device.CERT_TYPE_KEYLOADER_ROOT, alias);
                }
                Log.d(APP_TAG, "delete certificate result：" + isSuccess);
                return isSuccess;
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
        close();
        return isSuccess;
    }

    public boolean existOwnerCert(String ownerAlias) {
        boolean isSuccess = open();
        if (isSuccess) {
            String[] alias = queryCerts(device.CERT_TYPE_TERMINAL_OWNER);
            close();
            if (alias != null) {
                for (String alia : alias) {
                    if (alias != null && alia.equals(ownerAlias)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

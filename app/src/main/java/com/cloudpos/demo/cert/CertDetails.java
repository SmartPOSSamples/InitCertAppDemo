package com.cloudpos.demo.cert;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslCertificate;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cloudpos.demo.cert.R;


public class CertDetails {
    
    public static void showCertDialog(final Context host, SslCertificate mSslCert) {

        View view = createCertificateView(host,mSslCert);
        AlertDialog.Builder builder = new AlertDialog.Builder(host);
        builder.setTitle(host.getString(R.string.certmgr_cert));
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        final Dialog certDialog = builder.create();
        certDialog.show();
    }

    public static View createCertificateView(Context context, SslCertificate sslCertificate) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.certificate_view, null);

        String issuer = sslCertificate.getIssuedTo().getCName();
        String validity = sslCertificate.getValidNotAfterDate().toString();

        TextView issuerNameTextView = view.findViewById(R.id.issuer_name);
        TextView validityPeriodTextView = view.findViewById(R.id.validity_period);

        issuerNameTextView.setText("Issuer: " + issuer);
        validityPeriodTextView.setText("Valid Until: " + validity);

        return view;
    }


}

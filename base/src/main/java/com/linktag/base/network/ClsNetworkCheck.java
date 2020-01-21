package com.linktag.base.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ClsNetworkCheck {

    public static boolean isConnectable(Context context) {
        if (context == null)
            return false;

        ConnectivityManager connectivityManager = null;
        NetworkInfo networkInfo = null;
        boolean isConnectable = false;

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = (connectivityManager).getActiveNetworkInfo();

        if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
            isConnectable = true;
        }

        return isConnectable;
    }

}
